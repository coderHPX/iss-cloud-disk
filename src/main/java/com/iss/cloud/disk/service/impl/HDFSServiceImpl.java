package com.iss.cloud.disk.service.impl;

import com.iss.cloud.disk.service.HDFSService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class HDFSServiceImpl implements HDFSService {

    @Autowired
    private FileSystem fileSystem;

    /**
     * 创建文件夹
     *
     * @param dirs /zz/a/b/c
     * @return
     */
    @Override
    public boolean mkdirs(String dirs) {
        boolean flag = false;
        try {
            flag = this.fileSystem.mkdirs(new Path(dirs));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 创建文件
     *
     * @param path = currentPath + filename
     */
    @Override
    public boolean upload(String path, InputStream input) {
        boolean flag = false;
        FSDataOutputStream fos = null;
        try {
            fos = this.fileSystem.create(new Path(path));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            IOUtils.closeStream(fos);
            flag = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 删除文件夹 or 文件
     */
    @Override
    public boolean delete(String filePath) {
        boolean flag = false;
        try {
            flag = this.fileSystem.delete(new Path(filePath), true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 重命名文件夹 or 文件
     */
    @Override
    public boolean rename(String oldPath, String newPath) {
        boolean flag = false;
        try {
            flag = this.fileSystem.rename(new Path(oldPath), new Path(newPath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 文件复制
     */
    @Override
    public boolean copy(String src, String dst) {
        boolean flag = false;
        try {
            flag = this.upload(src,this.download(dst));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 文件是否存在
     *
     * @param path = currentPath + filename
     */
    @Override
    public boolean exists(String path) {
        boolean flag = false;
        try {
            flag = this.fileSystem.exists(new Path(path));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 下载
     */
    @Override
    public InputStream download(String filePath) {
        FSDataInputStream fis = null;
        try {
            fis = this.fileSystem.open(new Path(filePath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return fis;
    }

    /**
     * 遍历指定路径下的文件
     */
    @Override
    public List<String> list(String path) {
        List<String> list = new ArrayList<String>();
        try {
            // 方式一
            RemoteIterator<LocatedFileStatus> iterator = this.fileSystem.listFiles(new Path(path), true);
            while (iterator.hasNext()) {
                LocatedFileStatus ls = iterator.next();
                list.add(ls.getPath().getName());
            }
            // 方式二
            FileStatus[] fileStatus = this.fileSystem.listStatus(new Path(path));
            for (int i = 0; i < fileStatus.length; i++) {
                System.out.println(fileStatus[i].getPath().toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    /**
     * 查找某个文件在集群中的位置
     */
    @Override
    public void getFileLocation(String fileName) {
        try {
            FileStatus fileStatus = this.fileSystem.getFileStatus(new Path(fileName));
            BlockLocation[] blockLocation = this.fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
            for (int i = 0; i < blockLocation.length; i++) {
                String[] hosts = blockLocation[i].getHosts();
                System.out.println("block_" + i + "_location:" + hosts[0]);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
