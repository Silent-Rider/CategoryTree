package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CommandRegistry;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommand implements Command {

    private final CommandRegistry commandRegistry;
    private final TelegramBot bot;

    @Override
    public void execute(long chatId, String[] args) {
        StringBuilder startMessage = new StringBuilder("\"Добро пожаловать в бот дерева категорий! " + 
        "Вот доступные команды:\n");
        commandRegistry.getAllCommands().forEach((name, command) ->
            startMessage.append("/").append(name).append(" - ").append(command.getDescription()).append("\n")
        );
        bot.sendMessage(chatId, startMessage.toString());
    }

    @Override
    public String getDescription() {
        return "Перезапускает бота";
    }
}