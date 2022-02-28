package io.workshop.c3s1;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
//@ActivityInterface(namePrefix = "A_")
public interface TypeOneActivities {
//    @ActivityMethod(name = "abc")
    void doSomething(String input);

//    @ActivityMethod(name = "xyz")
    void doSomethingElse(String input);
}
