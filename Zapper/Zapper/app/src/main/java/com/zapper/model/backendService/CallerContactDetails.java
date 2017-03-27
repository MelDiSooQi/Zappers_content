package com.zapper.model.backendService;

import android.util.Log;

import com.zapper.model.backendService.restful.ApiClient;
import com.zapper.model.backendService.restful.ApiInterface;
import com.zapper.model.beans.Contact;
import com.zapper.model.beans.ContactDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public class CallerContactDetails
{
    private ContactDetailsListener callBackListener;

    public CallerContactDetails(final String URL, final String TAG, int id)
    {
        ApiInterface apiService = ApiClient.getClient(URL)
                .create(ApiInterface.class);

        Call<ContactDetails> call = apiService.getContactDetails(id);
        call.enqueue(new Callback<ContactDetails>() {
            @Override
            public void onResponse(Call<ContactDetails> call,
                                   Response<ContactDetails> response) {
                int statusCode = response.code();
                if(statusCode == 200) {
                    ContactDetails contactDetails = response.body();
                    if(callBackListener != null) {
                        callBackListener.onContactDetailsAvailable(contactDetails);
                    }
                }else
                {
                    if(callBackListener != null) {
                        callBackListener.onContactDetailsAvailable(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactDetails> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, URL+"\n"+"Error : "+ t.toString());
                if(callBackListener != null) {
                    callBackListener.onContactDetailsAvailable(null);
                }
            }
        });
    }

    public void setCallBackListener(ContactDetailsListener callBackListener) {
        this.callBackListener = callBackListener;
    }
}
