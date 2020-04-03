package com.tgp.tgpglideapp;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {

    private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
    private static final char[] SHA_256_CHARS = new char[64];

    /**
     * 返回表示SHA-256结果的十六进制字符串。
     */
    public static String sha256BytesToHex(byte[] bytes) {
        synchronized (SHA_256_CHARS) {
            return bytesToHex(bytes, SHA_256_CHARS);
        }
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    // /9655275#9655275
    private static String bytesToHex(byte[] bytes, char[] hexChars) {
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static <T> List<T> getSnapshot(Collection<T> other) {
        List<T> result = new ArrayList<>(other.size());
        for (T item : other) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }


    public static String AES256Encode(String stringToEncode) {
//        return AES256Encode(stringToEncode, "0123456789abcdef");
        return "0123456789abcdef";
    }

    /**
     * 说明: AES256加密
     *
     * @param stringToEncode
     *            明文
     * @param keyString
     *            密钥
     * @return Bses64格式密文
     */
    public static String AES256Encode(String stringToEncode, String keyString)
            throws NullPointerException {
        if (keyString.length() == 0 || keyString == null) {
            return null;
        }
        if (stringToEncode.length() == 0 || stringToEncode == null) {
            return null;
        }
        try {
            SecretKeySpec skeySpec = getKey(keyString);
            byte[] data = stringToEncode.getBytes("UTF8");
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            String encrypedValue = Base64.encodeToString(cipher.doFinal(data),
                    Base64.DEFAULT);
            return encrypedValue;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * 说明 :AES256解密
     *
     * @param text
     *            Base64格式密文
     * @param keyString
     *            密钥
     * @return String格式明文
     */
    public static String AES256Decrypt(String text, String keyString) {
        // byte[] rawKey = getRawKey(key);
        if (keyString.length() == 0 || keyString == null) {
            return null;
        }
        if (text.length() == 0 || text == null) {
            return null;
        }
        try {
            SecretKey key = getKey(keyString);
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            byte[] data = Base64.decode(text, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decrypedValueBytes = (cipher.doFinal(data));
            String decrypedValue = new String(decrypedValueBytes, "UTF-8");
            return decrypedValue;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * 说明 :将密钥转行成SecretKeySpec格式
     *
     * @param password
     *            16进制密钥
     * @return SecretKeySpec格式密钥
     */
    private static SecretKeySpec getKey(String password)
            throws UnsupportedEncodingException {
        // 如果为128将长度改为128即可
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);
        byte[] passwordBytes = toByte(password);
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length
                : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    /**
     *
     * 说明 :随机生成一组AES密钥
     *
     * @return 16进制AES密钥
     */
    public static String getRawKey() {
        KeyGenerator kgen = null;
        SecureRandom sr = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            } else {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        kgen.init(256, sr); // 256 bits or 128 bits,192bits
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        String result = bytes2Hex(raw);
        return result;
    }

    /**
     * byte数组转换为16进制字符串
     *
     * @param bts
     *            数据源
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    /**
     * 将16进制转换为byte数组
     *
     * @param hexString
     *            16进制字符串
     * @return byte数组
     */
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }



}
