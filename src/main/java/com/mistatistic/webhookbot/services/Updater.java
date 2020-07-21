package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Updater {
    private final List<Home> homes;
    private List<Home> currentHomes;

    public Updater(List<Home> homes) {
        this.homes = homes;
    }

    private boolean hasHomes() {
        MilByAPI milByAPI = new MilByAPI();
        currentHomes = milByAPI.getHomes();
        try {
            Date date = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm");
            TimeUnit.MINUTES.sleep(61 - Integer.parseInt(formatForDateNow.format(date)));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return !currentHomes.equals(homes);
    }

    public List<Home> getHomes() {
        if (hasHomes()) {
            return currentHomes;
        }
        return null;
    }
}
