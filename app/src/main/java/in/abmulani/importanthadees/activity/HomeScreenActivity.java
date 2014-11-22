package in.abmulani.importanthadees.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.abmulani.importanthadees.BaseActivity;
import in.abmulani.importanthadees.BaseApplication;
import in.abmulani.importanthadees.R;
import in.abmulani.importanthadees.adaptor.HadeesListAdaptor;
import in.abmulani.importanthadees.database.HadeesTable;
import in.abmulani.importanthadees.models.HadeesResponse;
import in.abmulani.importanthadees.utils.AppConstants;
import in.abmulani.importanthadees.utils.AppSharedPreference;
import in.abmulani.importanthadees.utils.Utils;
import in.abmulani.importanthadees.utils.WebServiceInterface;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class HomeScreenActivity extends BaseActivity {

    public static final String EXTRA_OBJECT = "extra_object";
    public static final String EXTRA_REFRESH = "extra_refresh";
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private AppSharedPreference sharedPreference;
    private String registrationId;
    private GoogleCloudMessaging gcm;
    private Integer highestCount = -1, lowestCount = -1;
    private boolean isLoading;
    private HadeesListAdaptor hadeesAdaptorList;

    @InjectView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.listView)
    ListView listView;
    private List<HadeesTable> currentHadeesList = new ArrayList<HadeesTable>();
    private boolean loadMoreOldData = true;
    private boolean doubleBackToExitPressedOnce;
    private boolean doForceFullRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.inject(this);
        setActionBarTitle();
        sharedPreference = new AppSharedPreference(activity);
        if (!sharedPreference.isGCMSent()) {
            Utils.showProgressBar(activity, getString(R.string.initial_processing), false, null);
            gcmRegistration();
        }
        doForceFullRefresh = getIntent().getBooleanExtra(EXTRA_REFRESH, false);
        hadeesAdaptorList = new HadeesListAdaptor(activity, currentHadeesList, getWindowHeight());
        listView.setAdapter(hadeesAdaptorList);
        listView.setOnScrollListener(listScrollListener);
        listView.setOnItemClickListener(onItemClickListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHadeesApiCall(highestCount, false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.theme_main_color,
                R.color.theme_main_color,
                R.color.theme_main_color,
                R.color.theme_main_color);

    }

    @Override
    public void onResume() {
        super.onResume();
        readFromDatabaseFirst();
    }

    private void readFromDatabaseFirst() {
        Timber.d("readFromDatabaseFirst");
        List<HadeesTable> tableList = HadeesTable.getAllHadees();
        if (tableList != null) {
            showThisInTheListView(tableList);
        } else {
            getHadeesApiCall(-1, false);
        }
    }

    private void showThisInTheListView(List<HadeesTable> hadeesTables) {
        Timber.d("showThisInTheListView");
        highestCount = Integer.valueOf(hadeesTables.get(0).getRowCount());
        lowestCount = Integer.valueOf(hadeesTables.get(hadeesTables.size() - 1).getRowCount());
        loadMoreOldData = lowestCount < 4;
        currentHadeesList.clear();
        currentHadeesList.addAll(hadeesTables);
        hadeesAdaptorList.notifyDataSetChanged();
        if (doForceFullRefresh) {
            getHadeesApiCall(highestCount, false);
            doForceFullRefresh = false;
        }
    }

    private void getHadeesApiCall(int limit, boolean getLowerData) {
        Timber.d("getHadeesApiCall");
        String msg;
        swipeRefreshLayout.setRefreshing(true);
        if (limit == -1) {
            msg = getString(R.string.loading_hadees);
        } else if (getLowerData) {
            msg = getString(R.string.loading_older_hadees);
        } else {
            msg = getString(R.string.loading_new_hadees);
        }
//        Utils.showProgressBar(activity, msg, false, null);
        if (getLowerData) {
            if (limit > 50) {
                limit = limit - 50;
            } else {
                limit = 0;
            }
        }
        WebServiceInterface webServiceInterface = ((BaseApplication) getApplication()).getWebServiceInterface();
        webServiceInterface.getHadees(String.valueOf(limit), new Callback<List<HadeesResponse>>() {
            @Override
            public void success(List<HadeesResponse> list, Response response) {
//                Utils.hideProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (list.size() > 0) {
                    HadeesTable.storeAllThisHadees(list);
                    List<HadeesTable> tableList = HadeesTable.getAllHadees();
                    showThisInTheListView(tableList);
                } else {
                    Utils.showToast(activity, "No New Hadees Found");
                }
            }

            @Override
            public void failure(RetrofitError error) {
//                Utils.hideProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                try {
                    String err = error.getCause().toString();
                    if (err != null) {
                        Utils.showToast(activity, err);
                        Timber.e("[899]: " + err);
                    }
                } catch (Exception ex) {
                    Timber.e(Log.getStackTraceString(ex));
                } finally {
                    Utils.showToast(activity, "#ERROR:[899]");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Utils.showToast(activity, "Please click BACK again to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void setActionBarTitle() {
        Spannable actionBarTitle = new SpannableString(getString(R.string.app_name));
        actionBarTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)),
                0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getHadeesApiCall(highestCount, false);
            return true;
        }
        if (id == R.id.action_share_app) {
            Utils.showToast(activity, "Share This App [Not in Beta Version]");
            return true;
        }
        if (id == R.id.action_rate_app) {
            Utils.showToast(activity, "Rate Us [Not in Beta Version]");
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void gcmRegistration() {
        Timber.d("gcmRegistration");
        //check for google play service
        if (checkPlayServices()) {
            //get device token from shared preference if present
            String regId = sharedPreference.getGCMRegID();
            int appVer = sharedPreference.getAppVersion();
            int currentVersion = getAppVersion(activity);
            if (TextUtils.isEmpty(regId) || appVer != currentVersion) {
                //generate new device token
                registerInBackground();
            } else {
                registrationId = regId;
                sendRegistrationIdToBackend();
            }
        } else {
            Utils.hideProgressDialog();
        }
    }

    private void registerInBackground() {
        Timber.d("registerInBackground");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    String GCMRegistrationID = gcm.register(AppConstants.GOOGLE_SENDER_ID);
                    registrationId = GCMRegistrationID;
                    msg = "Device registered, registration ID=" + GCMRegistrationID;
                    storeRegistrationId(activity, GCMRegistrationID);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Timber.i("registration id = " + result);
                if ((registrationId == null) || registrationId.isEmpty()) {
                    registerInBackground();
                } else {
                    sendRegistrationIdToBackend();
                }
            }
        }.execute();
    }

    private void storeRegistrationId(Context context, String regId) {
        Timber.d("storeRegistrationId");
        sharedPreference.setGCMRegID(regId);
        sharedPreference.setAppVersion(getAppVersion(context));
    }

    public void sendRegistrationIdToBackend() {
        Timber.d("sendRegistrationIdToBackend");
        WebServiceInterface webServiceInterface = ((BaseApplication) getApplication()).getWebServiceInterface();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        hashMap.put("name", telephonyManager.getDeviceId());
        hashMap.put("device_token", registrationId);
        webServiceInterface.addDevice(hashMap, new Callback<String>() {

                    @Override
                    public void success(String message, Response response) {
                        Utils.hideProgressDialog();
                        Utils.showToast(activity, message);
                        sharedPreference.setGCMSent(true);
                        Utils.showToast(activity, "DONE");
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Utils.hideProgressDialog();
                        try {
                            String err = retrofitError.getCause().toString();
                            if (err != null) {
                                Utils.showToast(activity, retrofitError.getResponse().getBody().toString() + "");
                                Utils.showToast(activity, err);
                                Timber.e("[688]: " + err);
                            }
                        } catch (Exception ex) {
                            Timber.e(Log.getStackTraceString(ex));
                        } finally {
                            Utils.showToast(activity, "#ERROR: [688]");
                        }
                    }
                }
        );
    }

    private int getAppVersion(Context context) {
        Timber.d("getAppVersion");
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkPlayServices() {
        Timber.d("checkPlayServices");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Utils.hideProgressDialog();
                Utils.showThisMsg(activity, getString(R.string.err_play_service_title),
                        activity.getString(R.string.err_no_play_service), null, null);
            }
            return false;
        }
        return true;
    }

    android.widget.AbsListView.OnScrollListener listScrollListener = new AbsListView
            .OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (listView.getAdapter() == null) {
                return;
            }

            if (listView.getAdapter().getCount() == 0) {
                return;
            }

            if (currentHadeesList == null || currentHadeesList.size() == 0) {
                return;
            }

            int itemCount = visibleItemCount + firstVisibleItem;
            if (itemCount >= totalItemCount && !isLoading && loadMoreOldData) {
                // It is time to add new data.
                isLoading = true;
                getHadeesApiCall(lowestCount, true);
            }
        }
    };

    public int getWindowHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HadeesTable hadeesTable = hadeesAdaptorList.getItem(position);
            HadeesTable.setThisAsRead(hadeesTable.getRowCount(), true);
            Intent intent = new Intent(activity, DetailViewActivity.class);
            intent.putExtra(EXTRA_OBJECT, hadeesTable);
            startActivity(intent);
        }
    };

}
