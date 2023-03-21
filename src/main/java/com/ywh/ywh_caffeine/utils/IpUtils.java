package com.ywh.ywh_caffeine.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class IpUtils {
    private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);
    public static String ip = null;

    /**
     * windows linux 通用
     * @return
     */
    public static String getLocalHost(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        String hostAddress = ip.getHostAddress();
                        System.out.println("本机的IP111 = " + ip.getHostAddress());
                        return hostAddress;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String localHost = getLocalHost();
        getAllIps();

    }

    private static void getAllIps() {
        try{

            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();

            while (allNetInterfaces.hasMoreElements()){

                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();

                Enumeration addresses = netInterface.getInetAddresses();

                while (addresses.hasMoreElements()){

                    InetAddress ip = (InetAddress) addresses.nextElement();

                    if (ip != null

                            && ip instanceof Inet4Address

                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255

                            && ip.getHostAddress().indexOf(":")==-1){

                        System.out.println("本机的IP = " + ip.getHostAddress());

                    }

                }

            }

        }catch(Exception e){

            e.printStackTrace();

        }
    }

    //判断是否linux 还是windows
    public static final boolean isLinux() throws Exception{
        String sysStr = System.getProperty("os.name");
        if(sysStr.startsWith("Windows")){
            return false;
        }else {
            return sysStr.startsWith("Linux");
        }
    }


}
