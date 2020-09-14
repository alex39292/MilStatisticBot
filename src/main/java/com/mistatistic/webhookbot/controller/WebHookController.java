package com.mistatistic.webhookbot.controller;

import com.mistatistic.webhookbot.MilStatisticBot;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final MilStatisticBot bot;

    public WebHookController(MilStatisticBot bot) {
        this.bot = bot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/check")
    public String check() {
        return "Application is alive \n" + bot.getUsers();
    }
}
