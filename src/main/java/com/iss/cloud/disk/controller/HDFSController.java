package com.iss.cloud.disk.controller;

import com.iss.cloud.disk.model.ResultModel;
import com.iss.cloud.disk.service.HDFSService;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@Api(value = "HDFS 测试接口")
@RestController
@RequestMapping("/hdfs")
public class HDFSController {

    @Autowired
    private HDFSService hdfsService;

    @GetMapping("/mkdirs")
    @ApiOperation(value = "新建文件夹", notes = "http://localhost:8080/disk/hdfs/mkdirs?dirs=...", response = ResultModel.class)
    @ApiImplicitParam(name = "dirs", value = "folder's path", required = true, dataType = "String", example = "/hdfs/a/b/c/")
    public ResultModel mkdirs(@RequestParam("dirs") String dirs) {
        return this.hdfsService.mkdirs(dirs) ? ResultModel.success() : ResultModel.error();
    }

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件", notes = "http://localhost:8080/disk/hdfs/upload", response = ResultModel.class)
    @ApiImplicitParam(name = "file", value = "File InputStream", required = true, dataType = "MultipartFile")
    public ResultModel upload(@RequestParam("file") MultipartFile file) throws IOException {
        return this.hdfsService.upload(file.getOriginalFilename(), file.getInputStream()) ? ResultModel.success() : ResultModel.error();
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除文件", notes = "http://localhost:8080/disk/hdfs/delete?filePath=...", response = ResultModel.class)
    @ApiImplicitParam(name = "filePath", value = "file's path", required = true, dataType = "String")
    public ResultModel delete(@RequestParam("filePath") String filePath) {
        return this.hdfsService.delete(filePath) ? ResultModel.success() : ResultModel.error();
    }

    @GetMapping("/rename")
    @ApiOperation(value = "重命名文件", notes = "http://localhost:8080/disk/hdfs/rename?oldStr=...&newStr=...", response = ResultModel.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPath", value = "oldFile's path", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPath", value = "newFile's path", required = true, dataType = "String")
    })
    public ResultModel rename(@RequestParam("oldPath") String oldPath, @RequestParam("newPath") String newPath) {
        return this.hdfsService.rename(oldPath, newPath) ? ResultModel.success() : ResultModel.error();
    }

    @GetMapping("/exists")
    @ApiOperation(value = "检查文件是否存在", notes = "http://localhost:8080/hdfs/exists?path=...", response = ResultModel.class)
    @ApiImplicitParam(name = "path", value = "file's path", required = true, dataType = "String")
    public ResultModel exists(@RequestParam("path") String path) {
        return this.hdfsService.exists(path) ? ResultModel.success() : ResultModel.error();
    }

    // http://localhost:8080/hdfs/download?fileName=
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileName") String fileName) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, "UTF-8"));
        InputStream input = this.hdfsService.download(fileName);
        return new ResponseEntity<byte[]>(IOUtils.toByteArray(input), headers, HttpStatus.OK);
    }

}
