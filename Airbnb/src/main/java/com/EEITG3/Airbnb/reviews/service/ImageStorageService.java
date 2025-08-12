package com.EEITG3.Airbnb.reviews.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.EEITG3.Airbnb.reviews.entity.Review;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Component
public class ImageStorageService {
	private String saveFileDir = "D:/photo/listing";
   
	public String saveImg(MultipartFile file) {
    	
        try {
            Path dir = Paths.get(saveFileDir);
            Files.createDirectories(dir);
            String name = UUID.randomUUID() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[\\\\/]", "_");
            Files.copy(file.getInputStream(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image", e);
        }
    }
    
    public void deleteImg(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
          Files.deleteIfExists(Paths.get(saveFileDir, fileName));
        } catch (IOException e) {
          // 可記 log，不一定要丟 500（檔案可能已不存在）
        }
      }
    
    public void setImageByIndex(Review review, int index, String filename) {
        switch (index) {
            case 0 -> review.setImage1(filename);
            case 1 -> review.setImage2(filename);
            case 2 -> review.setImage3(filename);
        }
    }

}
