package com.github.btnguyen2k.queue.jclient.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.queue.jclient.IQueueClient;
import com.github.btnguyen2k.queue.jclient.QueueMessage;
import com.github.btnguyen2k.queue.jclient.QueueResponse;
import com.github.btnguyen2k.queue.jclient.QueueSizeResponse;
import com.github.btnguyen2k.queue.jclient.utils.QueueClientUtils;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * REST-implementation of {@link IQueueClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestQueueClient extends AbstractQueueClient {

    private Logger LOGGER = LoggerFactory.getLogger(RestQueueClient.class);

    private String queueServerUrl;

    public RestQueueClient() {
    }

    public RestQueueClient(String queueServerUrl) {
        setQueueServerUrl(queueServerUrl);
    }

    public String getQueueServerUrl() {
        return queueServerUrl;
    }

    public RestQueueClient setQueueServerUrl(String queueServerUrl) {
        this.queueServerUrl = queueServerUrl;
        if (this.queueServerUrl.endsWith("/")) {
            this.queueServerUrl = this.queueServerUrl
                    .substring(0, this.queueServerUrl.length() - 1);
        }
        return this;
    }

    private Map<String, Object> callApi(String url, Object data) {
        return callApi(url, data, "POST");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callApi(String url, Object data, String method) {
        try {
            HttpRequest httpRequest = StringUtils.equalsIgnoreCase("POST", method) ? HttpRequest
                    .post(url) : HttpRequest.get(url);
            if (data != null) {
                httpRequest.body(SerializationUtils.toJsonString(data));
            }
            HttpResponse httpResponse = httpRequest.timeout(10000).send();
            try {
                if (httpResponse.statusCode() != 200) {
                    return null;
                }
                int contentLength = Integer.parseInt(httpResponse.contentLength());
                if (contentLength == 0 || contentLength > 1024) {
                    LOGGER.warn("Invalid response length: " + contentLength);
                    return null;
                }
                return SerializationUtils.fromJsonString(httpResponse.charset("UTF-8").bodyText(),
                        Map.class);
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    public static QueueMessage queueMessageFromResponse(Object value) {
        if (value == null) {
            return null;
        }
        Long qId = DPathUtils.getValue(value, "queue_id", Long.class);
        Date qOrgTimestamp = DPathUtils.getValue(value, "org_timestamp", Date.class);
        Date qTimestamp = DPathUtils.getValue(value, "timestamp", Date.class);
        Integer qNumRequeues = DPathUtils.getValue(value, "num_requeues", Integer.class);
        String contentBase64 = DPathUtils.getValue(value, "content", String.class);

        QueueMessage queueMessage = new QueueMessage();
        queueMessage.queueId = qId != null ? qId.longValue() : 0;
        queueMessage.orgTimestamp = qOrgTimestamp;
        queueMessage.timestamp = qTimestamp;
        queueMessage.numRequeues = qNumRequeues != null ? qNumRequeues.intValue() : 0;
        queueMessage.content = QueueClientUtils.base64Decode(contentBase64);

        return queueMessage;
    }

    private static QueueSizeResponse makeSizeResponse(Map<String, Object> serverResponse) {
        if (serverResponse == null) {
            return makeSizeResponse(500, "Empty server response / Server-side exception.", -1);
        }
        Integer status = DPathUtils.getValue(serverResponse, "s", Integer.class);
        String message = DPathUtils.getValue(serverResponse, "m", String.class);
        Long size = DPathUtils.getValue(serverResponse, "v", Long.class);

        QueueSizeResponse response = new QueueSizeResponse(status != null ? status.intValue() : 0,
                message, size != null ? size.longValue() : -1);
        return response;
    }

    private static QueueSizeResponse makeSizeResponse(int status, String message, long size) {
        QueueSizeResponse response = new QueueSizeResponse(status, message, size);
        return response;
    }

    private static QueueResponse makeResponse(Map<String, Object> serverResponse) {
        if (serverResponse == null) {
            return makeResponse(500, "Empty server response / Server-side exception.", false, null);
        }
        Integer status = DPathUtils.getValue(serverResponse, "s", Integer.class);
        String message = DPathUtils.getValue(serverResponse, "m", String.class);
        Boolean result = DPathUtils.getValue(serverResponse, "r", Boolean.class);
        Object value = DPathUtils.getValue(serverResponse, "v");

        QueueResponse response = new QueueResponse(status != null ? status.intValue() : 0, message,
                result != null ? result.booleanValue() : false, queueMessageFromResponse(value));
        return response;
    }

    private static QueueResponse makeResponse(int status, String message, boolean result,
            QueueMessage queueMessage) {
        QueueResponse response = new QueueResponse(status, message, result, queueMessage);
        return response;
    }

    private static Map<String, Object> makeRequestParams(String secret, String queueName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("secret", secret);
        params.put("queue_name", queueName);
        return params;
    }

    private static Map<String, Object> makeRequestParams(String secret, String queueName,
            byte[] content) {
        Map<String, Object> params = makeRequestParams(secret, queueName);
        params.put("content", QueueClientUtils.base64Encode(content));
        return params;
    }

    private static Map<String, Object> makeRequestParams(String secret, String queueName,
            QueueMessage queueMessage) {
        Map<String, Object> params = makeRequestParams(secret, queueName);
        params.put("queue_id", queueMessage.queueId);
        params.put("org_timestamp", queueMessage.orgTimestamp);
        params.put("timestamp", queueMessage.timestamp);
        params.put("num_requeues", queueMessage.numRequeues);
        params.put("content", QueueClientUtils.base64Encode(queueMessage.content));
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse queueExists(String secret, String queueName) {
        String apiUri = "/queueExists";
        Map<String, Object> params = makeRequestParams(secret, queueName);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse initQueue(String secret, String queueName) {
        String apiUri = "/initQueue";
        Map<String, Object> params = makeRequestParams(secret, queueName);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse queue(String secret, String queueName, byte[] content) {
        String apiUri = "/queue";
        Map<String, Object> params = makeRequestParams(secret, queueName, content);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
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
        String apiUri = "/requeue";
        Map<String, Object> params = makeRequestParams(secret, queueName, queueMessage);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse requeueSilent(String secret, String queueName, QueueMessage queueMessage) {
        String apiUri = "/requeueSilent";
        Map<String, Object> params = makeRequestParams(secret, queueName, queueMessage);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse finish(String secret, String queueName, QueueMessage queueMessage) {
        String apiUri = "/finish";
        Map<String, Object> params = makeRequestParams(secret, queueName, queueMessage);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueResponse take(String secret, String queueName) {
        String apiUri = "/take";
        Map<String, Object> params = makeRequestParams(secret, queueName);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueSizeResponse queueSize(String secret, String queueName) {
        String apiUri = "/queueSize";
        Map<String, Object> params = makeRequestParams(secret, queueName);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeSizeResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueSizeResponse ephemeralSize(String secret, String queueName) {
        String apiUri = "/ephemeralSize";
        Map<String, Object> params = makeRequestParams(secret, queueName);
        Map<String, Object> apiResult = callApi(queueServerUrl + apiUri, params);
        return makeSizeResponse(apiResult);
    }

}
