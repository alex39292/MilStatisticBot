package com.mistatistic.webhookbot;

import com.mistatistic.webhookbot.models.Home;
import com.mistatistic.webhookbot.models.TelegramUser;
import com.mistatistic.webhookbot.models.UserState;
import com.mistatistic.webhookbot.services.MilByAPI;
import com.mistatistic.webhookbot.services.Updater;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class MilStatisticBot extends TelegramWebhookBot {
    private String userName;
    private String botToken;
    private String webhookPath;
    private static final List<TelegramUser> USERS = new ArrayList<>();
    private MilByAPI milByAPI;
    private List<Home> homes;
    private String address;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            TelegramUser user = getUserByChatId(chatId);
            if (user!=null && user.getState() == UserState.RUN) {
                try {
                    user.changeState(UserState.ONSEARCHING);
                    execute(new SendMessage(chatId, "Вы подписались на уведомления"));
                    updateHomes(chatId, address, user);

                }
                catch (TelegramApiRequestException e) {
                    onWebhookUpdateReceived(update);
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            address = update.getMessage().getText();
            try {
                if (!address.equals("/start")) {
                    TelegramUser user = getUserByChatId(chatId);
                    if (user!=null && user.getState() == UserState.START) {
                        execute(sendButton(chatId).setText(findHomes(address)));
                        user.changeState(UserState.RUN);
                        user.setHomes(milByAPI.findByAddress(address));
                    }
                } else {
                    TelegramUser user = new TelegramUser(chatId, update.getMessage().getChat().getUserName());
                    user.setState(UserState.START);
                    USERS.add(user);
                    execute(new SendMessage(chatId, "Введите город. Например: Минск"));
                }
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    private TelegramUser getUserByChatId(Long chatId) {
        for (TelegramUser user :
                USERS) {
            if (user.getChatId().equals(chatId)) {
                return user;
            }
        }
        return null;
    }

    private String findHomes(String address) {
        StringBuilder text = new StringBuilder();
        List<Home> selectedHomes = new ArrayList<>(homes);
        selectedHomes.removeIf(o -> !o.getAddress().contains(address));

        if (selectedHomes.isEmpty()) {
            text.append("Нет квартир в г.").append(address);
        } else {
            for (Home home :
                    selectedHomes) {
                text.append(home.getAddress()).append("\n")
                        .append("Комнат: ").append(home.getFlats()).append("\n")
                        .append("Этаж: ").append(home.getFloor()).append("\n")
                        .append("Площадь: ").append(home.getArea()).append("\n")
                        .append("Срок подачи документов: ").append(home.getDeadline()).append("\n\n");
            }
        }
        return text.toString();
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

    private void updateHomes(Long chatId, String address, TelegramUser user) {
        if (user!= null) {
            Thread thread = new Thread(() -> {
                Updater updater = new Updater(homes, address);
                while (true) {
                    if (updater.hasHomes()) {
                        try {
                            execute(new SendMessage(chatId, findHomes(address)));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            execute(new SendMessage(chatId, "Нет обновлений "));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
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

    public void setMilByAPI(MilByAPI milByAPI) {
        this.milByAPI = milByAPI;
    }

    public void setHomes(MilByAPI milByAPI) {
        this.homes = milByAPI.getHomes();
    }
}
