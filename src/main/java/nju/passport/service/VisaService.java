package nju.passport.service;

import nju.passport.config.UploadConfig;
import nju.passport.dao.FileDao;
import nju.passport.dao.PhotoDao;
import nju.passport.dao.VisaDao;
import nju.passport.model.Photo;
import nju.passport.model.Visa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private VisaDao visaDao;
//    @Autowired
//    private VisaDao visaDao;

    public List<Visa> getVisaResult(List<String> names){
        List<Visa> res = new ArrayList<>();

        List<Photo> allPhotos = photoDao.findAll();

        for(String name : names) {
            String path = UploadConfig.path + name;

            Visa visa = new Visa();
            visa.setExist(false);
            visa.setBirthExist(false);
            visa.setNameExist(false);
            visa.setImageName(path);

            String ocr = ocrService.getVisaResult(path);

            String[] strs = ocr.split("\n");
            int index = -1;//生日所在行的index
            for (int i = strs.length - 1; i >= 0; i--) {
                strs[i] = strs[i].replaceAll(" ", "");
                String reg = ".*\\d{2}/\\d{2}/\\d{4}.*";
                Pattern p = Pattern.compile(reg);
                Matcher m = p.matcher(strs[i]);
                if (m.matches()){
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                if (strs[index - 1].length() >= 6) {
                    strs[index - 1] = strs[index - 1].replaceAll(" ", "");
                    for (int i = 0; i < allPhotos.size(); i++) {
                        if (strs[index].contains(allPhotos.get(i).getBirth())) {
                            visa.setBirthExist(true);
                            if (strs[index - 1].contains(allPhotos.get(i).getName())) {
                                visa.setNameExist(true);
                                visa.setExist(true);
                                break;
                            }
                        }
                    }
                } else {
                    strs[index - 2] = strs[index - 2].replaceAll(" ", "");
                    for (int i = 0; i < allPhotos.size(); i++) {
                        if (strs[index].contains(allPhotos.get(i).getBirth())) {
                            visa.setBirthExist(true);
                            if (strs[index - 2].contains(allPhotos.get(i).getName())) {
                                visa.setNameExist(true);
                                visa.setExist(true);
                                break;
                            }
                        }
                    }
                }


            }
            List<Visa> visaList = visaDao.findAll();
            for(int i =0; i <visaList.size(); i ++) {
                if(visa.getImageName().equals(visaList.get(i).getImageName())) {
                    visaDao.delete(visaList.get(i));
                    break;
                }
            }
            visaDao.save(visa);
            res.add(visa);
        }

        return res;
    }

    public List<String> getUnmatchedPhotos(){
        List<String> result = new ArrayList<>();
        List<Visa> visaList = visaDao.findAll();
        for(int i = 0; i <visaList.size(); i ++) {
            if(!visaList.get(i).isExist()) result.add(imageToBase64ByLocal(visaList.get(i).getImageName()));
        }
        return result;
    }

    private String imageToBase64ByLocal(String path)  {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            File file = new File(path);
            in = new FileInputStream(file.getPath());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(data);

        return encode;
    }


}
