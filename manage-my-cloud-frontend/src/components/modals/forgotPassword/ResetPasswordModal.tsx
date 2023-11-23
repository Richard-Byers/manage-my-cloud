import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import "./ResetPasswordModal.css";
import "../Modal.css";
import React, {useState} from "react";

interface ForgotPasswordModalProps {
    setForgotPasswordModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export const ResetPasswordModal: React.FC<ForgotPasswordModalProps> = ({
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

                    <div className={"forgot-password-modal-logo"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    {!showForgotPassword && (
                        <div className={"modal-form-container"}>

                            <div className={"modal-title"}>
                                Reset Password
                            </div>

                            <div className={"modal-description"}>
                                Enter your email address and
                                <br/>
                                we will send you a password reset link.
                            </div>

                            <form className={"modal-form"}>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="text"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}/>
                                    <EmailIcon/>
                                </label>
                                <button className={"modal-form-submit-button"} type="submit"
                                        onClick={showConfirmationEmail}>Reset
                                    Password
                                </button>
                            </form>
                        </div>
                    )}
                    {showForgotPassword && (
                        <div className={"confirmation-container"}>
                            <div className={"modal-title"}>
                                Reset Link Sent
                            </div>
                            <div className={"modal-confirmation-email"}>
                                Password reset link has been sent to:
                                <br/>
                                Johndoe@gmail.com
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </>
    )

}