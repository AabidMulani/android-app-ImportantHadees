/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.abmulani.importanthadees.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import in.abmulani.importanthadees.R;
import timber.log.Timber;

public class NetworkUtility {
    private static final String TAG = "$NetworkUtility";
    private static String mUserAgent = null;
    private static NetworkUtility instance;
    private Context context;
    private ConnectivityManager connectivityManager = null;
    private static AlertDialog alert;
    int networkType = -1;

    private NetworkUtility(Context context) {
        super();
        this.context = context;
    }

    private static NetworkUtility getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtility(context);
        }
        return instance;
    }

    public static boolean isNetworkAvailable(Context context) {
        return getInstance(context).isNetworkAvailable();
    }

    public static String getUserAgent(Context mContext) {
        if (mUserAgent == null) {
            mUserAgent = mContext.getResources().getString(R.string.app_name);
            try {
                String packageName = mContext.getPackageName();
                String version = mContext.getPackageManager().getPackageInfo(packageName,
                        0).versionName;
                mUserAgent = mUserAgent + " (" + packageName + "/" + version + ")";
                Timber.d(TAG, "User agent set to: " + mUserAgent);
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(TAG, "Unable to find self by package name", e);
            }
        }
        return mUserAgent;
    }

    private boolean isNetworkAvailable() {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            networkType = networkInfo.getType();
        }
        boolean isNetworkAvailable = false;
        if (networkInfo == null) return false;
        NetworkInfo.State network = networkInfo.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public boolean isWIFI() {
        boolean isWIFI = false;
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            isWIFI = true;
        }
        return isWIFI;
    }

    public boolean isMobile() {
        boolean isMobile = false;
        if (networkType == ConnectivityManager.TYPE_MOBILE) {
            isMobile = true;
        }
        return isMobile;
    }

    public static void noNetworkDialog(Context context) {
        if (isNoNetworkAlertDialogVisible()) {
            Timber.d(TAG, "dialog already showing");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string
                    .string_error_no_network_dialog))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }
                    );
            alert = builder.create();
            alert.show();
        }
    }

    private static boolean isNoNetworkAlertDialogVisible() {
        if (alert == null || !alert.isShowing()) {
            return false;
        } else {
            return true;
        }
    }
}
