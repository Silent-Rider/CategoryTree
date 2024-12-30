package com.example.category_tree.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.category_tree.command.Command;

@Component
public class CommandRegistry {
    
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public Command getCommand(String commandName){
        return commands.get(commandName);
    }

    public Map<String, Command> getAllCommands(){
        return commands;
    }
}
