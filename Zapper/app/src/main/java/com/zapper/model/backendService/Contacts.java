package com.zapper.model.backendService;

import android.util.Log;

import com.zapper.model.backendService.restful.ApiClient;
import com.zapper.model.backendService.restful.ApiInterface;
import com.zapper.model.beans.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public class Contacts
{
    private ContactsListener callBackListener;

    public Contacts(final String URL, final String TAG)
    {
        ApiInterface apiService = ApiClient.getClient(URL)
                .create(ApiInterface.class);

        Call<List<Contact>> call = apiService.getAllContact();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                int statusCode = response.code();
                if(statusCode == 200) {
                    List<Contact> contacts = response.body();
                    callBackListener.onContactAvailable(contacts);
                }else
                {
                    callBackListener.onContactAvailable(null);
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, URL+"\n"+"Error : "+ t.toString());
                callBackListener.onContactAvailable(null);
            }
        });
    }

    public void setCallBackListener(ContactsListener callBackListener) {
        this.callBackListener = callBackListener;
    }
}
