package com.zapper.model.backendService.restful;

import com.zapper.model.beans.Contact;
import com.zapper.model.beans.ContactDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public interface ApiInterface {
    @GET("person")
    Call<List<Contact>> getAllContact();

    @GET("person/{id}")
    Call<ContactDetails> getContactDetails(@Path("id") int id);
}
