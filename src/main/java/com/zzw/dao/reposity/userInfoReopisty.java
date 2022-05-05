package com.zzw.dao.reposity;

import com.zzw.Entity.UserInfo;
        import com.zzw.Entity.Video;
        import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
        import org.springframework.stereotype.Repository;

@Repository
public interface userInfoReopisty extends ElasticsearchRepository<UserInfo,Long> {

}
