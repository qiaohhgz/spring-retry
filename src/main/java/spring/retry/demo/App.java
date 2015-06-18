package spring.retry.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    private RetryTemplate retryTemplate = new RetryTemplate();

    private RetryMethods retryMethods = new RetryMethods();

    public void invokeWithRetrySupport() {
        LOG.info("Running WITH RETRY");

        try {
            retryTemplate.execute(new RetryCallback<Double>() {

                public Double doWithRetry(RetryContext arg0) throws Exception {
                    LOG.info("\tRetry count ->  {} ", arg0.getRetryCount());
                    return retryMethods.doRetrySupported();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void invokeWithoutRetrySupport() throws Exception {
        LOG.info("Running WITHOUT RETRY");

        retryTemplate.execute(new RetryCallback<Double>() {

            public Double doWithRetry(RetryContext arg0) throws Exception {
                LOG.info("\tRetry count ->  {} ", arg0.getRetryCount());
                return retryMethods.doRetryNotSupported();
            }
        });

    }

    public void invokeWithMethodRetrySupport() throws Exception {
        LOG.info("Running without method");

        Double val = retryTemplate.execute(new RetryCallback<Double>() {
            @Override
            public Double doWithRetry(RetryContext arg0) throws Exception {
                LOG.info("\tRetry count ->  {} ", arg0.getRetryCount());
                return doRetrySupported();
            }
        });
        LOG.info("-> {}", val);
    }

    public double doRetrySupported() throws IOException {
        double random = Math.random();
        if (random < 0.5) {
            throw new IOException("Retry is supported here");
        }
        return random;
    }

    public void invokeTimeoutRetrySupport() throws Exception {
        LOG.info("Running invoke timeout");

        Double val = retryTemplate.execute(new RetryCallback<Double>() {
            @Override
            public Double doWithRetry(RetryContext arg0) throws Exception {
                LOG.info("\tRetry count ->  {} ", arg0.getRetryCount());
                Thread.sleep(2000);
                throw new TimeoutException("API timeout");
            }
        });
        LOG.info("-> {}", val);
    }

    {
        LOG.info("init...");
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<Class<? extends Throwable>, Boolean>();
        retryableExceptions.put(IOException.class, true);
        retryableExceptions.put(TimeoutException.class, true);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMaxInterval(20000);
        backOffPolicy.setMultiplier(1.5);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(10, retryableExceptions);
        retryPolicy.setMaxAttempts(5);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }
}
