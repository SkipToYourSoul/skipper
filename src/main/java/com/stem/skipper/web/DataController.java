package com.stem.skipper.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by liye on 2017/3/28.
 */
@RestController
@RequestMapping("/")
public class DataController {
    @Autowired
    DataService service;

    @GetMapping(value = "/sensor/data")
    List<DataService.SensorInfoBean> getSensorInfo(){
        return service.findOnlineSensorInfo();
    }
}
