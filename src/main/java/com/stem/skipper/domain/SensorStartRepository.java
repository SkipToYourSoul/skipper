package com.stem.skipper.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by liye on 2017/4/15.
 */
public interface SensorStartRepository extends JpaRepository<SensorStart, String> {
    SensorStart findTopByOrderByTimeDesc();
}
