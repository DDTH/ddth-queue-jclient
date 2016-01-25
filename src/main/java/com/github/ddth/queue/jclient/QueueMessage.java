package com.github.ddth.queue.jclient;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.github.btnguyen2k.queueserver.thrift.TQueueMessage;

/**
 * Represent a queue message from/to queue-server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QueueMessage {

    public static QueueMessage newInstance(TQueueMessage _msg) {
        if (_msg == null) {
            return null;
        }
        QueueMessage msg = new QueueMessage();
        msg.queueId = _msg.getQueueId();
        msg.orgTimestamp = new Date(_msg.getMsgOrgTimestamp());
        msg.timestamp = new Date(_msg.getMsgOrgTimestamp());
        msg.numRequeues = _msg.getMsgNumRequeues();
        msg.content = _msg.getMsgContent();
        return msg;
    }

    /**
     * Message's queue unique id.
     */
    public long queueId;

    /**
     * Timestamp when the message was first put to the queue.
     */
    public Date orgTimestamp;

    /**
     * Timestamp when the message was last put to the queue.
     */
    public Date timestamp;

    /**
     * How many times has the message been put to the queue.
     */
    public int numRequeues;

    /**
     * Message content.
     */
    public byte[] content;

    public QueueMessage() {
    }

    public QueueMessage(long queueId, Date orgTimestamp, Date timestamp, int numRequeues,
            byte[] content) {
        this.queueId = queueId;
        this.orgTimestamp = orgTimestamp;
        this.timestamp = timestamp;
        this.numRequeues = numRequeues;
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(null);
        tsb.append("queue_id", queueId);
        tsb.append("org_timestamp", orgTimestamp);
        tsb.append("timestamp", timestamp);
        tsb.append("num_requeues", numRequeues);
        tsb.append("content", content);
        return tsb.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(queueId);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QueueMessage) {
            QueueMessage other = (QueueMessage) obj;

            EqualsBuilder eq = new EqualsBuilder();
            eq.append(queueId, other.queueId);
            eq.append(orgTimestamp, other.orgTimestamp);
            eq.append(timestamp, other.timestamp);
            eq.append(numRequeues, other.numRequeues);
            eq.append(content, other.content);
        }
        return false;
    }
}
