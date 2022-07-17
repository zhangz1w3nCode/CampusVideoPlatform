package com.zzw.service;

import com.mysql.cj.util.StringUtils;
import com.zzw.Entity.File;
import com.zzw.dao.fileDao;
import com.zzw.service.utils.MD5Util;
import com.zzw.service.utils.fastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class fileService {

    @Autowired
    private fileDao fileDao;

    @Autowired
    private fastDFSUtil fastDFSUtil;

    //加了秒传
    public String uploadFileBySlices(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        //传过该文件
//        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
//        if(dbFileMD5!=null) return dbFileMD5.getUrl();
        //没传过该文件
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
//        if(!StringUtils.isNullOrEmpty(url)){
//            dbFileMD5 = new File();
//            dbFileMD5.setCreateTime(new Date());
//            dbFileMD5.setMd5(fileMD5);
//            dbFileMD5.setUrl(url);
//            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
//            fileDao.addFile(dbFileMD5);
//        }
        return url;
    }

    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }
}
