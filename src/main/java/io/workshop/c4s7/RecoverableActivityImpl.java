package io.workshop.c4s7;

public class RecoverableActivityImpl implements MyRecoverableActivity {

    @Override
    public String recover(String input, String type, String msg) {
        System.out.println("***** in recover activity");
        return "recovered with input: " + input +
                " after failure: " + type + ":" + msg;
    }

    @Override
    public String exec(String input) {
        throw new NullPointerException("simulated..."); 
    }
}
