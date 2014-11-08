package in.abmulani.importanthadees.utils;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import in.abmulani.importanthadees.R;

public class NoInternetException extends IOException {

    public NoInternetException(Context context) {
        super(context.getString(R.string.error_no_network));
        Intent i = new Intent("com.citruspay.NO_INTERNET");
        context.sendBroadcast(i);
    }
}
