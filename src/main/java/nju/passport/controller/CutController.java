package nju.passport.controller;

import nju.passport.model.CutPhoto;
import nju.passport.model.Photo;
import nju.passport.service.FileService;
import nju.passport.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/5
 * Time:23:15
 * Description：
 */

@RestController
@RequestMapping("/Cut")
@CrossOrigin
public class CutController {

    @Autowired
    private FileService fileService;
    @Autowired
    private OcrService ocrService;

    @RequestMapping(value = "/Photo")

    public List<CutPhoto> getResult(@RequestParam(value = "file") List<String> names) throws IOException {


        List<CutPhoto> cutPhoto = ocrService.getCutPhoto(names);



        return cutPhoto;

    }
}
