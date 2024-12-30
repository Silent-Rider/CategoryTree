package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CategoryService;

public class RemoveElementCommand implements Command {

    private final CategoryService categoryService;
    private final TelegramBot bot;

    public RemoveElementCommand(CategoryService categoryService, TelegramBot bot) {
        this.categoryService = categoryService;
        this.bot = bot;
    }

    @Override
    public void execute(long chatId, String[] args) {
        if (args.length != 1) {
            bot.sendMessage(chatId, "Неверный формат команды. Используйте /removeElement <название>.");
            return;
        }
        bot.sendMessage(chatId, categoryService.removeCategory(args[0]));
    }

    @Override
    public String getDescription() {
        return "Удалить категорию и её дочерние элементы";
    }
}
