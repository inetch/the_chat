package ru.gb.chat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChatFileSaver {
    private Path chatFile;

    public ChatFileSaver(String fileName){
        chatFile = Paths.get(fileName);
    }

    public void appendFile(String str){
        try{
            boolean newFile;
            if(!Files.exists(chatFile)){
                Files.createFile(chatFile);
                newFile = true;
            } else {
                newFile = false;
            }
            Files.write(chatFile, ((newFile?"":"\n") + str).getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e){
            System.out.println("File append failed: " + e.getMessage());
        }
    }
}
