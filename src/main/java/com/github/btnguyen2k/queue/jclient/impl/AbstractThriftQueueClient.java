package com.github.btnguyen2k.queue.jclient.impl;

import java.nio.ByteBuffer;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.queue.jclient.IQueueClient;
import com.github.btnguyen2k.queue.jclient.QueueMessage;
import com.github.btnguyen2k.queue.jclient.QueueResponse;
import com.github.btnguyen2k.queue.jclient.QueueSizeResponse;
import com.github.btnguyen2k.queueserver.thrift.TQueueMessage;
import com.github.btnguyen2k.queueserver.thrift.TQueueResponse;
import com.github.btnguyen2k.queueserver.thrift.TQueueService;
import com.github.btnguyen2k.queueserver.thrift.TQueueSizeResponse;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Abstract Thrift-implementation of {@link IQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractThriftQueueClient extends AbstractQueueClient {

    private Logger LOGGER = LoggerFactory.getLogger(AbstractThriftQueueClient.class);

    protected ThriftClientPool<TQueueService.Client, TQueueService.Iface> thriftClientPool;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (thriftClientPool != null) {
            try {
                thriftClientPool.destroy();
            } catch (Exception e) {
            }
        }

        super.destroy();
    }

    private static QueueResponse makeResponse(TQueueResponse serverResponse) {
        if (serverResponse == null) {
            return makeResponse(500, "Empty server response / Server-side exception.", false, null);
        }
        QueueResponse response = new QueueResponse(serverResponse.getStatus(),
                serverResponse.getMessage(), serverResponse.isResult(),
                QueueMessage.newInstance(serverResponse.getQueueMessage()));
        return response;
    }

    private static QueueResponse makeResponse(int status, String message, boolean result,
            QueueMessage queueMessage) {
        QueueResponse response = new QueueResponse(status, message, result, queueMessage);
        return response;
    }

    private static QueueSizeResponse makeSizeResponse(TQueueSizeResponse serverResponse) {
        if (serverResponse == null) {
            return makeSizeResponse(500, "Empty server response / Server-side exception.", -1);
        }
        QueueSizeResponse response = new QueueSizeResponse(serverResponse.getStatus(),
                serverResponse.getMessage(), serverResponse.getSize());
        return response;
    }

    private static QueueSizeResponse makeSizeResponse(int status, String message, long size) {
        QueueSizeResponse response = new QueueSizeResponse(status, message, size);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse queueExists(String secret, String queueName) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    serverResponse = queueClient.queueExists(secret, queueName);
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse initQueue(String secret, String queueName) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    serverResponse = queueClient.initQueue(secret, queueName);
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse queue(String secret, String queueName, byte[] content) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    Date now = new Date();
                    serverResponse = queueClient.queue(
                            secret,
                            queueName,
                            new TQueueMessage(0L, now.getTime(), now.getTime(), 0, ByteBuffer
                                    .wrap(content)));
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse queue(String secret, String queueName, QueueMessage queueMessage) {
        return queue(secret, queueName, queueMessage.content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse requeue(String secret, String queueName, QueueMessage queueMessage) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    Date now = new Date();
                    serverResponse = queueClient.requeue(
                            secret,
                            queueName,
                            new TQueueMessage(queueMessage.queueId,
                                    queueMessage.orgTimestamp != null ? queueMessage.orgTimestamp
                                            .getTime() : now.getTime(),
                                    queueMessage.timestamp != null ? queueMessage.timestamp
                                            .getTime() : now.getTime(), queueMessage.numRequeues,
                                    ByteBuffer.wrap(queueMessage.content)));
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse requeueSilent(String secret, String queueName, QueueMessage queueMessage) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    Date now = new Date();
                    serverResponse = queueClient.requeueSilent(
                            secret,
                            queueName,
                            new TQueueMessage(queueMessage.queueId,
                                    queueMessage.orgTimestamp != null ? queueMessage.orgTimestamp
                                            .getTime() : now.getTime(),
                                    queueMessage.timestamp != null ? queueMessage.timestamp
                                            .getTime() : now.getTime(), queueMessage.numRequeues,
                                    ByteBuffer.wrap(queueMessage.content)));
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse finish(String secret, String queueName, QueueMessage queueMessage) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    Date now = new Date();
                    serverResponse = queueClient.finish(
                            secret,
                            queueName,
                            new TQueueMessage(queueMessage.queueId,
                                    queueMessage.orgTimestamp != null ? queueMessage.orgTimestamp
                                            .getTime() : now.getTime(),
                                    queueMessage.timestamp != null ? queueMessage.timestamp
                                            .getTime() : now.getTime(), queueMessage.numRequeues,
                                    ByteBuffer.wrap(queueMessage.content)));
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse take(String secret, String queueName) {
        try {
            TQueueResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    serverResponse = queueClient.take(secret, queueName);
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeResponse(500, e.getMessage(), false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueSizeResponse queueSize(String secret, String queueName) {
        try {
            TQueueSizeResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    serverResponse = queueClient.queueSize(secret, queueName);
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeSizeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeSizeResponse(500, e.getMessage(), -1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueSizeResponse ephemeralSize(String secret, String queueName) {
        try {
            TQueueSizeResponse serverResponse = null;
            TQueueService.Iface queueClient = thriftClientPool.borrowObject();
            if (queueClient != null) {
                try {
                    serverResponse = queueClient.ephemeralSize(secret, queueName);
                } finally {
                    thriftClientPool.returnObject(queueClient);
                }
            }
            return makeSizeResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return makeSizeResponse(500, e.getMessage(), -1);
        }
    }

}
