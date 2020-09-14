package com.mistatistic.webhookbot;

import com.mistatistic.webhookbot.models.Home;
import com.mistatistic.webhookbot.models.TelegramUser;
import com.mistatistic.webhookbot.models.UserState;
import com.mistatistic.webhookbot.services.HomeSelector;
import com.mistatistic.webhookbot.services.Parser;
import com.mistatistic.webhookbot.services.Updater;
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
    private static final List<TelegramUser> USERS = new ArrayList<>();
    private List<Home> homes;

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
        TelegramUser user = getUserByChatId(chatId);
        if (user.getState() == UserState.RUN) {
            try {
                user.setState(UserState.ONSEARCHING);
                execute(new SendMessage(chatId, "Вы подписались на уведомления"));
                updateHomes(user);
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
                createAndAddUser(update);
                execute(new SendMessage(chatId, "Введите город. Например: Минск"));
            } else {
                TelegramUser user = getUserByChatId(chatId);
                if (user.getState() == UserState.START || user.getState() == UserState.RUN) {
                    setHomes();
                    addInfoIntoUser(user, message);
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
        TelegramUser user = new TelegramUser(update.getMessage().getChatId(), update.getMessage().getChat().getUserName());
        user.setState(UserState.START);
        USERS.add(user);
    }

    private void addInfoIntoUser(TelegramUser user, String address) {
        user.setState(UserState.RUN);
        user.setCityName(address);
        user.setHomes(new HomeSelector(address, homes).selectHomes());
    }

    private TelegramUser getUserByChatId(Long chatId) {
        for (TelegramUser user :
                USERS) {
            if (user != null && user.getChatId().equals(chatId)) {
                return user;
            }
        }
        throw new NullPointerException("No target user in user's list");
    }

    private String writeMessageWithHomes(String address) {
        return new HomeSelector(address, homes).buildMessage();
    }

    private SendMessage sendButton(Long chatId) {
        List<InlineKeyboardButton> raw = new ArrayList<>();
        List<List<InlineKeyboardButton>> keys = new ArrayList<>();
        raw.add(new InlineKeyboardButton().setText("Подписаться на обновление")
                .setCallbackData("OK"));
        keys.add(raw);
        return new SendMessage().setChatId(chatId)
                .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keys));
    }

    private void updateHomes(TelegramUser user) {
        new Thread(() -> {
            while (user.getState().equals(UserState.ONSEARCHING)) {
                List<Home> resultHomes = new Updater(homes).getUpdatedHomes();
                if (resultHomes != null) {
                    try {
                        homes = resultHomes;
                        execute(new SendMessage(user.getChatId(), writeMessageWithHomes(user.getCityName())));
                    } catch (TelegramApiException e) {
                        user.setState(UserState.START);
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
        this.homes = Parser.getHomes();
    }

    public List<TelegramUser> getUsers() {
        return USERS;
    }
}
