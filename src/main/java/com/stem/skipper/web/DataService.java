package com.stem.skipper.web;

import com.stem.skipper.domain.*;
import com.stem.skipper.web.bean.SensorDetailInfo;
import com.stem.skipper.web.bean.SensorDownloadInfo;
import com.stem.skipper.web.bean.SensorSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liye on 2017/3/29.
 */
@Service
public class DataService {
    @Autowired
    SensorInfoRepository sensorInfoRepository;

    @Autowired
    SensorStatusRepository sensorStatusRepository;

    @Autowired
    SensorStartRepository sensorStartRepository;

    private static final String temperaturePort = "A0";
    private static final String humidityPort = "A1";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat doubleFormat = new DecimalFormat("#.00");

    /**
     * find monitor start time
     * view in index.html
     * @return
     * @throws ParseException
     */
    public List<String> findSensorStartTime() throws ParseException {
        String startTime = sensorStartRepository.findTopByOrderByTimeDesc().getTime();
        long interval = new Date().getTime() - dateFormat.parse(startTime).getTime();
        long hour = interval / 1000 / 3600;
        long minute = (interval - hour*3600*1000) / 1000 / 60;

        List<String> result = new ArrayList<String>();
        String dataTime = sensorInfoRepository.findTopByOrderByTimestampDesc().getTimestamp();
        if (new Date().getTime() - dateFormat.parse(dataTime).getTime() > 60*1000*5) {
            result.add("-");
            result.add("已关闭");
        } else {
            result.add(String.valueOf(hour) + "小时" + String.valueOf(minute) + "分钟");
            result.add("记录中");
        }
        return result;
    }

    public int findOnlineSensorCount(){
        return sensorStatusRepository.countByStatus(1);
    }

    /**
     * change sensor status: 0 or 1
     * @param queryParams: key value
     */
    public void changeSensorStatus(Map<String, String> queryParams){
        String address = queryParams.get("address");
        int status = Integer.valueOf(queryParams.get("status"));
        String equipment = "设备1";
        if (status == 1)
            status = 0;
        else
            status = 1;

        sensorStatusRepository.save(new SensorStatus(address, status, equipment,
                Double.valueOf(queryParams.get("uppertemp")),
                Double.valueOf(queryParams.get("lowertemp")),
                Double.valueOf(queryParams.get("upperhumi")),
                Double.valueOf(queryParams.get("lowerhumi"))));
    }

    /**
     * modify sensor status info
     * @param queryParams: key value
     */
    public int modifySensorStatus(Map<String, String> queryParams){
        String address = queryParams.get("address");
        String equipment = "设备1";
        SensorStatus sensorStatus = sensorStatusRepository.findByAddress(address);
        int result = 0;
        int status = 0;
        if (sensorStatus != null){
            status = sensorStatus.getStatus();
            result = 1;
        }
        sensorStatusRepository.save(new SensorStatus(address, status, equipment,
                Double.valueOf(queryParams.get("upper-temp")),
                Double.valueOf(queryParams.get("lower-temp")),
                Double.valueOf(queryParams.get("upper-humi")),
                Double.valueOf(queryParams.get("lower-humi"))));
        return result;
    }

    /**
     * find online sensor config info
     * @return online sensors' status
     */
    public List<SensorStatus> findOnlineSensorStatus(){
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);
        return sensorStatuses;
    }

    /**
     * find all sensor config info
     * @return
     */
    public List<SensorStatus> findAllSensorStatus(){
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findAll();
        return sensorStatuses;
    }

    /**
     * find online sensor simple info for monitor page
     * return newest temperature and humidity
     * @return SensorSimpleInfo list
     */
    public List<SensorSimpleInfo> findOnlineSensorInfo(){
        // find online sensors
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);
        List<String> addresses = new ArrayList<String>();
        for (SensorStatus sensors : sensorStatuses){
            addresses.add(sensors.getAddress());
        }

        List<SensorSimpleInfo> sensorInfoBeanList = new ArrayList<SensorSimpleInfo>();
        List<SensorInfo> infoList = sensorInfoRepository.findNewestDataByAddresses(addresses);
        // order by address and receive port in query
        for (int i=0; i<infoList.size(); i+=2){
            String address = infoList.get(i).getAddress();
            double temperature = infoList.get(i).getValue();
            double humidity = infoList.get(i+1).getValue();
            String timeStamp = infoList.get(i).getTimestamp().compareTo(infoList.get(i+1).getTimestamp())>0?
                    infoList.get(i).getTimestamp():infoList.get(i+1).getTimestamp();
            sensorInfoBeanList.add(new SensorSimpleInfo(address, temperature, humidity, timeStamp));
        }

        return sensorInfoBeanList;
    }

    /**
     * find online detail sensor info for overview page
     * @param dataTime(min) the begin time of the data range
     * @param dataInterval(s) the interval time of sample data
     * @return sensor detail info list
     * @throws ParseException
     */
    public List<SensorDetailInfo> findOnlineDetailInfo(String dataTime, String dataInterval) throws ParseException {
        // get the online sensor
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);

        // the begin time of get data
        long time = new Date().getTime() - Long.parseLong(dataTime)*60*1000;
        String date = dateFormat.format(new Date(time));

        // the interval of sample data
        long interval = Long.parseLong(dataInterval)*1000;
        List<SensorDetailInfo> sensorDetailInfoList = new ArrayList<SensorDetailInfo>();

        // traverse the online status
        for (SensorStatus sensors : sensorStatuses) {
            // get sensor info in recent time order by time desc
            String sensorAddress = sensors.getAddress();
            List<SensorInfo> sensorInfoList = sensorInfoRepository.findByAddressAndTimestampGreaterThanEqualOrderByTimestampDesc(sensorAddress, date);

            // merge sensor info to simple info
            List<SensorSimpleInfo> sensorSimpleInfoList = mergeSimpleSensorInfo(sensorInfoList);
            if (sensorSimpleInfoList.size() == 0)
                continue;

            // filter data according to the interval
            sensorSimpleInfoList = filterSensorInfoAccordingInterval(sensorSimpleInfoList, interval);

            // make simple data to detail data
            sensorDetailInfoList.add(getDetailInfoAccordingSimpleInfo(sensorSimpleInfoList));
        }

        return sensorDetailInfoList;
    }

    private List<SensorSimpleInfo> mergeSimpleSensorInfo(List<SensorInfo> infoList){
        List<SensorSimpleInfo> simpleList = new ArrayList<SensorSimpleInfo>();
        for (int i=0; i<infoList.size(); i+=2){
            if (i+1 >= infoList.size())
                break;
            if (!infoList.get(i).getTimestamp().equals(infoList.get(i+1).getTimestamp()))
                continue;
            SensorInfo info = infoList.get(i);
            String address = info.getAddress();
            String timeStamp = info.getTimestamp();
            double temperature = -1.0;
            double humidity = -1.0;
            if (info.getReceive_port().equals(temperaturePort)) {
                temperature = info.getValue();
                humidity = infoList.get(i+1).getValue();
            }
            else if (info.getReceive_port().equals(humidityPort)) {
                humidity = info.getValue();
                temperature = infoList.get(i+1).getValue();
            }
            simpleList.add(new SensorSimpleInfo(address, temperature, humidity, timeStamp));
        }
        return simpleList;
    }

    private List<SensorSimpleInfo> filterSensorInfoAccordingInterval(List<SensorSimpleInfo> sensorInfoList, long interval) throws ParseException {
        List<SensorSimpleInfo> list = new ArrayList<SensorSimpleInfo>();
        long nextTime = dateFormat.parse(sensorInfoList.get(0).getTimeStamp()).getTime();
        for (SensorSimpleInfo info : sensorInfoList){
            long currentTime = dateFormat.parse(info.getTimeStamp()).getTime();
            if (currentTime <= nextTime) {
                list.add(info);
                nextTime = currentTime - interval;
            }
        }
        return list;
    }

    private SensorDetailInfo getDetailInfoAccordingSimpleInfo(List<SensorSimpleInfo> sensorSimpleInfoList){
        String address = sensorSimpleInfoList.get(0).getAddress();
        double temperature = sensorSimpleInfoList.get(0).getTemperature();
        double humidity = sensorSimpleInfoList.get(0).getHumidity();
        double averageTemperature = 0.0;
        double averageHumidity = 0.0;
        double shiftTemperature;
        double shiftHumidity;

        int size = sensorSimpleInfoList.size();
        double minTemperature = 100.0;
        double maxTemperature = 0.0;
        double minHumidity = 100.0;
        double maxHumidity = 0.0;

        for (SensorSimpleInfo info: sensorSimpleInfoList){
            averageTemperature += info.getTemperature();
            averageHumidity += info.getHumidity();
            if (info.getTemperature() > maxTemperature)
                maxTemperature = info.getTemperature();
            if (info.getTemperature() < minTemperature)
                minTemperature = info.getTemperature();
            if (info.getHumidity() > maxHumidity)
                maxHumidity = info.getHumidity();
            if (info.getHumidity() < minHumidity)
                minHumidity = info.getHumidity();
        }

        averageHumidity = averageHumidity/size;
        averageTemperature = averageTemperature/size;
        shiftHumidity = maxHumidity - minHumidity;
        shiftTemperature = maxTemperature - minTemperature;

        return new SensorDetailInfo(address, temperature, humidity, averageTemperature, averageHumidity,
                shiftTemperature, shiftHumidity, maxTemperature, minTemperature, maxHumidity, minHumidity);
    }

    /**
     * query download data
     * @param dataTime
     * @param dataInterval
     * @return
     * @throws ParseException
     */
    public List<SensorDownloadInfo> findOnlineDownloadInfo(String dataTime, String dataInterval) throws ParseException {
        // get the online sensor
        List<SensorStatus> sensorStatuses = sensorStatusRepository.findByStatus(1);

        // the begin time of get data
        long time = new Date().getTime() - Long.parseLong(dataTime)*60*1000;
        String date = dateFormat.format(new Date(time));

        // the interval of sample data
        long interval = Long.parseLong(dataInterval)*1000;
        Map<String, List<String>> timeData = new HashMap<String, List<String>>();

        // traverse the online status
        for (SensorStatus sensors : sensorStatuses) {
            // get sensor info in recent time order by time desc
            String sensorAddress = sensors.getAddress();
            List<SensorInfo> sensorInfoList = sensorInfoRepository.findByAddressAndTimestampGreaterThanEqualOrderByTimestampDesc(sensorAddress, date);

            // merge sensor info to simple info
            List<SensorSimpleInfo> sensorSimpleInfoList = mergeSimpleSensorInfo(sensorInfoList);
            if (sensorSimpleInfoList.size() == 0)
                continue;

            // filter data according to the interval
            sensorSimpleInfoList = filterSensorInfoAccordingInterval(sensorSimpleInfoList, interval);

            String firstTime = sensorSimpleInfoList.get(0).getTimeStamp();
            long firstTimeMills = dateFormat.parse(firstTime).getTime();
            int timeCount = 0;
            for (SensorSimpleInfo simpleInfo : sensorSimpleInfoList){
                String timestamp = dateFormat.format(firstTimeMills - interval*(timeCount++));
                double temperature = simpleInfo.getTemperature();
                double humidity = simpleInfo.getHumidity();
                String data = sensorAddress + "#" + doubleFormat.format(temperature) + "#" + doubleFormat.format(humidity);

                if (timeData.containsKey(timestamp)){
                    List<String> list = timeData.get(timestamp);
                    list.add(data);
                    timeData.put(timestamp, list);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(data);
                    timeData.put(timestamp, list);
                }
            }
        }

        // handle to result data
        List<SensorDownloadInfo> sensorDownloadInfoList = new ArrayList<SensorDownloadInfo>();
        for (Map.Entry<String, List<String>> entry : timeData.entrySet()){
            String timestamp = entry.getKey();
            List<String> values = entry.getValue();
            sensorDownloadInfoList.add(new SensorDownloadInfo(timestamp, values));
        }

        return sensorDownloadInfoList;
    }
}
