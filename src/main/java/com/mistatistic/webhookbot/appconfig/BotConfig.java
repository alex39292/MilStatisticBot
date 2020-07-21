package com.mistatistic.webhookbot.appconfig;

import com.mistatistic.webhookbot.MilStatisticBot;
import com.mistatistic.webhookbot.services.MilByAPI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String userName;
    private String botToken;
    private String webhookPath;

    @Bean
    public MilStatisticBot milStatisticBot() {

        MilStatisticBot bot = new MilStatisticBot();
        bot.setUserName(userName);
        bot.setBotToken(botToken);
        bot.setWebhookPath(webhookPath);
        bot.setMilByAPI(milByAPI());
        bot.setHomes(milByAPI());

        return bot;
    }

    @Bean
    public MilByAPI milByAPI() {
        return new MilByAPI();
    }

}
