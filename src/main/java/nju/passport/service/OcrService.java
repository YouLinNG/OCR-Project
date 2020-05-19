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
import nju.passport.config.UploadConfig;
import nju.passport.model.CutPhoto;
import nju.passport.model.Photo;
import org.apache.pdfbox.jbig2.Bitmap;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }


    public static BufferedImage sharpen(BufferedImage image) {
        float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0, 0f };
        BufferedImage bimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        cop.filter(image, bimg);
        return bimg;
    }

    public String[] getResult(String path) {

        ITesseract tesseract = new Tesseract();
        File file = new File(path);
//        String lagnguagePath = "D:\\Tesseract-OCR\\tessdata";
//
//        //System.out.println(System.getenv("TESSDATA_PREFIX"));
//
//        //tesseract.setDatapath(System.getenv("TESSDATA_PREFIX"));
//        System.out.println(file.getPath());
//        System.out.println(file.getName());
//        tesseract.setDatapath(lagnguagePath);
        tesseract.setLanguage("passport");


        try {

            long startTime = System.currentTimeMillis();

            BufferedImage bi = ImageIO.read(file);
            ImageDeskew id = new ImageDeskew(bi);


//            BufferedImage textImage = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(bi, 0, 0, bi.getWidth(), bi.getHeight()));





            bi = ImageHelper.convertImageToGrayscale(bi);

            bi = sharpen(bi);
//            bi = ImageHelper.convertImageToBinary(bi);

            bi = ImageHelper.getScaledInstance(bi, bi.getWidth() * 2, bi.getHeight() * 1);

            double imageSkewAngle = id.getSkewAngle(); // determine skew angle

            if ((imageSkewAngle > 0.05 || imageSkewAngle < -(0.05))) {
//                textImage = ImageHelper.rotateImage(textImage, -imageSkewAngle); // deskew image
            }

//            int width = bi.getWidth();
//            int height = bi.getHeight();
//
//            BufferedImage grayBufferedImage = new BufferedImage(width, height, bi.getType());
//            for (int i = 0; i < bi.getWidth(); i++) {
//                for (int j = 0; j < bi.getHeight(); j++) {
//                    final int color = bi.getRGB(i, j);
//                    final int r = (color >> 16) & 0xff;
//                    final int g = (color >> 8) & 0xff;
//                    final int b = color & 0xff;
//                    int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
//                    int newPixel = colorToRGB(255, gray, gray, gray);
//                    grayBufferedImage.setRGB(i, j, newPixel);
//                }
//            }

            String result = tesseract.doOCR(bi);
            System.out.println(result);
            long endTime = System.currentTimeMillis();
            System.out.println("Time is：" + (endTime - startTime) + " 毫秒");

            String[] last = extractLastLine(result);

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

    public static String extractNameLine(String input) {
        String[] strs = input.split("\n");
        int index = 0;
        for(int i=0;i<strs.length;i++){

            if(strs[i].contains("<<<<<<")){
                index = i;
            }
        }

        return strs[index];
    }

    public static String[] extractLastLine(String input) {
        String [] res = new String[2];
        String[] strs = input.split("\n");
        int index = 0;
        for(int i=0;i<strs.length;i++){

            if(strs[i].contains("<<<<<<")){
                index = i;
            }
        }
        res [0] = strs[index];
        res [1] = strs[index+1];

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

            if(ocr[1].contains("F")||ocr[1].contains("P")){
                photo.setSex("F");
            }else{
                photo.setSex("M");
            }

            System.out.println(ocr[0]);
            System.out.println(ocr[1]);
            String passnum = ocr[1].substring(0,8);

            photo.setPassnum(passnum);

            String nameline = ocr[0];
            String[] namesplit = nameline.split("<<");
            String xing = namesplit[0].substring(4);
            String ming = namesplit[1];

            photo.setName(ming+"/"+xing);

            String birthdate = ocr[1].substring(13,18);
            photo.setBirth(birthdate);


            res.add(photo);
        }


        return res;
    }
}

