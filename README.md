ddth-queue-jclient
==================

Java client for [https://github.com/btnguyen2k/queue-server](https://github.com/btnguyen2k/queue-server).

Project home: [https://github.com/DDTH/ddth-queue-jclient](https://github.com/DDTH/ddth-queue-jclient).


## License ##

See [LICENSE.txt](LICENSE.txt) for details. Copyright (c) 2015-2016 Thanh Ba Nguyen.

Third party libraries are distributed under their own license(s).


## Installation ##

Latest release: `0.1.1`, see [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency:

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-queue-jclient</artifactId>
    <version>0.1.1</version>
</dependency>
```


## Usage ##

```java
// obtain an IQueueClient instance: REST client
IQueueClient queueClient = new RestQueueClient().setQueueServerUrl("http://localhost:8080").init();
//or preferred way
IQueueClient queueClient = RestQueueClientFactory.newQueueClient("http://localhost:8080");

// obtain an IQueueClient instance: Thrift client
IQueueClient queueClient = new ThriftqueueClient().setQueueServerHostsAndPorts("localhost:9090,host2:9090,host3:9090").init();
//or preferred way
IQueueClient queueClient = ThriftqueueClientFactory.newQueueClient("localhost:9090,host2:9090,host3:9090");
// Thrift client supports host fail-over!

// obtain an IQueueClient instance: Thrift-over-http client
IQueueClient queueClient = new ThriftHttpQueueClient().setQueueServerUrls("http://localhost:8080/thrift,http://host2/thrift,http://host3/thrift").init();
//or preferred way
IQueueClient queueClient = ThriftHttpQueueClientFactory.newQueueClient("http://localhost:8080/thrift,http://host2/thrift,http://host3/thrift");
// Thrift client supports host fail-over!
```

```java
// do some cool stuff

// initialize a queue
QueueResponse response = queueClient.initQueue("secret", "queue_name");

// queue a message
QueueResponse response = queueClient.queue("secret", "queue_name", "message".getBytes());
// or, queue a message
QueueMessage msg = new QueueMessage();
msg.content("message content".getBytes());
QueueResponse response = queueClient.queue("secret", "queue_name", msg);

// take a message out of queue
QueueResponse response = queueClient.take("secret", "queue_name");
QueueMessage msg = response.queueMessage;

// call method "finish" when done with the message to cleanup ephemeral storage
QueueResponse response = queueClient.finish("secret", "queue_name", msg);

// or, requeue the message to retry latter
QueueResponse response = queueClient.requeue("secret", "queue_name", msg);
```
