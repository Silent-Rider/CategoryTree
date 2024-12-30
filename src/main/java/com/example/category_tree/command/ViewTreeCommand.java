package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CategoryService;

public class ViewTreeCommand implements Command {

    private final CategoryService categoryService;
    private final TelegramBot bot;

    public ViewTreeCommand(CategoryService categoryService, TelegramBot bot) {
        this.categoryService = categoryService;
        this.bot = bot;
    }

    @Override
    public void execute(long chatId, String[] args) {
        bot.sendMessage(chatId, categoryService.viewTree());
    }

    @Override
    public String getDescription() {
        return "Просмотреть дерево категорий";
    }
}
