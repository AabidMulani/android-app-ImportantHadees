package in.abmulani.importanthadees;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import in.abmulani.importanthadees.utils.AppConstants;
import in.abmulani.importanthadees.utils.Utils;

/**
 * Created by AABID on 02-09-2014.
 */
public class BaseActivity extends ActionBarActivity {

    public static final String NO_INTERNET_FILTER = "in.abmulani.importanthadees.NO_INTERNET";
    protected Activity activity;
    protected BaseApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        application = ((BaseApplication) getApplication());
    }


    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNoNetworkDialog(context);
        }
    };


    public void onResume() {
        super.onResume();
        AppConstants.resumeCount++;
        registerReceiver(myBroadcastReceiver, new IntentFilter(NO_INTERNET_FILTER));
    }

    public void onPause() {
        super.onPause();
        AppConstants.pauseCount++;
        unregisterReceiver(myBroadcastReceiver);
    }


    public void showNoNetworkDialog(Context context) {
        Utils.showThisMsg(activity, getString(R.string.error_no_network),
                getString(R.string.string_error_no_network_dialog), null, null);
    }

    public Activity getBaseActivity() {
        return activity;
    }

    public BaseApplication getBaseApplication() {
        return application;
    }

}
