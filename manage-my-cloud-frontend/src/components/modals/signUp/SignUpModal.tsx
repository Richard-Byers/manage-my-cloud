import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import BadgeIcon from '@mui/icons-material/Badge';
import "./SignUpModal.css";
import "../Modal.css";
import React, {useState} from "react";
import {buildAxiosRequest} from "../../helpers/AxiosHelper";

interface SignUpProps {
    setShowSignUpModal: React.Dispatch<React.SetStateAction<boolean>>;
}

interface ConfirmationProps {
    confirmationMessage: string | null;
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

    const handleFirstNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const firstName = event.target.value;
        setSignupInput((prevState) => ({...prevState, firstName}));
    };

    const handleLastNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const lastName = event.target.value;
        setSignupInput((prevState) => ({...prevState, lastName}));
    };

    const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const email = event.target.value;
        setSignupInput((prevState) => ({...prevState, email}));
    };

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setSignupInput((prevState) => ({...prevState, password}));
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
                setConfirmationEmailMessage((prevState) => ({...prevState, confirmationMessage: response.data}));
            }).catch((error) => {
                
            });
        } else {
            console.error("Please fill in all the fields");
        }
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeSignUpModal}>
                <div className="modal" onClick={stopPropagation}>

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

                                <button className={"modal-form-submit-button"} type="submit"
                                        onClick={handleSignupSubmission}>Sign Up
                                </button>
                            </form>
                        </div>
                    )}
                    {confirmationEmailMessage.confirmationMessage !== null ? (
                        <div className={"confirmation-container"}>
                            <div className={"modal-title"}>
                                Confirmation Sent
                            </div>
                            <div className={"modal-confirmation-email"}>
                                <div>Confirmation email has been sent to:</div>
                                <div>{confirmationEmailMessage.confirmationMessage}</div>
                                <div>Please check your inbox</div>
                            </div>
                        </div>
                    ) : null}
                </div>
            </div>
        </>
    )

}