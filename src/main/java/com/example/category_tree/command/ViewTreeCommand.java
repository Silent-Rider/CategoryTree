package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CategoryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewTreeCommand implements Command {

    private final CategoryService categoryService;
    private final TelegramBot bot;

    @Override
    public void execute(long chatId, String[] args) {
        bot.sendMessage(chatId, categoryService.viewTree());
    }

    @Override
    public String getDescription() {
        return "Просмотреть дерево категорий";
    }
}
