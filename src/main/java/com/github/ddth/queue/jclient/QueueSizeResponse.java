package com.github.ddth.queue.jclient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Size-response from queue-server API call.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QueueSizeResponse {

	public int status;
	public long size;
	public String message;

	public QueueSizeResponse() {
	}

	public QueueSizeResponse(int status, String message, long size) {
		this.status = status;
		this.message = message;
		this.size = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(null);
		tsb.append("status", status);
		tsb.append("message", message);
		tsb.append("size", size);
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
		hcb.append(size);
		return hcb.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QueueSizeResponse) {
			QueueSizeResponse other = (QueueSizeResponse) obj;

			EqualsBuilder eq = new EqualsBuilder();
			eq.append(status, other.status);
			eq.append(message, other.message);
			eq.append(size, other.size);
		}
		return false;
	}
}
