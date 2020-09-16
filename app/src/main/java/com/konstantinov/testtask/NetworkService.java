package com.konstantinov.testtask;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class NetworkService {//код для настройки библиотеки Retrofit.
    private static NetworkService mInstance; //singleton
    private static final String BASE_URL = "http://ios.pixli.site/";
    private Retrofit mRetrofit;

    private NetworkService() {
        RxJava2CallAdapterFactory rxAdapter =
                RxJava2CallAdapterFactory
                        .createWithScheduler( Schedulers.io());
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()//все данные запроса, включая URL, заголовки, тело, выведены в лог
                .addInterceptor(interceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public static NetworkService getInstance() {//singleton
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public IosPixliSiteApi getJSONApi() {
        return mRetrofit.create(IosPixliSiteApi.class); //Retrofit предоставляет реализацию интерфейса
    }
}
