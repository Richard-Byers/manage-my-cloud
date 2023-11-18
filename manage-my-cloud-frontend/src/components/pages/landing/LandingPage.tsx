import React from "react";
import "./LandingPage.css";
import manageMyCloudLogo from "../../images/managemycloudlogo.svg";

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
                <button className={"landing-page-login-button"}>Login</button>
            </div>
        </div>
    )
};

export default LandingPage;