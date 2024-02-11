import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import BadgeIcon from '@mui/icons-material/Badge';
import "./SignUpModal.css";
import "../Modal.css";
import React, {useState} from "react";
import {buildAxiosRequest} from "../../helpers/AxiosHelper";
import CloseIcon from "@mui/icons-material/Close";

interface SignUpProps {
    setShowSignUpModal: React.Dispatch<React.SetStateAction<boolean>>;
}

interface ConfirmationProps {
    confirmationMessage: string | null;
}

interface ShowErrorProps {
    errorMessage: string | null;
}

interface ShowSuccessProps {
    successMessage: string | null;
}

interface SignupProps {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}

export const SignUpModal: React.FC<SignUpProps> = ({
                                                       setShowSignUpModal,
                                                   }) => {
    const [confirmationEmailMessage, setConfirmationEmailMessage] = useState<ConfirmationProps>({confirmationMessage: null});
    const [signupInput, setSignupInput] = useState<SignupProps>({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        role: "USER",
    });
    const [showError, setShowError] = useState<ShowErrorProps>({errorMessage: null});
    const [emailConfirmation, setShowEmailConfirmation] = useState<ShowSuccessProps>({successMessage: null});

    const handleFirstNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const firstName = event.target.value;
        setSignupInput((prevState) => ({...prevState, firstName}));
        setShowError({errorMessage: null});
    };

    const handleLastNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const lastName = event.target.value;
        setSignupInput((prevState) => ({...prevState, lastName}));
        setShowError({errorMessage: null});
    };

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const email = event.target.value;
        setSignupInput((prevState) => ({...prevState, email}));
        setShowError({errorMessage: null});
    };

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setSignupInput((prevState) => ({...prevState, password}));
        setShowError({errorMessage: null});
    };

    const closeSignUpModal = () => {
        setShowSignUpModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const handleSignupSubmission = (e: React.FormEvent) => {
        const {firstName, lastName, email, password} = signupInput;
        e.preventDefault();

        if (firstName && lastName && email && password) {

            buildAxiosRequest("POST", "/register", signupInput).then((response) => {
                setShowEmailConfirmation((prevState) => ({successMessage: response.data}));
            }).catch((error) => {
            });
        } else {
            setShowError((prevState) => ({...prevState, errorMessage: "Please fill in all the fields"}));
        }
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeSignUpModal}>
                <div className="modal" onClick={stopPropagation}>

                    <button className={"modal-close-button"} onClick={closeSignUpModal}><CloseIcon/></button>

                    <div className={"modal-logo-signup"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    {confirmationEmailMessage.confirmationMessage == null && (
                        <div className={"modal-form-container"}>

                            <div className={"modal-title"}>
                                Sign up
                            </div>

                            <div className={"modal-description"}>
                                Are you ready to start saving money
                                <br/>
                                and help the environment?
                            </div>

                            <form className={"modal-form"}>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="text"
                                           placeholder={"Enter your First Name"}
                                           onClick={stopPropagation}
                                           onChange={handleFirstNameChange}/>
                                    <BadgeIcon/>
                                </label>
                                <label className={"modal-form-label"}>
                                    <input className={"modal-form-input"}
                                           type="text"
                                           placeholder={"Enter your Last Name"}
                                           onClick={stopPropagation}
                                           onChange={handleLastNameChange}/>
                                    <BadgeIcon/>
                                </label>
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
                                           placeholder={"Enter your password"}
                                           onClick={stopPropagation}
                                           onChange={handlePasswordChange}/>
                                    <LockIcon/>
                                </label>

                                {showError.errorMessage !== null && (
                                    <div className={"modal-form-error"}>
                                        {showError.errorMessage}
                                    </div>
                                )}

                                {emailConfirmation.successMessage && (
                                    <div className={"modal-form-success"}>
                                        Confirmation email sent to:
                                        <br/>
                                        {emailConfirmation.successMessage}
                                    </div>
                                )}

                                <button className={"modal-form-submit-button"} type="submit"
                                        onClick={handleSignupSubmission}>Sign Up
                                </button>
                            </form>
                        </div>
                    )}
                    {/*{confirmationEmailMessage.confirmationMessage !== null ? (*/}
                    {/*    <div className={"confirmation-container"}>*/}
                    {/*        <div className={"modal-title"}>*/}
                    {/*            Confirmation Sent*/}
                    {/*        </div>*/}
                    {/*        <div className={"modal-confirmation-email"}>*/}
                    {/*            <div>Confirmation email has been sent to:</div>*/}
                    {/*            <div>{confirmationEmailMessage.confirmationMessage}</div>*/}
                    {/*            <div>Please check your inbox</div>*/}
                    {/*        </div>*/}
                    {/*    </div>*/}
                    {/*) : null}*/}
                </div>
            </div>
        </>
    )

}