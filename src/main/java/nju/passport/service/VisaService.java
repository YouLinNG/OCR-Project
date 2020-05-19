package nju.passport.service;

import nju.passport.config.UploadConfig;
import nju.passport.dao.FileDao;
import nju.passport.dao.PhotoDao;
import nju.passport.model.Photo;
import nju.passport.model.Visa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/19
 * Time:13:30
 * Description：
 */
@Service
public class VisaService {

    @Autowired
    private FileDao fileService;
    @Autowired
    private OcrService ocrService;
    @Autowired
    private PhotoDao photoDao;

    public List<Visa> getVisaResult(List<String> names){
        List<Visa> res = new ArrayList<>();

        List<Photo> allPhotos = photoDao.findAll();

        for(String name : names){

            String path = UploadConfig.path + name ;
            String[] ocr = ocrService.getResult(path);
            Visa visa = new Visa();

            if(ocr[1].contains(name)){
                visa.setExist(true);
            }else{
                visa.setExist(false);
            }

            res.add(visa);

        }

        return res;
    }



}
