package com.iss.cloud.disk.service;


import java.io.InputStream;
import java.util.List;


public interface HDFSService {

    boolean mkdirs(String dirs);

    boolean upload(String path, InputStream input);

    boolean delete(String filePath);

    boolean rename(String oldPath, String newPath);

    boolean copy(String src, String dst);

    boolean exists(String path);

    InputStream download(String filePath);

    List<String> list(String path);

    void getFileLocation(String fileName);


}
