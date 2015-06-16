package com.github.btnguyen2k.queue.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link RestQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestQueueClientFactory {
    private static LoadingCache<String, RestQueueClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, RestQueueClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, RestQueueClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, RestQueueClient>() {
                @Override
                public RestQueueClient load(String queueServerUrl) throws Exception {
                    RestQueueClient queueClient = new RestQueueClient();
                    queueClient.setQueueServerUrl(queueServerUrl).init();
                    return queueClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link RestQueueClient} instance.
     * 
     * @param queueServerUrl
     * @return
     */
    public static RestQueueClient newQueueClient(String queueServerUrl) {
        try {
            return cache.get(queueServerUrl);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
