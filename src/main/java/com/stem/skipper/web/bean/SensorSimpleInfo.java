package com.stem.skipper.web.bean;

/**
 * Created by liye on 2017/4/14.
 */
public class SensorSimpleInfo {
    private String address;
    private double temperature;
    private double humidity;
    private String timeStamp;

    public SensorSimpleInfo(String address, double temperature, double humidity, String timeStamp) {
        this.address = address;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timeStamp = timeStamp;
    }

    public String getAddress() {
        return address;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "SensorSimpleInfo{" +
                "address='" + address + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
