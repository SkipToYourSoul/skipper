package com.stem.skipper.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by liye on 2017/3/29.
 */
public interface SensorStatusRepository extends JpaRepository<SensorStatus, String> {
    List<SensorStatus> findByStatus(int status);

    SensorStatus findByAddress(String address);

    int countByStatus(int status);
}
