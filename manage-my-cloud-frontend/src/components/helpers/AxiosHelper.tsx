import axios from "axios";

axios.defaults.baseURL = "http://localhost:8080";
axios.defaults.headers.post["Content-Type"] = "application/json";

export let DEFAULT_ONEDRIVE_REDIRECT_URL = "http://localhost:3000/manage-connections";
export let DEFAULT_PROGRESS_ENDPOINT = "http://localhost:8080/progress";
export let DEFAULT_RECOMMENDATION_PROGRESS_ENDPOINT = "http://localhost:8080/recommendation-progress";
export let DEFAULT_DELETION_PROGRESS_ENDPOINT = "http://localhost:8080/deletion-progress";

if (process.env.REACT_APP_ENV === "production") {
    // update axios default url
    axios.defaults.baseURL = "https://manage-my-cloud-backend-evofni4tqa-nw.a.run.app";

    // update onedrive redirect url
    DEFAULT_ONEDRIVE_REDIRECT_URL = "https://manage-my-cloud-frontend-evofni4tqa-nw.a.run.app/manage-connections";

    // update progress endpoint urls
    DEFAULT_PROGRESS_ENDPOINT = "https://manage-my-cloud-backend-evofni4tqa-nw.a.run.app/progress";
    DEFAULT_RECOMMENDATION_PROGRESS_ENDPOINT = "https://manage-my-cloud-backend-evofni4tqa-nw.a.run.app/recommendation-progress";
    DEFAULT_DELETION_PROGRESS_ENDPOINT = "https://manage-my-cloud-backend-evofni4tqa-nw.a.run.app/deletion-progress";
}

export const buildAxiosRequest = (method: string, url: string, data: any) => {

    return axios({
        method: method,
        url: url,
        data: data,
    });
}

export const buildAxiosRequestWithHeaders = (method: string, url: string, headers: any, data: any) => {

    return axios({
        method: method,
        url: url,
        data: data,
        headers: headers
    });
}

