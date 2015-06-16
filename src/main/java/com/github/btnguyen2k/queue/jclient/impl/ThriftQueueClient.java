package com.github.btnguyen2k.queue.jclient.impl;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.github.btnguyen2k.queue.jclient.IQueueClient;
import com.github.btnguyen2k.queueserver.thrift.TQueueService;
import com.github.ddth.thriftpool.AbstractTProtocolFactory;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Thrift-implementation of {@link IQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftQueueClient extends AbstractThriftQueueClient {

    private String queueServerHostsAndPorts = "localhost:9090";

    /**
     * Constructs a new {@link ThriftQueueClient} object.
     */
    public ThriftQueueClient() {
    }

    /**
     * Constructs a new {@link ThriftQueueClient} object.
     * 
     * @param queueServerHostsAndPorts
     *            format {@code host1:port1,host2:port2,host3:port3...}
     */
    public ThriftQueueClient(String queueServerHostsAndPorts) {
        setQueueServerHostsAndPorts(queueServerHostsAndPorts);
    }

    public String getQueueServerHostsAndPorts() {
        return queueServerHostsAndPorts;
    }

    public ThriftQueueClient setQueueServerHostsAndPorts(String queueServerHostsAndPorts) {
        this.queueServerHostsAndPorts = queueServerHostsAndPorts;
        return this;
    }

    /**
     * Helper method to create a new {@link ITProtocolFactory} for queue-server
     * Thrift client.
     * 
     * @param hostsAndPorts
     * @param soTimeout
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String hostsAndPorts, final int soTimeout) {
        ITProtocolFactory protocolFactory = new AbstractTProtocolFactory(hostsAndPorts) {
            @Override
            protected TProtocol create(HostAndPort hostAndPort) throws Exception {
                TSocket socket = new TSocket(hostAndPort.host, hostAndPort.port);
                socket.setTimeout(soTimeout);
                TTransport transport = new TFramedTransport(socket);
                try {
                    transport.open();
                } catch (TTransportException e) {
                    transport.close();
                    throw e;
                }
                TProtocol protocol = new TCompactProtocol(transport);
                return protocol;
            }
        };
        return protocolFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftQueueClient init() {
        super.init();

        final int timeout = 10000000;
        thriftClientPool = new ThriftClientPool<TQueueService.Client, TQueueService.Iface>();
        thriftClientPool.setClientClass(TQueueService.Client.class).setClientInterface(
                TQueueService.Iface.class);
        thriftClientPool.setTProtocolFactory(protocolFactory(queueServerHostsAndPorts, timeout));
        thriftClientPool.setPoolConfig(new PoolConfig().setMaxActive(32).setMaxWaitTime(timeout));
        thriftClientPool.init();

        return this;
    }
}
