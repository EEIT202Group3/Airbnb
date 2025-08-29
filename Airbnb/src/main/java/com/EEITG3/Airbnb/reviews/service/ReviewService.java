package com.EEITG3.Airbnb.reviews.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.reviews.dto.ReviewDTO;
import com.EEITG3.Airbnb.reviews.dto.ReviewInsertDto;
import com.EEITG3.Airbnb.reviews.dto.ReviewMapper;
import com.EEITG3.Airbnb.reviews.dto.ReviewPatchRequest;
import com.EEITG3.Airbnb.reviews.dto.ReviewWithCustomerDto;
import com.EEITG3.Airbnb.reviews.entity.Review;
import com.EEITG3.Airbnb.reviews.repository.ReviewRepository;
import com.EEITG3.Airbnb.reviews.utils.ReviewUtils;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;
import com.EEITG3.Airbnb.users.repository.HostRepository;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;;

@Service
@Transactional
public class ReviewService {

	private final ReviewRepository rRepository;
	private final CustomerRepository customerRepository;
	private final HostRepository hostRepository;
	private final OrderRepository orderRepository;
	private final ListRepository listingRepository;
    private final ReviewUtils reviewUtils;
    private final ReviewMapper mapper;

    public ReviewService(ReviewRepository rRepository, CustomerRepository customerRepository, HostRepository hostRepository, OrderRepository orderRepository, ListRepository listingRepository, ReviewUtils reviewUtils, ImageStorageService storage, ReviewMapper mapper) {
		this.rRepository = rRepository;
		this.customerRepository = customerRepository;
		this.hostRepository = hostRepository;
		this.orderRepository = orderRepository;
		this.listingRepository = listingRepository;
        this.reviewUtils = reviewUtils;
        this.mapper = mapper;
    }
    
    public void reportReview(Integer reviewId) {
        Review review = rRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setReport(1);
        rRepository.save(review);
    }
	
	public List<ReviewDTO> findByTypeAndKeyword(String type, String keyword) {

	    switch (type) {
	        case "hostId":
	            return rRepository.findByHost_HostIdContainingIgnoreCase(keyword).stream()
	    	            .map(ReviewMapper::toDTO)
	    	            .collect(Collectors.toList());
	        case "custId":
	            return rRepository.findByCustomer_CustomerIdContainingIgnoreCase(keyword).stream()
	    	            .map(ReviewMapper::toDTO)
	    	            .collect(Collectors.toList());
	        case "listId":

	                return rRepository.findByListing_ListId(Integer.parseInt(keyword.trim())).stream()
	        	            .map(ReviewMapper::toDTO)
	        	            .collect(Collectors.toList());
	        default:
	            return rRepository.findAll().stream()
	    	            .map(ReviewMapper::toDTO)
	    	            .collect(Collectors.toList());
	    }
	}
	
	public List<ReviewDTO> getAllReviews() {
	    return rRepository.findAll().stream()
	            .map(ReviewMapper::toDTO)
	            .collect(Collectors.toList());
	}

	
	public List<ReviewDTO> findByCustId(String id){
		return rRepository.findByCustomer_CustomerIdContainingIgnoreCase(id).stream()
	            .map(ReviewMapper::toDTO)
	            .collect(Collectors.toList());
	}

	public ReviewDTO findByReviewID(Integer id) {
		  return rRepository.findByReviewId(id)
                  .map(ReviewMapper::toDTO)
                  .orElse(null); // 或丟出例外
	}
	
	public List<ReviewDTO> getReviewsByHostToken(String email) {
		  Optional<Host> hostByEmail = hostRepository.findHostByEmail(email);
		  String HostId = hostByEmail.get().getHostId();
		  System.out.println(HostId);
		
		return rRepository.findByHost_HostIdContainingIgnoreCase(HostId).stream()
	            .map(ReviewMapper::toDTO)
	            .collect(Collectors.toList());
		
	}
	
	public List<ReviewDTO> getReviewsByCustomerToken(String email) {
		  Optional<Customer> customerByEmail = customerRepository.findCustomerByEmail(email);
		  String customerId = customerByEmail.get().getCustomerId();
		
		return rRepository.findByCustomer_CustomerIdContainingIgnoreCase(customerId).stream()
	            .map(ReviewMapper::toDTO)
	            .collect(Collectors.toList());
		
	}

	public void deleteById(Integer id) {
		rRepository.deleteById(id);
	}

	// list reviews
	public List<ReviewWithCustomerDto> listingReview(Integer id) {
		return rRepository.findReviewsByListId(id);
	};

	public ResponseEntity<?> insertReview(Integer listId, String bookingId, String custId, String hostId,
			Integer cleanScore, Integer commScore, Integer valueScore, String custComm, List<MultipartFile> images) {
		Review insertBean = new Review();
		List<String> imageList =  reviewUtils.uploadImg(images);
		// --- 關聯 Customer（不打 DB，可用 getReferenceById） ---
		// 若你沒有 CustomerRepository，也可改用 em.getReference(Customer.class, custId)
		Customer customerRef = customerRepository.getReferenceById(custId);
		Order bookingRef = orderRepository.getReferenceById(bookingId);
		Host hostRef = hostRepository.getReferenceById(hostId);
		LisBean listingRef = listingRepository.getReferenceById(listId);
		insertBean.setBooking(bookingRef); // << 關鍵：關聯
		insertBean.setHost(hostRef); // << 關聯
		insertBean.setListing(listingRef); // << 關聯
		insertBean.setCustomer(customerRef); // << 關鍵：設關聯
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
		if (resultBean != null) {
			return ResponseEntity.ok("成功");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
		}
	}

	public Review save(Review review) {
		return rRepository.save(review);
	}

	public ResponseEntity<?> updateReview(Integer reviewId, Integer listId, String bookingId, String custId,
			String hostId, String reviewDate, int cleanScore, int commScore, int valueScore, String cusComm,
			String hostComm) {
		Review updateBean = new Review();
		Customer customerRef = customerRepository.getReferenceById(custId);
		Order bookingRef = orderRepository.getReferenceById(bookingId);
		Host hostRef = hostRepository.getReferenceById(hostId);
		LisBean listingRef = listingRepository.getReferenceById(listId);
		updateBean.setReviewId(reviewId);
		updateBean.setBooking(bookingRef); // << 關鍵：關聯
		updateBean.setHost(hostRef); // << 關聯
		updateBean.setListing(listingRef); // << 關聯
		updateBean.setCustomer(customerRef); // << 關鍵：設關聯
		updateBean.setReviewDate(reviewDate);
		updateBean.setCleanScore(cleanScore);
		updateBean.setCommScore(commScore);
		updateBean.setValueScore(valueScore);
		updateBean.setCusComm(cusComm);
		updateBean.setHostComm(hostComm);

		Review resultBean = save(updateBean);
		if (resultBean != null) {
			return ResponseEntity.ok("成功");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
		}
	}

	public ResponseEntity<ReviewDTO> patchReview(Integer id, ReviewPatchRequest req, MultipartFile image1,
			MultipartFile image2, MultipartFile image3) {
		Review r = rRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
		// 1) 局部更新非檔案欄位
		if (req != null) {
			if (req.getCleanScore() != null)
				r.setCleanScore(req.getCleanScore());
			if (req.getCommScore() != null)
				r.setCommScore(req.getCommScore());
			if (req.getValueScore() != null)
				r.setValueScore(req.getValueScore());
			if (req.getCusComm() != null)
				r.setCusComm(req.getCusComm());
		}
		// 2) 只有在收到新檔案時才覆蓋；否則不動舊值（也不刪檔）

		if (image1 != null && !image1.isEmpty()) {
			// 視需求：若要覆蓋前刪舊檔，可加 storage.deleteQuietly(r.getImage1());
			r.setImage1(reviewUtils.saveImage(image1));
		}
		if (image2 != null && !image2.isEmpty()) {
			// storage.deleteQuietly(r.getImage2());
			r.setImage2(reviewUtils.saveImage(image2));
		}
		if (image3 != null && !image3.isEmpty()) {
			// storage.deleteQuietly(r.getImage3());
			r.setImage3(reviewUtils.saveImage(image3));
		}

		Review saved = rRepository.save(r);
		
		return ResponseEntity.ok(mapper.toDTO(saved));
	}
	
	public ReviewInsertDto insertData(String bookingId) {
		ReviewInsertDto reviewInsertDto = new ReviewInsertDto();
		Optional<Order> order = orderRepository.findByBookingId(bookingId);
		reviewInsertDto.setBookingId(bookingId);
		reviewInsertDto.setCustomerId(order.get().getCustomerId());
		reviewInsertDto.setHostId(order.get().getHostId());
		reviewInsertDto.setListId(order.get().getListing().getListId());
		reviewInsertDto.setListImg(order.get().getListing().getPhoto1());
		reviewInsertDto.setHouseName(order.get().getListing().getHouseName());
		return reviewInsertDto;
	}

	public ResponseEntity<?> hostReplyReview(Integer reviewId,Map<String, String> payload) {
		Optional<Review> reviewOptional = rRepository.findById(reviewId);
		Review r = reviewOptional.get();
		String hostComm = payload.getOrDefault("hostComm", "").trim();
	    
	    if (hostComm.isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("message", "回覆不得為空"));
	    }
	    // 可加權限檢查：principal 是否為此 review 的房東
	    r.setHostComm(hostComm);
	    rRepository.save(r);

	    return ResponseEntity.ok(Map.of("message", "ok"));
	}

}
