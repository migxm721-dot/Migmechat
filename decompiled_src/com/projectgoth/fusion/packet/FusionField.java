/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceUtilInternal.Base64
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.packet;

import IceUtilInternal.Base64;
import com.projectgoth.fusion.packet.ByteValueEnum;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class FusionField
implements Serializable {
    private static final Logger log = Logger.getLogger(FusionField.class);
    private static final String CHAR_ENCODING = "UTF-8";
    public static final int FIELD_HEADER_LENGTH = 6;
    private static final byte BYTEARRAY = 1;
    private static final byte BYTE = 2;
    private static final byte INT = 3;
    private static final byte SHORT = 4;
    private static final byte LONG = 5;
    private static final byte CHAR = 6;
    private static final byte STRING = 7;
    private static final byte STRINGARRAY = 8;
    private static final byte JSONSTRING = 9;
    private static final byte NUMERICARRAY = 10;
    private byte dataType;
    private int length;
    private Object data;

    public FusionField(boolean val) {
        this.setByteVal((byte)(val ? 1 : 0));
    }

    public FusionField(byte val) {
        this.setByteVal(val);
    }

    public FusionField(int val) {
        this.setIntVal(val);
    }

    public FusionField(short val) {
        this.setShortVal(val);
    }

    public FusionField(long val) {
        this.setLongVal(val);
    }

    public FusionField(char val) {
        this.setCharVal(val);
    }

    public FusionField(String val) {
        this.setStringVal(val);
    }

    public static FusionField createJsonStringField(String val) {
        FusionField f = new FusionField(val);
        f.dataType = (byte)9;
        return f;
    }

    public FusionField(byte[] val) {
        this.setByteArrayVal(val);
    }

    public static FusionField createByteEnumArrayField(ByteValueEnum[] val) {
        byte[] data = new byte[val.length];
        for (int i = 0; i < val.length; ++i) {
            data[i] = val[i].value();
        }
        return new FusionField(data);
    }

    public FusionField(String[] val) {
        this.setStringArrayVal(val);
    }

    public FusionField(long[] val) {
        this.setNumericArrayVal(val);
    }

    public int length() {
        return this.length;
    }

    public int totalLength() {
        return this.length() + 6;
    }

    public Byte getByteVal() {
        if (this.dataType == 2) {
            return (Byte)this.data;
        }
        if (this.dataType == 1) {
            byte[] ba = (byte[])this.data;
            return ba[0];
        }
        if (this.dataType == 7 || this.dataType == 9) {
            try {
                return Byte.parseByte((String)this.data);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setByteVal(byte byteVal) {
        this.dataType = (byte)2;
        this.length = 1;
        this.data = byteVal;
    }

    public Character getCharVal() {
        if (this.dataType == 6) {
            return (Character)this.data;
        }
        if (this.dataType == 1) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream((byte[])this.data));
                return Character.valueOf(dis.readChar());
            }
            catch (IOException e) {
                return null;
            }
        }
        if (this.dataType == 7 || this.dataType == 9) {
            String s = (String)this.data;
            if (s.length() == 0) {
                return null;
            }
            return Character.valueOf(s.charAt(0));
        }
        return null;
    }

    public void setCharVal(char charVal) {
        this.dataType = (byte)6;
        this.length = 2;
        this.data = Character.valueOf(charVal);
    }

    public Integer getIntVal() {
        if (this.dataType == 3) {
            return (Integer)this.data;
        }
        if (this.dataType == 1) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream((byte[])this.data));
                return dis.readInt();
            }
            catch (IOException e) {
                return null;
            }
        }
        if (this.dataType == 7 || this.dataType == 9) {
            try {
                return Integer.parseInt((String)this.data);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setIntVal(int intVal) {
        this.dataType = (byte)3;
        this.length = 4;
        this.data = intVal;
    }

    public Long getLongVal() {
        if (this.dataType == 5) {
            return (Long)this.data;
        }
        if (this.dataType == 1) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream((byte[])this.data));
                return dis.readLong();
            }
            catch (IOException e) {
                return null;
            }
        }
        if (this.dataType == 7 || this.dataType == 9) {
            try {
                return Long.parseLong((String)this.data);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setLongVal(long longVal) {
        this.dataType = (byte)5;
        this.length = 8;
        this.data = longVal;
    }

    public Short getShortVal() {
        if (this.dataType == 4) {
            return (Short)this.data;
        }
        if (this.dataType == 1) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream((byte[])this.data));
                return dis.readShort();
            }
            catch (IOException e) {
                return null;
            }
        }
        if (this.dataType == 7 || this.dataType == 9) {
            try {
                return Short.parseShort((String)this.data);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setShortVal(short shortVal) {
        this.dataType = (byte)4;
        this.length = 2;
        this.data = shortVal;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String getStringVal() {
        switch (this.dataType) {
            case 6: {
                return String.valueOf(this.getCharVal().charValue());
            }
            case 2: {
                return String.valueOf(this.getByteVal().byteValue());
            }
            case 4: {
                return String.valueOf(this.getShortVal().shortValue());
            }
            case 3: {
                return String.valueOf(this.getIntVal());
            }
            case 5: {
                return String.valueOf(this.getLongVal());
            }
            case 7: 
            case 9: {
                return (String)this.data;
            }
            case 1: {
                ** GOTO lbl21
            }
            default: {
                return null;
            }
        }
        {
            catch (Exception e) {
                return null;
            }
lbl21:
            // 1 sources

            try {
                return new String((byte[])this.data, "UTF-8");
            }
            catch (Exception e) {}
            return new String((byte[])this.data);
        }
    }

    public void setStringVal(String stringVal) {
        try {
            this.dataType = (byte)7;
            this.data = stringVal;
            this.length = stringVal.getBytes(CHAR_ENCODING).length;
        }
        catch (Exception e) {
            this.length = stringVal.getBytes().length;
        }
    }

    public boolean[] getBooleanArrayVal() {
        byte[] byteArray = this.getByteArrayVal();
        if (byteArray == null) {
            return null;
        }
        boolean[] booleanArray = new boolean[byteArray.length];
        for (int i = 0; i < byteArray.length; ++i) {
            booleanArray[i] = byteArray[i] != 0;
        }
        return booleanArray;
    }

    public byte[] getByteArrayVal() {
        ByteBuffer buffer = ByteBuffer.allocate(this.length);
        try {
            switch (this.dataType) {
                case 6: {
                    buffer.putChar(this.getCharVal().charValue());
                    break;
                }
                case 2: {
                    buffer.put(this.getByteVal());
                    break;
                }
                case 4: {
                    buffer.putShort(this.getShortVal());
                    break;
                }
                case 3: {
                    buffer.putInt(this.getIntVal());
                    break;
                }
                case 5: {
                    buffer.putLong(this.getLongVal());
                    break;
                }
                case 7: {
                    buffer.put(this.getStringVal().getBytes(CHAR_ENCODING));
                    break;
                }
                case 9: {
                    return Base64.decode((String)((String)this.data));
                }
                case 1: {
                    buffer.put((byte[])this.data);
                    break;
                }
                case 8: {
                    String[] stringArray = this.getStringArrayVal();
                    for (int i = 0; i < stringArray.length; ++i) {
                        try {
                            buffer.put(stringArray[i].getBytes(CHAR_ENCODING));
                            if (i >= stringArray.length - 1) continue;
                            buffer.put((byte)0);
                            continue;
                        }
                        catch (Exception e) {
                            return null;
                        }
                    }
                    break;
                }
                case 10: {
                    long[] numericArray;
                    for (long num : numericArray = (long[])this.data) {
                        buffer.putLong(num);
                    }
                    break;
                }
            }
        }
        catch (IOException e) {
            return null;
        }
        return buffer.array();
    }

    public void setByteArrayVal(byte[] byteArrayVal) {
        this.dataType = 1;
        byte[] ba = new byte[byteArrayVal.length];
        System.arraycopy(byteArrayVal, 0, ba, 0, byteArrayVal.length);
        this.length = ba.length;
        this.data = ba;
    }

    public long[] getNumericArrayVal() {
        if (this.dataType == 10) {
            return (long[])this.data;
        }
        if (this.dataType == 1) {
            ByteBuffer buffer = ByteBuffer.wrap((byte[])this.data);
            long[] numbers = new long[buffer.limit() / 8];
            int i = 0;
            while (buffer.hasRemaining() && buffer.remaining() % 8 == 0) {
                numbers[i++] = buffer.getLong();
            }
            if (buffer.remaining() != 0) {
                log.warn((Object)("Incomplete numeric array field received, remainder=" + buffer.remaining()));
            }
            return numbers;
        }
        return null;
    }

    public String[] getStringArrayVal() {
        if (this.dataType == 8) {
            return (String[])this.data;
        }
        if (this.dataType == 1) {
            try {
                Vector<String> v = new Vector<String>();
                byte[] byteArray = (byte[])this.data;
                int offset = 0;
                int length = 0;
                while (offset + length < byteArray.length) {
                    if (byteArray[offset] != 0) {
                        length = 0;
                        while (offset + length <= byteArray.length) {
                            if (byteArray[offset + length] == 0 && length >= 1) {
                                v.add(new String(byteArray, offset, length, CHAR_ENCODING));
                                break;
                            }
                            if (offset + length == byteArray.length - 1) {
                                v.add(new String(byteArray, offset, length + 1, CHAR_ENCODING));
                                break;
                            }
                            ++length;
                        }
                    }
                    offset = offset + length + 1;
                    length = 0;
                }
                return v.toArray(new String[v.size()]);
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public void setStringArrayVal(String[] stringArrayVal) {
        this.dataType = (byte)8;
        this.length = 0;
        for (int i = 0; i < stringArrayVal.length; ++i) {
            try {
                this.length += stringArrayVal[i].getBytes(CHAR_ENCODING).length;
            }
            catch (Exception e) {
                this.length += stringArrayVal[i].getBytes().length;
            }
            if (i >= stringArrayVal.length - 1) continue;
            ++this.length;
        }
        this.data = stringArrayVal;
    }

    public void setNumericArrayVal(long[] numericArrayVal) {
        this.dataType = (byte)10;
        this.length += 8 * numericArrayVal.length;
        this.data = numericArrayVal;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        switch (this.dataType) {
            case 6: {
                b.append("[Char ").append(this.length()).append("] ").append(this.getCharVal());
                break;
            }
            case 2: {
                b.append("[Byte ").append(this.length()).append("] ").append(this.getByteVal());
                break;
            }
            case 4: {
                b.append("[Short ").append(this.length()).append("] ").append(this.getShortVal());
                break;
            }
            case 3: {
                b.append("[Int ").append(this.length()).append("] ").append(this.getIntVal());
                break;
            }
            case 5: {
                b.append("[Long ").append(this.length()).append("] ").append(this.getLongVal());
                break;
            }
            case 7: {
                b.append("[String ").append(this.length()).append("] ").append(this.getStringVal());
                break;
            }
            case 9: {
                b.append("[JsonString ").append(this.length()).append("] ").append(this.getStringVal());
                break;
            }
            case 1: {
                try {
                    byte[] byteArrayVal = this.getByteArrayVal();
                    b.append("[ByteArray ").append(this.length()).append("] ").append(this.byteArrayToHexString(byteArrayVal)).append(", ").append(this.translate(byteArrayVal));
                }
                catch (Exception e) {
                    b.append("EXCEPTION: " + e.getMessage());
                }
                break;
            }
            case 8: {
                String[] stringArrayVal = this.getStringArrayVal();
                b.append("[StringArray ").append(this.length()).append("] [");
                for (int i = 0; i < stringArrayVal.length; ++i) {
                    b.append(stringArrayVal[i]);
                    if (i >= stringArrayVal.length - 1) continue;
                    b.append(", ");
                }
                b.append("]");
                break;
            }
            case 10: {
                long[] numericArrayVal = this.getNumericArrayVal();
                b.append("[NumericArray ").append(this.length()).append("] [");
                for (int i = 0; i < numericArrayVal.length; ++i) {
                    b.append(numericArrayVal[i]);
                    if (i >= numericArrayVal.length - 1) continue;
                    b.append(", ");
                }
                b.append("]");
                break;
            }
        }
        return b.toString();
    }

    public boolean isNumeric() {
        return this.dataType == 2 || this.dataType == 4 || this.dataType == 3 || this.dataType == 5;
    }

    public byte getDataType() {
        return this.dataType;
    }

    public void setValueInJsonObject(JSONObject obj, String key) {
        try {
            switch (this.dataType) {
                case 6: {
                    char[] buf = new char[]{this.getCharVal().charValue()};
                    obj.put(key, (Object)String.valueOf(buf));
                    return;
                }
                case 2: {
                    obj.put(key, (int)this.getByteVal().byteValue());
                    return;
                }
                case 4: {
                    obj.put(key, (int)this.getShortVal().shortValue());
                    return;
                }
                case 3: {
                    obj.put(key, this.getIntVal().intValue());
                    return;
                }
                case 5: {
                    obj.put(key, this.getLongVal().longValue());
                    return;
                }
                case 7: 
                case 9: {
                    obj.put(key, (Object)((String)this.data));
                    return;
                }
                case 1: {
                    obj.put(key, (Object)Base64.encode((byte[])((byte[])this.data)));
                    return;
                }
                case 8: {
                    String[] stringArrayVal = this.getStringArrayVal();
                    JSONArray arr = new JSONArray();
                    for (String s : stringArrayVal) {
                        arr.put((Object)s);
                    }
                    obj.put(key, (Object)arr);
                    return;
                }
                case 10: {
                    long[] numericArrayVal = this.getNumericArrayVal();
                    JSONArray arr = new JSONArray();
                    for (long l : numericArrayVal) {
                        arr.put(l);
                    }
                    obj.put(key, (Object)arr);
                    return;
                }
            }
            return;
        }
        catch (Exception exception) {
            return;
        }
    }

    private String translate(byte[] in) {
        String s;
        StringBuffer b = new StringBuffer();
        try {
            s = new String(in, CHAR_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            s = new String(in);
        }
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c < ' ' || c > '~') {
                if (b.length() <= 0) break;
                b.append("...");
                break;
            }
            b.append(c);
        }
        if (b.length() > 0) {
            b.insert(0, "String=\"");
            b.append("\"");
        }
        if (in.length == 1) {
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append("Byte=").append(in[0]);
        } else if (in.length == 2) {
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append("Short=").append(ByteBuffer.wrap(in).getShort());
        } else if (in.length == 4) {
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append("Int=").append(ByteBuffer.wrap(in).getInt());
        } else if (in.length == 8) {
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append("Long=").append(ByteBuffer.wrap(in).getLong());
        }
        return b.toString();
    }

    private String byteArrayToHexString(byte[] in) {
        byte ch = 0;
        if (in == null || in.length <= 0) {
            return null;
        }
        String[] pseudo = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);
        for (int i = 0; i < in.length; ++i) {
            if (out.length() > 0) {
                out.append(' ');
            }
            ch = (byte)(in[i] & 0xF0);
            ch = (byte)(ch >>> 4);
            ch = (byte)(ch & 0xF);
            out.append(pseudo[ch]);
            ch = (byte)(in[i] & 0xF);
            out.append(pseudo[ch]);
        }
        return new String(out);
    }
}

