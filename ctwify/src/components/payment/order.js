// 共用資料
import axios from "axios";

axios.defaults.withCredentials = true;

// 取得後端計算過、驗證過的預覽資料
export async function previewOrder(payload) {
    const { data } = await axios.post("/api/orderconfirm/preview", payload);
    return data; // { listing, customer, days, total, checkindate, checkoutdate, people }
}
//送出訂單
export async function finalizeOrder(payload) {
    const { data } = await axios.post("/api/orderconfirm/finalize", payload);

    if (typeof data === "string") {
        const m = data.match(/訂單編號[:：]\s*([A-Za-z0-9-]+)/);
        return { bookingId: m ? m[1] : "" };
    }

    return data;
}
//查詢訂單明細
export async function getOrderDetail(bookingId) {
    const { data } = await axios.get(`/api/orderconfirm/detail?booking_id=${bookingId}`);
    return data;
}
// 藍新：向後端拿要 POST 到藍新的四個欄位 + action
export async function payNewebpayCheckout(bookingId) {
    const { data } = await axios.post("/api/newebPay/checkout", { bookingId });
    // 預期 data = { action, MerchantID, TradeInfo, TradeSha, Version }
    return data;
}