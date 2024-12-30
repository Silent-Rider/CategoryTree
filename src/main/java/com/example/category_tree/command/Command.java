package com.example.category_tree.command;

public interface Command {
    
    void execute(long chatId, String[] args);
    
    String getDescription();
}
