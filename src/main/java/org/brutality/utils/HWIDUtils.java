package org.brutality.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class HWIDUtils {
    public static String getHWID() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] macAddress = ni.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < macAddress.length; i++) {
                sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
