package com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils;

public class ResourceHelper {


    private ResourceHelper(){




    }


    private static ResourceHelper instance;

    public static ResourceHelper getInstance(){
        if(instance == null){
            instance = new ResourceHelper();
        }
        return instance;
    }


}
