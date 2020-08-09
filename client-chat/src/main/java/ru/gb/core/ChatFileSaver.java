package ru.gb.core;

import java.io.*;
import java.util.LinkedList;

public class ChatFileSaver {
    private String filename;

    public ChatFileSaver(String fileName){
        this.filename = fileName;
    }

    public void appendFile(String str){
        try(FileWriter out = new FileWriter(filename, true)){
            out.write(str);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> loadLastLines(int count){
        LinkedList<String> lines = new LinkedList<>();
        try(RandomAccessFile file = new RandomAccessFile(filename, "r")){
            long length = file.length();
            int lineCount = 0;
            StringBuilder string = new StringBuilder();

            for(long seek = length - 1; seek >= 0 && lineCount <= count; seek--){
                file.seek(seek);
                char c = (char)file.read();
                if(c != '\n'){
                    string.append(c);
                }else{
                    lines.push(string.reverse().toString());
                    lineCount++;
                    string = new StringBuilder(); //is it possible to do it better?
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return lines;
    }
}
