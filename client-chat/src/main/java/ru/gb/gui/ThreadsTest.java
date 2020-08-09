package ru.gb.gui;

public class ThreadsTest {
    MessageSender sender;
    private final char[] charSeq = {'A', 'B', 'C'};
    private int printCount;
    private volatile char currentLetter = 'A';

    public ThreadsTest(MessageSender sender){
        this.sender = sender;
    }

    public void start(int count) {
        printCount = count;
        Thread[] threads = new Thread[charSeq.length];

        for(int i = 0; i < threads.length; i++){
            int x = i;
            threads[i] = new Thread(() -> print(x));
        }

        for(Thread thread: threads){
            thread.start();
        }
    }

    private synchronized void print(int index){
        try {
            for (int i = 0; i < printCount; i++) {
                while (currentLetter != charSeq[index]) {
                    this.wait();
                }
                sender.textMessageSend(String.valueOf(currentLetter));
                currentLetter = charSeq[index == charSeq.length - 1 ? 0 : index + 1];
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
