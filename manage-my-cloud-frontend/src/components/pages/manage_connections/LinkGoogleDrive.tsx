import React from 'react';
import './LinkGoogleDrive.css';
import {useGoogleLogin} from '@react-oauth/google';
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import GoogleDriveLogo from "../../images/manage-connections/GoogleDriveLogo.png";
import {AuthData} from "../../routing/AuthWrapper";
import {useTranslation} from "react-i18next";

const LinkGoogleDrive = () => {

    const {t} = useTranslation();

    const {user} = AuthData();

    const handleGoogleDrive = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            // Get the code from the response
            const authCode = codeResponse.code;

            const headers = {
                Authorization: `Bearer ${user?.token}`
            }

            // Send the code to the server
            try {
                await buildAxiosRequestWithHeaders("POST", `/link-google-account?email=${encodeURIComponent(user?.email ?? '')}`, headers, {authCode});
                window.location.reload();
            } catch (error) {
                // Handle the error
                console.error('Error:', error);
            }
        },
        flow: 'auth-code',
        scope: 'https://www.googleapis.com/auth/drive https://mail.google.com/',
    });

    return (
        <div>
            <button className={"link-googledrive-button"} onClick={handleGoogleDrive}>
                <img src={GoogleDriveLogo} alt={"GoogleDrive Logo"}/>
                <p>{t("main.manageConnectionsPage.addConnectionModal.linkWithGoogleDrive")}</p>
            </button>
        </div>
    )
};

export default LinkGoogleDrive;