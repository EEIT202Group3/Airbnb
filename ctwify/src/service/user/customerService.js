import axios from "axios";

const BASE_URL = 'http://localhost:8080';

export async function loginService(loginData) {
    try {
        const response = await axios.post(`${BASE_URL}/api/customers/login`, loginData, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true,
        });
        if (response.status === 200) {
            return response.data;
        } else {
            return null;
        }
    } catch (error) {
        console.log('發生錯誤:' + error.response)
        throw error
    }
}

export async function signupService(signupData) {
    try {
        const response = await axios.post(`${BASE_URL}/api/customers/signup`, signupData, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        console.log('發生錯誤:' + error.response)
        throw error
    }
}

export async function findMe() {
    try {
        const response = await axios.get(`${BASE_URL}/api/customers/current`, {
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        console.log('發生錯誤:' + error.response);
        throw error;
    }
}

export async function updateCustomerInfo(data) {
    try {
        const response = await axios.patch(`${BASE_URL}/api/customers/update`, data, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true,

        });
        if (response.status === 200) {
            return response.data;
        } else {
            return null;
        }
    } catch (error) {
        console.log('發生錯誤:' + error);
        throw error;
    }
}

export async function updateAvatar(data) {
    try {
        const response = await axios.post(`${BASE_URL}/api/customers/avatar`, data, {
            withCredentials: true,
        });
        if (response.status === 200) {
            return response.data;
        } else {
            return null;
        }
    } catch (error) {
        console.log('發生錯誤:' + error);
        throw error;
    }
}