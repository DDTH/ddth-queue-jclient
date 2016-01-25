package qnd;

import com.github.ddth.queue.jclient.IQueueClient;
import com.github.ddth.queue.jclient.QueueResponse;
import com.github.ddth.queue.jclient.impl.RestQueueClient;

public class QndRestQueueClient {

    public static void main(String[] args) {
        final String SECRET = "s3cr3t";
        final String QUEUE_NAME = "default";

        IQueueClient queueClient = new RestQueueClient().setQueueServerUrl("http://localhost:9000")
                .init();
        QueueResponse response;

        response = queueClient.initQueue(SECRET, QUEUE_NAME);
        System.out.println("Init: " + response);
        System.out.println();

        response = queueClient.queueExists(SECRET, QUEUE_NAME);
        System.out.println("Queue Exists: " + response);
        System.out.println();

        response = queueClient.take(SECRET, QUEUE_NAME);
        System.out.println("Take 1: " + response);
        System.out.println("Queue Size:" + queueClient.queueSize(SECRET, QUEUE_NAME));
        System.out.println("Ephemeral Size:" + queueClient.ephemeralSize(SECRET, QUEUE_NAME));
        System.out.println();

        byte[] content = "content".getBytes();
        response = queueClient.queue(SECRET, QUEUE_NAME, content);
        System.out.println("Queue: " + response);
        System.out.println("Queue Size:" + queueClient.queueSize(SECRET, QUEUE_NAME));
        System.out.println("Ephemeral Size:" + queueClient.ephemeralSize(SECRET, QUEUE_NAME));
        System.out.println();

        response = queueClient.take(SECRET, QUEUE_NAME);
        System.out.println("Take 2: " + response);
        System.out.println("Queue Size:" + queueClient.queueSize(SECRET, QUEUE_NAME));
        System.out.println("Ephemeral Size:" + queueClient.ephemeralSize(SECRET, QUEUE_NAME));
        System.out.println();

        response = queueClient.finish(SECRET, QUEUE_NAME, response.queueMessage);
        System.out.println("Finish: " + response);
        System.out.println("Queue Size:" + queueClient.queueSize(SECRET, QUEUE_NAME));
        System.out.println("Ephemeral Size:" + queueClient.ephemeralSize(SECRET, QUEUE_NAME));
        System.out.println();
    }

}
