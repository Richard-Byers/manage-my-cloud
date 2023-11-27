import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import "./LoginModal.css";
import "../Modal.css";
import logo from "../../images/managemycloudlogo.png";
import googleLogo from "../../images/login/google.png";
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import {SignUpModal} from "../signUp/SignUpModal";
import {ResetPasswordModal} from "../forgotPassword/ResetPasswordModal";
import { useGoogleLogin } from '@react-oauth/google';

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

    // TODO: Implement login functionality
    const handleLoginClick = () => {
        navigate('/dashboard');
    };

    const login = useGoogleLogin({
        onSuccess: codeResponse => {
            console.log(codeResponse);
            const authCode = codeResponse.code; // or codeResponse.authCode, depending on the structure of codeResponse
    
            // Send the code to the server
            fetch('http://localhost:8080/storeauthcode', {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Content-Type': 'application/octet-stream; charset=utf-8'
                },
                body: authCode
            })
            .then(response => response.json())
            .then(data => {
                // Handle or verify the server response
                console.log(data);
                // Navigate to the dashboard
                navigate('/dashboard');
            })
            .catch(error => {
                // Handle the error
                console.error('Error:', error);
            });
        },
        flow: 'auth-code',
    });

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

                        <div className={"modal-form-container"}>

                            <div className={"modal-description"}>
                                Are you ready to start saving money
                                <br/>
                                and help the environment?
                            </div>

                            <button className={"modal-login-google-button"} onClick={() => login()}>
                                <img className={"modal-login-google-logo"} src={googleLogo} alt={"Google Logo"}/>
                                Log in using Google
                            </button>
                           

                            <div className={"or-separator"}>
                                <span>Or</span>
                            </div>

                            <form className={"modal-form"}>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="text"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}/>
                                    <EmailIcon/>
                                </label>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="password"
                                           placeholder={"Enter your password"}
                                           onClick={stopPropagation}/>
                                    <LockIcon/>
                                </label>
                                <button className={"modal-form-submit-button"} type="submit"
                                        onClick={handleLoginClick}>Login
                                </button>
                            </form>

                            <div className={"separator"}></div>

                            <div className={"sign-up-login-container"}>
                                <button className={"modal-login-reset-signup"} onClick={toggleForgotPasswordModal}>
                                    Reset Password
                                </button>
                                <button className={"modal-login-reset-signup"} onClick={toggleSignUpModal}>Sign
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
                <ResetPasswordModal
                    setForgotPasswordModal={setShowForgotPasswordModal}/>
            )}
        </>
    );
};

export default LoginModal;
