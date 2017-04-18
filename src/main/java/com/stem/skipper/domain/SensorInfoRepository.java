package com.stem.skipper.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by liye on 2017/3/28.
 */
public interface SensorInfoRepository extends JpaRepository<SensorInfo, Integer> {
    List<SensorInfo> findTop2ByAddressOrderByTimestampDesc(String address);

    List<SensorInfo> findByAddressAndTimestampGreaterThanEqualOrderByTimestampDesc(String address, String timestamp);

    SensorInfo findTopByOrderByTimestampDesc();
}
