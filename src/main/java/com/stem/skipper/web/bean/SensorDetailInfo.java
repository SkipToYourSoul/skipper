package com.stem.skipper.web.bean;

/**
 * Created by liye on 2017/4/14.
 */
public class SensorDetailInfo {
    private String address;
    private double temperature;
    private double humidity;
    private double averageTemperature;
    private double averageHumidity;
    private double shiftTemperature;
    private double shiftHumidity;
    private double maxTemperature;
    private double minTemperature;
    private double maxHumidity;
    private double minHumidity;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public double getAverageHumidity() {
        return averageHumidity;
    }

    public void setAverageHumidity(double averageHumidity) {
        this.averageHumidity = averageHumidity;
    }

    public double getShiftTemperature() {
        return shiftTemperature;
    }

    public void setShiftTemperature(double shiftTemperature) {
        this.shiftTemperature = shiftTemperature;
    }

    public double getShiftHumidity() {
        return shiftHumidity;
    }

    public void setShiftHumidity(double shiftHumidity) {
        this.shiftHumidity = shiftHumidity;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public SensorDetailInfo(String address, double temperature, double humidity, double averageTemperature, double averageHumidity, double shiftTemperature, double shiftHumidity, double maxTemperature, double minTemperature, double maxHumidity, double minHumidity) {
        this.address = address;
        this.temperature = temperature;
        this.humidity = humidity;
        this.averageTemperature = averageTemperature;
        this.averageHumidity = averageHumidity;
        this.shiftTemperature = shiftTemperature;
        this.shiftHumidity = shiftHumidity;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.maxHumidity = maxHumidity;
        this.minHumidity = minHumidity;
    }

    @Override
    public String toString() {
        return "SensorDetailInfo{" +
                "address='" + address + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", averageTemperature=" + averageTemperature +
                ", averageHumidity=" + averageHumidity +
                ", shiftTemperature=" + shiftTemperature +
                ", shiftHumidity=" + shiftHumidity +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", maxHumidity=" + maxHumidity +
                ", minHumidity=" + minHumidity +
                '}';
    }
}
