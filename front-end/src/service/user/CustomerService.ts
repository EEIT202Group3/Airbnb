import axios from "axios";

const BASE_URL = 'http://localhost:8080';
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