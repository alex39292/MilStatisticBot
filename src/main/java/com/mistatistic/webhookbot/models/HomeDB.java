package com.mistatistic.webhookbot.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "homes")
public @Data class HomeDB implements Serializable {
    @Id
    private Integer id;
    private String address;
    private String floor;
    private String flats;
    private String area;
    private String deadline;
}
