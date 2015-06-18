package spring.retry.demo;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeoutException;


public class AppTest {

    @After
    public void setDown() throws Exception {
        System.out.println();
    }

    @Test
    public void testRetrySupported() throws Exception {
        for (int i = 0; i < 2; i++) {
            new App().invokeWithRetrySupport();
        }
    }

    @Test(expected = NumberFormatException.class)
    public void testRetryNotSupported() throws Exception {

        for (int i = 0; i < 2; i++) {
            new App().invokeWithoutRetrySupport();
        }
    }

    @Test
    public void testInvokeWithMethodRetrySupport() throws Exception {
        for (int i = 0; i < 2; i++) {
            new App().invokeWithMethodRetrySupport();
        }
    }

    @Test(expected = TimeoutException.class)
    public void testInvokeTimeoutRetrySupport() throws Exception {
        for (int i = 0; i < 2; i++) {
            new App().invokeTimeoutRetrySupport();
        }
    }
}
