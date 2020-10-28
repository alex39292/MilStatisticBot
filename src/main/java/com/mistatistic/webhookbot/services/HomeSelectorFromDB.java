package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.HomeDB;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class HomeSelectorFromDB {
    private final String address;
    private final HomeRepository homeRepository;
    private List<HomeDB> homes;

    public HomeSelectorFromDB(String address, HomeRepository homeRepository) {
        this.address = address;
        this.homeRepository = homeRepository;
        homes = getHomesFromDB();
    }

    public String buildMessage() {
        selectHomes();
        StringBuilder outputMessage = new StringBuilder();
        if (homes.isEmpty())   {
            outputMessage.append("Нет квартир в г.").append(address);
        } else {
            for (HomeDB home : homes) {
                outputMessage.append(home.getAddress()).append("\n")
                        .append("Комнат: ").append(home.getFlats()).append("\n")
                        .append("Этаж: ").append(home.getFloor()).append("\n")
                        .append("Площадь: ").append(home.getArea()).append("\n")
                        .append("Срок подачи документов: ").append(home.getDeadline()).append("\n\n");
            }
        }
        return outputMessage.toString();
    }

    private void selectHomes() {
        homes.removeIf(o -> !containsIgnoreCase(o.getAddress(),address));
    }

    private List<HomeDB> getHomesFromDB() {
        homes = new ArrayList<>();
        homeRepository.findAll().forEach(homes :: add);
        return homes;
    }
}