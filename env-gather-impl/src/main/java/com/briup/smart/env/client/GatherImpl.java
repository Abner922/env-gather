package com.briup.smart.env.client;


import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author SDX
 * @create 2022-08-25 11:18
 */

public class GatherImpl implements Gather, ConfigurationAware, PropertiesAware {
//    String filePath = "src\\main\\resources\\data-file";
//    String filePath = "src\\main\\resources\\data-file-simple";
    String filePath,backFilePath;
    Backup backup;
    Log log;

    //读取数据文件
    @Override
    public Collection<Environment> gather() throws Exception {
        File file = new File(filePath);
        //1.读取文件
        BufferedReader br = new BufferedReader(new FileReader(file));
        Long len = (Long)backup.load(backFilePath, Backup.LOAD_UNREMOVE);
        if(len != null){
            br.skip(len);
        }
        List<Environment> lists = new ArrayList<>();
        String str;
        while((str = br.readLine()) != null){
//            System.out.println(str);
            //2.字符串分割 按照|分割
            String[] arr = str.split("[|]");//或 "\\|"
            if(arr.length != 9){
                continue;
            }
            Environment env = new Environment();
            //3.set不需要处理的数据
            env.setSrcId(arr[0]);
            env.setDesId(arr[1]);
            env.setDevId(arr[2]);
            env.setSersorAddress(arr[3]);
            env.setCount(Integer.parseInt(arr[4]));
            env.setCmd(arr[5]);
            env.setStatus(Integer.parseInt(arr[7]));
            //4.如何把最后一个数据转换为时间戳类型
            Timestamp timestamp = new Timestamp(Long.parseLong(arr[8]));
            env.setGather_date(timestamp);
            Environment copyEnv = copyEnv(env);
            //也可以使用seitch-case
            if("16".equals(arr[3])){
                env.setName("温度");
                int sub1 = sub(arr, 0, 4, 6);
//                如果是温度数据：(data*(0.00268127F)-46.85F
//                如果是湿度数据：(data*0.00190735F)-6
                float warm = (sub1 * (0.00268127F)) - 46.85F;
                env.setData(warm);

                //处理湿度
                copyEnv.setName("湿度");
                int sub2 = sub(arr, 4, 8, 6);
                float wet = (sub2 * 0.00190735F) - 6;
                copyEnv.setData(wet);
            }
            else if("256".equals(arr[3])){
                env.setName("光照强度");
                int light = sub(arr, 0, 4, 6);
                env.setData(light);
                copyEnv.setData(light);
            }
            else if("1280".equals(arr[3])){
                env.setName("二氧化碳");
                int co2 = sub(arr, 0, 4, 6);
                env.setData(co2);
                copyEnv.setData(co2);
            }
            else{
                break;
            }

            //5.转入集合
            lists.add(env);
            lists.add(copyEnv);
        }
        backup.store(backFilePath,file.length(),Backup.STORE_OVERRIDE);
        return lists;
    }

    //复制对象
    //传入复制的对象
    private Environment copyEnv(Environment env){
        Environment copyEnv = new Environment();
        copyEnv.setSrcId(env.getSrcId());
        copyEnv.setDesId(env.getDesId());
        copyEnv.setDevId(env.getDevId());
        copyEnv.setSersorAddress(env.getSersorAddress());
        copyEnv.setCount(env.getCount());
        copyEnv.setCmd(env.getCmd());
        copyEnv.setData(env.getData());
        copyEnv.setStatus(env.getStatus());
        copyEnv.setGather_date(env.getGather_date());
        return copyEnv;
    }
    //截取字符串,转换10进制
    private static int sub(String[] arr, int start, int end,int index){
        String str = arr[index].substring(start, end);
        int i = Integer.parseInt(str, 16);
        return i;
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.backup = configuration.getBackup();
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.filePath = properties.getProperty("data-file-path");
        this.backFilePath = properties.getProperty("file-path");
    }
}
