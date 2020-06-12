package nju.passport;

import net.sourceforge.tess4j.util.ImageHelper;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisaUtilss {
    static {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String reg = ".*\\d{2}/\\d{2}/\\d{4}.*";
        Pattern p=Pattern.compile(reg);
        Matcher m=p.matcher("a77 01/01/1990 78");
        if(m.matches()) System.out.println("success");
    }
//        File file = new File("D:\\train\\visa\\11.jpg");
//        try {
//            BufferedImage bi = ImageIO.read(file);
//            bi = ImageHelper.convertImageToGrayscale(bi);
//            File outputfile = new File("D:\\train\\visa\\111.jpg");
//            ImageIO.write(bi, "jpg", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Mat srcImgMat  = Imgcodecs.imread("D:\\train\\visa\\11.jpg");
//        Mat desImgMat = new Mat(srcImgMat.size(),srcImgMat.type());
//        Imgproc.cvtColor(srcImgMat, desImgMat, Imgproc.COLOR_RGB2HSV);
//
//        //白色HSV阈值范围
//        Scalar lowerbScalar = new Scalar(0,0,181); //hsv色域的蓝色最低阈值
//        Scalar highbScalar = new Scalar(180, 15, 255); //hsv色域蓝色最高阈值
//        Core.inRange(desImgMat, lowerbScalar, highbScalar, desImgMat);
//
//        ImageViewer imageViewer = new ImageViewer(desImgMat, "照片");
//        imageViewer.imshow();
//
//        List<MatOfPoint> pointsVector = new Vector<MatOfPoint>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(desImgMat, pointsVector, hierarchy, 		 Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);
//        Mat img= new Mat(desImgMat.size(),desImgMat.type());
//        double area = Imgproc.boundingRect(pointsVector.get(0)).area();
//        int index = 0;
//
//        // 找出匹配到的最大轮廓
//        for (int i = 0; i < pointsVector.size(); i++) {
//            double tempArea = Imgproc.boundingRect(pointsVector.get(i)).area();
//            System.out.println("i:"+i+"    "+tempArea);
//            if (tempArea > area) {
//                area = tempArea;
//                index = i;
//            }
//        }
//        MatOfPoint cntMatOfPoint = pointsVector.get(index);
//        Rect rect = Imgproc.boundingRect(cntMatOfPoint);
//        int y = rect.y;
//
//
//        Imgproc.drawContours(img,pointsVector,index,new Scalar(255,0,0));
//        imageViewer = new ImageViewer(img, "轮廓");
//        imageViewer.imshow();
//        Imgproc.rectangle(srcImgMat, new Point(0,0), new Point(srcImgMat.width(), y) , new Scalar(0,0,255));;
//
//        imageCut("D:\\train\\visa\\11.jpg","D:\\train\\visa\\111.jpg",0,0,srcImgMat.width(),y);
//
//
//
//    }
//    public static void imageCut(String imagePath,String outFile, int posX,int posY,int width,int height ) {
//        //原始图像
//        Mat image = Imgcodecs.imread(imagePath);
//        //截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
//        Rect rect = new Rect(posX, posY, width, height);
//        //两句效果一样
//        Mat sub = image.submat(rect);   //Mat sub = new Mat(image,rect);
//        Mat mat = new Mat();
//        Size size = new Size(width, height);
//        Imgproc.resize(sub, mat, size);//将人脸进行截图并保存
//
//
//        ImageViewer imageViewer = new ImageViewer(mat, "照片");
//        imageViewer.imshow();
//        Imgcodecs.imwrite(outFile, mat);
////        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
//    }
}
