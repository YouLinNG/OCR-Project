package nju.passport.service;

import nju.passport.PassnumUtils;
import nju.passport.config.UploadConfig;
import nju.passport.model.CutPhoto;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CutService {
    @Autowired
    private OcrService ocrService;

    public void identify(String path){
        File file = new File(path);
        int[] RectPosition = new int[4];
        RectPosition[0] = Integer.MAX_VALUE;

            CascadeClassifier faceDetector = new CascadeClassifier("lbpcascade_frontalface.xml");

        Mat mat = Imgcodecs.imread(path);
//            ImageViewer imageViewer = new ImageViewer(mat, "护照");
//            imageViewer.imshow();
//        RotateHelper.correct(mat);
        MatOfRect faceDetections = new MatOfRect();

        //指定人脸识别的最大和最小像素范围

        Size minSize = new Size(30, 30);

        Size maxSize = new Size(1850, 1850);

//参数设置为scaleFactor=1.1f, minNeighbors=4, flags=0 以此来增加识别人脸的正确率

        faceDetector.detectMultiScale(mat, faceDetections, 1.1f, 4, 0, minSize, maxSize);

        //对识别出来的头像画个方框，并且返回这个方框的位置坐标和大小

        for (Rect rect : faceDetections.toArray()) {

            Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x

                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));

            if(rect.x <RectPosition[0]) {
                RectPosition[0]=rect.x;

                RectPosition[1]=rect.y;

                RectPosition[2]=rect.width;

                RectPosition[3]=rect.height;
            }


            System.out.println(RectPosition[0] +" "+ RectPosition[1] + " "+RectPosition[2]+" "+RectPosition[3]);

        }

        imageCut(path,"",RectPosition[0],RectPosition[1],RectPosition[2],RectPosition[3]);
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

    private String imageToBase64ByLocal(String path)  {
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
            identify(path);

            String[] ocr = ocrService.getResult(path);

            String data = imageToBase64ByLocal(path);

            CutPhoto cutPhoto = new CutPhoto();
            String passnum = null;
            if(ocr[1].length()>= 9) {
                if(ocr[1].contains("CHN")){
                    String[] numAndBirth = ocr[1].split("CHN");
                    if(numAndBirth[0].length()>=10) {
                        passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10, numAndBirth[0].length() - 1);
                    }
                    else passnum = numAndBirth[0];
                }
                else if(ocr[1].contains("0HN")){
                    String[] numAndBirth = ocr[1].split("0HN");
                    passnum = numAndBirth[0].substring(numAndBirth[0].length() - 10,numAndBirth[0].length() - 1);
                }
                else {
                    passnum = ocr[1].substring(0, 9);

                }
                cutPhoto.setPassnum(passnum);

            }
            else cutPhoto.setPassnum("无法识别");
            cutPhoto.setBase64(data);
            res.add(cutPhoto);
        }
        return res;
    }
}
