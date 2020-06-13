package nju.passport;

import net.sourceforge.tess4j.util.ImageHelper;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class VisaUtils {
    static {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {

    }

    public static BufferedImage visaHandle(BufferedImage image) {
//        File file = new File("D:\\train\\visa\\1088.jpg");
//        try {
//            BufferedImage bi = ImageIO.read(file);
//            bi = ImageHelper.convertImageToGrayscale(bi);
//            File outputfile = new File("D:\\train\\visa\\1088g.jpg");
//            ImageIO.write(bi, "jpg", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Mat srcImgMat  = bufferToMartix(image);
        Mat desImgMat = new Mat(srcImgMat.size(),srcImgMat.type());
        Imgproc.cvtColor(srcImgMat, desImgMat, Imgproc.COLOR_RGB2HSV);
//        ImageViewer imageViewer = new ImageViewer(srcImgMat, "原图");
//        imageViewer.imshow();
        //白色HSV阈值范围
        Scalar lowerbScalar = new Scalar(0,0,181); //hsv色域的蓝色最低阈值
        Scalar highbScalar = new Scalar(180, 15, 255); //hsv色域蓝色最高阈值
        Core.inRange(desImgMat, lowerbScalar, highbScalar, desImgMat);

//        imageViewer = new ImageViewer(desImgMat, "色域分割照片");
//        imageViewer.imshow();

        List<MatOfPoint> pointsVector = new Vector<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(desImgMat, pointsVector, hierarchy, 		 Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat img= new Mat(desImgMat.size(),desImgMat.type());
        double area = Imgproc.boundingRect(pointsVector.get(0)).area();
        int index1 = 0;
        int index2 = 0;

        // 找出匹配到的最大轮廓
        for (int i = 0; i < pointsVector.size(); i++) {
            double tempArea = Imgproc.boundingRect(pointsVector.get(i)).area();
//            System.out.println("i:"+i+"    "+tempArea);
            if (tempArea > area) {
                area = tempArea;
                index2 = index1;
                index1 = i;

            }
        }
        MatOfPoint cntMatOfPoint = pointsVector.get(index1);
        Rect rect = Imgproc.boundingRect(cntMatOfPoint);
        int y = rect.y;
        if(y == 0) {
            index1 = index2;
           cntMatOfPoint = pointsVector.get(index1);
            rect = Imgproc.boundingRect(cntMatOfPoint);
            y = rect.y;
        }

        Imgproc.drawContours(img,pointsVector,index1,new Scalar(255,0,0));
//        imageViewer = new ImageViewer(img, "轮廓");
//        imageViewer.imshow();
        Imgproc.rectangle(srcImgMat, new Point(0,0), new Point(srcImgMat.width(), y) , new Scalar(0,0,255));;

        return imageCut(srcImgMat,"",0,0,srcImgMat.width(),y);



    }
    public static BufferedImage imageCut(Mat srcImgMat,String outFile, int posX,int posY,int width,int height ) {
        //原始图像
        Mat image = srcImgMat;
        //截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(posX, posY, width, height);
        //两句效果一样
        Mat sub = image.submat(rect);   //Mat sub = new Mat(image,rect);
        Mat mat = new Mat();
        Size size = new Size(width, height);
//        System.err.println(width+"And"+height);
        Imgproc.resize(sub, mat, size);//将人脸进行截图并保存

//        ImageViewer imageViewer = new ImageViewer(mat, "最终照片");
//        imageViewer.imshow();
//        Imgcodecs.imwrite(outFile, mat);
        return toBufferedImage(mat);
//        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }

    public static BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // get all pixel from martix
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public static Mat bufferToMartix(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        if (mat != null) {
            try {
                mat.put(0, 0, data);
            } catch (Exception e) {
                return null;
            }
        }
        return mat;
    }
}
