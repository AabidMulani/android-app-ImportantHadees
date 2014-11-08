package in.abmulani.importanthadees.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSharedPreference {

    private final String PREF_APP_VERSION = "app_version";
    private final String PREF_GCM_REG_ID = "gcm_reg_id";
    private final String APP_PREF = "important_hadees";
    private final String NAME_USER = "first_user";
    private final String GCM_SENT = "gcm_sent";
    private SharedPreferences sharedPreferences;

    private Context context;

    public AppSharedPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    public String getName() {
        return sharedPreferences.getString(NAME_USER, null);
    }

    public void setName(String name) {
        Editor editor = sharedPreferences.edit();
        editor.putString(NAME_USER, name);
        editor.commit();
    }

    public boolean isGCMSent() {
        return sharedPreferences.getBoolean(GCM_SENT, false);
    }

    public void setGCMSent(boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(GCM_SENT, value);
        editor.commit();
    }


    public void setGCMRegID(String gcmId) {
        Editor editor = sharedPreferences.edit();
        editor.putString(PREF_GCM_REG_ID, gcmId);
        editor.commit();
    }

    public String getGCMRegID() {
        return sharedPreferences.getString(PREF_GCM_REG_ID, "");
    }

    public void setAppVersion(int appVersion) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_APP_VERSION, appVersion);
        editor.commit();
    }

    public int getAppVersion() {
        return sharedPreferences.getInt(PREF_APP_VERSION, -1);
    }


}
