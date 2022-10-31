package com.briup.smart.env.util;


import com.briup.smart.env.ConfigurationImpl;
import org.junit.Test;

/**
 * @author SDX
 * @create 2022-08-29 9:20
 */

public class LogTest {
    @Test
    public void configTest(){
        ConfigurationImpl.parseXml();
        System.out.println("map的长度是"+ConfigurationImpl.map.size());
        System.out.println("pro的长度是"+ConfigurationImpl.properties.size());
    }
}
