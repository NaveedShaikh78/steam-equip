package examples.com.intelligt.modbus.examples;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

class LicenseKey {

    public static String getMacID() {
        StringBuilder sb = new StringBuilder();
        final Enumeration<NetworkInterface> e;
        try {
            e = NetworkInterface.getNetworkInterfaces();
            while ( e.hasMoreElements()) {
                final byte[] mac = e.nextElement().getHardwareAddress();
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    System.out.println(sb.toString());
                    break;
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(LicenseKey.class.getName()).log(Level.SEVERE, null, ex);
        }
            return  sb.toString();
    }

    public static String getMacID1() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
            byte mac[] = nwi.getHardwareAddress();
            System.out.println(mac.toString());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            System.out.println(sb.toString());

            return (sb.toString());

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
