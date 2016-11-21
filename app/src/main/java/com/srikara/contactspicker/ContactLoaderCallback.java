package com.srikara.contactspicker;

import com.srikara.contactspicker.model.Contact;

import java.util.List;

/**
 * Created by vijayasrikaradas on 21/11/16.
 */
public interface ContactLoaderCallback {

    void onLoadCompleted(List<Contact> contactList);
}
