package in.abmulani.importanthadees;

import com.crashlytics.android.Crashlytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.orm.SugarApp;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

import in.abmulani.importanthadees.database.HadeesTable;
import in.abmulani.importanthadees.utils.AppConstants;
import in.abmulani.importanthadees.utils.ConnectivityAwareUrlClient;
import in.abmulani.importanthadees.utils.CrashlyticsTree;
import in.abmulani.importanthadees.utils.WebServiceInterface;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;


/**
 * Created by AABID on 02-09-2014.
 */
public class BaseApplication extends SugarApp {

    private WebServiceInterface webServiceInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Crashlytics.start(this);
            Timber.plant(new CrashlyticsTree());
        }
        Timber.e("Initial Start");
        HadeesTable.hasThisRowCount("");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public WebServiceInterface getWebServiceInterface() {

        if (webServiceInterface != null) {
            return webServiceInterface;
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConstants.BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setClient(new ConnectivityAwareUrlClient(new OkClient(okHttpClient),
                        getApplicationContext()))
                .build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        webServiceInterface = restAdapter.create(WebServiceInterface.class);

        return webServiceInterface;
    }

}
