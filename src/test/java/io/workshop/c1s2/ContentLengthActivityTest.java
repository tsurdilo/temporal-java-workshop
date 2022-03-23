package io.workshop.c1s2;

import io.temporal.testing.TestActivityEnvironment;
import org.junit.Test;

public class ContentLengthActivityTest {
    @Test
    public void testWithRealActivities() {
        TestActivityEnvironment testEnvironment = TestActivityEnvironment.newInstance();
        testEnvironment.registerActivitiesImplementations(new ContentLengthActivityImpl());

        ContentLengthActivity activity = testEnvironment.newActivityStub(ContentLengthActivity.class);
        ContentLengthInfo c = activity.count("http://www.google.com");

        System.out.println("C: " + c.getWebsiteMap().size());

        testEnvironment.close();
    }
}
