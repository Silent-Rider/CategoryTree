package com.example.category_tree.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.ExcelService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadCommand implements Command {

    private final ExcelService excelService;
    private final TelegramBot bot;

    @Override
    public void execute(long chatId, String[] args) {
        bot.sendMessage(chatId, "Отправьте Excel-файл с помощью команды /upload в чат");
    }

    public void handleDocument(long chatId, Document document) {
        try {
            // Получаем fileId из документа
            String fileId = document.getFileId();

            // Получаем объект File с помощью GetFile
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);

            // Скачиваем файл
            String filePath = bot.execute(getFileRequest).getFilePath();
            File localFile = bot.downloadFile(filePath);
            try (InputStream inputStream = new FileInputStream(localFile)) {
                excelService.importExcel(inputStream);
                bot.sendMessage(chatId, "Файл успешно обработан и категории добавлены.");
            }
        } catch (TelegramApiException | IOException e) {
            bot.sendMessage(chatId, "Произошла ошибка при обработке файла: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Загрузить Excel-файл с деревом категорий";
    }
}
