package com.iss.cloud.disk.service;

import com.iss.cloud.disk.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface FileService {

    /**
     * 获得文件列表
     */
    Pagination<MyFile> getFiles(Pagination page);

    Pagination<MyFile> getSearchFiles(Pagination page, String dataItem) throws IOException;

    /**
     * 获得文件列表
     */
    Pagination<MyFile> getCollectFiles(Pagination page);

    /**
     * 获得文件列表
     */
    Pagination<MyFile> getRecoveryFiles(Pagination page);

    /**
     * 获得文件列表
     */
    Pagination<MyFile> getFilesByPid(Pagination page);

    List<Node> getFolders(int pid, int userId);

    Map<String, Integer> getCountByType(int userId);

    ResultModel insertFile(InputStream inputStream, MyFile myFile,EsFile esFile) throws IOException;

    ResultModel mkdir(MyFile myFile);

    ResultModel deleteFile(int[] ids) throws IOException;

    ResultModel recoveryFile(int[] ids);

    ResultModel share(int[] ids, int fileId, User currentUser);

    ResultModel acceptShare(int id, User user);

    ResultModel cancelRecovery(int id);

    ResultModel collectFile(int id);

    ResultModel cancelCollect(int id);

    ResultModel rename(MyFile myFile, String newPath);

    ResultModel move(MoveVO vo);

    Pagination<MyFile> getShareFiles(Pagination page);

    ResultModel cannotShare(int id);

    MyFile getFileByName(String name);
}
