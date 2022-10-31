package com.briup.smart.env.client;


import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.ServerImpl;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

/**
 * @author SDX
 * @create 2022-08-25 11:40
 */

public class GatherTest {
    @Test
    public void t(){
        Gather gather = new GatherImpl();
//        ServerImpl server = new ServerImpl();
        Client client = new ClientImpl();
        try {
            Collection<Environment> envLists = gather.gather();
//            server.reciver();
            client.send(envLists);
//            for (Environment envList : envLists) {
//                System.out.println(envList);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDay(){
        //按照对应日期存入对应的表，获取时间戳对应的天

        Timestamp timestamp = new Timestamp(1516323596029L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("通过时间戳得到的天是：" + day);
    }

}
