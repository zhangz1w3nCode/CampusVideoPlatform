package com.zzw.bilibili.api;

import com.zzw.Entity.*;
import com.zzw.bilibili.api.support.userSupport;
import com.zzw.domain.jsonResponse;
import com.zzw.service.DemoService;
import com.zzw.service.ElasticSearchService;
import com.zzw.service.danmuService;
import com.zzw.service.videoService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.Map;

@RestController
@EnableSwagger2
public class videoApi {

    @Autowired
    private videoService videoService;

    @Autowired
    private userSupport userSupport;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private danmuService danmuService;

    //视频投稿-功能
    @PostMapping("/videos")
    public jsonResponse<String> addVideos(@RequestBody Video video){
        Long userId = userSupport.getCurrentUserID();
        video.setUserId(userId);
        videoService.addVideos(video);

        //在es中添加数据
        System.out.println(" //在es中添加数据");
        elasticSearchService.addVideo(video);

     return jsonResponse.success();
    }

    //查询es 中的数据
    @GetMapping("/es-videos")
    public jsonResponse<List<Video>> getEsVideo(@RequestParam String keyWord){
        List<Video> list = elasticSearchService.getVideo(keyWord);
        return new jsonResponse<>(list);
    }

    //视频投稿-功能
    @PostMapping("/addUserInfo")
    public jsonResponse<String> addUserInfo(@RequestParam Long id,@RequestParam String nick){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setNick(nick);
        elasticSearchService.addUserInfo(userInfo);
        return jsonResponse.success();
    }

    //查询es 中的数据  ---全文搜索  --- 查到的类型可能是不同的 所以使用 map
    @GetMapping("/contents")
    public jsonResponse<List<Map<String, Object>>> getContents(@RequestParam String keyWord,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws Exception {
        List<Map<String, Object>> contents = elasticSearchService.getContents(keyWord, pageNo, pageSize);
        return new jsonResponse<>(contents);
    }


    @GetMapping("/danmusvideoId=31")
    public jsonResponse<List<Video>> findAllVideos(){
        List<Video> allVideos = elasticSearchService.findAllVideos();
        return new jsonResponse<>(allVideos);
    }

//    @GetMapping("/danmus")
//    public jsonResponse<List<Video>> danmus(@RequestParam Long videoId){
//
//        return new jsonResponse<>();
//    }

    //推荐功能
    @GetMapping("/recommendations")
    public jsonResponse<List<Video>> recommendations() throws TasteException {
        Long userId = userSupport.getCurrentUserID();
        List<Video> res =  videoService.recommend(userId);
        return new jsonResponse<>(res);
    }


    //分页查询
    @GetMapping("/videos")
    public jsonResponse<PageResult<Video>> pageListVideos(Integer size,Integer no,String area){
        PageResult<Video> result = videoService.pageListVideos(size,no,area);
        return new jsonResponse<>(result);
    }


    //用户点赞功能

    @PostMapping("/video-likes")
    public jsonResponse<String> addVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserID();
        videoService.addVideoLike(videoId,userId);
        return jsonResponse.success();
    }

    //取消点赞
    @DeleteMapping("/video-like")
    public jsonResponse<String> deleteVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserID();
        videoService.deleteVideoLike(videoId,userId);
        return jsonResponse.success();
    }

    //查询视频点赞数量
    @GetMapping("/video-likes")
    public jsonResponse<Map<String,Object>> getVideoLike(@RequestParam Long videoId){

        Long userId = null;//如果获取token不到的话就是访客 访客也可以看 点赞数量
        try {
            userId=userSupport.getCurrentUserID();
        }catch (Exception e){
        }
        Map<String,Object> res = videoService.getVideoLike(videoId,userId);
        return new jsonResponse<>(res);
    }


    //用户收藏功能

    @PostMapping("/video-collections")
    public jsonResponse<String> addVideoCollections(@RequestBody VideoCollection videoCollection){
        Long userId = userSupport.getCurrentUserID();
        videoService.addVideoCollections(videoCollection,userId);
        return jsonResponse.success();
    }

    //取消收藏
    @DeleteMapping("/video-collections")
    public jsonResponse<String> deleteVideoCollections(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserID();
        videoService.deleteVideoCollections(videoId,userId);
        return jsonResponse.success();
    }

    //查询视频收藏数量
    @GetMapping("/video-collections")
    public jsonResponse<Map<String,Object>> getVideoCollections(@RequestParam Long videoId){

        Long userId = null;//如果获取token不到的话就是访客 访客也可以看 点赞数量
        try {
            userId=userSupport.getCurrentUserID();
        }catch (Exception e){
        }
        Map<String,Object> res = videoService.getVideoCollections(videoId,userId);
        return new jsonResponse<>(res);
    }


    //用户投币功能

    @PostMapping("/video-coins")
    public jsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin){
        Long userId = userSupport.getCurrentUserID();
        videoService.addVideoCoins(videoCoin,userId);
        return jsonResponse.success();
    }


    //查询视频收藏数量
    @GetMapping("/videos-coins")
    public jsonResponse<Map<String,Object>> getVideoCoins(@RequestParam Long videoId){

        Long userId = null;//如果获取token不到的话就是访客 访客也可以看 点赞数量
        try {
            userId=userSupport.getCurrentUserID();
        }catch (Exception e){
        }
        Map<String,Object> res = videoService.getVideoCoins(videoId,userId);
        return new jsonResponse<>(res);
    }




    //视频评论-功能--只实现了二级评论--如果要实现多级树形的评论则需要闭包表
    @PostMapping("/video-comments")
    public jsonResponse<String> addVideosComments(@RequestBody VideoComment videoComment){
        Long userId = userSupport.getCurrentUserID();
        videoService.addVideosComments(videoComment,userId);
        return jsonResponse.success();
    }

    //分页查询视频-评论-只实现了二级评论
    @GetMapping("/video-comments")
    public jsonResponse<PageResult<VideoComment>> pageListVideosComments(@RequestParam Integer size,@RequestParam Integer no ,@RequestParam Long videoId){
        PageResult<VideoComment> result = videoService.pageListVideosComments(size,no,videoId);
        return new jsonResponse<>(result);
    }



    //视频详情
    @GetMapping("/video-details")
    public jsonResponse<Map<String,Object>> getVideoDetails(@RequestParam Long videoId){
        Map<String,Object> res = videoService.getVideoDetails(videoId);
        return new jsonResponse<>(res);
    }




}
