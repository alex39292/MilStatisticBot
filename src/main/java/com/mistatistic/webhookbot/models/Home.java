package com.mistatistic.webhookbot.models;

import lombok.Data;

import java.io.Serializable;

public @Data
class Home implements Serializable {
    private String id;
    private String address;
    private String floor;
    private String flats;
    private String area;
    private String deadline;
}
