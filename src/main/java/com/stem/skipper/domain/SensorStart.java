package com.stem.skipper.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liye on 2017/4/15.
 */
@Entity
@Table(name = "skipper_info")
public class SensorStart {
    @Id
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
