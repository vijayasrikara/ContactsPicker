package com.srikara.contactspicker.model;

import java.util.List;

/**
 * Created by vijayasrikaradas on 21/11/16.
 */
public class Contact {

    public String id;
    public String name;
    public List<ContactEmail> emailList;
    public List<ContactPhone> numberList;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ContactEmail> getEmailList() {
        return emailList;
    }

    public List<ContactPhone> getNumberList() {
        return numberList;
    }

    public void setEmailList(List<ContactEmail> emailList) {
        this.emailList = emailList;
    }

    public void setNumberList(List<ContactPhone> numberList) {
        this.numberList = numberList;
    }
}
