package com.github.ddth.queue.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link ThriftQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftQueueClientFactory {
    private static LoadingCache<String, ThriftQueueClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, ThriftQueueClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, ThriftQueueClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, ThriftQueueClient>() {
                @Override
                public ThriftQueueClient load(String hostsAndPorts) throws Exception {
                    ThriftQueueClient queueClient = new ThriftQueueClient(hostsAndPorts);
                    queueClient.init();
                    return queueClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link ThriftQueueClient} instance.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2,host3:port3...}
     * @return
     */
    public static ThriftQueueClient newQueueClient(String hostAndPort) {
        try {
            return cache.get(hostAndPort);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
