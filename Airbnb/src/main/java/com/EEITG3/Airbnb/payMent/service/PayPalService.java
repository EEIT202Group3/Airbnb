package com.EEITG3.Airbnb.payMent.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {
    
    @Autowired
    private PayPalHttpClient payPalHttpClient;
    
    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;
    
    public String createOrder(String bookingId, BigDecimal amount) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        
        // 設定回調 URL
        ApplicationContext applicationContext = new ApplicationContext()
            .returnUrl(baseUrl + "/payment/success?bookingId=" + bookingId)
            .cancelUrl(baseUrl + "/payment/cancel?bookingId=" + bookingId);
        orderRequest.applicationContext(applicationContext);
        
        // 建立購買項目
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(
            new PurchaseUnitRequest()
                .referenceId(bookingId)
                .description("Airbnb 房源預訂 - " + bookingId)
                .customId(bookingId)
                .softDescriptor("AIRBNB-BOOKING")
                .amountWithBreakdown(
                    new AmountWithBreakdown()
                        .currencyCode("USD")  // 使用美金比較穩定
                        .value(amount.toString())
                        .amountBreakdown(
                            new AmountBreakdown()
                                .itemTotal(new Money().currencyCode("USD").value(amount.toString()))
                        )
                )
                .items(List.of(
                    new Item()
                        .name("房源預訂")
                        .description("Airbnb 房源預訂服務")
                        .sku("ROOM-BOOKING")
                        .unitAmount(new Money().currencyCode("USD").value(amount.toString()))
                        .quantity("1")
                        .category("DIGITAL_GOODS")
                ))
        );
        orderRequest.purchaseUnits(purchaseUnits);
        
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(orderRequest);
        
        HttpResponse<Order> response = payPalHttpClient.execute(request);
        return response.result().id();
    }
    
    public Order captureOrder(String paypalOrderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.requestBody(new OrderActionRequest());
        
        HttpResponse<Order> response = payPalHttpClient.execute(request);
        return response.result();
    }
    
    public Order getOrderDetails(String paypalOrderId) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(paypalOrderId);
        HttpResponse<Order> response = payPalHttpClient.execute(request);
        return response.result();
    }
}
