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
    private int uppertemp;
    private int lowertemp;
    private int upperhumi;
    private int lowerhumi;

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

    public int getUppertemp() {
        return uppertemp;
    }

    public int getLowertemp() {
        return lowertemp;
    }

    public int getUpperhumi() {
        return upperhumi;
    }

    public int getLowerhumi() {
        return lowerhumi;
    }

    public void setUppertemp(int uppertemp) {
        this.uppertemp = uppertemp;
    }

    public void setLowertemp(int lowertemp) {
        this.lowertemp = lowertemp;
    }

    public void setUpperhumi(int upperhumi) {
        this.upperhumi = upperhumi;
    }

    public void setLowerhumi(int lowerhumi) {
        this.lowerhumi = lowerhumi;
    }

    public SensorStatus(String address, int status, int uppertemp, int lowertemp, int upperhumi, int lowerhumi) {
        this.address = address;
        this.status = status;
        this.uppertemp = uppertemp;
        this.lowertemp = lowertemp;
        this.upperhumi = upperhumi;
        this.lowerhumi = lowerhumi;
    }

    public SensorStatus(){}
}
