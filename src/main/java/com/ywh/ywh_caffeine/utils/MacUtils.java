package com.ywh.ywh_caffeine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MacUtils {

    private static String computerName = System.getenv().get("COMPUTERNAME");
    private static final String[] windowsCommand = {"ipconfig", "/all"};
    private static final String[] linuxCommand = {"/sbin/ifconfig", "-a"};
    private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);

    private static List<String> getMacAddressList() throws IOException {
        ArrayList<String> macAddressList = new ArrayList<String>();
        final String os = System.getProperty("os.name");
        final String[] command;
        if (os.startsWith("Windows")) {
            command = windowsCommand;
        } else if (os.startsWith("Linux")) {
            command = linuxCommand;
        } else {
            throw new IOException("Unknown operating system: " + os);
        }
        final Process process = Runtime.getRuntime().exec(command);
        // Discard the stderr
        new Thread() {

            @Override
            public void run() {
                try {
                    InputStream errorStream = process.getErrorStream();
                    while (errorStream.read() != -1) {
                    }
                    errorStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        // Extract the MAC addresses from stdout
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line = null; (line = reader.readLine()) != null;) {
            Matcher matcher = macPattern.matcher(line);
            if (matcher.matches()) {
                //macAddressList.add(matcher.group(1));
                macAddressList.add(matcher.group(1).replaceAll("[-:]", ""));
            }
        }
        reader.close();
        return macAddressList;
    }

    public static String getMacAddress() {
        try {
            List<String> addressList = getMacAddressList();
            if (addressList.isEmpty()) {
                return "";
            }
            return addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String[] getMacAddresses() {
        try {
            return getMacAddressList().toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    //一台机器不一定只有一个网卡
    public static  List<String> getMacList() throws  Exception{
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        ArrayList<String> tmpMacList = new ArrayList<>();
        ArrayList<String> tmpIpList = new ArrayList<>();
        while(en.hasMoreElements()){
            NetworkInterface iface = en.nextElement();
            List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
            for (InterfaceAddress addr :addrs) {
                InetAddress ip = addr.getAddress();
                tmpIpList.add(ip.getHostAddress());
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if(null == network){
                    continue;
                }
                byte[] mac = network.getHardwareAddress();
                if(null == mac){
                    continue;
                }
                sb.delete(0,sb.length());
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s",mac[i],(i<mac.length)?"-":""));
                }
                tmpMacList.add(sb.toString());
            }
        }
        if(tmpMacList.size()<=0){
            return tmpMacList;
        }
        return tmpMacList.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 获取电脑名
     *
     * @return
     */
    public static String getComputerName() {
        if (computerName == null || computerName.equals("")) {
            computerName = System.getenv().get("COMPUTERNAME");
        }
        return computerName;
    }

    /**
     * 获取客户端IP地址
     *
     * @return
     */
    public static String getIpAddrAndName() throws IOException {
        return InetAddress.getLocalHost().toString();
    }

    /**
     * 获取客户端IP地址
     *
     * @return
     */
    public static String getIpAddr() throws IOException {
        return InetAddress.getLocalHost().getHostAddress().toString();
    }

    /**
     * 获取电脑唯一标识
     *
     * @return
     */
    public static String getComputerID() {
        String id = getMacAddress();
        if (id == null || id.equals("")) {
            try {
                id = getIpAddrAndName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return computerName;
    }

    public static void main(String[] args) throws Exception {
        String macAddress = getMacAddress();
        System.out.println("本机mac地址："+macAddress);
        List<String> macList = getMacList();
        System.out.println("本机mac地址："+macList);

        String ipAddr = getIpAddr();
        System.out.println("ipAddr："+ipAddr);

        String ipAddrName = getIpAddrAndName();
        System.out.println("ipAddrName："+ipAddrName);

        String computerName = getComputerName();
        System.out.println("computerName："+computerName);

        String computerId = getComputerID();
        System.out.println("computerId："+computerId);

    }
}
