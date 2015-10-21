package com.omeletlab.argora.model;

/**
 * Created by akashs on 10/21/15.
 */
public class Crop {

    private String cropName;
    private String stateName;
    private String year;
    private String value;

    public Crop(String cropName, String stateName, String year, String value) {
        this.cropName = cropName;
        this.stateName = stateName;
        this.year = year;
        this.value = value;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
