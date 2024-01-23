import axios from "axios";

axios.defaults.baseURL = "http://localhost:8080";
axios.defaults.headers.post["Content-Type"] = "application/json";

export const buildAxiosRequest = (method: string, url: string, data: any) => {

    return axios({
        method: method,
        url: url,
        data: data,
    });
}

export const buildAxiosRequestWithHeaders = (method: string, url: string, headers: any ,data: any) => {

    return axios({
        method: method,
        url: url,
        data: data,
        headers: headers
    });
}

