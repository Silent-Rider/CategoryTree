package com.example.category_tree.bot;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.category_tree.command.AddElementCommand;
import com.example.category_tree.command.Command;
import com.example.category_tree.command.DownloadCommand;
import com.example.category_tree.command.HelpCommand;
import com.example.category_tree.command.RemoveElementCommand;
import com.example.category_tree.command.UploadCommand;
import com.example.category_tree.command.ViewTreeCommand;
import com.example.category_tree.service.CategoryService;
import com.example.category_tree.service.CommandRegistry;
import com.example.category_tree.service.ExcelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final CommandRegistry commandRegistry;
    private final CategoryService categoryService;
    private final ExcelService excelService;    

    public TelegramBot (@Value("${telegram.bot.token}") String botToken,
    @Value("${telegram.bot.username}")String botUsername, CommandRegistry commandRegistry, 
    CategoryService categoryService, ExcelService excelService) {
        super(new DefaultBotOptions(), botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.commandRegistry = commandRegistry;
        this.categoryService = categoryService;
        this.excelService = excelService;
        registerCommands();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        CompletableFuture.runAsync(() -> processUpdate(update)).exceptionally(e -> {
                log.error("Exception during async processing of update", e);
                return null;
            });
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try{
            execute(message);
        } catch(TelegramApiException e){
            log.error("TelegramApiException occurred while sending a message to user: {}", chatId, e);
        }
    }

    public void sendDocument(long chatId, InputFile file) {
        SendDocument document = new SendDocument(String.valueOf(chatId), file);
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("TelegramApiException occurred while sending a document to user: {}", chatId, e);
        }
    }

    private void processUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String[] parts = message.getText().split(" ", 2);
                String commandName = parts[0].substring(1);
                Command command = commandRegistry.getCommand(commandName);

                if (command != null) {
                    command.execute(message.getChatId(), parts.length > 1 ? parts[1].split(" ") : new String[0]);
                }
            } else if (message.hasDocument()) {
                Document document = message.getDocument();
                UploadCommand uploadCommand = (UploadCommand)commandRegistry.getCommand("upload");
                uploadCommand.handleDocument(message.getChatId(), document);
            }
        }
    }

    private void registerCommands() {
        commandRegistry.register("viewTree", new ViewTreeCommand(categoryService, this));
        commandRegistry.register("addElement", new AddElementCommand(categoryService, this));
        commandRegistry.register("removeElement", new RemoveElementCommand(categoryService, this));
        commandRegistry.register("help", new HelpCommand(commandRegistry, this));
        commandRegistry.register("download", new DownloadCommand(excelService, this));
        commandRegistry.register("upload", new UploadCommand(excelService, this));
    }
}
