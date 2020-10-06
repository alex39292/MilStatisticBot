package com.mistatistic.webhookbot.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public @Data class User implements Serializable{
    @Id
    private Long id;

    private String state;

    @Column(name = "user_name")
    private String userName;

    private String city;
}
