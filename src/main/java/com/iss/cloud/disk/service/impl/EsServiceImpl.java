package com.iss.cloud.disk.service.impl;

import com.alibaba.fastjson.JSON;
import com.iss.cloud.disk.model.EsFile;
import com.iss.cloud.disk.service.EsService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EsServiceImpl implements EsService {
    private final RestHighLevelClient restHighLevelClient;

    private String index;


    @Autowired
    public EsServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public boolean putData(EsFile esFile) throws IOException {
        IndexRequest indexRequest = new IndexRequest("docwrite");


        indexRequest.source(JSON.toJSONString(esFile), XContentType.JSON);
        indexRequest.setPipeline("attachment");
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);


        return index.status().toString()=="CREATED";
    }

    @Override
    public List<String> getFileNameByItem(String item) throws IOException {
        SearchRequest searchRequest = new SearchRequest("docwrite");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("attachment.content", item));

        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        List<String> fileNames = new ArrayList<>();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsMap().get("fileName"));
            fileNames.add((String) hit.getSourceAsMap().get("fileName"));
        }

        return fileNames;
    }

    @Override
    public boolean deleteData(String fileName) throws IOException {
        DeleteByQueryRequest docwrite = new DeleteByQueryRequest("docwrite");


        docwrite.setQuery(QueryBuilders.termQuery("fileName",fileName));

        BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(docwrite, RequestOptions.DEFAULT);

        return bulkByScrollResponse.getDeleted()>0?true:false;
    }


}
