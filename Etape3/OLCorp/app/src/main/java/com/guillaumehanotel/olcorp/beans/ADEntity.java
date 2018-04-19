package com.guillaumehanotel.olcorp.beans;

public class ADEntity {

    protected int id;
    protected String name;
    protected String distinguishedName;


    public ADEntity(int id, String name, String distinguishedName) {
        this.id = id;
        this.name = name;
        this.distinguishedName = distinguishedName;
    }

    public ADEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ADEntity(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }


}
