package com.zzw.service;

import com.zzw.Entity.*;
import com.zzw.dao.videoDao;
import com.zzw.exception.conditionException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class videoService {

    @Autowired
    private com.zzw.dao.videoDao videoDao;

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private userService userService;


    public void addVideos(Video video) {
        Date now = new Date();

        video.setCreateTime(new Date());

        videoDao.addVideos(video);

//        Long videoId = video.getId();
//
//        List<VideoTag> tagList = video.getVideoTagList();
//
//        if(tagList==null){
//            tagList = new ArrayList<>();
//        }
//
//        tagList.forEach(item ->{
//            item.setCreateTime(now);
//            item.setVideoId(videoId);
//        });
//
//        videoDao.batchAddVideoTag(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {

        if(size==null||no==null) throw new conditionException("参数异常！");

        Map<String,Object> params = new HashMap<>();

        params.put("start",(no-1)*size);
        params.put("limit",size);
        params.put("area",area);

        List<Video> list = new ArrayList<>();
        Integer total =  videoDao.pageCountVideos(params);

        if(total>0){

            list = videoDao.pageListVideos(params);

        }

        return new PageResult<>(total,list);


    }
    //对视频点赞
    public void addVideoLike(Long videoId, Long userId) {
       Video video =  videoDao.getVideoById(videoId);
       if(video == null) throw new conditionException("非法视频!");
       VideoLike videoLike =  videoDao.getVideoLikeByVideoId(videoId,userId);
       if(videoLike!=null)throw new conditionException("对视频已经点过赞!");
        videoLike  = new VideoLike();
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId,userId);
    }

    public Map<String, Object> getVideoLike(Long videoId, Long userId) {
        Long count  =videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoId(videoId,userId);//判断登录用户对该视频是否点赞
        Boolean flag = videoLike !=null;
        Map<String, Object> res = new HashMap<>();
        res.put("count",count);
        res.put("like",flag);
        return res;
    }
    @Transactional
    public void addVideoCollections(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if(videoId == null||groupId==null) throw new conditionException("参数异常！");
        Video video = videoDao.getVideoById(videoId);
        if(video==null)  throw new conditionException("非法视频!");

        //先删除原来视频的收藏
        videoDao.deleteVideoColletion(videoId,userId);
        //再添加新的视频收藏
        videoCollection.setCreateTime(new Date());
        videoCollection.setUserId(userId);
        videoDao.addVideoColletion(videoCollection);
    }

    public void deleteVideoCollections(Long videoId, Long userId) {
        videoDao.deleteVideoColletion(videoId,userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Long count  =videoDao.getVideoColletion(videoId);
        VideoCollection VideoCollection = videoDao.getVideoColletionByVideoIdAndUserId(videoId, userId);//判断登录用户对该视频是否点赞
        Boolean flag = VideoCollection !=null;
        Map<String, Object> res = new HashMap<>();
        res.put("count",count);
        res.put("like",flag);
        return res;
    }

    //视频投币功能实现
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        Long videoId = videoCoin.getVideoId();
        Long amount = videoCoin.getAmount();
        if(videoId == null) throw new conditionException("参数异常！");

        Video video = videoDao.getVideoById(videoId);
        if(video==null)  throw new conditionException("非法视频!");

        Long userCoinsAmount =userCoinService.getUserCoinsAmount(userId);//````

        userCoinsAmount = userCoinsAmount==null?0:userCoinsAmount;

        if(amount>userCoinsAmount) throw new conditionException("用户硬币不足!");

        //当前用户对该视频投了多少币
        VideoCoin dbVideoCoins = videoDao.getVideoCoinsByVideoIdAndUserId(videoId,userId);

        if(dbVideoCoins==null){//之前没投过币-添加
            videoCoin.setCreateTime(new Date());
            videoCoin.setUserId(userId);
            videoDao.addVideoCoins(videoCoin);
        }else{//之前投过币-更新

            Long dbAmount = dbVideoCoins.getAmount();
            dbAmount+=amount;
            //更新操作
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);//更新
        }
        //更新用户的硬币总数 不能投完币之后 不减少硬币数
        userCoinService.updateUserCoinsAmount(userId,(userCoinsAmount-amount));

    }

    //投币总数
    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        Long count  =videoDao.getVideoCoins(videoId);
        VideoCoin videoCoin = videoDao.getVideoCoinsByVideoIdAndUserId(videoId, userId);//判断登录用户对该视频是否点赞
        Boolean flag = videoCoin !=null;
        Map<String, Object> res = new HashMap<>();
        res.put("count",count);
        res.put("like",flag);
        return res;
    }


    //添加评论
    public void addVideosComments(VideoComment videoComment, Long userId) {

        Long videoId = videoComment.getVideoId();

        if(videoId==null) throw new conditionException("参数异常!");

        Video video = videoDao.getVideoById(videoId);

        if(video==null)throw new conditionException("视频不存在!");

        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());


        //添加评论到评论表
        videoDao.addVideosComments(videoComment);




    }


    //分页查询-评论功能
    public PageResult<VideoComment> pageListVideosComments(Integer size, Integer no, Long videoId) {
        Video video = videoDao.getVideoById(videoId);

        if(video==null) throw new conditionException("找不到视频");

        Map<String,Object> params = new HashMap<>();
        params.put("start",(no-1)*size);
        params.put("limit",size);
        params.put("videoId",videoId);
        Integer total =  videoDao.pageCountVideosComents(params);
        List<VideoComment> list = new ArrayList<>();

        if(total>0){
            list = videoDao.pageListVideosComments(params);
            //批量查询二级用户
            List<Long> parentList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootId(parentList);
            //批量查询用户信息
            Set<Long> userIdSet = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdSet = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            userIdSet.addAll(replyUserIdSet);
            List<UserInfo> userInfoList =  userService.batchGetUserInfoByUserIds(userIdSet);
            Map<Long,UserInfo> userInfoMap =userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId,userInfo -> userInfo));
            list.forEach(comment->{
                Long id = comment.getId();
                ArrayList<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child->{
                    if(id.equals(child.getRootId())){
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserInfo()));
                        childList.add(child);
                    }
                });
                comment.setChildList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });

        }

        return new PageResult<VideoComment>(total,list);
    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video =  videoDao.getVideoDetails(videoId);
        Long userId = video.getUserId();
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();//获取视频投稿人
        HashMap<String, Object> res = new HashMap<>();
        res.put("video",video);
        res.put("userInfo",userInfo);
        return res;
    }

    //推荐功能
    public List<Video> recommend(Long userId) throws TasteException {

        List<userPreference> list=  videoDao.getAllUserPreference();//得到所有用户的偏好

        DataModel dataModel = this.creatDataModel(list);//处理用户偏好数据 得到 数据模型
        //获取用户相似度
        UncenteredCosineSimilarity similarity = new UncenteredCosineSimilarity(dataModel);//余弦方式计算相似度
                System.out.println(similarity.userSimilarity(11,12));//测试 11 用户和 12 用户的用户相似度
        //获取相邻用户
        NearestNUserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity,dataModel);//获取两个最接近的邻居
        long[] ar = userNeighborhood.getUserNeighborhood(userId);

        //构建推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel,userNeighborhood,similarity);//基于用户

        //推荐列表
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);

        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());

         List<Video> rem = videoDao.batchGetVideoByIds(itemIds); //从数据库 批量获取视频 通过 视频id

        return rem;
    }

    private DataModel creatDataModel(List<userPreference> userPreferenceList) {

        FastByIDMap<PreferenceArray> fastByIDMap = new FastByIDMap<>();

        Map<Long, List<userPreference>> map = userPreferenceList.stream().collect(Collectors.groupingBy(userPreference::getUserId));

        Collection<List<userPreference>> list = map.values();

        for(List<userPreference> userPreferences:list){

            GenericPreference[] array= new GenericPreference[userPreferences.size()];

            for(int i=0;i<userPreferences.size();++i){
                userPreference userPreference = userPreferences.get(i);
                GenericPreference item = new GenericPreference(userPreference.getUserId(),userPreference.getVideoId(),userPreference.getValue());
                array[i] = item;
            }

            fastByIDMap.put(array[0].getUserID(),new GenericUserPreferenceArray(Arrays.asList(array)));

        }

        return new GenericDataModel(fastByIDMap);

    }
}
