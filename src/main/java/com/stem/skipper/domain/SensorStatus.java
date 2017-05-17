package com.stem.skipper.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liye on 2017/3/29.
 */

@Entity
@Table(name = "skipper_status")
public class SensorStatus {
    @Id
    private String address;
    private int status;
    private double uppertemp;
    private double lowertemp;
    private double upperhumi;
    private double lowerhumi;

    public SensorStatus(String address, int status, double uppertemp, double lowertemp, double upperhumi, double lowerhumi) {
        this.address = address;
        this.status = status;
        this.uppertemp = uppertemp;
        this.lowertemp = lowertemp;
        this.upperhumi = upperhumi;
        this.lowerhumi = lowerhumi;
    }

    public SensorStatus(){}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getUppertemp() {
        return uppertemp;
    }

    public void setUppertemp(double uppertemp) {
        this.uppertemp = uppertemp;
    }

    public double getLowertemp() {
        return lowertemp;
    }

    public void setLowertemp(double lowertemp) {
        this.lowertemp = lowertemp;
    }

    public double getUpperhumi() {
        return upperhumi;
    }

    public void setUpperhumi(double upperhumi) {
        this.upperhumi = upperhumi;
    }

    public double getLowerhumi() {
        return lowerhumi;
    }

    public void setLowerhumi(double lowerhumi) {
        this.lowerhumi = lowerhumi;
    }

    @Override
    public String toString() {
        return "SensorStatus{" +
                "address='" + address + '\'' +
                ", status=" + status +
                ", uppertemp=" + uppertemp +
                ", lowertemp=" + lowertemp +
                ", upperhumi=" + upperhumi +
                ", lowerhumi=" + lowerhumi +
                '}';
    }
}
