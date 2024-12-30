package com.example.category_tree.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.telegram.telegrambots.meta.api.objects.InputFile;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.ExcelService;

public class DownloadCommand implements Command {

    private final ExcelService excelService;
    private final TelegramBot bot;

    public DownloadCommand(ExcelService excelService, TelegramBot bot) {
        this.excelService = excelService;
        this.bot = bot;
    }

    @Override
    public void execute(long chatId, String[] args) {
        try {
            ByteArrayInputStream excelFile = excelService.generateExcel();
            // Создаем файл для отправки
            InputFile file = new InputFile();
            file.setMedia(excelFile, "categories.xlsx");
            bot.sendDocument(chatId, file);
        } catch (IOException e) {
            bot.sendMessage(chatId, "Произошла ошибка при генерации файла: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Скачать Excel-файл с деревом категорий";
    }
}
