package com.zzw.dao.reposity;

import com.zzw.Entity.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface videoReopisty extends ElasticsearchRepository<Video,Long> {

    List<Video> findByTitleLike(String keyWord);
}
