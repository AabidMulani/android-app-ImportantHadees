package in.abmulani.importanthadees.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;

import in.abmulani.importanthadees.BaseActivity;
import in.abmulani.importanthadees.BaseApplication;
import in.abmulani.importanthadees.R;
import in.abmulani.importanthadees.utils.AppConstants;
import in.abmulani.importanthadees.utils.AppSharedPreference;
import in.abmulani.importanthadees.utils.Utils;
import in.abmulani.importanthadees.utils.WebServiceInterface;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class HomeScreenActivity extends BaseActivity {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private AppSharedPreference sharedPreference;
    private String registrationId;
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_home_screen);
        sharedPreference = new AppSharedPreference(activity);
        if (!sharedPreference.isGCMSent()) {
            Utils.showProgressBar(activity, getString(R.string.initial_processing), false, null);
            gcmRegistration();
        } else {
            processListViewData();
        }
    }

    private void processListViewData() {
        Timber.d("processListViewData");
        Utils.showToast(activity, "Process");
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
        if (id == R.id.action_settings) {
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
                String msg = "";
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
        WebServiceInterface webServiceInterface = ((BaseApplication) getApplication())
                .getWebServiceInterface();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "no name");
        hashMap.put("device_token", registrationId);
        webServiceInterface.addDevice(hashMap, new Callback<String>() {

                    @Override
                    public void success(String message, Response response) {
                        Utils.hideProgressDialog();
                        Utils.showToast(activity, message);
                        sharedPreference.setGCMSent(true);
                        Utils.showToast(activity, "DONE");
                        processListViewData();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Utils.hideProgressDialog();
                        Utils.showToast(activity, "BAD GATEWAY");
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


}
