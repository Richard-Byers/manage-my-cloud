import React from "react";
import "./LinkOneDrive.css";
import OneDriveLogo from "../../images/manage-connections/OneDriveLogo.png";

const LinkOneDrive = () => {

    const handleOneDriveLink = async () => {

        const params = new URLSearchParams();
        params.append('client_id', 'a1418afc-b4bb-4e55-8184-cc77c502f087');
        params.append('response_type', 'code');
        params.append('redirect_uri', 'http://localhost:3000/manage-connections');
        params.append('response_mode', 'query');
        params.append('scope', 'user.read files.readwrite.all offline_access');

        const loginUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?" + params.toString();

        try {
            window.location.href = loginUrl;
        } catch (err) {
            console.log(err);
        }
    }

    return (
        <div>
            <button className={"link-onedrive-button"} onClick={handleOneDriveLink}>
                <img src={OneDriveLogo} alt={"OneDrive Logo"}/>
                <p>Link with OneDrive</p>
            </button>
        </div>
    )
};

export default LinkOneDrive;