package com.stem.skipper.web.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by liye on 2017/4/17.
 */
public class SensorDownloadInfo {
    private String timestamp;
    private List<String> list;

    public SensorDownloadInfo(String timestamp, List<String> list) {
        this.timestamp = timestamp;
        this.list = list;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
