package com.stem.skipper.web;

import com.stem.skipper.domain.SensorInfo;
import com.stem.skipper.domain.SensorInfoRepository;
import com.stem.skipper.domain.SensorStatus;
import com.stem.skipper.domain.SensorStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liye on 2017/3/29.
 */
@Service
public class DataService {
    @Autowired
    SensorInfoRepository sensorInfoRepository;

    @Autowired
    SensorStatusRepository sensorStatusRepository;

    private static final String temperaturePort = "a0";
    private static final String humidityPort = "a1";

    public int findOnlineSensorCount(){
        return sensorStatusRepository.countByStatus(1);
    }

    public List<SensorStatus> findOnlineSensorStatus(){
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);
        return sensorStatuses;
    }

    public List<SensorInfoBean> findOnlineSensorInfo(){
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);
        List<SensorInfoBean> sensorInfoBeanList = new ArrayList<SensorInfoBean>();
        for (SensorStatus sensors : sensorStatuses){
            String sensorAddress = sensors.getAddress();
            List<SensorInfo> infoList = sensorInfoRepository.findTop2ByAddress(sensorAddress);

            // merge data
            String address = infoList.get(0).getAddress();
            String timeStamp = infoList.get(0).getTimestamp();
            int temperature = -1;
            int humidity = -1;
            for (SensorInfo info: infoList){
                if (info.getReceive_port().equals(temperaturePort))
                    temperature = info.getValue();
                else if (info.getReceive_port().equals(humidityPort))
                    humidity = info.getValue();
            }
            sensorInfoBeanList.add(new SensorInfoBean(address, temperature, humidity, timeStamp));
        }
        return sensorInfoBeanList;
    }

    public class SensorInfoBean{
        private String address;
        private int temperature;
        private int humidity;
        private String timeStamp;

        public SensorInfoBean(String address, int temperature, int humidity, String timeStamp) {
            this.address = address;
            this.temperature = temperature;
            this.humidity = humidity;
            this.timeStamp = timeStamp;
        }

        public String getAddress() {
            return address;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getHumidity() {
            return humidity;
        }

        public String getTimeStamp() {
            return timeStamp;
        }
    }
}
