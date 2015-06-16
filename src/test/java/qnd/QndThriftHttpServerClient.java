package qnd;

import com.github.btnguyen2k.queue.jclient.IQueueClient;
import com.github.btnguyen2k.queue.jclient.QueueResponse;
import com.github.btnguyen2k.queue.jclient.impl.ThriftHttpQueueClient;

public class QndThriftHttpServerClient {

    public static void main(String[] args) throws Exception {
        final String SECRET = "s3cr3t";
        final String QUEUE_NAME = "default";

        IQueueClient queueClient = new ThriftHttpQueueClient().setQueueServerUrls(
                "http://localhost:9000/thrift").init();
        QueueResponse response;

        response = queueClient.initQueue(SECRET, QUEUE_NAME);
        System.out.println("Init: " + response);
        System.out.println();

        response = queueClient.queueExists(SECRET, QUEUE_NAME);
        System.out.println("Queue Exists: " + response);
        System.out.println();

        byte[] content = "content".getBytes();
        response = queueClient.queue(SECRET, QUEUE_NAME, content);
        System.out.println("Queue: " + response);
        System.out.println("Queue Size:" + queueClient.queueSize(SECRET, QUEUE_NAME));
        System.out.println("Ephemeral Size:" + queueClient.ephemeralSize(SECRET, QUEUE_NAME));
        System.out.println();

        response = queueClient.take(SECRET, QUEUE_NAME);
        System.out.println("Take: " + response);
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
