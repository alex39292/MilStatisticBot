package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Updater {
    private final List<Home> homes;

    public Updater(List<Home> homes) {
        this.homes = homes;
    }

    public boolean hasHomes() {
        MilByAPI milByAPI = new MilByAPI();
        List<Home> currentHomes = milByAPI.getHomes();
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return !currentHomes.equals(homes);
    }
}
