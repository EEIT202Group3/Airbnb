package com.EEITG3.Airbnb.reviews.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.reviews.dto.ReviewPatchRequest;
import com.EEITG3.Airbnb.reviews.entity.Review;
import com.EEITG3.Airbnb.reviews.service.ReviewService;


@RestController
@RequestMapping("/api")
public class ReviewController {
	/*
	 * reviews Controller
	 */

	@Autowired
	private ReviewService rService;

	@GetMapping("/reviews")
	public List<Review> getAllReviews(
			@RequestParam(required = false) String type,
		    @RequestParam(required = false) String keyword) { 
		if (type == null || keyword == null || keyword.isBlank()) {
		        return rService.findAll();
		    }
		System.out.println(type + keyword);
		 return rService.findByTypeAndKeyword(type, keyword);
	}

	@GetMapping("/listing/{id}")
	public List<Review> getAllReviewsByList(Integer id) {
		return rService.findByListId(id);
	}

	@GetMapping("/admins/reviews/get/{id}")
	public Review getReviewById(@PathVariable Integer id) {
		return rService.findByReviewID(id); // 找不到可回 null 或拋異常
	}
	@GetMapping("/reviews/getByCust/{id}")
	public List<Review> getAllReviewsByCustId(@PathVariable String id){
		return rService.findByCustId(id);
	}

	@DeleteMapping("/admins/reviews/del/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Integer id) {
		System.out.println("此api接收參數:" + id);
		try {
			rService.deleteById(id);
			return ResponseEntity.ok("刪除成功");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗: " + e.getMessage());

		}
	}

	/*
	 * 呼叫service.insert 傳入參數，儲存review對象進行 repo.save(reviews) 用util 處理上傳圖片
	 */

	@PostMapping("/reviews/insert")
	public ResponseEntity<?> insertReview(
		@RequestParam Integer listId, 
		@RequestParam String bookingId,
		@RequestParam String custId, 
		@RequestParam String hostId,
		@RequestParam int cleanScore, 
		@RequestParam int commScore, 
		@RequestParam int valueScore,
		@RequestParam String custComm, 
		@RequestPart(required = false) List<MultipartFile> images // 可上傳多張圖片
	) {
		return rService.insertReview(listId, bookingId, custId, hostId, cleanScore, commScore, valueScore, custComm,
				images);
	}


	/*
	@PatchMapping(value= "/reviews/update/{id}")
	public ResponseEntity<?> patchReview(
			@PathVariable("id") Integer reviewId,
			@RequestParam int cleanScore, 
			@RequestParam int commScore, 
			@RequestParam int valueScore,
			@RequestParam String cusComm,
			@RequestParam String hostComm){
		
		  return rService.patchReview(reviewId, cleanScore, commScore, valueScore, cusComm, hostComm);
		
	}
	*/
	@PatchMapping(value = "/reviews/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> patchReview(
	        @PathVariable("id") Integer reviewId,
	        @RequestPart("review") ReviewPatchRequest review,
	        @RequestPart(value = "images", required = false) List<MultipartFile> images,
	        @RequestPart(value = "deleteSlots", required = false) List<Integer> deleteSlots

	        ) {
		return rService.patchReview(reviewId, review, images);
	}
	
    
}
