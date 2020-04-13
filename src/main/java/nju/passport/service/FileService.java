package nju.passport.service;

import nju.passport.FileUtils;
import nju.passport.config.UploadConfig;
import nju.passport.dao.FileDao;
import nju.passport.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static nju.passport.FileUtils.generateFileName;
import static nju.passport.UploadUtils.*;


/**
 * 文件上传服务
 */
@Service
public class FileService {

    @Autowired
    private FileDao fileService;
    @Autowired
    private OcrService ocrService;

    /**
     * 上传文件
     * @param md5
     * @param file
     */
    public void upload(String name,
                       String md5,
                       MultipartFile file) throws IOException {
        String path = UploadConfig.path + generateFileName() + ".jpg";
        FileUtils.write(path, file.getInputStream());
        File file1 = new File();
        file1.setName(name);
        file1.setMd5(md5);
        file1.setPath(path);
        file1.setUploadTime(new Date());
        fileService.save(file1);

        System.out.println(ocrService.getResult(path)+"???????");

    }

    /**
     * 分块上传文件
     * @param md5
     * @param size
     * @param chunks
     * @param chunk
     * @param file
     * @throws IOException
     */
    public void uploadWithBlock(String name,
                                String md5,
                                Long size,
                                Integer chunks,
                                Integer chunk,
                                MultipartFile file) throws IOException {
        String fileName = getFileName(md5, chunks);
        FileUtils.writeWithBlok(UploadConfig.path + fileName, size, file.getInputStream(), file.getSize(), chunks, chunk);
        addChunk(md5,chunk);
        if (isUploaded(md5)) {
            removeKey(md5);
            File file1 = new File();
            file1.setName(name);
            file1.setMd5(md5);
            file1.setPath(UploadConfig.path + fileName);
            file1.setUploadTime(new Date());
            fileService.save(file1);
        }
    }

    /**
     * 检查Md5判断文件是否已上传
     * @param md5
     * @return
     */
    public boolean checkMd5(String md5) {

        return fileService.getByMd5(md5) == null;
    }
}
