package io.workshop.c4s2;

public class C4S2ActivitiesImpl implements C4S2Activities {
    private int count = 1;
    @Override
    public String first() {
        return null;
    }

    @Override
    public boolean second() {
        System.out.println("***** COUNT: " + count);
        System.out.println("***** RETURN : " + ((count % 4) == 0));
        if( (count % 4) == 0) {
            return true;
        } else {
            count++;
            return false;
        }
    }
}
