package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CategoryService;

public class AddElementCommand implements Command {

    private final CategoryService categoryService;
    private final TelegramBot bot;

    public AddElementCommand(CategoryService categoryService, TelegramBot bot) {
        this.categoryService = categoryService;
        this.bot = bot;
    }

    @Override
    public void execute(long chatId, String[] args) {
        if (args.length == 1) {
            bot.sendMessage(chatId, categoryService.addCategory(args[0], null));
        } else if (args.length == 2) {
            bot.sendMessage(chatId, categoryService.addCategory(args[1], args[0]));
        } else {
            bot.sendMessage(chatId, "Неверный формат команды. Используйте /addElement <родитель> <дочерний> или /addElement <название>.");
        }
    }

    @Override
    public String getDescription() {
        return "Добавить категорию (корневую или дочернюю)";
    }
}
