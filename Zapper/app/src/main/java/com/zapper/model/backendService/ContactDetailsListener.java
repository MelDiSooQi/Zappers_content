package com.zapper.model.backendService;


import com.zapper.model.beans.ContactDetails;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public interface ContactDetailsListener {

    void onContactDetailsAvailable(ContactDetails contactDetails);
}
