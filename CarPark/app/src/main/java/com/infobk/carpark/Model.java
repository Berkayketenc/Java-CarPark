package com.infobk.carpark;

import java.io.Serializable;

public class Model implements Serializable {

    public  String name;
    public  Double latitude;
    public  Double longitude;

    public Model(String name , Double latitude,Double longitude){

        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;


    }

}
