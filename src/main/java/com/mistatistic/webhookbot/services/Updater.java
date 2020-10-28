package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;
import lombok.Setter;

import java.util.List;

@Setter
public class Updater {
    private List<Home> homes;
    private List<Home> currentHomes;
    private final HomeRepository homeRepository;

    public Updater(List<Home> homes, HomeRepository homeRepository) {
        this.homes = homes;
        this.homeRepository = homeRepository;
    }

    public List<Home> getUpdatedHomes() {
        Parser parser = new Parser(homeRepository);
        currentHomes = parser.getHomes();
        return currentHomes.containsAll(homes) ? null : currentHomes;
    }
}
