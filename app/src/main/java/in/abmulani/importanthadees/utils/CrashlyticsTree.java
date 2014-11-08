package in.abmulani.importanthadees.utils;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Created by Aabid on 03/06/14.
 */
public class CrashlyticsTree extends Timber.HollowTree {
    @Override
    public void v(String message, Object... args) {
        logMessage(message, args);
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        logMessage(message, args);
        // NOTE: We are explicitly not sending the exception to Crashlytics here.
    }

    @Override
    public void i(String message, Object... args) {
        logMessage(message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        logMessage(message, args);
        // NOTE: We are explicitly not sending the exception to Crashlytics here.
    }

    @Override
    public void w(String message, Object... args) {
        logMessage("WARN: " + message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        logMessage("WARN: " + message, args);
        // NOTE: We are explicitly not sending the exception to Crashlytics here.
    }

    @Override
    public void e(String message, Object... args) {
        logMessage("ERROR: " + message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        logMessage("ERROR: " + message, args);
        Crashlytics.logException(t);
    }

    private void logMessage(String message, Object... args) {
        try {
            Crashlytics.log(String.format(message, args));
        } catch (Exception e) {
            //don't handle
        }
    }
}