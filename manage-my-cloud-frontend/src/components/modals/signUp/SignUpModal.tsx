import logo from "../../images/managemycloudlogo.png";
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";
import BadgeIcon from '@mui/icons-material/Badge';
import "./SignUpModal.css";
import "../Modal.css";
import React, {useState} from "react";
import {buildAxiosRequest} from "../../helpers/AxiosHelper";
import CloseIcon from "@mui/icons-material/Close";
import {useTranslation} from "react-i18next";

interface SignUpProps {
    setShowSignUpModal: React.Dispatch<React.SetStateAction<boolean>>;
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
    const {t} = useTranslation();
    const [signupInput, setSignupInput] = useState<SignupProps>({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        role: "USER",
    });
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [showError, setShowError] = useState<ShowErrorProps>({errorMessage: null});
    const [emailConfirmation, setShowEmailConfirmation] = useState<ShowSuccessProps>({successMessage: null});

    const handleFirstNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const firstName = formatName(event.target.value);
        setSignupInput((prevState) => ({...prevState, firstName}));
        setShowError({errorMessage: null});
    };

    const handleLastNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const lastName = formatName(event.target.value);
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

    const handleConfirmPasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setConfirmPassword(event.target.value);
        setShowError({errorMessage: null});
    };

    const closeSignUpModal = () => {
        setShowSignUpModal(false);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };
    const formatName = (name: string): string => {
        return name.trim().charAt(0).toUpperCase() + name.trim().slice(1).toLowerCase();
    };

    const handleSignupSubmission = (e: React.FormEvent) => {
        setShowError(() => ({errorMessage: null}));
        setShowEmailConfirmation(() => ({successMessage: null}));
        const {firstName, lastName, email, password} = signupInput;
        e.preventDefault();

        if (password !== confirmPassword) {
            setShowError({errorMessage: "Passwords don't match"});
            return;
        }

        if (firstName && lastName && email && password) {

            buildAxiosRequest("POST", "/register", signupInput).then((response) => {
                if (response.data === "User already exists") {
                    setShowError((prevState) => ({errorMessage: "User already exists"}));
                } else {
                    setShowEmailConfirmation((prevState) => ({successMessage: response.data}));
                }
            }).catch((error) => {
            });
        } else {
            setShowError((prevState) => ({...prevState, errorMessage: "Please fill in all the fields"}));
        }
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeSignUpModal}>
                <div className="modal" id={"signup-modal"} onClick={stopPropagation}>

                    <button className={"modal-close-button"} onClick={closeSignUpModal}><CloseIcon
                        className={"svg_icons"}/></button>

                    <div className={"modal-logo-signup"}>
                        <img src={logo} alt={"Manage My Cloud Logo"}/>
                    </div>
                    <div className={"modal-form-container"}>

                        <div className={"modal-title"}>
                            {t("main.landingPage.signUpModal.signUpTitle")}
                        </div>

                        <div className={"modal-description"}>
                            {t("main.landingPage.signUpModal.mainTextOne")}
                            <br/>
                            {t("main.landingPage.signUpModal.mainTextTwo")}
                        </div>

                        <form className={"modal-form"} id={"signup-modal-form"}>
                            <label className={"modal-form-label"}>
                                <input className={"modal-form-input"}
                                       type="text"
                                       placeholder={"Enter your First Name"}
                                       onClick={stopPropagation}
                                       onChange={handleFirstNameChange}
                                       id={"signup-firstname-input"}/>
                                <BadgeIcon/>
                            </label>
                            <label className={"modal-form-label"}>
                                <input className={"modal-form-input"}
                                       type="text"
                                       placeholder={"Enter your Last Name"}
                                       onClick={stopPropagation}
                                       onChange={handleLastNameChange}
                                       id={"signup-lastname-input"}/>
                                <BadgeIcon/>
                            </label>
                            <label className={"modal-form-label"}>
                                <input className={"modal-form-input"}
                                       type="email"
                                       placeholder={"Enter your email Address"}
                                       onClick={stopPropagation}
                                       onChange={handleEmailChange}
                                       id={"signup-email-input"}/>
                                <EmailIcon/>
                            </label>
                            <label className={"modal-form-label"}>
                                <input className={"modal-form-input"}
                                       type="password"
                                       placeholder={"Enter your password"}
                                       onClick={stopPropagation}
                                       onChange={handlePasswordChange}
                                       id={"signup-password-input"}/>
                                <LockIcon/>
                            </label>

                            <label className={"modal-form-label"}>
                                <input className={"modal-form-input"}
                                       type="password"
                                       placeholder={"Confirm your password"}
                                       onClick={stopPropagation}
                                       onChange={handleConfirmPasswordChange}
                                       id={"signup-confirm-password-input"}/>
                                <LockIcon/>
                            </label>

                            {showError.errorMessage !== null && (
                                <div className={"modal-form-error"}>
                                    {showError.errorMessage}
                                </div>
                            )}

                            {emailConfirmation.successMessage && (
                                <div className={"modal-form-success"}>
                                    {t("main.landingPage.signUpModal.signUpConfirmationText")}
                                    <br/>
                                    {emailConfirmation.successMessage}
                                </div>
                            )}

                            <button className={"modal-form-submit-button"} id={"signup-modal-submit-button"} type="submit"
                                    onClick={handleSignupSubmission}>
                                {t("main.landingPage.signUpModal.signUpButton")}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </>
    )

}