package in.abmulani.importanthadees.utils;

import java.util.HashMap;
import java.util.List;

import in.abmulani.importanthadees.models.HadeesResponse;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by chetan on 18/06/14.
 */

public interface WebServiceInterface {

    @GET("/get_hadees.php")
    void getHadees(@Query("id") String limit, Callback<List<HadeesResponse>> callback);

    @FormUrlEncoded
    @POST("/add_device.php")
    void addDevice(@FieldMap HashMap<String, String> map, Callback<String> callback);

}
