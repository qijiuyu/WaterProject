package com.water.project.utils.ble;


public class ByteStringHexUtil {

    /**
     * 16进制byte[]转string
     *
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * 16进制字符串转16进制byte[]
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    /**
     * 普通的字符串转换为16进制的字符串
     * @param str
     * @return
     */
    public static String toHex(String str) {
        String hexString="0123456789ABCDEF";
        byte[] bytes=str.getBytes();
        StringBuilder hex=new StringBuilder(bytes.length * 2);
        for(int i=0;i<bytes.length;i++) {
            hex.append(hexString.charAt((bytes[i] & 0xf0) >> 4));  // 作用同 n / 16
            hex.append(hexString.charAt((bytes[i] & 0x0f) >> 0));  // 作用同 n
//            hex.append(' ');  //中间用空格隔开
        }
        return hex.toString();
    }

}
