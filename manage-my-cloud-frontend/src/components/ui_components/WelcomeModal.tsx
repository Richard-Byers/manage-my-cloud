import {useState} from "react";
import './WelcomeModal.css';
import logo from '../images/managemycloudlogo.svg';
import {AuthData} from "../routing/AuthWrapper";
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import {useTranslation} from "react-i18next";

const WelcomeModal = () => {

    const {t} = useTranslation()
    const [showModal, setShowModal] = useState(true);
    const {refreshUser, user} = AuthData();

    const handleCloseModal = () => {
        setShowModal(false);
        refreshUser(user?.email)
    }

    return (
        <>
            {showModal ?
                <div className={"modal-overlay"}>
                    <div className={"welcome-modal"}>
                        <div className={"welcome-container"} id={"welcome-container"}>
                        <span className={"welcome-container-text-container"} id={"welcome-container-text-container"}>
                            <h2>{t("main.welcome.welcomeTo")}</h2>
                            <br/>
                            <p>{t("main.welcome.toGetStarted1")} <strong>{t("main.welcome.toGetStarted2")}</strong> {t("main.welcome.toGetStarted3")}</p>
                            <br/>
                            <p>{t("main.welcome.keepAnEyeOut1")} <HelpOutlineIcon/> {t("main.welcome.keepAnEyeOut2")}</p>
                            <br/>
                            <p>{t("main.welcome.whenFinished1")} <strong>{t("main.welcome.gotIt")}</strong> {t("main.welcome.whenFinished2")}</p>
                        </span>
                            <button onClick={handleCloseModal} className={"welcome-button"} id={"welcome-button"}>
                                {t("main.welcome.gotIt")}
                            </button>
                        </div>
                        <div className={"welcome-modal-art"} id={"welcome-modal-art"}>
                            <img src={logo} alt={"Welcome modal art"}/>
                        </div>
                    </div>
                </div>
                : null}
        </>
    )
}

export default WelcomeModal;