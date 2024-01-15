import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import "./LoginModal.css";
import "../Modal.css";
import logo from "../../images/managemycloudlogo.png";
import googleLogo from "../../images/login/google.png";
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import {SignUpModal} from "../signUp/SignUpModal";
import {ResetPasswordModal} from "../resetPassword/ResetPasswordModal";
import { useGoogleLogin } from '@react-oauth/google';
import {AuthData} from "../../routing/AuthWrapper";

interface LoginProps {
    email: string;
    password: string;
}

const LoginModal: React.FC = () => {
    const {login} = AuthData();
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const [showSignUpModal, setShowSignUpModal] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);
    const [showError, setShowError] = useState(false);

    const [loginInput, setLoginInput] = useState<LoginProps>({
        email: "",
        password: "",
    });

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

    const handleLoginSubmission = (e: React.FormEvent) => {
        e.preventDefault();

        // Use the login function from the context
        login(loginInput.email, loginInput.password)
            .then(() => {

                navigate('/profile');
            })
            .catch((error) => {
                setShowError(true);
            });
    };

    const googleLogin = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            console.log(codeResponse);
            const authCode = codeResponse.code; 

            // Send the code to the server
            try {
                const response = await fetch('http://localhost:8080/storetoken', {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest',
                        'Content-Type': 'application/octet-stream; charset=utf-8'
                    },
                    body: authCode
                });
                const data = await response.json();
                localStorage.setItem('token', data.token); // Set the token
                navigate('/dashboard');
            } catch (error) {
                // Handle the error
                console.error('Error:', error);
            }
        },
        flow: 'auth-code',
        scope: 'https://www.googleapis.com/auth/drive',
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

                            <button className={"modal-login-google-button"} onClick={() => googleLogin()}>
                                <img className={"modal-login-google-logo"} src={googleLogo} alt={"Google Logo"}/>
                                Log in using Google
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
                                           onChange={handleEmailChange}/>
                                    <EmailIcon/>
                                </label>
                                <label className={modalFormInputLabel}>
                                    <input className={"modal-form-input"}
                                           type="password"
                                           placeholder={"Enter your password"}
                                           onClick={stopPropagation}
                                           onChange={handlePasswordChange}/>
                                    <LockIcon/>
                                </label>
                                {showError && (
                                    <div className={"modal-form-error"}>
                                        Invalid email or password
                                    </div>
                                )}
                                <button className={"modal-form-submit-button"} type="submit">
                                    Login
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
