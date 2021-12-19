package io.workshop.c1s2;

import io.temporal.activity.Activity;

import java.net.URL;
import java.util.Scanner;

/**
 * Activity with single activity method that gives content length of a provided URL
 */
public class ContentLengthActivityImpl implements ContentLengthActivity {
    @Override
    public ContentLengthInfo count(String url) {

        // Activity execution context is your friend :)
        // Plug in your logging here...
        Activity.getExecutionContext().getTaskToken();
        System.out.println("Activity Type: " +  Activity.getExecutionContext().getInfo().getActivityType());
        System.out.println("Called from workflow: " + Activity.getExecutionContext().getInfo().getWorkflowType());

        // In case of retries you can get the attempt count
        // Activity.getExecutionContext().getInfo().getAttempt();

        try {
            URL website = new URL(url);
            Scanner sc = new Scanner(website.openStream());
            StringBuffer sb = new StringBuffer();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }

            String result = sb.toString();
            result = result.replaceAll("<[^>]*>", "");

            ContentLengthInfo info = new ContentLengthInfo();
            info.add(url, result.length());
            return info;

        } catch(Exception e) {
            // Use this to rethrow a checked exception from an Activity
            System.out.println("Exception: " + e.getMessage());
            throw Activity.wrap(e);
        }
    }
}
