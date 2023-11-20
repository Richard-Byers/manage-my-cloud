import React, {useState} from "react";
import "./LoginModal.css";
import logo from "../../images/managemycloudlogo.png";
import googleLogo from "../../images/login/google.png";
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import CloseIcon from '@mui/icons-material/Close';

const LoginModal: React.FC = () => {
    const [showModal, setShowModal] = useState(false);

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const closeModal = () => {
        setShowModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
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
                                <button className={"login-button"} type="submit">Login</button>
                            </form>

                            <div className={"separator"}></div>

                            <div className={"sign-up-login-container"}>
                                <a className={"modal-login-forgot-signup"} href={"#"}>Forgot Password</a>
                                <a className={"modal-login-forgot-signup"} href={"#"}>Sign Up</a>
                            </div>

                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default LoginModal;
