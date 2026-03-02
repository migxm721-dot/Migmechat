/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ning.compress.lzf.LZFDecoder
 *  com.ning.compress.lzf.LZFEncoder
 */
package com.projectgoth.fusion.chatsync;

import com.ning.compress.lzf.LZFDecoder;
import com.ning.compress.lzf.LZFEncoder;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.slice.FusionException;

public class ChatSyncCompression {
    public static final char HEADER_SEPARATOR = '#';
    public static final String LZF_ALGO = "LZF";
    public static final String LZF_HEADER = "LZF#";
    public static final String GZIP_ALGO = "GZP";
    public static final String GZIP_HEADER = "GZP#";

    public static byte[] compressFromString(String value, String algo) throws FusionException {
        byte[] data = Redis.safeEncoderEncode(value);
        return ChatSyncCompression.compress(data, algo);
    }

    public static byte[] compress(byte[] data, String algo) throws FusionException {
        try {
            if (StringUtil.isBlank(algo)) {
                return data;
            }
            if (algo.equals(LZF_ALGO)) {
                byte[] compressedBytes = LZFEncoder.encode((byte[])data);
                byte[] headerPlusCompressed = new byte[compressedBytes.length + 4];
                headerPlusCompressed[0] = 76;
                headerPlusCompressed[1] = 90;
                headerPlusCompressed[2] = 70;
                headerPlusCompressed[3] = 35;
                System.arraycopy(compressedBytes, 0, headerPlusCompressed, 4, compressedBytes.length);
                return headerPlusCompressed;
            }
            if (algo.equals(GZIP_ALGO)) {
                throw new FusionException("Unimplemented");
            }
            throw new FusionException("Unrecognized compression algorithm specified for chat sync msgs");
        }
        catch (Exception e) {
            throw new FusionException("Compression of message failed: " + e.getMessage());
        }
    }

    public static String adaptiveDecompressToString(byte[] storedBytes) throws FusionException {
        byte[] decompressed = ChatSyncCompression.adaptiveDecompress(storedBytes);
        String sDecompressed = Redis.safeEncoderEncode(decompressed);
        return sDecompressed;
    }

    public static byte[] adaptiveDecompress(byte[] storedBytes) throws FusionException {
        if (ChatSyncCompression.bytesStartWith(storedBytes, LZF_HEADER)) {
            try {
                return LZFDecoder.decode((byte[])storedBytes, (int)LZF_HEADER.length(), (int)(storedBytes.length - LZF_HEADER.length()));
            }
            catch (Exception e) {
                throw new FusionException("LZF decompression of stored data failed: " + e.getMessage());
            }
        }
        return storedBytes;
    }

    private static boolean bytesStartWith(byte[] test, String startsWith) {
        if (test.length < startsWith.length()) {
            return false;
        }
        for (int i = 0; i < startsWith.length(); ++i) {
            if (test[i] == startsWith.charAt(i)) continue;
            return false;
        }
        return true;
    }
}

