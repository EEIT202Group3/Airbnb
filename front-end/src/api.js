// src/api.js
import axios from "axios";

const api = axios.create({
    baseURL: 'http://localhost:8080/api/admins/',
    headers: {
        'content-type': 'application/json;charset=UTF-8'
    },
    withCredentials: true,
});

console.log('[api.js] baseURL =', api.defaults.baseURL);

export default api;
