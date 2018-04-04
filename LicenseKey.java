package examples.com.intelligt.modbus.examples;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;

class LicenseKey {

    public static String getMacID() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
            byte mac[] = nwi.getHardwareAddress();
            System.out.println(mac.toString());
            
            return (mac.toString());
            
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
