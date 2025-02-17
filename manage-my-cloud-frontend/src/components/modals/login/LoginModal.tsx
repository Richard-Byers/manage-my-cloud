import React, {useEffect, useState} from "react";
import {useLocation, useNavigate, useSearchParams} from "react-router-dom";
import "./LoginModal.css";
import "../Modal.css";
import logo from "../../images/managemycloudlogo.png";
import googleLogo from "../../images/login/google.png";
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import CloseIcon from '@mui/icons-material/Close';
import {SignUpModal} from "../signUp/SignUpModal";
import {ResetPasswordModal} from "../resetPassword/ResetPasswordModal";
import {AuthData} from "../../routing/AuthWrapper";
import {useTranslation} from "react-i18next";
import {ROUTES} from "../../../constants/RouteConstants";

interface LoginProps {
    email: string;
    password: string;
}

const LoginModal: React.FC = () => {
    const {t} = useTranslation();
    const {login, googleLogin} = AuthData();
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const [showSignUpModal, setShowSignUpModal] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);
    const [showError, setShowError] = useState(false);
    const location = useLocation();

    const [searchParams] = useSearchParams();
    const message = searchParams.get('message');


    let verificationMessage;
    if (message === 'verification_success') {
        verificationMessage = t('verificationMessages.verificationSuccess');
    } else if (message === 'already_verified') {
        verificationMessage = t('verificationMessages.alreadyVerified');
    } else if (message === 'link_broken') {
        let resendLink = searchParams.get('resendLink');
        if (resendLink) {
            verificationMessage = <>{t('verificationMessages.linkBroken')} <a
                href={resendLink}>{t('verificationMessages.clickHere')}</a> {t('verificationMessages.toResend')}</>;
        }
    }

    useEffect(() => {
        setShowModal(location.pathname === '/login');
    }, [location]);

    const [loginInput, setLoginInput] = useState<LoginProps>({
        email: "",
        password: "",
    })

    if (!showModal) {
        return null;
    }

    const modalFormInputLabel = showError ? "modal-form-label-error" : "modal-form-label"

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setShowError(false)
        const email = event.target.value;
        setLoginInput((prevState) => ({...prevState, email}));
    };

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setShowError(false)
        const password = event.target.value;
        setLoginInput((prevState) => ({...prevState, password}));
    };

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const toggleSignUpModal = () => {
        setShowSignUpModal(!showSignUpModal);
    };


    const openSignUpModal = () => {
        setShowSignUpModal(true);
    };

    const toggleForgotPasswordModal = () => {
        setShowForgotPasswordModal(!showForgotPasswordModal);
    };

    const closeModal = () => {
        setShowError(false);
        setShowSignUpModal(false);
        setShowModal(false);
        navigate("/");
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleLoginSubmission = (e: React.FormEvent) => {
        e.preventDefault();

        // Use the login function from the context
        login(loginInput.email, loginInput.password)
            .then(() => {

                navigate(ROUTES.DASHBOARD);
            })
            .catch((error) => {
                setShowError(true);
            });
    };

    const handleGoogleLogin = () => {
        googleLogin()
    };

    return (
        <>
            {location.pathname !== '/login' && (
                <button className={"modal-login-button"} id={"modal-login-button"} onClick={toggleModal}>
                    {t('main.landingPage.loginModal.getStartedButton')}
                </button>
            )}

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={stopPropagation}>

                        <button className={"modal-close-button"} onClick={closeModal}><CloseIcon className="svg_icons"/>
                        </button>

                        <div className={"modal-logo"}>
                            <img src={logo} alt={"Manage My Cloud Logo"}/>
                        </div>

                        <div className={"modal-form-container"}>

                            <div className={"modal-description"}>
                                {t('main.landingPage.loginModal.mainTextOne')}
                                <br/>
                                {t('main.landingPage.loginModal.mainTextTwo')}
                            </div>

                            <button className={"modal-login-google-button"} onClick={handleGoogleLogin}>
                                <img className={"modal-login-google-logo"} src={googleLogo} alt={"Google Logo"}/>
                                {t('main.landingPage.loginModal.loginUsingGoogleButton')}
                            </button>

                            <div className={"or-separator"}>
                                <span>Or</span>
                            </div>

                            <form className={"modal-form"} onSubmit={handleLoginSubmission}>
                                <label className={modalFormInputLabel}>
                                    <input className={"modal-form-input"}
                                           type="email"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}
                                           onChange={handleEmailChange}
                                           id={"login-form-email"}/>
                                    <EmailIcon/>
                                </label>
                                <label className={modalFormInputLabel}>
                                    <input className={"modal-form-input"}
                                           type="password"
                                           placeholder={"Enter your password"}
                                           onClick={stopPropagation}
                                           onChange={handlePasswordChange}
                                           id={"login-form-password"}/>
                                    <LockIcon/>
                                </label>
                                {showError && (
                                    <div className={"modal-form-error"}>
                                        {t('main.landingPage.loginModal.invalidUserOrEmailError')}
                                    </div>
                                )}
                                {verificationMessage && message !== "verification_success" ? (
                                        <div className={"modal-form-error"}>
                                            {verificationMessage}
                                        </div>
                                    ) :
                                    <div className={"modal-form-success"}>
                                        {verificationMessage}
                                    </div>
                                }

                                <button className={"modal-form-submit-button"} type="submit">
                                    {t('main.landingPage.loginModal.loginButton')}
                                </button>
                            </form>

                            <div className={"separator"}></div>

                            <div className={"sign-up-login-container"}>
                                <button className={"modal-login-reset-signup"} id={"reset-password-button"}
                                        onClick={toggleForgotPasswordModal}>
                                    {t('main.landingPage.loginModal.resetPasswordText')}
                                </button>
                                <button className={"modal-login-reset-signup"} id={"signup-button"}
                                        onClick={toggleSignUpModal}>
                                    {t('main.landingPage.loginModal.signUpText')}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

            )}
            {showSignUpModal && (<SignUpModal
                    setShowSignUpModal={setShowSignUpModal}/>
            )}
            {showForgotPasswordModal && (
                <ResetPasswordModal
                    setForgotPasswordModal={setShowForgotPasswordModal}/>
            )}
        </>
    );

}
export default LoginModal;
