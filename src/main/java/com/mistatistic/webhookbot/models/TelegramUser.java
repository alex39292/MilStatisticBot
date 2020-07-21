package com.mistatistic.webhookbot.models;

import lombok.Data;

import java.util.List;

public @Data
class TelegramUser {
    private final Long chatId;
    private final String userName;
    private String cityName;
    private UserState state;
    private List<Home> homes;

    public void changeState(UserState state) {
        this.setState(state);
    }
}