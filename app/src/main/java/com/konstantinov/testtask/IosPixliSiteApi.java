package com.konstantinov.testtask;

import com.konstantinov.testtask.POJO.RespSendImage;
import com.konstantinov.testtask.POJO.ResponseIosPixli;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IosPixliSiteApi {
    @GET("get.php")
    //Call<ResponseIosPixli> getPostWithID(@Query("action") String call);
    Observable<ResponseIosPixli> getPostWithID(@Query("action") String call);

   @Multipart
   @POST("send.php")
   Observable<String> sendImage(
          @Part("action") RequestBody send_data,
          @Part("contact[name]") String name,
          @Part("contact[surname]") String surname,
          @Part("contact[patronymic]") String patronymic,
          @Part("id") RequestBody  id,
            @Part MultipartBody.Part imagePart
   );
}
