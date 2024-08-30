// Decompiled with: CFR 0.152
// Class Version: 8
package org.brutality.feature;

import java.security.MessageDigest;

public class HWID {
    public static String getHWID() {
        try {
            byte[] byteData;
            String toEncrypt = String.valueOf(System.getenv("COMPUTERNAME")) + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();
            byte[] byArray = byteData = md.digest();
            int n = byteData.length;
            int n2 = 0;
            while (n2 < n) {
                byte aByteData = byArray[n2];
                String hex = Integer.toHexString(0xFF & aByteData);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
                ++n2;
            }
            return hexString.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
