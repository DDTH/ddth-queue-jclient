package com.github.ddth.queue.jclient.impl;

import com.github.ddth.queue.jclient.IQueueClient;

/**
 * Abstract implementation of {@link IQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractQueueClient implements IQueueClient {

    public AbstractQueueClient init() {
        return this;
    }

    public void destroy() {
    }

}
