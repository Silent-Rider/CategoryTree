package com.example.category_tree.command;

import com.example.category_tree.bot.TelegramBot;
import com.example.category_tree.service.CommandRegistry;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final CommandRegistry commandRegistry;
    private final TelegramBot bot;

    @Override
    public void execute(long chatId, String[] args) {
        StringBuilder helpMessage = new StringBuilder("Список команд:\n");
        commandRegistry.getAllCommands().forEach((name, command) ->
            helpMessage.append("/").append(name).append(" - ").append(command.getDescription()).append("\n")
        );
        bot.sendMessage(chatId, helpMessage.toString());
    }

    @Override
    public String getDescription() {
        return "Выводит список доступных команд";
    }
}

