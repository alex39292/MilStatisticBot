package com.mistatistic.webhookbot.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mistatistic.webhookbot.models.Home;
import com.mistatistic.webhookbot.models.HomeDB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parser implements Serializable {
    private static final String URL = "https://www.mil.by/ru/housing/commerc/";
    private static final String XPATH = "/html/body/div/div[1]/div/div[1]/div/div/div/div/main/div/div/div[2]/table/tbody/tr[position() > 2]";
    private List<HtmlElement> id;
    private List<HtmlElement> address;
    private List<HtmlElement> floor;
    private List<HtmlElement> flats;
    private List<HtmlElement> area;
    private List<HtmlElement> deadLine;
    private static WebClient client;
    private final HomeRepository homeRepository;

    public Parser(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    private void parseHtml() {
        try {
            setWebClientOptions();
            HtmlPage page = client.getPage(URL);
            id = page.getByXPath(XPATH + "/td[1]/p");
            address = page.getByXPath(XPATH + "/td[2]/p[1]");
            floor = page.getByXPath(XPATH + "/td[3]/p");
            flats = page.getByXPath(XPATH + "/td[4]/p");
            area = page.getByXPath(XPATH + "/td[5]/p");
            deadLine = page.getByXPath(XPATH + "/td[6]/p");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setWebClientOptions() {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    public void addHomesIntoDB() {
        parseHtml();
        if (isParsedFieldsNotEmpty()) {
            for (int i = 0; i < id.size(); i++) {
                HomeDB home = new HomeDB();
                home.setId(Integer.valueOf(id.get(i).asText()));
                home.setAddress(address.get(i).asText());
                home.setFloor(floor.get(i).asText());
                home.setFlats(flats.get(i).asText());
                home.setArea(area.get(i).asText());
                home.setDeadline(deadLine.get(i).asText());
                homeRepository.save(home);
            }
        }
    }

    public List<Home> getHomes() {
        List<Home> homes = new ArrayList<>();
        parseHtml();
        if (isParsedFieldsNotEmpty()) {
            for (int i = 0; i < id.size(); i++) {
                Home home = new Home();
                home.setId(id.get(i).asText());
                home.setAddress(address.get(i).asText());
                home.setFloor(floor.get(i).asText());
                home.setFlats(flats.get(i).asText());
                home.setArea(area.get(i).asText());
                home.setDeadline(deadLine.get(i).asText());
                homes.add(home);
            }
        }
        return homes;
    }

    private boolean isParsedFieldsNotEmpty() {
        return !(id.isEmpty() & address.isEmpty() & floor.isEmpty() & flats.isEmpty() & area.isEmpty() & deadLine.isEmpty());
    }
}
