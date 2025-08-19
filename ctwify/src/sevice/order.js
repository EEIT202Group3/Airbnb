import axios from "axios";

axios.defaults.withCredentials = true;

// 取得後端計算過、驗證過的預覽資料
export async function previewOrder(payload) {
    const { data } = await axios.post("/api/orderconfirm/preview", payload);
    return data; // { listing, customer, days, total, checkindate, checkoutdate, people }
}

// 送出訂單
export async function finalizeOrder(payload) {
    const { data } = await axios.post("/api/orderconfirm/finalize", payload);

    if (typeof data === "string") {
        const m = data.match(/訂單編號[:：]\s*([A-Za-z0-9-]+)/);
        return { bookingId: m ? m[1] : "" };
    }

    return data;
}

// 查詢訂單明細 - 修正參數名稱
export async function getOrderDetail(bookingId) {
    console.log('呼叫 getOrderDetail API，bookingId:', bookingId);
    try {
        const { data } = await axios.get(`/api/orderconfirm/detail?bookingId=${bookingId}`);
        console.log('getOrderDetail 回應:', data);
        return data;
    } catch (error) {
        console.error('getOrderDetail 錯誤:', error);
        throw error;
    }
}

