package io.workshop.c1s2;

import io.temporal.testing.TestActivityEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContentLengthActivityTest {

    private TestActivityEnvironment testEnv;

    @Before
    public void setUp() {
        testEnv = TestActivityEnvironment.newInstance();
    }

    @Test
    public void testContentLengthForGoogle() {
        testEnv.registerActivitiesImplementations(new ContentLengthActivityImpl());

        ContentLengthActivity activity = testEnv.newActivityStub(ContentLengthActivity.class);
        ContentLengthInfo c = activity.count("http://www.google.com");

        assertNotNull(c.getWebsiteMap());
        assertEquals(1, c.getWebsiteMap().size());
        assertTrue(c.getWebsiteMap().get("http://www.google.com") > 0);
    }

    @Test
    public void testContentLengthForTemporal() {
        testEnv.registerActivitiesImplementations(new ContentLengthActivityImpl());

        ContentLengthActivity activity = testEnv.newActivityStub(ContentLengthActivity.class);
        ContentLengthInfo c = activity.count("https://temporal.io/");

        assertNotNull(c.getWebsiteMap());
        assertEquals(1, c.getWebsiteMap().size());
        assertTrue(c.getWebsiteMap().get("https://temporal.io/") > 0);
    }
}
