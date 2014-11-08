package in.abmulani.importanthadees.utils;

import android.content.Context;

import java.io.IOException;

import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import timber.log.Timber;

public class ConnectivityAwareUrlClient implements Client {

    Client wrappedClient;
    private Context context;

    public ConnectivityAwareUrlClient(Client wrappedClient, Context context) {
        this.wrappedClient = wrappedClient;
        this.context = context;

        Timber.e("ConnectivityAwareUrlClient REACHED");
    }

    @Override
    public Response execute(Request request) throws IOException {

        Timber.e("execute REACHED");
        if (!NetworkUtility.isNetworkAvailable(context)) {
            Timber.e("NO INTERNET CONNECTION");
            throw new NoInternetException(context);
        }
        return wrappedClient.execute(request);
    }
}



