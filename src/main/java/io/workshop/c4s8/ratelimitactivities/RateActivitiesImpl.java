package io.workshop.c4s8.ratelimitactivities;

public class RateActivitiesImpl implements RateActivities {
    @Override
    public String doA(String input) {
        sleep(4);
        return "act A done..." + input;
    }

    @Override
    public String doB(String input) {
        sleep(3);
        return "act B done..." + input;
    }

    @Override
    public String doC(String input) {
        sleep(2);
        return "act C done..." + input;
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
