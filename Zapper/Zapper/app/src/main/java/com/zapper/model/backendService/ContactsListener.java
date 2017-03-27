package com.zapper.model.backendService;

import com.zapper.model.beans.Contact;

import java.util.List;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

public interface ContactsListener {

    void onContactAvailable(List<Contact> contacts);
}
