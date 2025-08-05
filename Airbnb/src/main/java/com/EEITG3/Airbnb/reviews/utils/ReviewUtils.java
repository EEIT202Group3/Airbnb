package com.EEITG3.Airbnb.reviews.utils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class ReviewUtils {
	
	public String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	}

    public List<String> uploadImg(List<MultipartFile> images){
    	
    	String saveFileDir = "c:/temp/upload/";
    	File dir = new File(saveFileDir);
    	if (!dir.exists()) {
    		dir.mkdirs();
    	}
    	List<String> imgList = new ArrayList<>();
    	String image1 = null, image2 = null, image3 = null;
        int count = 0;
        if (images != null) {
            for (MultipartFile mf : images) {
                if (mf.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + mf.getOriginalFilename(); // 避免覆蓋
                File saveFilePath = new File(saveFileDir, fileName);
                System.out.println("Utils.uploadImg()" + saveFileDir + fileName);
                imgList.add(saveFileDir + fileName);

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
}
