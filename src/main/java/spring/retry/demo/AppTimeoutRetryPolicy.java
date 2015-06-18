package spring.retry.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.TimeoutException;

/**
 * JimQiao
 * 2015-06-17 16:26
 */
public class AppTimeoutRetryPolicy {
    private static final Logger log = LoggerFactory.getLogger(AppTimeoutRetryPolicy.class);
    private RetryTemplate retryTemplate;

    public AppTimeoutRetryPolicy() {
        retryTemplate = new RetryTemplate();

        TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
        policy.setTimeout(3000);

        retryTemplate.setRetryPolicy(policy);
    }

    public void invokeTimeout() throws Exception {
        String result = retryTemplate.execute(new RetryCallback<String>() {
            @Override
            public String doWithRetry(RetryContext retryContext) throws Exception {
                log.info("retry is {}", retryContext.getRetryCount());
                Thread.sleep(1000);
                throw new TimeoutException();
            }
        });
        log.info("Result is {}", result);
    }

    public void invokeRecovery() throws Exception {
        final String result = retryTemplate.execute(new RetryCallback<String>() {
            @Override
            public String doWithRetry(RetryContext retryContext) throws Exception {
                log.info("retry is {}", retryContext.getRetryCount());
                Thread.sleep(1000);
                throw new TimeoutException("Timeout " + retryContext.getRetryCount());
            }
        }, new RecoveryCallback<String>() {
            @Override
            public String recover(RetryContext retryContext) throws Exception {
                log.info("recover is " + retryContext.getLastThrowable().getMessage());
                return "save error to db";
            }
        });
        log.info("Result is {}", result);

    }
}
