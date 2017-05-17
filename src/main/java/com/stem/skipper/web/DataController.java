package com.stem.skipper.web;

import com.stem.skipper.domain.SensorStatus;
import com.stem.skipper.web.bean.SensorDetailInfo;
import com.stem.skipper.web.bean.SensorDownloadInfo;
import com.stem.skipper.web.bean.SensorSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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

    /**
     * newest sensor info
     * @return address, temperature, humidity, timestamp
     */
    @GetMapping(value = "/sensor/data")
    List<SensorSimpleInfo> getSensorInfo(){
        return service.findOnlineSensorInfo();
    }

    /**
     * all sensor config status
     * @return
     */
    @GetMapping(value = "/sensor/status")
    List<SensorStatus> getSensorStatus(){
        return service.findAllSensorStatus();
    }

    @GetMapping(value = "/sensor/overview/data")
    List<SensorDetailInfo> getDetailSensorInfo(@RequestParam String dataTime, @RequestParam String dataInterval){
        try {
            return service.findOnlineDetailInfo(dataTime, dataInterval);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/sensor/overview/download")
    List<SensorDownloadInfo> getDownloadSensorInfo(@RequestParam String dataTime, @RequestParam String dataInterval){
        try {
            return service.findOnlineDownloadInfo(dataTime, dataInterval);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update sensor status
     * @param queryParams
     * @return
     */
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

    /**
     * insert or update sensor status info
     * @param queryParams
     * @return
     */
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

    /**
     * return the status that sensor is in monitor status or not
     * @return
     */
    @GetMapping(value = "/sensor/status/is/monitor")
    String isSensorMonitor() throws ParseException {
        String result = "已关闭";
        try {
            return service.findSensorStartTime().get(1);
        } catch (ParseException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
