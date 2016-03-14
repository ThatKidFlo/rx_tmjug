package ro.tpg.tmjug.omdb;

import com.google.gson.Gson;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static OmdbApi api;

    public static OmdbApi getApi() {
        if(api == null) {
            api = initApi();
        }

        return api;
    }

    private static OmdbApi initApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        return retrofit.create(OmdbApi.class);
    }
}
