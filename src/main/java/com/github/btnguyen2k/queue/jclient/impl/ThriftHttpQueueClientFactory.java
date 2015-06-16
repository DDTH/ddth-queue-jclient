package com.github.btnguyen2k.queue.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link ThriftHttpQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftHttpQueueClientFactory {
    private static LoadingCache<String, ThriftHttpQueueClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, ThriftHttpQueueClient>() {
                @Override
                public void onRemoval(
                        RemovalNotification<String, ThriftHttpQueueClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, ThriftHttpQueueClient>() {
                @Override
                public ThriftHttpQueueClient load(String urls) throws Exception {
                    ThriftHttpQueueClient queueClient = new ThriftHttpQueueClient(urls);
                    queueClient.init();
                    return queueClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link ThriftHttpQueueClient} instance.
     * 
     * @param urls
     *            format
     *            {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * @return
     */
    public static ThriftHttpQueueClient newQueueClient(String urls) {
        try {
            return cache.get(urls);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
