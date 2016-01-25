package com.github.ddth.queue.jclient.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class QueueClientUtils {
    public static byte[] base64Decode(String encodedStr) {
        return encodedStr != null ? Base64.decodeBase64(encodedStr) : null;
    }

    public static String base64Encode(byte[] data) {
        return data != null ? Base64.encodeBase64String(data) : null;
    }
}
