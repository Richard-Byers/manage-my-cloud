import manageMyCloudLogo from "../../images/managemycloudlogo.svg";
import React from "react";
import "./LandingPage.css";
import LoginModal from "../../modals/login/LoginModal";

const LandingPage: React.FC = () => {
    return (
        <div className={"landing-page-main"}>
            <div className={"landing-page-logo-div"}><img className={"landing-page-logo"} src={manageMyCloudLogo}
                                                          alt={"Manage my cloud logo"}/></div>
            <div className={"landing-page-main-text"}>
                Optimise your cloud storage
            </div>
            <div className={"landing-page-sub-text"}>
                and save money
            </div>
            <div className={"landing-page-sub-text-description"}>
                We aim to de-clutter your cloud storage, save you money
                <br/>
                and reduce the cloud storage carbon footprint.
            </div>
            <div className={"landing-page-login-button-div"}>
                <LoginModal/>
            </div>
        </div>
    )
};

export default LandingPage;