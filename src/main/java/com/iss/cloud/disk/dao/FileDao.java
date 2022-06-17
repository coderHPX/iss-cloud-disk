package com.iss.cloud.disk.dao;

import com.iss.cloud.disk.model.MyFile;
import com.iss.cloud.disk.model.Node;
import com.iss.cloud.disk.model.ResultModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FileDao {

    int getCount(@Param("flag") int flag, @Param("userId") int userId, @Param("pid") int pid);
    int getCountShare(int userId);

    List<MyFile> getFiles(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("userId") int userId);

    List<MyFile> getRecoveryFiles(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("userId") int userId);

    List<MyFile> getCollectFiles(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("userId") int userId);

    List<MyFile> getFilesByPid(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("pid") int pid);

    List<Node> getFolders(@Param("pid") int pid, @Param("userId") int userId);

    List<MyFile> getFilesByIds(int[] ids);

    MyFile getFile(int id);

    Map<String, Integer> getCountByType(int userId);

    int insertFile(MyFile file);

    int deleteFile(int id);

    int recoveryFile(int[] ids);

    int cancelRecovery(int id);

    int collectFile(int id);

    int cancelCollect(int id);

    int exists(String path);

    int rename(MyFile file);

    int renameByFilePath(@Param("oldPath") String oldPath, @Param("newPath") String newPath);

    List<MyFile> getShareFiles(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("userId") int userId);

    int cancelShare(int id);
    MyFile getFileByFileName(String name);

    List<MyFile> getSearchFiles(@Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("fileNames") List<String> fileNames );
    int getSearchCount(@Param("fileNames") List<String> fileNames);
}
