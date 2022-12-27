package com.hushaorui.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public abstract class IpUtils {
    private static String localIp;
    static {
        localIp = getLocalIp();
    }
    public static String getLocalIp() {
        if (localIp != null) {
            return localIp;
        }
        Enumeration<NetworkInterface> networks = null;
        try {
            // 获取网卡设备
            networks = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (networks == null) {
            return null;
        }
        InetAddress ip;
        Enumeration<InetAddress> address;
        // 遍历网卡设备
        while (networks.hasMoreElements()){
            address = networks.nextElement().getInetAddresses();
            while (address.hasMoreElements()){
                ip = address.nextElement();
                if (ip != null && ip.isSiteLocalAddress()){
                    if (ip.getHostAddress()==null || "".equals(ip.getHostAddress())){
                        //logger.info("获取到的客户端内网ip为空，从配置文件读取本地ip。");
                        return null;
                    }
                    return ip.getHostAddress(); // 客户端ip
                }
            }
        }
        return null;
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;    //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }    //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }    //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
