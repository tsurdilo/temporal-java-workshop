package io.workshop.c4s7;

import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;

public class RecoveryUtils {
    public interface InvokeActivity<I, O> {
        O invoke(I input);
    }

    public interface RecoverableActivity<I, O> {
        O recover(I input, String type, String msg);
    }

    public static <I, O> O tryOrRecover(RecoverableActivity<I, O> recoverableActivity,
                                       InvokeActivity<I, O> invokeActivity, I result) {
        try {
            return invokeActivity.invoke(result);
        } catch (ActivityFailure e) {
            ApplicationFailure cause = (ApplicationFailure) e.getCause();
            System.out.println("**** tryOrRecover: activity failure: " + cause.getMessage());
            return recoverableActivity.recover(result, cause.getType(), cause.getOriginalMessage());
        }
    }

}
