import React from 'react';
import './LinkGoogleDrive.css';
import { useGoogleLogin } from '@react-oauth/google';
import {buildAxiosRequest } from "../../helpers/AxiosHelper";
import GoogleDriveLogo from "../../images/manage-connections/GoogleDriveLogo.png";
import {AuthData} from "../../routing/AuthWrapper";

const LinkGoogleDrive = () => {
    const {user, refreshUser} = AuthData();

const handleGoogleDrive = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            // Get the code from the response
            const authCode = codeResponse.code;

            // Send the code to the server
            try {
                const response = await buildAxiosRequest("POST", `/link-google-account?email=${encodeURIComponent(user?.email ?? '')}`, {authCode});
                const data = response.data;
                
                window.location.reload();
            } catch (error) {
                // Handle the error
                console.error('Error:', error);
            }
        },
        flow: 'auth-code',
        scope: 'https://www.googleapis.com/auth/drive',
    });

    return (
        <div>
            <button className={"link-googledrive-button"} onClick={handleGoogleDrive}>
            <img src={GoogleDriveLogo} alt={"GoogleDrive Logo"}/>
                <p>Link with GoogleDrive</p>
            </button>
        </div>
    )
};

    export default LinkGoogleDrive;