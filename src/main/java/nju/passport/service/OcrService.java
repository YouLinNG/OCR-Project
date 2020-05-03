package nju.passport.service;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import nju.passport.ImageViewer;
import nju.passport.config.UploadConfig;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public String getResult(String path) {

        ITesseract tesseract = new Tesseract();
        File file = new File(path);
        String lagnguagePath = "D:\\Tesseract-OCR\\tessdata";

        //System.out.println(System.getenv("TESSDATA_PREFIX"));

        //tesseract.setDatapath(System.getenv("TESSDATA_PREFIX"));
        System.out.println(file.getPath());
        System.out.println(file.getName());
        tesseract.setDatapath(lagnguagePath);
        tesseract.setLanguage("passport");


        try {

            long startTime = System.currentTimeMillis();

            BufferedImage bi = ImageIO.read(file);
            ImageDeskew id = new ImageDeskew(bi);


//            BufferedImage textImage = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(bi, 0, 0, bi.getWidth(), bi.getHeight()));

            bi = ImageHelper.convertImageToGrayscale(bi);
//            bi = ImageHelper.convertImageToBinary(bi);

            bi = ImageHelper.getScaledInstance(bi, bi.getWidth() * 2, bi.getHeight() * 1);

            double imageSkewAngle = id.getSkewAngle(); // determine skew angle
            System.out.println(imageSkewAngle + "????");
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

            String last = extractLastLine(result);
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

        return "";
    }

    public static String extractLastLine(String input) {
        String[] strs = input.split("\n");
        int lastIndexOf = input.lastIndexOf('\n');
        if (lastIndexOf == -1 || lastIndexOf + 1 > input.length())
            return "";
        return strs[strs.length-1];
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
        Imgcodecs.imwrite(imagePath+"Photo.jpg", mat);
//        ImageViewer imageViewer = new ImageViewer(mat, "照片");
//        imageViewer.imshow();
//        Imgcodecs.imwrite(outFile, mat);
//        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }
}

