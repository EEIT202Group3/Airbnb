import axios from "axios";

const api = axios.create({
    baseURL: 'http://localhost:8080',
    // headers: {
    //     'content-type': 'application/json;charset=UTF-8'
    // }
});
export default api;