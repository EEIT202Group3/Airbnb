package com.EEITG3.Airbnb.payMent.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.service.NewebPayService;

@RestController
@RequestMapping("/api/newebPay")
public class NewebPayController {
	
	private final NewebPayService newebPayService;
	
	public NewebPayController(NewebPayService newebPayService) {
		this.newebPayService = newebPayService;
	}
	
	/**Step1:前端帶bookingId取得要post到藍新的欄位*/
	@PostMapping(
	        value = "/checkout",
	        consumes = MediaType.APPLICATION_JSON_VALUE,
	        produces = MediaType.APPLICATION_JSON_VALUE
	    )
	public Map<String , String > checkout(@RequestBody Map<String , String> body){
		String bookingId = body.get("bookingId");
		if(bookingId == null || bookingId.isBlank()) {
			throw new IllegalArgumentException("bookingId is required");
		}
		// 回傳的 Map 內容應包含：MerchantID、TradeInfo、TradeSha、Version
        return newebPayService.buildMpgFormByBookingId(bookingId);
	}
	
	/** Step2：Notify（Server->Server），以這個為準更新訂單狀態 */
	@PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String notifyCallback(
            @RequestParam("Status") String status,
            @RequestParam("TradeInfo") String tradeInfo,
            @RequestParam("TradeSha") String tradeSha
    ) {
		// 服務層請務必驗證 TradeSha 與金額、訂單狀態、防重複更新
    	newebPayService.handleNotify(status, tradeInfo, tradeSha);
        return "SUCCESS"; // 藍新預期成功回覆
    }

    /** Step3：Return（Browser->Server），僅顯示用途，狀態仍以 Notify 為準 */
    @PostMapping(value = "/return", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String returnCallback() {
        return "OK";
    }
}
