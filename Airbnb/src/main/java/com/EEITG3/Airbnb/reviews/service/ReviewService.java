package com.EEITG3.Airbnb.reviews.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.EEITG3.Airbnb.reviews.dto.ReviewPatchRequest;
import com.EEITG3.Airbnb.reviews.entity.Review;
import com.EEITG3.Airbnb.reviews.repository.ReviewRepository;
import com.EEITG3.Airbnb.reviews.utils.ReviewUtils;

@Service
@Transactional
public class ReviewService {

	@Autowired
	private ReviewRepository rRepository;
	
	private ImageStorageService storage;
	
	 public ReviewService(ReviewRepository rRepository, ImageStorageService storage) {
	        this.rRepository = rRepository; this.storage = storage;
	    }
	
	public List<Review> findByTypeAndKeyword(String type, String keyword) {
	    return rRepository.findByTypeAndKeyword(type, keyword);
	}

	public Review findByReviewID(Integer id) {
		return rRepository.findByReviewId(id);
	}

	public List<Review> findByListId(Integer id) {
		return rRepository.findByListId(id);
	}
	
	public List<Review> findByHostId(String id) {
		return rRepository.findByHostId(id);
	}
	public List<Review> findByCustId(String id) {
		return rRepository.findByCustId(id);
	}

	public List<Review> findAll() {
		return rRepository.findAll();
	}

	public void deleteById(Integer id) {
		rRepository.deleteById(id);
	}

	
	
	public ResponseEntity<?> insertReview(Integer listId, String bookingId, String custId, String hostId,
			Integer cleanScore, Integer commScore, Integer valueScore, String custComm, List<MultipartFile> images) {
		Review insertBean = new Review();
		List<String> imageList = new ReviewUtils().uploadImg(images);
		insertBean.setListId(listId);
		insertBean.setBookingId(bookingId);
		insertBean.setCustId(custId);
		insertBean.setHostId(hostId);
		insertBean.setCleanScore(cleanScore);
		insertBean.setCommScore(commScore);
		insertBean.setValueScore(valueScore);
		insertBean.setCusComm(custComm);
		String reviewDate = new ReviewUtils().getToday();
		insertBean.setReviewDate(reviewDate);
		for (int i = 0; i < imageList.size() && i < 3; i++) {
		    switch (i) {
		        case 0 -> insertBean.setImage1(imageList.get(0));
		        case 1 -> insertBean.setImage2(imageList.get(1));
		        case 2 -> insertBean.setImage3(imageList.get(2));
		    }
		}
	 	 Review resultBean = save(insertBean);
	 	 if(resultBean != null) {
	 		return ResponseEntity.ok("成功");
	 	 } else {
	 		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
	 	 }
	}
	
	
	public Review save(Review review) {
		return rRepository.save(review);
	}

	public ResponseEntity<?> updateReview(Integer reviewId,Integer listId, String bookingId,String custId, String hostId, String reviewDate, int cleanScore, int commScore, int valueScore,String cusComm,String hostComm) {
		Review updateBean = new Review();
		updateBean.setReviewId(reviewId);
		updateBean.setListId(listId);
		updateBean.setBookingId(bookingId);
		updateBean.setCustId(custId);
		updateBean.setHostId(hostId);
		updateBean.setReviewDate(reviewDate);
		updateBean.setCleanScore(cleanScore);
		updateBean.setCommScore(commScore);
		updateBean.setValueScore(valueScore);
		updateBean.setCusComm(cusComm);
		updateBean.setHostComm(hostComm);
		 
		Review resultBean = save(updateBean);
		if(resultBean != null) {
	 		return ResponseEntity.ok("成功");
	 	 } else {
	 		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
	 	 }
	}
	
	public ResponseEntity<?> patchReview(Integer id, ReviewPatchRequest req, List<MultipartFile> images) {
        Review r = rRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        ImageStorageService iss = new ImageStorageService();
        if (req.getCleanScore()!=null) r.setCleanScore(req.getCleanScore());
        if (req.getCommScore()!=null)  r.setCommScore(req.getCommScore());
        if (req.getValueScore()!=null) r.setValueScore(req.getValueScore());
        if (req.getCusComm()!=null)    r.setCusComm(req.getCusComm());

        // 處理每一張圖片（0, 1, 2）
        for (int i = 0; i < 3; i++) {
            MultipartFile file = (images != null && images.size() > i) ? images.get(i) : null;

            if (file != null && !file.isEmpty()) {
                // 有新圖要上傳
                String oldFilename = getImageBySlot(r, i + 1);
                iss.deleteImg(oldFilename);

                String newName = iss.saveImg(file);
                setImageBySlot(r, i + 1, newName);
            }
            // else: 沒檔案傳來，保留原圖
        }

        rRepository.save(r);
        return ResponseEntity.ok().build();
    }
	
	private String getImageBySlot(Review review, int slot) {
	    return switch (slot) {
	        case 1 -> review.getImage1();
	        case 2 -> review.getImage2();
	        case 3 -> review.getImage3();
	        default -> throw new IllegalArgumentException("圖片槽位必須是 1~3");
	    };
	}

	private void setImageBySlot(Review review, int slot, String filename) {
	    switch (slot) {
	        case 1 -> review.setImage1(filename);
	        case 2 -> review.setImage2(filename);
	        case 3 -> review.setImage3(filename);
	        default -> throw new IllegalArgumentException("圖片槽位必須是 1~3");
	    }
	}
	
	


}
