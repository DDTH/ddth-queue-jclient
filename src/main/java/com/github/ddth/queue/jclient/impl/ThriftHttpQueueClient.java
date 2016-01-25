package com.github.ddth.queue.jclient.impl;

import org.apache.http.client.HttpClient;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.github.btnguyen2k.queueserver.thrift.TQueueService;
import com.github.ddth.queue.jclient.IQueueClient;
import com.github.ddth.thriftpool.AbstractTProtocolFactory;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Thrift-over-http implementation of {@link IQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftHttpQueueClient extends AbstractThriftQueueClient {

    private String thriftServerUrls = "http://localhost:9000/thrift";

    /**
     * Constructs a new {@link ThriftHttpQueueClient} object.
     */
    public ThriftHttpQueueClient() {
    }

    /**
     * Constructs a new {@link ThriftHttpQueueClient} object.
     * 
     * @param queueServerUrls
     *            format
     *            {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     */
    public ThriftHttpQueueClient(String queueServerUrls) {
        setQueueServerUrls(queueServerUrls);
    }

    /**
     * Format
     * {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * 
     * @return
     */
    public String getQueueServerUrls() {
        return thriftServerUrls;
    }

    /**
     * format
     * {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * 
     * @param queueServerUrls
     * @return
     */
    public ThriftHttpQueueClient setQueueServerUrls(String queueServerUrls) {
        this.thriftServerUrls = queueServerUrls;
        return this;
    }

    private static class MyTProtocolFactory extends AbstractTProtocolFactory {
        private HttpClient httpClient;

        public MyTProtocolFactory(String queueServerUrls, HttpClient httpClient) {
            super(queueServerUrls);
            this.httpClient = httpClient;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void parseHostAndPortList() {
            String[] urlTokens = getHostsAndPorts().split("[,\\s]+");

            clearHostAndPortList();
            for (String url : urlTokens) {
                HostAndPort hap = new HostAndPort(url);
                addHostAndPort(hap);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected TProtocol create(HostAndPort hostAndPort) throws Exception {
            TTransport transport = new THttpClient(hostAndPort.host, httpClient);
            try {
                transport.open();
            } catch (TTransportException e) {
                transport.close();
                throw e;
            }
            TProtocol protocol = new TCompactProtocol(transport);
            return protocol;
        }
    }

    /**
     * Helper method to create a new {@link ITProtocolFactory} for queue-server
     * Thrift-over-http client.
     * 
     * @param queueServerUrls
     * @param httpClient
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String queueServerUrls,
            HttpClient httpClient) {
        ITProtocolFactory protocolFactory = new MyTProtocolFactory(queueServerUrls, httpClient);
        return protocolFactory;
    }

    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftHttpQueueClient init() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(16);
        connectionManager.setMaxTotal(128);
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(10000).build();
        httpClient = HttpClients.custom().disableAuthCaching().disableCookieManagement()
                .setDefaultSocketConfig(socketConfig).setConnectionManager(connectionManager)
                .build();

        super.init();

        final int timeout = 10000;
        thriftClientPool = new ThriftClientPool<TQueueService.Client, TQueueService.Iface>();
        thriftClientPool.setClientClass(TQueueService.Client.class).setClientInterface(
                TQueueService.Iface.class);
        thriftClientPool.setTProtocolFactory(protocolFactory(thriftServerUrls, httpClient));
        thriftClientPool.setPoolConfig(new PoolConfig().setMaxActive(32).setMaxWaitTime(timeout));
        thriftClientPool.init();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
            }
        }

        if (connectionManager != null) {
            try {
                connectionManager.shutdown();
            } catch (Exception e) {
            }
        }

        super.destroy();
    }
}
