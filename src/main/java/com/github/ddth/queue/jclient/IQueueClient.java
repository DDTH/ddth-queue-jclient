package com.github.ddth.queue.jclient;

/**
 * Client API to interact with queue-server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IQueueClient {

    /**
     * Checks if a queue exists.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public QueueResponse queueExists(String secret, String queueName);

    /**
     * Creates & Initializes a new queue.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public QueueResponse initQueue(String secret, String queueName);

    /**
     * Puts a message to a queue.
     * 
     * @param secret
     * @param queueName
     * @param content
     * @return
     */
    public QueueResponse queue(String secret, String queueName, byte[] content);

    /**
     * Puts a message to a queue.
     * 
     * @param secret
     * @param queueName
     * @param queueMessage
     * @return
     */
    public QueueResponse queue(String secret, String queueName, QueueMessage queueMessage);

    /**
     * Re-queues a message.
     * 
     * @param secret
     * @param queueName
     * @param queueMessage
     * @return
     */
    public QueueResponse requeue(String secret, String queueName, QueueMessage queueMessage);

    /**
     * Re-queues a message silently.
     * 
     * @param secret
     * @param queueName
     * @param queueMessage
     * @return
     */
    public QueueResponse requeueSilent(String secret, String queueName, QueueMessage queueMessage);

    /**
     * Called when finish processing the message to cleanup ephemeral storage.
     * 
     * @param secret
     * @param queueName
     * @param queueMessage
     * @return
     */
    public QueueResponse finish(String secret, String queueName, QueueMessage queueMessage);

    /**
     * Takes a message from a queue.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public QueueResponse take(String secret, String queueName);

    /**
     * Gets number of items currently in a queue.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public QueueSizeResponse queueSize(String secret, String queueName);

    /**
     * Gets number of items currently in a queue's ephemeral storage.
     * 
     * @param secret
     * @param queueName
     * @return
     */
    public QueueSizeResponse ephemeralSize(String secret, String queueName);
}
