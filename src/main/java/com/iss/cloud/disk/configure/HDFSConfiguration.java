package com.iss.cloud.disk.configure;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.URI;

@Configuration
public class HDFSConfiguration {

    @Value("${hadoop.hdfs.uri}")
    private String hdfs_uri;

    /**
     * 获取 HDFS 文件系统对象
     */
    @Bean
    public FileSystem getFileSystem() {
        System.setProperty("HADOOP_USER_NAME", "root");
        FileSystem fs = null;
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("fs.defaultFS", "hdfs://ha-cluster");
        configuration.set("dfs.nameservices","ha-cluster");
        configuration.set("dfs.ha.namenodes.ha-cluster","nn1,nn2");
        configuration.set("dfs.namenode.rpc-address.ha-cluster.nn1","192.168.78.137:8020");
        configuration.set("dfs.namenode.rpc-address.ha-cluster.nn2","192.168.78.138:8020");
        try {
            fs = FileSystem.get(URI.create(hdfs_uri), configuration);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return fs;
    }
}