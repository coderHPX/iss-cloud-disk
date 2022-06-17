package com.iss.cloud.disk.controller;

import com.iss.cloud.disk.model.*;
import com.iss.cloud.disk.service.FileService;
import com.iss.cloud.disk.service.HDFSService;
import com.iss.cloud.disk.service.MessageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.POST;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private HDFSService hdfsService;

    @GetMapping("/getFiles")
    @ResponseBody
    public Pagination<MyFile> getFiles(Pagination page) {
        return this.fileService.getFiles(page);
    }

    @GetMapping("/getRecoveryFiles")
    @ResponseBody
    public Pagination<MyFile> getRecoveryFiles(Pagination page) {
        return this.fileService.getRecoveryFiles(page);
    }

    @GetMapping("/getCollectFiles")
    @ResponseBody
    public Pagination<MyFile> getCollectFiles(Pagination page) {
        return this.fileService.getCollectFiles(page);
    }

    @GetMapping("/getShareFiles")
    @ResponseBody
    public Pagination<MyFile> getShareFiles(Pagination page){
        return this.fileService.getShareFiles(page);
    }


    @GetMapping("/getFilesByPid")
    @ResponseBody
    public Pagination<MyFile> getFilesByPid(Pagination page) {
        return this.fileService.getFilesByPid(page);
    }

    @GetMapping("/getFolders")
    @ResponseBody
    public List<Node> getFolders(@RequestParam int pid, @SessionAttribute("user") User user) {
        return this.fileService.getFolders(pid, user.getId());
    }

    @PostMapping("/insertFile")
    @ResponseBody
    public ResultModel insertFile(@RequestParam("file") MultipartFile file, MyFile myFile) throws IOException {
        System.out.println(myFile);
        byte[] bytes = file.getBytes();

        EsFile esFile = new EsFile();
        esFile.setFileName(myFile.getFileName());//XX.pdf,docx
        esFile.setFileType(myFile.getFileName().substring(myFile.getFileName().lastIndexOf(".")+1));
        esFile.setContent(Base64.getEncoder().encodeToString(bytes));

        myFile.setFileSize(file.getSize());
        return this.fileService.insertFile(file.getInputStream(), myFile,esFile);
    }

    @PostMapping("/mkdir")
    @ResponseBody
    public ResultModel mkdir(MyFile myFile) {
        return this.fileService.mkdir(myFile);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileName, @RequestParam String filePath) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, "UTF-8"));
        InputStream input = this.hdfsService.download(filePath);
        return new ResponseEntity<byte[]>(IOUtils.toByteArray(input), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/deleteFile")
    @ResponseBody
    public ResultModel deleteFile(@RequestParam int[] ids) throws IOException {
         return this.fileService.deleteFile(ids);
//        return ResultModel.success();
    }

    @GetMapping(value = "/recoveryFile")
    @ResponseBody
    public ResultModel recoveryFile(@RequestParam int[] ids) {
        return this.fileService.recoveryFile(ids);
    }

    @GetMapping(value = "/shareFile")
    @ResponseBody
    public ResultModel shareFile(@RequestParam int[] ids, @RequestParam int fileId, @SessionAttribute("user") User currentUser) {
        return this.fileService.share(ids, fileId, currentUser);
    }

    @GetMapping(value = "/acceptShare")
    @ResponseBody
    public ResultModel acceptShare(@RequestParam int id, @SessionAttribute("user") User currentUser) {
        return this.fileService.acceptShare(id, currentUser);
    }

    @GetMapping(value = "/cancelRecovery")
    @ResponseBody
    public ResultModel cancelRecovery(@RequestParam int id) {
        return this.fileService.cancelRecovery(id);
    }

    @GetMapping(value = "/collectFile")
    @ResponseBody
    public ResultModel collectFile(@RequestParam int id) {
        return this.fileService.collectFile(id);
    }

    @GetMapping(value = "/cancelCollect")
    @ResponseBody
    public ResultModel cancelCollect(@RequestParam int id) {
        return this.fileService.cancelCollect(id);
    }

    @GetMapping(value = "/cannotShare")
    @ResponseBody
    public ResultModel cannotShare(@RequestParam int id){
        return this.fileService.cannotShare(id);
    }

    @GetMapping(value = "/rename")
    @ResponseBody
    public ResultModel rename(MyFile myFile, @RequestParam String newPath) {
        return this.fileService.rename(myFile, newPath);
    }

    @PostMapping(value = "/move")
    @ResponseBody
    public ResultModel move(@RequestBody MoveVO vo) {
        return this.fileService.move(vo);
    }


    @GetMapping(value = "/getFileByName")
    @ResponseBody
    public MyFile getFileByName(String name){
        return this.fileService.getFileByName(name);
    }

    @GetMapping(value = "/searchFile")
    public String search(String dataItem ,Model model){
        model.addAttribute("dataItem",dataItem);
        System.out.println(dataItem);
        return "view/searchFiles";
    }

    @GetMapping(value = "getSearchFiles")
    @ResponseBody
    public Pagination<MyFile> getSearchFile(Pagination page,String dataItem) throws IOException {
        return fileService.getSearchFiles(page,dataItem);
    }



}
