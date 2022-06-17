package com.iss.cloud.disk.service;

import com.iss.cloud.disk.model.EsFile;

import java.io.IOException;
import java.util.List;

public interface EsService {

    boolean putData(EsFile esFile) throws IOException;
    List<String> getFileNameByItem(String item) throws IOException;
    boolean deleteData(String fileName) throws IOException;
}
