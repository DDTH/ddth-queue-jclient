package qnd;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.btnguyen2k.queue.jclient.QueueResponse;
import com.github.btnguyen2k.queue.jclient.impl.RestQueueClientFactory;
import com.github.btnguyen2k.queue.jclient.impl.ThriftHttpQueueClient;
import com.github.btnguyen2k.queue.jclient.impl.ThriftHttpQueueClientFactory;

public class QndBenchmarkThriftHttpClient {
    private static AtomicLong NUM_SENT = new AtomicLong(0);
    private static AtomicLong NUM_TAKEN = new AtomicLong(0);
    private static AtomicLong NUM_EXCEPTION = new AtomicLong(0);
    private static ConcurrentMap<Object, Object> SENT = new ConcurrentHashMap<Object, Object>();
    private static ConcurrentMap<Object, Object> RECEIVE = new ConcurrentHashMap<Object, Object>();
    private static AtomicLong TIMESTAMP = new AtomicLong(0);
    private static long NUM_ITEMS = 8192;
    private static int NUM_THREADS = 8;

    public static void main(String[] args) throws Exception {
        final String SECRET = "s3cr3t";
        final String QUEUE_NAME = "default";

        final ThriftHttpQueueClient queueClient = ThriftHttpQueueClientFactory
                .newQueueClient("http://localhost:9000/thrift");
        QueueResponse response;

        response = queueClient.initQueue(SECRET, QUEUE_NAME);
        System.out.println("Init: " + response);
        System.out.println();

        response = queueClient.queueExists(SECRET, QUEUE_NAME);
        System.out.println("Queue Exists: " + response);
        System.out.println();

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread t = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            QueueResponse response = queueClient.take(SECRET, QUEUE_NAME);
                            if (response != null && response.status == 200
                                    && response.queueMessage != null) {
                                queueClient.finish(SECRET, QUEUE_NAME, response.queueMessage);
                                long numItems = NUM_TAKEN.incrementAndGet();
                                if (numItems == NUM_ITEMS) {
                                    TIMESTAMP.set(System.currentTimeMillis());
                                }
                                RECEIVE.put(new String(response.queueMessage.content), Boolean.TRUE);
                            } else {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                }
                            }
                        } catch (Exception e) {
                            NUM_EXCEPTION.incrementAndGet();
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.setDaemon(true);
            t.start();
        }

        Thread.sleep(2000);

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < NUM_ITEMS; i++) {
            String content = "Content: [" + i + "] " + new Date();
            queueClient.queue(SECRET, QUEUE_NAME, content.getBytes());
            NUM_SENT.incrementAndGet();
            SENT.put(content, Boolean.TRUE);
        }
        long t2 = System.currentTimeMillis();

        while (NUM_TAKEN.get() < NUM_ITEMS) {
            Thread.sleep(1);
        }
        System.out.println("Duration Queue: " + (t2 - t1));
        System.out.println("Duration Take : " + (TIMESTAMP.get() - t1));
        System.out.println("Num sent      : " + NUM_SENT.get());
        System.out.println("Num taken     : " + NUM_TAKEN.get());
        System.out.println("Num exception : " + NUM_EXCEPTION.get());
        System.out.println("Sent size     : " + SENT.size());
        System.out.println("Receive size  : " + RECEIVE.size());
        System.out.println("Check         : " + SENT.equals(RECEIVE));

        Thread.sleep(4000);
        System.exit(-1);

        RestQueueClientFactory.cleanup();
    }
}
