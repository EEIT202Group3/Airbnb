// 統一放 fetch 呼叫；若需要帶 cookie（JWT），加上 credentials: 'include'

export async function previewOrder(payload) {
    // payload: { listid, checkindate, checkoutdate, people, ... }
    const res = await fetch('/api/orderconfirm/preview', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json(); // { listing, customer, days, total, ... }
}

export async function finalizeOrder(payload) {
    // payload 建議沿用 preview 時送的資料 + 付款方式 bookingMethod
    const res = await fetch('/api/orderconfirm/finalize', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(await res.text());
    // 目前你的後端回 "訂單成功！訂單編號：{bookingId}"（純字串）
    // 建議後端改成 { bookingId: "..."}，這裡先用正則硬切出 bookingId
    const text = await res.text();
    const m = text.match(/訂單編號：([\w-]+)/);
    return { bookingId: m ? m[1] : text }; // 萬一格式不同，就先把整段丟回
}

export async function getOrderDetail(bookingId) {
    const url = `/api/orderconfirm/detail?booking_id=${encodeURIComponent(bookingId)}`;
    const res = await fetch(url, { credentials: 'include' });
    if (!res.ok) throw new Error(await res.text());
    return res.json(); // OrderDetailResponseDto
}

export async function payNewebpayCheckout(bookingId) {
    const res = await fetch('/api/pay/newebpay/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ bookingId }),
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json(); // { MerchantID, TradeInfo, TradeSha, Version, Gateway }
}