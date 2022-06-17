package com.iss.cloud.disk.service.impl;

import com.iss.cloud.disk.model.*;
import com.iss.cloud.disk.dao.FileDao;
import com.iss.cloud.disk.service.EsService;
import com.iss.cloud.disk.service.FileService;
import com.iss.cloud.disk.service.HDFSService;
import com.iss.cloud.disk.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private EsService esService;

    @Autowired
    private FileDao fileDao;

    @Autowired
    private HDFSService hdfsService;

    @Autowired
    private MessageService messageService;

    @Override
    public Pagination<MyFile> getFiles(Pagination page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getFiles(start, page.getPageSize(), page.getCurrentUser());
        page.setRows(rows);
        int total = this.fileDao.getCount(0, page.getCurrentUser(), 0);
        page.setTotal(total);
        return page;
    }

    @Override
    public Pagination<MyFile> getSearchFiles(Pagination page, String dataItem) throws IOException {
        List<String> fileNameByItem = esService.getFileNameByItem(dataItem);
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getSearchFiles(start,page.getPageSize(),fileNameByItem);
        page.setRows(rows);
        int total = this.fileDao.getSearchCount(fileNameByItem);
        page.setTotal(total);

        return page;
    }

    @Override
    public Pagination<MyFile> getShareFiles(Pagination page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getShareFiles(start, page.getPageSize(), page.getCurrentUser());
        page.setRows(rows);

        int total = this.fileDao.getCountShare(page.getCurrentUser());
        page.setTotal(total);
        return page;

    }

    @Override
    public Pagination<MyFile> getCollectFiles(Pagination page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getCollectFiles(start, page.getPageSize(), page.getCurrentUser());
        page.setRows(rows);
        int total = this.fileDao.getCount(1, page.getCurrentUser(), 0);
        page.setTotal(total);
        return page;
    }

    @Override
    public Pagination<MyFile> getRecoveryFiles(Pagination page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getRecoveryFiles(start, page.getPageSize(), page.getCurrentUser());
        page.setRows(rows);
        int total = this.fileDao.getCount(2, page.getCurrentUser(), 0);
        page.setTotal(total);
        return page;
    }

    @Override
    public Pagination<MyFile> getFilesByPid(Pagination page) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        List<MyFile> rows = this.fileDao.getFilesByPid(start, page.getPageSize(), page.getPid());
        page.setRows(rows);
        int total = this.fileDao.getCount(3, page.getCurrentUser(), page.getPid());
        page.setTotal(total);
        return page;
    }

    @Override
    public List<Node> getFolders(int pid, int userId) {
        List<Node> nodes = this.fileDao.getFolders(pid, userId);
        for (Node node : nodes) {
            node.setTags(new String[]{node.getFilePath()});
            List<Node> children = this.getFolders(node.getId(), userId);
            if (children.size() > 0) {
                node.setNodes(children);
            }
        }
        return nodes;
    }

    @Override
    public Map<String, Integer> getCountByType(int userId) {
        Map<String, Integer> map = this.fileDao.getCountByType(userId);
        if (map == null) {
            map = new HashMap<String, Integer>();
            map.put("img", 0);
            map.put("doc", 0);
            map.put("music", 0);
            map.put("radio", 0);
        }
        return map;
    }

    @Override
    public ResultModel insertFile(InputStream inputStream, MyFile myFile,EsFile esFile) throws IOException {
        // 上传文件到 HDFS中保存
        boolean result = this.hdfsService.upload(myFile.getFilePath(), inputStream);
        // 将文件信息保存至数据库
        if (result) {
            MyFile fileByFileName = this.fileDao.getFileByFileName(myFile.getFileName());
            if (null != fileByFileName){
                return ResultModel.fileError();
            }

            myFile.setCreateTime(new Date());

            int flag = this.fileDao.insertFile(myFile);

            if (flag > 0) {

                // 上传至ES
                esService.putData(esFile);

                return ResultModel.success();
            } else {

                this.hdfsService.delete(myFile.getFilePath());
                return ResultModel.dbError();
            }
        }
        return ResultModel.hdfsError();
    }

    @Override
    public ResultModel mkdir(MyFile myFile) {
        String hdfsPath = myFile.getFilePath() + "/" + myFile.getFileName(); // a/b/c/d
        boolean result = this.hdfsService.mkdirs(hdfsPath);
        if (result) {
            myFile.setFilePath(hdfsPath);
            myFile.setCreateTime(new Date());
            int flag = this.fileDao.insertFile(myFile);
            if (flag > 0) {
                return ResultModel.success();
            } else {
                this.hdfsService.delete(hdfsPath);
                return ResultModel.dbError();
            }
        }
        return ResultModel.hdfsError();
    }

    @Override
    public ResultModel deleteFile(int[] ids) throws IOException {
        for (int id : ids) {
            MyFile file = this.fileDao.getFile(id); // 查询文件完整信息
            // 先删除数据库记录
            int flag = this.fileDao.deleteFile(id);
            if (flag > 0) {
                // 数据记录删除成功，删除hdfs记录
                boolean result = this.hdfsService.delete(file.getFilePath());
                boolean b = false;
                if (result){
                     b = esService.deleteData(file.getFileName());
                }
                if (!b){
                    return ResultModel.error("es错误");
                }
                if (!result) {
                    this.fileDao.insertFile(file); // 如果 hdfs 删除失败，则还原数据库记录
                    return ResultModel.hdfsError();
                }
                return ResultModel.success();
            } else
                return ResultModel.dbError();
        }
        return ResultModel.error();
    }

    @Override
    public ResultModel recoveryFile(int[] ids) {
        return this.fileDao.recoveryFile(ids) > 0 ? ResultModel.success() : ResultModel.error();
    }

    @Override
    public ResultModel share(int[] ids, int fileId, User currentUser) {
        Message[] messages = new Message[ids.length];
        for (int i = 0; i < ids.length; i++) {
            String content = currentUser.getUsername() + " send a file to you, #Click to view ";
            messages[i] = new Message(content, currentUser, new User(ids[i]), new MyFile(fileId));
        }
        return this.messageService.insert(messages);
    }

    @Override
    public ResultModel acceptShare(int id, User user) {
        MyFile myFile = this.fileDao.getFile(id);
        String newPath = user.getPath() + myFile.getFileName();
        boolean result = this.hdfsService.copy(myFile.getFilePath(), newPath);
        if (result) {
            myFile.setPid(0);
            myFile.setFilePath(newPath);
            myFile.setUser(user);
            myFile.setCreateTime(new Date());
            int flag = this.fileDao.insertFile(myFile);
            if (flag > 0) {
                return ResultModel.success();
            } else {
                this.hdfsService.delete(myFile.getFilePath());
                return ResultModel.dbError();
            }
        }
        return ResultModel.hdfsError();
    }

    @Override
    public ResultModel cancelRecovery(int id) {
        return this.fileDao.cancelRecovery(id) > 0 ? ResultModel.success() : ResultModel.error();
    }

    @Override
    public ResultModel cannotShare(int id) {
        return this.fileDao.cancelShare(id) > 0 ? ResultModel.success() : ResultModel.error();
    }

    @Override
    public MyFile getFileByName(String name) {
        return this.fileDao.getFileByFileName(name);
    }

    @Override
    public ResultModel collectFile(int id) {
        return this.fileDao.collectFile(id) > 0 ? ResultModel.success() : ResultModel.error();
    }

    @Override
    public ResultModel cancelCollect(int id) {
        return this.fileDao.cancelCollect(id) > 0 ? ResultModel.success() : ResultModel.error();
    }

    @Override
    public ResultModel rename(MyFile myFile, String newPath) {
        boolean hdfsFlag = this.hdfsService.exists(newPath);
        boolean dbFlag = this.fileDao.exists(newPath) > 0;
        if (hdfsFlag && dbFlag) {
            return ResultModel.error("操作失败, 文件名已存在 !");
        }
        boolean result = this.hdfsService.rename(myFile.getFilePath(), newPath);
        if (!result) {
            return ResultModel.hdfsError();
        }
        // 保留旧 filepath 用于还原 HDFS 文件
        String oldPath = myFile.getFilePath();
        // 构造新的文件对象
        myFile.setFilePath(newPath);
        myFile.setFileName(newPath.substring(newPath.lastIndexOf("/") + 1, newPath.length()));
        if (this.fileDao.rename(myFile) > 0) {
            // 如果是文件夹，那么需要修改该文件夹下的子文件文件路径
            if (myFile.getFileType() == 5) {
                this.fileDao.renameByFilePath(oldPath, newPath);
            }
            return ResultModel.success();
        } else {
            // 数据库文件修改失败，则还原 HDFS 文件
            this.hdfsService.rename(myFile.getFilePath(), oldPath);
            return ResultModel.dbError();
        }
    }

    @Override
    public ResultModel move(MoveVO vo) {
        List<String> failNames = new ArrayList<String>();
        for (MyFile file : vo.getMyFiles()) {
            file.setPid(vo.getNewFile().getId());
            ResultModel result = this.rename(file, vo.getNewFile().getFilePath() + "/" + file.getFileName());
            if (!result.isOperate()) {
                failNames.add(file.getFileName());
            }
        }
        return failNames.size() == 0 ? ResultModel.success() : ResultModel.error("The following data operation failed: ", failNames.toString());
    }



}
