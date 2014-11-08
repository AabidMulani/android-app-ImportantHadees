package in.abmulani.importanthadees.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import in.abmulani.importanthadees.R;
import timber.log.Timber;

/**
 * Created by AABID on 03-09-2014.
 */
public class Utils {

    public static boolean checkInternetConnection(Context mContext) {
        boolean retVal = false;
        try {
            ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr
                    .getActiveNetworkInfo().isConnected()) {
                retVal = true;
            } else {
                retVal = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static Typeface getThisFont(Context context, int textStyleIndex) {
        final int FONT_BOLD = 1;
        final int FONT_CONDENSED = 2;
        final int FONT_LIGHT = 3;
        final int FONT_MEDIUM = 4;
        final int FONT_REGULAR = 0;
        final int FONT_THIN = 5;

        Typeface typeface;
        switch (textStyleIndex) {
            case FONT_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
                break;
            case FONT_CONDENSED:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Condensed.ttf");
                break;
            case FONT_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
                break;
            case FONT_MEDIUM:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
                break;
            case FONT_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                break;
            case FONT_THIN:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
                break;
            default:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
                break;
        }
        return typeface;
    }

    private static ProgressDialog progressDialog;

    public static boolean isEmpty(EditText editText) {
        return (editText.getText().toString().isEmpty());
    }

    public static void showToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setError(EditText editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void hideKeyboard(Context context, View editText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            if (editText != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Context context, View editText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            if (editText != null) {
                imm.showSoftInput(editText, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showThisMsg(Activity activity, String title, String message, DialogInterface.OnClickListener
            onOkClickListener, DialogInterface.OnClickListener onCancelClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) {
            builder.setTitle(title);
            builder.setIcon(R.drawable.ic_launcher);
        } else {
            builder.setTitle(null);
        }
        builder.setMessage(message);
        builder.setPositiveButton(activity.getString(android.R.string.ok), onOkClickListener);
        if (onCancelClickListener != null) {
            builder.setNegativeButton(activity.getString(android.R.string.cancel), onCancelClickListener);
        }
        Dialog dialog = builder.show();
        setAlertDialogTheme(dialog, activity.getResources().getColor(R.color.theme_main_color));
        dialog.show();
    }

    public static void showProgressBar(Activity activity, String message, boolean isCancellable,
                                       DialogInterface.OnCancelListener onCancelListener) {
        progressDialog = ProgressDialog.show(activity, null, message, true, isCancellable,
                onCancelListener);
    }

    public static void updateProgressMsg(String msg) {
        if (progressDialog != null) {
            progressDialog.setMessage(msg);
        }
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static void setAlertDialogTheme(Dialog dialog, int themeColor) {
        try {
            int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(dividerId);
            divider.setBackgroundColor(themeColor);
            int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView tv = (TextView) dialog.findViewById(textViewId);
            tv.setTextColor(themeColor);
        } catch (NullPointerException ex) {
            Timber.e("setAlertDialogTheme", Log.getStackTraceString(ex));
        }
    }

    public static String convertImageToBase64(Bitmap img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public static Bitmap convertBase64ToImage(String base64) {
        byte[] byteArray = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static boolean isApplicationInBackGround() {
        return AppConstants.resumeCount == AppConstants.pauseCount;
    }

    public static boolean isApplicationInForeGround() {
        return AppConstants.resumeCount > AppConstants.pauseCount;
    }


}
