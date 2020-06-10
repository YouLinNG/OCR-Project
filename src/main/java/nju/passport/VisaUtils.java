package nju.passport;

import nju.passport.config.UploadConfig;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

public class VisaUtils {
    static {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        canny();
    }

    public static void canny() {
        Mat src = Imgcodecs.imread("D:\\pass4.jpg");
        Mat dst= new Mat();
        Mat gray= new Mat();
        Mat image= new Mat();


        //1 高斯降噪
        Imgproc.GaussianBlur(src, dst, new Size(3,3),5,5);
        //2 转灰度图片
        Imgproc.cvtColor(dst, gray, Imgproc.COLOR_BGR2GRAY);
        //3 描绘边缘
        Imgproc.Canny(gray, image, 30, 6, 5, false);
        Imgproc.dilate(image, image, new Mat(), new Point(-1,-1), 3, 1, new Scalar(1));
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE);
        Mat img= new Mat(image.size(),image.type());

        double area = Imgproc.boundingRect(contours.get(0)).area();
        int index = 0;

        // 找出匹配到的最大轮廓
        for (int i = 0; i < contours.size(); i++) {
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
            System.out.println("i:"+i+"    "+tempArea);
            if (tempArea > area) {
                area = tempArea;
                index = i;
            }
        }
//        area = Imgproc.boundingRect(contours.get(0)).area();
//        int index1 = 0;
//        for (int i = 0; i < contours.size(); i++) {
//            if(i == index) continue;;
//            double tempArea = Imgproc.boundingRect(contours.get(i)).area();;
//            if (tempArea > area) {
//                area = tempArea;
//                index1 = i;
//            }
//        }
        System.out.println(contours.size());
//        for(int i =0;i<contours.size();i++){
//            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
//            if(tempArea < 10000) contours.remove(i);
//        }
        System.out.println(contours.size());
        Imgproc.drawContours(img,contours,-1,new Scalar(255,0,0));
        ImageViewer imageViewer = new ImageViewer(image, "t1");
        imageViewer.imshow();
        imageViewer = new ImageViewer(img, "t2");
        imageViewer.imshow();
//        System.out.println(contours.get(0).toArray()[0].y);
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());
        RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
        Rect rect1 = rect.boundingRect();
//        Mat mm = image.submat(rect1);

        int posX = (int)(contours.get(0).toArray()[0].x);
        int posY = (int)(contours.get(0).toArray()[0].y);
        int width = (int)rect.size.width;
        int height = (int)rect.size.height;
//        System.out.println(posX);
//        imageCut("D:\\visa.jpg","D:\\out.jpg",posX,posY,width,height);
//        ImageViewer imageViewer = new ImageViewer(mm, "申请表");
//        imageViewer.imshow();
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
//        String[] paths = imagePath.split("/");
//        String name = paths[2];

//        Imgcodecs.imwrite(UploadConfig.path + "HEAD_"+ name , mat);
        ImageViewer imageViewer = new ImageViewer(mat, "照片");
        imageViewer.imshow();
//        Imgcodecs.imwrite(outFile, mat);
//        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }
}
