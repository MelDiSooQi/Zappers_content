package com.zapper.model.backendService.restful;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public class ApiClient
{
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String URL)
    {
        if (retrofit==null)
        {
            retrofit = new Retrofit .Builder()
                                    .baseUrl(URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
        }
        return retrofit;
    }
}
