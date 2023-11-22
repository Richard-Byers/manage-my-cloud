import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import "./SignUpModal.css";
import "../login/LoginModal.css";
import React, {useState} from "react";

interface SignUpProps {
    setShowSignUpModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export const SignUpModal: React.FC<SignUpProps> = ({
                                                       setShowSignUpModal,
                                                   }) => {
    const [showConfirmation, setShowConfirmation] = useState(false);

    const showConfirmationEmail = () => {
        setShowConfirmation(true);
    };

    const closeSignUpModal = () => {
        setShowSignUpModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeSignUpModal}>
                <div className="modal" onClick={stopPropagation}>

                    <div className={"modal-logo-signup"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    {!showConfirmation && (
                        <div className={"modal-signup-form-container"}>

                            <div className={"modal-signup-title"}>
                                Sign up
                            </div>

                            <div className={"modal-login-description"}>
                                Are you ready to start saving money
                                <br/>
                                and help the environment?
                            </div>

                            <form className={"modal-signup-form"}>
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
                                <button className={"login-button"} type="submit" onClick={showConfirmationEmail}>Sign Up
                                </button>
                            </form>
                        </div>
                    )}
                    {showConfirmation && (
                        <div className={"modal-confirmation-email"}>
                            Confirmation email has been sent to:
                        </div>
                    )}
                </div>
            </div>
        </>
    )

}