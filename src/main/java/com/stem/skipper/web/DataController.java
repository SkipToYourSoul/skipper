package com.stem.skipper.web;

import com.stem.skipper.domain.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/sensor/status")
    List<SensorStatus> getSensorStatus(){
        return service.findAllSensorStatus();
    }

    @PostMapping(value = "/sensor/status/save/switch")
    String switchSensorStatus(@RequestParam Map<String, String> queryParams){
        try {
            service.changeSensorStatus(queryParams);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @PostMapping(value = "/sensor/status/save/modify")
    String modifySensorStatus(@RequestParam Map<String, String> queryParams){
        String message = "";
        try {
            int result = service.modifySensorStatus(queryParams);
            if (result == 1)
                message = "修改传感器配置: " + queryParams.get("address");
            else
                message = "新增传感器配置: " + queryParams.get("address");
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return message;
    }
}
