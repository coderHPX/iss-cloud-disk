package com.iss.cloud.disk.model;

public class EsFile {
    private String fileName;
    private String fileType;
    private String content;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EsFile{" +
                "fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
