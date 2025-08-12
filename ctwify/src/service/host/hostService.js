import axios from "axios";

const BASE_URL = 'http://localhost:8080';

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