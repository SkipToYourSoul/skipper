package com.stem.skipper.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liye on 2017/3/28.
 */

@Entity
@Table(name = "skipper")
public class SensorInfo {
    @Id
    private int id;
    private String address;
    private String send_port;
    private String receive_port;
    private double value;
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSend_port() {
        return send_port;
    }

    public void setSend_port(String send_port) {
        this.send_port = send_port;
    }

    public String getReceive_port() {
        return receive_port;
    }

    public void setReceive_port(String receive_port) {
        this.receive_port = receive_port;
    }

    public double getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensorInfo{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", send_port='" + send_port + '\'' +
                ", receive_port='" + receive_port + '\'' +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
