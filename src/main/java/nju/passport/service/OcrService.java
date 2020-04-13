package nju.passport.service;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import nju.passport.config.UploadConfig;
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
        //System.out.println(System.getenv("TESSDATA_PREFIX"));

        //tesseract.setDatapath(System.getenv("TESSDATA_PREFIX"));
        System.out.println(file.getPath());
        System.out.println(file.getName());
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
            return last;

        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (
                IOException io) {
            io.printStackTrace();
        }
        return "";
    }

    public static String extractLastLine(String input) {
        String[] strs = input.split("\n");
        int lastIndexOf = input.lastIndexOf('\n');
        if (lastIndexOf == -1 || lastIndexOf + 1 > input.length())
            return "";
        return strs[strs.length-1];
    }
}

