package nju.passport.service;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Base64;
import com.recognition.software.jdeskew.ImageDeskew;
import io.netty.handler.codec.base64.Base64Encoder;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import nju.passport.ImageViewer;
import nju.passport.PassnumUtils;
import nju.passport.SharpenUtils;
import nju.passport.WordUtils;
import nju.passport.config.UploadConfig;
import nju.passport.dao.PhotoDao;
import nju.passport.model.CutPhoto;
import nju.passport.model.Photo;
import nju.passport.model.Visa;
import org.apache.pdfbox.jbig2.Bitmap;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Time:21:43
 * Description：
 */
@Service
public class OcrService {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    @Autowired
    private PhotoDao photoDao;

    public String[] getResult(String path) {

        ITesseract tesseract = new Tesseract();
        File file = new File(path);
        String lagnguagePath = "D:\\Tesseract-OCR\\tessdata";
//
//        //System.out.println(System.getenv("TESSDATA_PREFIX"));
//
//        tesseract.setDatapath(System.getenv("TESSDATA_PREFIX"));
//        System.out.println(file.getPath());
//        System.out.println(file.getName());
        tesseract.setDatapath(lagnguagePath);
        tesseract.setLanguage("passport");


        try {

            long startTime = System.currentTimeMillis();

            BufferedImage bi = ImageIO.read(file);
            ImageDeskew id = new ImageDeskew(bi);


//            BufferedImage textImage = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(bi, 0, 0, bi.getWidth(), bi.getHeight()));





            bi = ImageHelper.convertImageToGrayscale(bi);
//            bi = SharpenUtils.sharpen(bi);
//            String tmp = "D:\\outpng.png";
//            File out12 = new File(tmp);
//            ImageIO.write(bi,"png",out12);

//            bi = ImageHelper.convertImageToBinary(bi);

            bi = ImageHelper.getScaledInstance(bi, bi.getWidth() * 2, bi.getHeight() * 1);

            double imageSkewAngle = id.getSkewAngle(); // determine skew angle

            if ((imageSkewAngle > 0.05 || imageSkewAngle < -(0.05))) {
//                textImage = ImageHelper.rotateImage(textImage, -imageSkewAngle); // deskew image
            }

            String result = tesseract.doOCR(bi);
            System.out.println(result);

            String[] last = extractLastLine(result);
            return last;

        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (
                IOException io) {
            io.printStackTrace();
        }



//        System.out.println(ocrService.getResult(path)+"???????");

        return null;
    }


    public static String[] extractLastLine(String input) {
        String [] res = new String[2];
        String[] strs = input.split("\n");
        int index = 0;
        for(int i=0;i<strs.length;i++){
            if(strs[i].contains("<<<<<<") && strs[i].length() > 6){
                index = i;
                break;
            }
        }
        res [0] = strs[index];
        res [0] = res [0].replaceAll(" ", "");
        System.err.println(res[0]);
        if(strs[index+1].length() >8) {
            res [1] = strs[index+1];
        }
        else res [1] = strs[index+2];
        res [1] = res [1].replaceAll(" ", "");
        System.err.println(res[1]);


        return res;
    }

    public static void imageCut(String imagePath,String outFile, int posX,int posY,int width,int height ) {
        //原始图像
        Mat image = Imgcodecs.imread(imagePath);
        //截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(posX, posY, width, height);
        //两句效果一样
        Mat sub = image.submat(rect);   //Mat sub = new Mat(image,rect);
        Mat mat = new Mat();
        Size size = new Size(300, 300);
        Imgproc.resize(sub, mat, size);//将人脸进行截图并保存
        String[] paths = imagePath.split("/");
        String name = paths[2];

        Imgcodecs.imwrite(UploadConfig.path + "HEAD_"+ name , mat);
//        ImageViewer imageViewer = new ImageViewer(mat, "照片");
//        imageViewer.imshow();
//        Imgcodecs.imwrite(outFile, mat);
//        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }

    public  String imageToBase64ByLocal(String path)  {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            //获取图片路径
            String[] paths = path.split("/");
            String name = paths[2];

            File file = new File(UploadConfig.path + "HEAD_"+ name);
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

    public List<CutPhoto> getCutPhoto(List<String> names){

        List<CutPhoto> res = new ArrayList<>();
        for(String name : names) {
            String path = UploadConfig.path + name ;

            String[] ocr = getResult(path);

            String data = imageToBase64ByLocal(path);

            CutPhoto cutPhoto = new CutPhoto();
            cutPhoto.setPassnum(ocr[1].substring(0, 8));
            cutPhoto.setBase64(data);
            res.add(cutPhoto);
        }
        return res;
    }


    public List<Photo> getOcrResult(List<String> names) {
        List<Photo> res = new ArrayList<>();

        for(String name : names){

            String path = UploadConfig.path + name ;

            String[] ocr = getResult(path);

            Photo photo = new Photo();

            if(ocr[1].length() >= 21) {
                if (ocr[1].charAt(20) == 'F' || ocr[1].charAt(20) == 'P') {
                    photo.setSex("F");
                } else {
                    photo.setSex("M");
                }
            }
            else{
                photo.setSex(null);
            }
            String passnum = null;
            String birthdate = null;
            if(ocr[1].length()>= 9) {
                if(ocr[1].contains("CHN")){
                    String[] numAndBirth = ocr[1].split("CHN");
                    if(numAndBirth[0].length()>=10) {
                        passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10, numAndBirth[0].length() - 1);
                    }
                    else passnum = numAndBirth[0];
                    birthdate = numAndBirth[1].substring(0,6);
                    photo.setBirth(birthdate);

                }
                else if(ocr[1].contains("0HN")){
                    String[] numAndBirth = ocr[1].split("0HN");
                    passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10,numAndBirth[0].length() - 1);
                    birthdate = numAndBirth[1].substring(0,6);
                    photo.setBirth(birthdate);

                }
               else {
                    passnum = ocr[1].substring(0, 9);
                    if(ocr[1].length()>=19) {
                        birthdate = ocr[1].substring(13, 19);
                        photo.setBirth(birthdate);
                    }
                    else photo.setBirth(null);
                }

                if (PassnumUtils.judgePassport(passnum))
                    photo.setPassnum(passnum);
                else photo.setPassnum(passnum + "(wrong)");
            }
            else photo.setPassnum(null);

            String nameline = ocr[0];
            String[] namesplit = nameline.split("<<");
            String xing=null;
            String ming = null;
            if(namesplit.length>1) {
                if(namesplit[0].contains("CHN")){
                    String[] namesplit2 = namesplit[0].split("CHN");
                    xing = namesplit2[1];
                    ming = namesplit[1];
                }
                else {
                    xing = namesplit[0].substring(5);
                    ming = namesplit[1];
                }
                if(WordUtils.isPYQPWord(xing+ming))
                    photo.setName(ming+"/"+xing);
                else photo.setName(ming+"/"+xing+"(wrong)");
            }
            else {
                photo.setName(null);
            }

            Photo photo1 = new Photo();
            photo1.setBirth(photo.getBirth());
            photo1.setName(photo.getName());
            photo1.setPassnum(photo.getPassnum());
            photo1.setSex(photo.getSex());
            photo1.setInsertDate(new Date());
            photoDao.save(photo1);



            res.add(photo);
        }


        return res;
    }


    public List<Visa> getVisaOcrResult(List<String> names) {
        List<Visa> res = new ArrayList<>();

        for (String name : names) {

            String path = UploadConfig.path + name;

            String[] ocr = getResult(path);

            Visa photo = new Visa();

            if (ocr[1].length() >= 21) {
                if (ocr[1].charAt(20) == 'F' || ocr[1].charAt(20) == 'P') {
                    photo.setSex("F");
                } else {
                    photo.setSex("M");
                }
            } else {
                photo.setSex(null);
            }
            String passnum = null;
            String birthdate = null;
            if (ocr[1].length() >= 9) {
                if (ocr[1].contains("CHN")) {
                    String[] numAndBirth = ocr[1].split("CHN");
                    if (numAndBirth[0].length() >= 10) {
                        passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10, numAndBirth[0].length() - 1);
                    } else passnum = numAndBirth[0];
                    birthdate = numAndBirth[1].substring(0, 6);
                    photo.setBirth(birthdate);

                } else if (ocr[1].contains("0HN")) {
                    String[] numAndBirth = ocr[1].split("0HN");
                    passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10, numAndBirth[0].length() - 1);
                    birthdate = numAndBirth[1].substring(0, 6);
                    photo.setBirth(birthdate);

                } else {
                    passnum = ocr[1].substring(0, 9);
                    if (ocr[1].length() >= 19) {
                        birthdate = ocr[1].substring(13, 19);
                        photo.setBirth(birthdate);
                    } else photo.setBirth(null);
                }

                if (PassnumUtils.judgePassport(passnum))
                    photo.setPassnum(passnum);
                else photo.setPassnum(passnum + "(wrong)");
            } else photo.setPassnum(null);

            String nameline = ocr[0];
            String[] namesplit = nameline.split("<<");
            String xing = null;
            String ming = null;
            if (namesplit.length > 1) {
                if (namesplit[0].contains("CHN")) {
                    String[] namesplit2 = namesplit[0].split("CHN");
                    xing = namesplit2[1];
                    ming = namesplit[1];
                } else {
                    xing = namesplit[0].substring(5);
                    ming = namesplit[1];
                }
                if (WordUtils.isPYQPWord(xing + ming))
                    photo.setName(ming + "/" + xing);
                else photo.setName(ming + "/" + xing + "(wrong)");
            } else {
                photo.setName(null);
            }

            Visa photo1 = new Visa();
            photo1.setBirth(photo.getBirth());
            photo1.setName(photo.getName());
            photo1.setPassnum(photo.getPassnum());
            photo1.setSex(photo.getSex());


            res.add(photo);
        }


        return res;
    }
}

