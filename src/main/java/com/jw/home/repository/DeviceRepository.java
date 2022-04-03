package com.jw.home.repository;

import com.jw.home.domain.Device;
import com.jw.home.domain.DeviceConnection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<Device, String> {
    boolean existsByConnectionAndSerial(DeviceConnection connection, String serial);
}
