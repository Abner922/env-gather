package com.briup.smart.env;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author SDX
 * @create 2022-08-29 9:47
 */
@SuppressWarnings("all")
public class ConfigurationImpl implements Configuration{
    public static Map<String,Object> map = new HashMap<>();
    public static Properties properties = new Properties();
    private static Configuration config = new ConfigurationImpl();
    public static Configuration getInstance(){
        return config;
    }
    private ConfigurationImpl(){
        //私有构造，防止直接创建对象
    }
    static{
        try{
            parseXml();
            initModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void parseXml(){
        //解析XML文件
        try {
            SAXReader reader = new SAXReader();
            //将XML文件读取为一份document对象
            Document document = reader.read("env-gather-impl\\src\\main\\resources\\conf.xml");
            //利用Element中的方法，获取根节点，返回的是Element
            Element rootElement = document.getRootElement();
            //获取根节点所有的子节点
            List<Element> element = rootElement.elements();
            //循环遍历子节点
            for (Element ele : element) {
//                拿到class属性，并根据他的值创建对象，放入集合中.
//                String attr1 = ele.attributeValue("class");
//                Object instance1 = Class.forName(attr1).newInstance();
                Attribute attr = ele.attribute("class");
                Object instance = Class.forName(attr.getValue()).newInstance();
                //放入集合
                map.put(ele.getName(),instance);
                //遍历子节点
                List<Element> childs = ele.elements();
                for (Element child : childs) {
                    //获取前标签名
                    String name = child.getName();
                    //获取标签文本值
                    String value = child.getText();
                    properties.setProperty(name,value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initModule() throws Exception {
        //1.循环遍历map
        for(Object value : map.values()){
            if(value instanceof ConfigurationAware){
                ((ConfigurationAware) value).setConfiguration(config);
            }
            if(value instanceof PropertiesAware){
                ((PropertiesAware) value).init(properties);
            }
        }
    }


    @Override
    public Log getLogger() throws Exception {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) map.get("dbStore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) map.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup) map.get("backup");
    }
}
