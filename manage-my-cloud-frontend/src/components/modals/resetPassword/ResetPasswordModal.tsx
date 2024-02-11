import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import "./ResetPasswordModal.css";
import "../Modal.css";
import React, {useState} from "react";
import {buildAxiosRequest} from "../../helpers/AxiosHelper";
import LockIcon from "@mui/icons-material/Lock";
import CloseIcon from "@mui/icons-material/Close";

interface ForgotPasswordModalProps {
    setForgotPasswordModal: React.Dispatch<React.SetStateAction<boolean>>;
}

interface ConfirmationProps {
    confirmationMessage: string | null;
}

interface ShowErrorProps {
    errorMessage: string | null;
}

interface ResetPasswordProps {
    email: string;
    newPassword: string;
    confirmPassword: string;
}

export const ResetPasswordModal: React.FC<ForgotPasswordModalProps> = ({
                                                                           setForgotPasswordModal,
                                                                       }) => {
    const [confirmationEmailMessage, setConfirmationEmailMessage] = useState<ConfirmationProps>({confirmationMessage: null});
    const [resetPasswordInput, setResetPasswordInput] = useState<ResetPasswordProps>({
        email: "",
        newPassword: "",
        confirmPassword: "",
    });
    const [showError, setShowError] = useState<ShowErrorProps>({errorMessage: null});
    const modalFormInputLabel = showError ? "modal-form-label-error" : "modal-form-label"

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const email = event.target.value;
        setResetPasswordInput((prevState) => ({...prevState, email}));
        setShowError({errorMessage: null});
    };

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setResetPasswordInput((prevState) => ({...prevState, newPassword: password}));
        setShowError({errorMessage: null});
    };

    const handleConfirmationPasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const confirmationPassword = event.target.value;
        setResetPasswordInput((prevState) => ({...prevState, confirmPassword: confirmationPassword}));
        setShowError({errorMessage: null});
    };

    const closeForgotPassword = () => {
        setForgotPasswordModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleResetPasswordSubmission = (e: React.FormEvent) => {
        const {email, newPassword, confirmPassword} = resetPasswordInput;
        e.preventDefault();

        if (email && newPassword && confirmPassword) {
            if (newPassword == confirmPassword) {
                buildAxiosRequest("POST", "/register/resetUserPassword", resetPasswordInput).then((response) => {
                    console.log(response);
                    setConfirmationEmailMessage((prevState) => ({...prevState, confirmationMessage: response.data}));
                }).catch((error) => {
                    setShowError((prevState) => ({...prevState, errorMessage: error}));
                });
            } else {
                setShowError((prevState) => ({...prevState, errorMessage: "Passwords do not match"}));
            }
        } else {
            setShowError((prevState) => ({...prevState, errorMessage: "Please fill in all the fields"}));
        }

    };

    return (
        <>
            <div className="modal-overlay" onClick={closeForgotPassword}>
                <div className="modal" onClick={stopPropagation}>

                    <button className={"modal-close-button"} onClick={closeForgotPassword}> <CloseIcon/> </button>

                    <div className={"forgot-password-modal-logo"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    {confirmationEmailMessage.confirmationMessage == null && (
                        <div className={"modal-form-container"}>

                            <div className={"modal-title"}>
                                Reset Password
                            </div>

                            <div className={"modal-description"}>
                                Enter your email and passwords and we will send you a password reset link.
                            </div>

                            <form className={"modal-form"}>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="email"
                                           placeholder={"Enter your email Address"}
                                           onClick={stopPropagation}
                                           onChange={handleEmailChange}/>
                                    <EmailIcon/>
                                </label>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="password"
                                           placeholder={"Enter your new password"}
                                           onClick={stopPropagation}
                                           onChange={handlePasswordChange}/>
                                    <LockIcon/>
                                </label>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="password"
                                           placeholder={"Confirm your new password"}
                                           onClick={stopPropagation}
                                           onChange={handleConfirmationPasswordChange}/>
                                    <LockIcon/>
                                </label>
                                {showError.errorMessage !== null && (
                                    <div className={"modal-form-error"}>
                                        {showError.errorMessage}
                                    </div>
                                )}
                                <button className={"modal-form-submit-button"} type="submit"
                                        onClick={handleResetPasswordSubmission}>Reset
                                    Password
                                </button>
                            </form>
                        </div>
                    )}
                    {confirmationEmailMessage.confirmationMessage !== null ? (
                        <div className={"confirmation-container"}>
                            <div className={"modal-title"}>
                                Reset Link Sent
                            </div>
                            <div className={"modal-confirmation-email"}>
                                {confirmationEmailMessage.confirmationMessage}
                            </div>
                        </div>
                    ) : null}
                </div>
            </div>
        </>
    )

}