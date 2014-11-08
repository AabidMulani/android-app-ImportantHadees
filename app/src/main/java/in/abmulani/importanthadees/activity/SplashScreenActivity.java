package in.abmulani.importanthadees.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import in.abmulani.importanthadees.R;
import timber.log.Timber;


public class SplashScreenActivity extends Activity {

    private boolean isBackPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        isBackPressed = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBackPressed) {
                    return;
                }
                Timber.d("Handler");
                startActivity(new Intent(SplashScreenActivity.this, HomeScreenActivity.class));
                finish();
            }
        }, 3000);
    }


    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");
        super.onBackPressed();
        isBackPressed = true;
    }

}