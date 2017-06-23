package com.stem.skipper.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by liye on 2017/3/28.
 */
public interface SensorInfoRepository extends JpaRepository<SensorInfo, Integer> {
    List<SensorInfo> findTop2ByAddressOrderByTimestampDesc(String address);

    @Query(value = "SELECT b.* FROM " +
            "( SELECT address, receive_port, MAX(timestamp) as time FROM skipper " +
            "WHERE address IN :addresses GROUP BY address, receive_port ) a " +
            "JOIN skipper b ON a.address = b.address AND a.time = b.timestamp AND a.receive_port = b.receive_port " +
            "ORDER BY address, receive_port",
            nativeQuery = true)
    List<SensorInfo> findNewestDataByAddresses(@Param("addresses") List<String> addresses);

    List<SensorInfo> findByAddressAndTimestampGreaterThanEqualOrderByTimestampDesc(String address, String timestamp);

    SensorInfo findTopByOrderByTimestampDesc();
}
