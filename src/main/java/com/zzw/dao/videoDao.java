package com.zzw.dao;

import com.zzw.Entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface videoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTag(List<VideoTag> videoTagList);

    List<Video> pageListVideos(Map<String, Object> params);

    Integer pageCountVideos(Map<String, Object> params);

    Video getVideoById(Long id);

    VideoLike getVideoLikeByVideoId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Integer addVideoLike(VideoLike videoLike);

    void deleteVideoLike(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Long getVideoLikes(Long videoId);

    void deleteVideoColletion(@Param("videoId")Long videoId, @Param("userId")Long userId);

    void addVideoColletion(VideoCollection videoCollection);

    Long getVideoColletion(Long videoId);

    VideoCollection  getVideoColletionByVideoIdAndUserId(@Param("videoId")Long videoId, @Param("userId")Long userId);


    void addVideoCoins(VideoCoin videoCoin);

    VideoCoin getVideoCoinsByVideoIdAndUserId(@Param("videoId")Long videoId, @Param("userId")Long userId);

    void updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoins(Long videoId);

    void addVideosComments(VideoComment videoComment);

    Integer pageCountVideosComents(Map<String, Object> params);

    List<VideoComment> pageListVideosComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootId(List<Long> parentList);

    Video getVideoDetails(Long videoId);

    List<userPreference> getAllUserPreference();

    List<Video> batchGetVideoByIds(List<Long> idList);
}
