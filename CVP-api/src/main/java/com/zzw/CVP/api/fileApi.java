package com.zzw.bilibili.api;

import com.zzw.domain.jsonResponse;
import com.zzw.service.fileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
public class fileApi {
    @Autowired
    private fileService fileService;

    @PostMapping("/md5files")
    public jsonResponse<String> getFileMD5(MultipartFile file) throws Exception {
      String fileMD5 =  fileService.getFileMD5(file);
        return new jsonResponse<>(fileMD5);
    }
//d7a23554b2fca7e2cc8b8e6c128562eb

    @PutMapping("/file-slices")
    public jsonResponse<String> uploadFileBySlices(MultipartFile slice,
                                                   String fileMD5,
                                                   Integer sliceNo,
                                                   Integer totalSliceNo) throws Exception{

      String filePath = fileService.uploadFileBySlices(slice,fileMD5,sliceNo,totalSliceNo);

      return new jsonResponse<>(filePath);
    }

}
