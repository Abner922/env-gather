package com.briup.smart.env.util;


import org.junit.Test;

import java.io.*;

/**
 * @author SDX
 * @create 2022-08-27 14:22
 */

public class BackupImpl implements Backup{
//    读取store方法记录的文件，在处理数据的时候跳过
    @Override
    public Object load(String fileName, boolean del) throws Exception {
        File file = new File(fileName);
        if(!file.exists()){
            return null;
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object o = ois.readObject();
        if(del){
            file.delete();
        }
        return o;
    }

    //记录已经入库的数据
    @Override
    //filename新文件存放的位置和名称，obj记录的事读取的文件的字节
    public void store(String fileName, Object obj, boolean append) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName,append));
        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }

}
