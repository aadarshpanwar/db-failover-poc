package com.example.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.InetAddress;

class TestAddress {
    private static final String TEST_DOMAIN = "avocadi.me";

    public static void main(String[] args) throws InterruptedException {
        java.security.Security.setProperty("networkaddress.cache.ttl" , "10");
        java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "0");
        for (int i = 0; i < 1000; i++) {
            try {
                InetAddress address = InetAddress.getByName("dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com");
                System.out.println(getCurrentTime() + " lookup success " + address);
            } catch (Exception ignore) {
                System.out.println(getCurrentTime() + " lookup failed");
            } finally {
                Thread.sleep(1000);
            }
        }
        return;
    }

    private static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

}