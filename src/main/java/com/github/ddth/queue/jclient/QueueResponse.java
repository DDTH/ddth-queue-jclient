package com.github.ddth.queue.jclient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Response from queue-server API call.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QueueResponse {

    public int status;
    public String message;
    public boolean result;
    public QueueMessage queueMessage;

    public QueueResponse() {
    }

    public QueueResponse(int status, String message, boolean result, QueueMessage queueMessage) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.queueMessage = queueMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(null);
        tsb.append("status", status);
        tsb.append("message", message);
        tsb.append("result", result);
        tsb.append("queueMessage", queueMessage);
        return tsb.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(status);
        hcb.append(message);
        hcb.append(result);
        hcb.append(queueMessage);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QueueResponse) {
            QueueResponse other = (QueueResponse) obj;

            EqualsBuilder eq = new EqualsBuilder();
            eq.append(status, other.status);
            eq.append(message, other.message);
            eq.append(queueMessage, other.queueMessage);
        }
        return false;
    }
}
