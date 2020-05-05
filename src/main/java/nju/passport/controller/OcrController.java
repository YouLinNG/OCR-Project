package nju.passport.controller;

import nju.passport.model.Photo;
import nju.passport.service.FileService;
import nju.passport.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/4/13
 * Time:22:19
 * Description：
 */
@RestController
@RequestMapping("/Ocr")
@CrossOrigin

public class OcrController {

    @Autowired
    private FileService fileService;
    @Autowired
    private OcrService ocrService;


    @RequestMapping(value = "/Name")

    public List<Photo> getResult(@RequestParam(value = "file") List<String> names) throws IOException {


        List<Photo> res = ocrService.getOcrResult(names);


//        System.out.println(uploadFile.length);
//        for (MultipartFile multipartFile:uploadFile) {
//            System.out.println("文件"+multipartFile.getOriginalFilename());
//        }


        return res;

    }
}
