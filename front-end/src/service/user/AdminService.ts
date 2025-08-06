import axios from "axios";

const BASE_URL = 'http://localhost:8080';
export async function getCurrentAdmin() {
    const response = await axios.get(`${BASE_URL}/api/admins/current`, {
        withCredentials: true
    });
    return response.data;
}