package io.workshop.c5s3;

import java.util.Random;

public class OrderActivityImpl implements OrderActivity {
    @Override
    public void execActivity(String input) {
        Random random = new Random();
        int rand = random.nextInt(4 - 1 + 1) + 1;
        try {
            Thread.sleep(rand * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*** Executed activity for: " + input);
    }
}
