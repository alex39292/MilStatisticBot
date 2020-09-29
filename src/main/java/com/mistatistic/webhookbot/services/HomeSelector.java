package com.mistatistic.webhookbot.services;

import com.mistatistic.webhookbot.models.Home;
import lombok.Setter;

import java.util.List;
import static org.apache.commons.lang3.StringUtils.*;

@Setter
public class HomeSelector {
    private String address;
    private List<Home> homes;

    public HomeSelector(String address, List<Home> homes) {
        this.address = address;
        this.homes = homes;
    }

    public String buildMessage() {
        selectHomes();
        StringBuilder outputMessage = new StringBuilder();
        if (homes.isEmpty()) {
            outputMessage.append("Нет квартир в г.").append(address);
        } else {
            for (Home home :
                    homes) {
                outputMessage.append(home.getAddress()).append("\n")
                        .append("Комнат: ").append(home.getFlats()).append("\n")
                        .append("Этаж: ").append(home.getFloor()).append("\n")
                        .append("Площадь: ").append(home.getArea()).append("\n")
                        .append("Срок подачи документов: ").append(home.getDeadline()).append("\n\n");
            }
        }
        return outputMessage.toString();
    }

    public List<Home> selectHomes() {
        homes.removeIf(o -> !containsIgnoreCase(o.getAddress(),address));
        return homes;
    }
}
