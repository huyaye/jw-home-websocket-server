package com.jw.home.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "device")
public class Device {
    @Id
    private String id;

    String homeId;

    // TODO unique index (serial + connection)
    DeviceConnection connection;

    String serial;

    DeviceType type;

    List<DeviceTrait> traits;

    String name;

    DeviceInfo deviceInfo;

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceInfo {
        private String manufacturer;
        private String model;
        private String hwVersion;
        private String swVersion;
    }

    public boolean valid() {
        for (DeviceTrait trait : traits) {
            if (!trait.valid(this.type)) {
                return false;
            }
        }
        return true;
    }
}
