import axios from "axios";

const BASE_URL = 'http://localhost:8080';

export async function loginService(loginData) {
    const response = await axios.post(`${BASE_URL}/api/customers/login`, loginData, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
    })
    return response.data
}

export async function signupService(signupData) {
    const response = await axios.post(`${BASE_URL}/api/customers/signup`, signupData, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true,
    });
    return response.data;
}

export async function logoutService() {
    const response = await axios.post(`${BASE_URL}/api/customers/logout`, null, {
        withCredentials: true,
    })
    return response.data
}

export async function findMe() {
    const response = await axios.get(`${BASE_URL}/api/customers/current`, {
        withCredentials: true,
    });
    return response.data;
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

export async function sendEmailService(email){
    const response = await axios.post(`${BASE_URL}/api/customers/forgetpwd`,{email},{
        headers:{
            'Content-Type':'application/json'
        },
        withCredentials:true,
    });
    return response.data;
}