import manageMyCloudLogo from "../../images/managemycloudlogo.svg";
import React from "react";
import { useNavigate } from "react-router-dom";
import "./LandingPage.css";
import LoginModal from "../../modals/login/LoginModal";
import { useTranslation } from 'react-i18next';

const LandingPage: React.FC = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleGetStartedClick = () => {
        navigate('/login');
    };

    return (
        <div className={"landing-page-main"}>
            <div className={"landing-page-logo-div"}><img className={"landing-page-logo"} src={manageMyCloudLogo}
                                                          alt={"Manage my cloud logo"}/></div>
            <div className={"landing-page-main-text"}>
                {t('main.landingPage.mainText')}
            </div>
            <div className={"landing-page-sub-text"}>
               {t('main.landingPage.subText')}
            </div>
            <div className={"landing-page-sub-text-description"}>
                {t('main.landingPage.subTextDescription')}
            </div>
            <div className={"landing-page-login-button-div"}>
                <button className={"modal-login-button"} onClick={handleGetStartedClick}>
                    {t('main.landingPage.loginModal.getStartedButton')}
                </button>
            </div>
        </div>
    )
};

export default LandingPage;