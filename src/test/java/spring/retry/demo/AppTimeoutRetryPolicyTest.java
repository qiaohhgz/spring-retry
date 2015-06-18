package spring.retry.demo;

import org.junit.Test;

public class AppTimeoutRetryPolicyTest {
    @Test
    public void testTimeout() throws Exception {
        for (int i = 0; i < 2; i++) {
            new AppTimeoutRetryPolicy().invokeTimeout();
        }
    }

    @Test
    public void testRecovery() throws Exception {
        for (int i = 0; i < 2; i++) {
            new AppTimeoutRetryPolicy().invokeRecovery();
        }

    }
}