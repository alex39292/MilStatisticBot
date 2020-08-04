//package com.mistatistic.webhookbot.services;
//
//import com.mistatistic.webhookbot.models.Home;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MilByAPI {
//
//    private List<String> getJson() {
//        try {
//            String urlAPI = "https://extraction.import.io/query/extractor/378ea545-41bc-49db-bb0f-9bd1dc7bc631?_apikey=3846c433b73b444794f33009628bdc05bd2420c7762751d18a7005408e52d8ac36de116c7b4e55638c93b34c8c59e9b9e58340b82dbf217b3c57b75a2e3db9a002be0991bc15a618853f3fa246347042&url=https%3A%2F%2Fwww.mil.by%2Fru%2Fhousing%2Fcommerc%2F";
//            URL url = new URL(urlAPI);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(30000);
//            connection.connect();
//            int resp = connection.getResponseCode();
//            if (resp == 200) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                StringBuilder sb = new StringBuilder();
//                while ((line = br.readLine()) != null) {
//                    sb.append(line).append("\n");
//                }
//                br.close();
//                List<String> objects = new ArrayList<>();
//                if (sb.toString().contains("ID")) {
//                    String text = sb.toString().substring(sb.indexOf("\"ID\":[{\"text\":"));
//
//                    while (text.contains("ru_RU") && text.contains(",{\"ID\":[{\"text\":")) {
//                        String temp = text.substring(text.indexOf("\"ID\":[{\"text\":"), text.indexOf(",{\"ID\":[{\"text\":"));
//                        text = text.substring(text.indexOf(",{\"ID\":[{\"text\":")).substring(1);
//                        objects.add(temp);
//                    }
//                    objects.add(text.substring(text.indexOf("\"ID\":[{\"text\":"), text.indexOf("]}]},")));
//                }
//                return objects;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public List<Home> getHomes() {
//        List<String> json = getJson();
//        List<Home> homes = new ArrayList<>();
//        if (json != null) {
//            for (String str : json) {
//                Home home = new Home();
//                home.setId(jsonToObject("ID", str));
//                home.setAddress(jsonToObject("ADDRESS", str));
//                home.setFloor(jsonToObject("FLOOR", str));
//                home.setFlats(jsonToObject("FLATS", str));
//                home.setArea(jsonToObject("AREA", str));
//                home.setDeadline(jsonToObject("DEADLINE", str));
//                homes.add(home);
//            }
//        }
//        return homes;
//    }
//
//    private String jsonToObject(String key, String json) {
//        json = json.substring(json.indexOf(key));
//        json = json.substring(12 + key.length());
//        return json.substring(0, json.indexOf("\"")).replace("\\n", "")
//                .replaceAll("[\\s]{2,}", " ");
//    }
//
//    public List<Home> findByAddress(String address) {
//        List<Home> homes = getHomes();
//        if (homes != null) {
//            homes.removeIf(o -> !o.getAddress().contains(address));
//        }
//        return homes;
//    }
//}