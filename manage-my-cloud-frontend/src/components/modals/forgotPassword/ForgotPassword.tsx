import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import "../signUp/SignUpModal.css";
import "../login/LoginModal.css";
import React, {useState} from "react";

interface ForgotPasswordModalProps {
    setForgotPasswordModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export const ForgotPasswordModal: React.FC<ForgotPasswordModalProps> = ({
                                                                            setForgotPasswordModal,
                                                                        }) => {
    const [showForgotPassword, setShowForgotPassword] = useState(false);

    const showConfirmationEmail = () => {
        setShowForgotPassword(true);
    };

    const closeForgotPassword = () => {
        setForgotPasswordModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeForgotPassword}>
                <div className="modal" onClick={stopPropagation}>

                    <div className={"modal-logo-signup"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    {!showForgotPassword && (
                        <div className={"modal-signup-form-container"}>

                            <div className={"modal-signup-title"}>
                                Reset Password
                            </div>

                            <div className={"modal-login-description"}>
                                Enter your email address and
                                <br/>
                                we will send you a password reset link.
                            </div>

                            <form className={"modal-signup-form"}>
                                <label className={"login-label"}>
                                    <input className={"login-input"}
                                           type="text"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}/>
                                    <EmailIcon/>
                                </label>
                                <button className={"login-button"} type="submit" onClick={showConfirmationEmail}>Reset
                                    Password
                                </button>
                            </form>
                        </div>
                    )}
                    {showForgotPassword && (
                        <div className={"modal-confirmation-email"}>
                            Password reset link has been sent to:
                        </div>
                    )}
                </div>
            </div>
        </>
    )

}