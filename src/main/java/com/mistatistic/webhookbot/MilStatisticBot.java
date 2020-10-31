package com.mistatistic.webhookbot;

import com.mistatistic.webhookbot.models.Home;
import com.mistatistic.webhookbot.models.User;
import com.mistatistic.webhookbot.models.UserState;
import com.mistatistic.webhookbot.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MilStatisticBot extends TelegramWebhookBot {
    private String userName;
    private String botToken;
    private String webhookPath;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HomeRepository homeRepository;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            submitOnUpdates(update);
        }
        if (update.hasMessage()) {
            processMessage(update);
        }
        return null;
    }

    private void submitOnUpdates(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        User user = getUserByChatId(chatId);
        if (user.getState().equals("RUN")) {
            try {
                //user.setState(UserState.ONSEARCHING.toString());
                //userRepository.save(user);
                //execute(new SendMessage(chatId, "Вы подписались на уведомления"));
                setHomes();
                execute(sendButton(chatId).setText(writeMessageWithHomes(user.getCity())));
                //updateHomes(userBD);
                System.out.println("Updated for user ID: " + user.getId());
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void processMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        try {
            if (isPressedStart(message)) {
                System.out.println("START was pressed");
                createAndAddUser(update);
                execute(new SendMessage(chatId, "Введите город. Например: Минск"));
            } else {
                System.out.println("Message is " + message);
                User userBD = getUserByChatId(chatId);
                if (userBD.getState().equals("START") || userBD.getState().equals("RUN")) {
                    setHomes();
                    addInfoIntoUser(userBD, message);
                    execute(sendButton(chatId).setText(writeMessageWithHomes(message)));
                }
            }
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isPressedStart(String message) {
        return message.equals("/start");
    }

    private void createAndAddUser(Update update) {
        User userBD = new User();
        userBD.setId(update.getMessage().getChatId());
        userBD.setUserName(update.getMessage().getChat().getUserName());
        userBD.setState(UserState.START.toString());
        userRepository.save(userBD);
    }

    private void addInfoIntoUser(User userBD, String address) {
        userBD.setState(UserState.RUN.toString());
        userBD.setCity(address);
        userRepository.save(userBD);
        System.out.println("user was updated. City: " + userBD.getCity());
    }

    private User getUserByChatId(Long chatId) {
        if (userRepository.findById(chatId).isPresent()) {
            return userRepository.findById(chatId).get();
        }
        throw new NullPointerException("No target user in user's list");
    }

    private String writeMessageWithHomes(String address) {
        return new HomeSelectorFrom(address, homeRepository).buildMessage();
    }

    private SendMessage sendButton(Long chatId) {
        List<InlineKeyboardButton> raw = new ArrayList<>();
        List<List<InlineKeyboardButton>> keys = new ArrayList<>();
        raw.add(new InlineKeyboardButton().setText("Проверить на обновление")
                .setCallbackData("OK"));
        keys.add(raw);
        return new SendMessage().setChatId(chatId)
                .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keys));
    }

    private void updateHomes(User userBD) {
        new Thread(() -> {
            while (userBD.getState().equals("ONSEARCHING")) {
                List<Home> resultHomes = new Updater(homeRepository).getUpdatedHomes();
                if (resultHomes != null) {
                    try {
                        homeRepository.deleteAll();
                        resultHomes.forEach(home -> homeRepository.save(home));
                        execute(new SendMessage(userBD.getId(), writeMessageWithHomes(userBD.getCity())));
                    } catch (TelegramApiException e) {
                        userBD.setState("START");
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
                sleepInLoop();
            }
        })
                .start();
    }

    private void sleepInLoop() {
        try {
            Date date = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm");
            TimeUnit.MINUTES.sleep(61 - Integer.parseInt(formatForDateNow.format(date)));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
    }

    public void setHomes() {
        Parser parser = new Parser(homeRepository);
        parser.addHomesIntoDB();
        System.out.println("homes were updated: " + homeRepository.count());
    }
}
