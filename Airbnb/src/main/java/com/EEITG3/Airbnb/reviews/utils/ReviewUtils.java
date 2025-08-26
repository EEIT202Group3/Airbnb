package com.EEITG3.Airbnb.reviews.utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
public class ReviewUtils {
    @Value("${app.storage.base-dir}")
    private String baseDir;
    @Getter
    private Path storageDir;
    @PostConstruct
    public void init() throws IOException {
        storageDir = Paths.get(baseDir, "reviews").toAbsolutePath().normalize();
        Files.createDirectories(storageDir);
        System.out.println("Storage directory created at: " + storageDir);
    }

    public String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	}

    public List<String> uploadImg(List<MultipartFile> images){
    	
    	List<String> imgList = new ArrayList<>();
    	String image1 = null, image2 = null, image3 = null;
        int count = 0;
        if (images != null) {
            for (MultipartFile mf : images) {
                if (mf.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + mf.getOriginalFilename(); // 避免覆蓋
                File saveFilePath = new File(storageDir.toString(), fileName);
                System.out.println("Utils.uploadImg()" + storageDir.toString() + fileName);
                imgList.add(fileName);

                try {
                    mf.transferTo(saveFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片儲存失敗");
                }

                // 對應到 image1 ~ image3
                if (count == 0) image1 = fileName;
                else if (count == 1) image2 = fileName;
                else if (count == 2) image3 = fileName;

                count++;
                if (count >= 3) break; // 最多處理3張
                
                
            }
            return imgList;
        }
        return imgList;
    	
    }
    
    public String saveImage(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(storageDir.toString(), fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("圖片儲存失敗", e);
        }
        return fileName;
    }
}
