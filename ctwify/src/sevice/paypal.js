import axios from "axios";
axios.defaults.withCredentials = true;

// 建立 PayPal 訂單（後端會回 { paypalOrderId, bookingId, amount, originalAmount }）
export async function createPayPalOrder(bookingId) {
    const { data } = await axios.post("/api/paypal/create-order", { bookingId });
    return data;
}

// 捕獲 PayPal 付款：**重點：key 名稱要是 paypalOrderId**
export async function capturePayPalPayment(paypalOrderId, bookingId) {
    const { data } = await axios.post("/api/paypal/capture-payment", {
        paypalOrderId,           // ← 名稱一定要這個
        bookingId: String(bookingId),
    });
    return data; // { success, status, bookingStatus, ... }
}

// 查詢訂單狀態（供 PaymentDone.vue 輪詢）
export async function getPayPalOrderStatus(bookingId) {
    const { data } = await axios.get(`/api/paypal/order-status/${bookingId}`);
    return data; // { mentStatus, bookingStatus, paidTime, ... }
}
