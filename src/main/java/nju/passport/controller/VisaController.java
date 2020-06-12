package nju.passport.controller;

import nju.passport.model.Photo;
import nju.passport.model.Visa;
import nju.passport.service.FileService;
import nju.passport.service.OcrService;
import nju.passport.service.VisaService;
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
 * Date:2020/5/19
 * Time:13:20
 * Description：
 */
@RestController
@RequestMapping("/Visa")
@CrossOrigin
public class VisaController {

    @Autowired
    private FileService fileService;
    @Autowired
    private OcrService ocrService;
    @Autowired
    private VisaService visaService;

    @RequestMapping(value = "/")
    public void uploadVisa(String md5,
                                  MultipartFile file) throws IOException {


        fileService.upload(file.getOriginalFilename(), md5,file);


    }

    @RequestMapping(value = "/Name")

    public List<Visa> getResult(@RequestParam(value = "file") List<String> names) throws IOException {


        List<Visa> res = visaService.getVisaResult(names);



        return res;
    }

    @RequestMapping(value = "/Unmatched")

    public List<String> getUnmatched() throws IOException {


        List<String> res = visaService.getUnmatchedPhotos();



        return res;
    }
}
