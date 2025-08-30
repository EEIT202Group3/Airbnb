import axios from "axios";

const BASE_URL = 'http://localhost:8080';

//取得所有客戶資料
export async function getAllCustomers() {
    try {
        const response = await axios.get(`${BASE_URL}/api/admins/customers`, {
            withCredentials: true,
        });
        return response.data
    } catch (error) {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            alert('請先登入');
            return null;
        } else {
            console.error('取得客戶資料失敗', error);
            throw error;
        }
    }
}

//停權
export async function suspendService(reason, email) {
    const data = {
        reason: reason,
        email: email
    }
    const response = await axios.patch(`${BASE_URL}/api/admins/customers/suspend`, data, {
        withCredentials: true,
    })
    return response.data
}


//更改權限
export async function permission(status, customerEmail) {
    const data = {
        status: status,
        email: customerEmail,
    };

    try {
        const response = await axios.patch(`${BASE_URL}/api/admins/customers/updatePermission`, data, {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 400) {
            alert('請求錯誤');
            return null
        } else {
            console.error('更新權限失敗', error);
            throw error;
        }
    }
}

//模糊搜尋
export async function findLike(keyword, context) {
    try {
        const response = await axios.get(`${BASE_URL}/api/admins/customers/findlike`, {
            params: {
                keyword: keyword,
                context: context,
            },
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            alert('請先登入');
            return null;
        } else if (error.response.status === 404) {
            return null;
        } else {
            console.error('查詢失敗', error)
            throw error;
        }
    }
}
//查當月註冊人數
export async function getMonthlyCustomers() {
    const response = await axios.get(`${BASE_URL}/api/getMonthlyRegistCustomers`)
    return response.data;
}
//查總客戶數
export async function getTotalCustomers() {
    const response = await axios.get(`${BASE_URL}/api/getTotalCustomers`)
    return response.data
}
//查已驗證客戶數
export async function getVerifiedCustomers() {
    const response = await axios.get(`${BASE_URL}/api/getVerifiedCustomers`)
    return response.data
}