package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;
import lombok.Setter;

import java.util.List;

@Setter
public class Updater {
    private List<Home> homes;
    private List<Home> currentHomes;

    public Updater(List<Home> homes) {
        this.homes = homes;
    }

    public List<Home> getUpdatedHomes() {
        currentHomes = Parser.getHomes();
        return currentHomes.containsAll(homes) ? null : currentHomes;
    }
}
