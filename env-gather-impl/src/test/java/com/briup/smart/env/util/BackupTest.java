package com.briup.smart.env.util;


import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;
import com.briup.smart.env.entity.Environment;
import org.junit.Test;

import java.io.*;
import java.util.Collection;

/**
 * @author SDX
 * @create 2022-08-27 14:11
 */

public class BackupTest {
    BackupImpl backup = new BackupImpl();
    Gather gather = new GatherImpl();

    @Test
    public void fileLength() throws Exception {
        File file = new File("src\\main\\resources\\data-file-simple");
        backup.store("src\\main\\resources\\backup",new Long(21999),false);
        Long len = (Long)backup.load("src\\main\\resources\\backup", false);
        System.out.println("读取到的长度是：" + len);
    }


    @Test
    public void gather() throws Exception {
        Collection<Environment> gather = this.gather.gather();
        System.out.println("获取到集合的长度是：" + gather.size());
    }
}
