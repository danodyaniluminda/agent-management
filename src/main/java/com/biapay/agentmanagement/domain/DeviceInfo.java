package com.biapay.agentmanagement.domain;


import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "Device_Info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeviceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deviceInfos_DeviceInfo_id_seq")
    @SequenceGenerator(
            name = "deviceInfos_DeviceInfo_id_seq",
            allocationSize = 1,
            sequenceName = "deviceInfos_DeviceInfo_id_seq")
    private Long deviceId;

    private String deviceUid;

    private String osName;

    private String osVersion;

    private String deviceManufacturer;

    private String osFirmwareBuildVersion;

    private String deviceModel;

    private String macAddress;

    private String imeiList;

    private String firebaseDeviceToken;

    private String deviceInfoSignature;

    private String rootedDevice;



}
