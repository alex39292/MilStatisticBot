package com.mistatistic.webhookbot.controller;

import com.mistatistic.webhookbot.MilStatisticBot;
import com.mistatistic.webhookbot.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final MilStatisticBot bot;
    @Autowired
    private UserRepository userRepository;

    public WebHookController(MilStatisticBot bot) {
        this.bot = bot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/check")
    public String check() {
        return "Application is alive \n" + userRepository.findAll();
    }
}
