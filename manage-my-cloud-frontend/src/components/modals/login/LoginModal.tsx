import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import "./LoginModal.css";
import logo from "../../images/managemycloudlogo.png";
import googleLogo from "../../images/login/google.png";
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import {SignUpModal} from "../signUp/SignUpModal";
import {ForgotPasswordModal} from "../forgotPassword/ForgotPassword";

const LoginModal: React.FC = () => {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const [showSignUpModal, setShowSignUpModal] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const toggleSignUpModal = () => {
        closeModal();
        setShowSignUpModal(!showSignUpModal);
    };

    const toggleForgotPasswordModal = () => {
        closeModal();
        setShowForgotPasswordModal(!showForgotPasswordModal);
    };

    const closeModal = () => {
        setShowSignUpModal(false);
        setShowModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleLoginClick = () => {
        navigate('/dashboard');
    };

    return (
        <>
            <button className={"modal-login-button"} onClick={toggleModal}>
                Login
            </button>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={stopPropagation}>

                        <div className={"modal-logo"}>
                            <img src={logo} alt={"Manage My Cloud Logo"}/>
                        </div>

                        <div className={"modal-login-form-container"}>

                            <div className={"modal-login-description"}>
                                Are you ready to start saving money
                                <br/>
                                and help the environment?
                            </div>

                            <button className={"modal-login-google-button"}>
                                <img className={"modal-login-google-logo"} src={googleLogo} alt={"Google Logo"}/>
                                Log in using Google
                            </button>

                            <div className={"or-separator"}>
                                <span>Or</span>
                            </div>

                            <form className={"modal-login-form"}>
                                <label className={"login-label"}>
                                    <input className={"login-input"}
                                           type="text"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}/>
                                    <EmailIcon/>
                                </label>
                                <label className={"login-label"}>
                                    <input className={"login-input"}
                                           type="password"
                                           placeholder={"Enter your password"}
                                           onClick={stopPropagation}/>
                                    <LockIcon/>
                                </label>
                                <button className={"login-button"} type="submit" onClick={handleLoginClick}>Login</button>
                            </form>

                            <div className={"separator"}></div>

                            <div className={"sign-up-login-container"}>
                                <button className={"modal-login-forgot-signup"} onClick={toggleForgotPasswordModal}>
                                    Forgot Password
                                </button>
                                <button className={"modal-login-forgot-signup"} onClick={toggleSignUpModal}>Sign
                                    Up
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
                <ForgotPasswordModal
                    setForgotPasswordModal={setShowForgotPasswordModal}/>
            )}
        </>
    );
};

export default LoginModal;
