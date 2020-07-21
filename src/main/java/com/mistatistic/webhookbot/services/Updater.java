package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Updater {
    private final List<Home> homes;
    private final String address;

    public Updater(List<Home> homes, String address) {
        this.homes = homes;
        this.address = address;
    }

    public boolean hasHomes() {
        MilByAPI milByAPI = new MilByAPI();
        List<Home> currentHomes = milByAPI.getHomes();
        try {
            Date date = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm");
            TimeUnit.MINUTES.sleep(60 - Integer.parseInt(formatForDateNow.format(date)));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return !currentHomes.equals(homes);
    }
}
