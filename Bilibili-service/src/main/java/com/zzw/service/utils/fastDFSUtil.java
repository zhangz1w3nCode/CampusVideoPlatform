package com.zzw.service.utils;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.cj.util.StringUtils;
import com.zzw.exception.conditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Component
public class fastDFSUtil {

    @Autowired
    //里面有upload方法(上传普通文件 不适合大文件)
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    //支持断点续传功能的类
    private AppendFileStorageClient appendFileStorageClient;// modify

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //常量
    public static final String DEFAULT_GROUP = "group1";
    public static final String PATH_KEY = "path-key";
    public static final String UPLOADED_Size_Key = "uploaded-size-Key";
    public static final String UPLOADED_NO_Key = "uploaded-no-Key";
    public static final int SLICE_SIZE = 1024;





    //获取文件的类型方法
    public String getFileType(MultipartFile file){ //比file文件功能更方便

        if(file==null) throw new conditionException("非法文件类型!");

        String filename = file.getOriginalFilename();

        int lastIndex = filename.lastIndexOf(".");

        //获取文件类型
        String fileType =  filename.substring(lastIndex+1);

        return fileType;

    }


    //上传一般文件
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet();
        String fileType = getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath(); //上传成功后返回的路径
    }

    //上传断点续传的文件
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String fileType = getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    //后续文件的添加
    public void modifyAppenderFile(MultipartFile file,String filePath,long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,file.getInputStream(),file.getSize(),offset);
    }


    //分片上传文件
    public String uploadFileBySlices(MultipartFile file,String fileMD5,Integer sliceNo,Integer totalSliceNo) throws Exception {
        if(file==null||sliceNo==null||totalSliceNo==null) throw new conditionException("参数异常");

        String pathKey = PATH_KEY+fileMD5;
        String uploadedSizeKey = UPLOADED_Size_Key+fileMD5;
        String uploadedNoKey = UPLOADED_NO_Key+fileMD5;

        Long uploadedSize=0L;
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);

        if(!StringUtils.isNullOrEmpty(uploadedSizeStr)){
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }

        String fileType = this.getFileType(file);

        if(sliceNo==1){ //上传的是第一个分片 直接上传

            String path = this.uploadAppenderFile(file);

            if(StringUtils.isNullOrEmpty(path)) throw new conditionException("上传分片异常");
            redisTemplate.opsForValue().set(pathKey,path);//存入redis
            redisTemplate.opsForValue().set(uploadedNoKey,"1");//存入redis
        }else{//上传的是不是第一个分片
            String filePath = redisTemplate.opsForValue().get(pathKey);

            if(StringUtils.isNullOrEmpty(filePath))throw new conditionException("上传失败");

            this.modifyAppenderFile(file,filePath,uploadedSize);

            redisTemplate.opsForValue().increment(uploadedNoKey);

        }
        //当前文件大小 =  自增 传入的文件的大小
        uploadedSize+=file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey,String.valueOf(uploadedSize));//存入redis

        //如果所有分片上传成功 则清空redis

        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);

        Integer uploadedNo = Integer.parseInt(uploadedNoStr);

        String resPathKey ="";//获取当前文件路径

        if(uploadedNo.equals(totalSliceNo)){ //分片完成 清空redis的键值对
            resPathKey = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(pathKey, uploadedSizeKey, uploadedNoKey);
            redisTemplate.delete(keyList);
        }

        return  resPathKey;

    }


    //把文件转换成多个分片
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        String filename = multipartFile.getOriginalFilename();
        String fileType = this.getFileType(multipartFile);
        File file = this.MultipartFileToFile(multipartFile);
        long length = file.length();
        int count=1;
        for(int i=0;i<length;i+=SLICE_SIZE){
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r"); //支持随机访问的读写 - 任意分片
            randomAccessFile.seek(i);
            //读数据
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);//实际上读了到了多少

            String path = "D:\\temp\\"+count+"."+fileType;//*****
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes,0,len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        file.delete();
    }

    //把MultipartFile类型文件 转换成 File类型文件
    public File MultipartFileToFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File fileN = File.createTempFile(fileName[0],"."+fileName[1]);
        file.transferTo(fileN);
        return fileN;
    }


    //下载

    //删除一般文件
    public void deleteFile(String filePath) throws Exception {
        fastFileStorageClient.deleteFile(filePath);
    }
}
