package com.EEITG3.Airbnb.reviews.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.reviews.model.Review;
import com.EEITG3.Airbnb.reviews.model.ReviewService;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
	private ReviewService rService;

	
//	@PostMapping()
//	public Review processFindByReviewId() {
//		return rService.findByReviewId(1);
//	}
    
//    @GetMapping("/reviewGetAll")
//    public String reviewGetAll(Model model ) {
//    	List<Review> reviews = rService.findAll();
//        model.addAttribute("reviews", reviews); // 給 JSP 用
//        return "reviewGetAll1"; // JSP 檔名 /WEB-INF/views/review-list.jsp
//    	
//    }
    @GetMapping
    public List<Review> getAllReviews() {
        return rService.findAll();
    }
    
    
    @GetMapping("/get/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return rService.findByReviewID(id); // 找不到可回 null 或拋異常
    }
	
    
    @DeleteMapping("/del/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
    	System.out.println("此api接收參數:" +id);
        try {
        	rService.deleteById(id);
        	return ResponseEntity.ok("刪除成功");
        } catch (Exception e) {            
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗: " + e.getMessage());

		}
    }
    
    @PostMapping("/insert")
    public Review createReview(@RequestBody Review review) {
        return rService.save(review);
    }
    
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateReview(@PathVariable Integer id, @RequestBody Map<String, Object> reviewData) throws ParseException {
    	
    	try {

        	System.out.println("收到的review :" +reviewData);
        	Review review = rService.findByReviewID(id);
        	review.setBookingId((String)reviewData.get("bookingId"));
        	review.setListId((int)reviewData.get("listId"));
        	review.setUserId((String) reviewData.get("userId"));
        	review.setCleanScore((int)reviewData.get("cleanScore"));
        	review.setCommScore((int)reviewData.get("commScore"));
        	review.setValueScore((int)reviewData.get("valueScore"));
        	review.setComment((String)reviewData.get("comment"));
        	String dateStr = (String) reviewData.get("reviewDate");
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	Date date = sdf.parse(dateStr); // 這裡會拋出 ParseException，記得 try-catch
        	review.setReviewDate(date);
        	rService.save(review);
        	return ResponseEntity.ok("更新成功");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗: " + e.getMessage());
		}
    }
    
}
