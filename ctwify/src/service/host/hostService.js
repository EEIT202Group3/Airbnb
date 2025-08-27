import axios from "axios";

const BASE_URL = 'http://localhost:8080';

export async function loginService(loginData) {
    const response = await axios.post(`${BASE_URL}/api/hosts/login`, loginData, {
        withCredentials: true,
    });
    return response.data;
}

export async function signupService(signupData) {
    const response = await axios.post(`${BASE_URL}/api/hosts/signup`, signupData, {
        withCredentials: true,
    });
    return response.data;
}

export async function logoutService() {
    const response = await axios.post(`${BASE_URL}/api/hosts/logout`, null, {
        withCredentials: true,
    });
    return response.data;
}

export async function findMe() {
    try {
        const response = await axios.get(`${BASE_URL}/api/hosts/current`, {
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        console.log('發生錯誤:' + error.response)
        throw error;
    }
}

export async function updateHostInfo(data) {
    const response = await axios.patch(`${BASE_URL}/api/hosts/update`, data, {
        withCredentials: true,
    });
    return response.data;
}

export async function updateAvatar(data) {
    const response = await axios.post(`${BASE_URL}/api/hosts/avatar`, data, {
        withCredentials: true,
    });
    return response.data;
}
export async function getRevenue() {
    const response = await axios.get(`${BASE_URL}/api/getRevenue`, {
        withCredentials: true,
    })
    return response.data
}
export async function getYearlyRevenue() {
    const response = await axios.get(`${BASE_URL}/api/getYearlyRevenue`, {
        withCredentials: true,
    })
    return response.data
}