package com.zzw.service;

import com.zzw.Entity.UserInfo;
import com.zzw.Entity.Video;
import com.zzw.dao.reposity.userInfoReopisty;
import com.zzw.dao.reposity.videoReopisty;
import com.zzw.service.config.ElasticSearchConfig;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.common.TimeUtil;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticSearchService {

    @Autowired
    private videoReopisty videoReopisty;
    @Autowired
    private userInfoReopisty userInfoReopisty;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public void addUserInfo(UserInfo userInfo){
        userInfoReopisty.save(userInfo);
    }


    public List<Map<String,Object>> getContents(String keyWord,Integer pageNo,Integer pageSize) throws Exception {

        String[] indices = {"userinfo","videos"}; //所有的index 放到这里来

        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder searchSource = new SearchSourceBuilder();


        //分页
        searchSource.from(pageNo-1);
        searchSource.size(pageSize);

        //多条件查询构建器-把标记字段 也就是要查的字段
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyWord,"title","nick","description");

        searchSource.query(matchQueryBuilder);

        searchRequest.source(searchSource);


        //高亮显示
//        String[] array = {"title","nick","description"};
//
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//
//        for(String key :array){
//            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
//        }
//
//
//        highlightBuilder.requireFieldMatch(false);//***********多字段高亮则需要设置成false
//
//        highlightBuilder.preTags("<span style=\"color:red\">");
//        highlightBuilder.postTags("</span>");
//
//        searchSource.highlighter(highlightBuilder);//searchSource



        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String,Object>> arrList = new ArrayList<>();

        // hit 为匹配到的条目
        for(SearchHit hit:searchResponse.getHits()){

            //把击中的集合转为Map
            Map<String,Object> sourceMap = hit.getSourceAsMap();

            //处理高亮字段
//            Map<String, HighlightField> highlightBuilderFields = hit.getHighlightFields();
//            for(String key:array){
//                HighlightField field = highlightBuilderFields.get(key);
//
//                if(field!=null){
//                    Text[] fragments = field.fragments();
//                    String s = Arrays.toString(fragments);
//                    s = s.substring(1,s.length()-1);
//                    sourceMap.put(key,s);
//                }
//            }

            //外部返回的集合 添加map
            arrList.add(sourceMap);
        }

        return  arrList;
    }

    public void addVideo(Video video){
        videoReopisty.save(video);
    }

    public List<Video> getVideo(String keyWord){
        List<Video> list = videoReopisty.findByTitleLike(keyWord);
        return list;
    }

    public List<Video> findAllVideos(){
        List<Video> list = new ArrayList<>();
        Iterable<Video> all = videoReopisty.findAll();
        Iterator<Video> iterator = all.iterator();
        while (iterator.hasNext()){
            Video obj = iterator.next();
            list.add(obj);
        }
        return list;
    }

    public void deleteVideo(){
        videoReopisty.deleteAll();
    }


}
