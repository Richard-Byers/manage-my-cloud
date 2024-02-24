import React, { useState } from "react";
import { AuthData } from "../../routing/AuthWrapper";
import './UpdateDetailsModal.css';
import "../Modal.css";
import {Trans, useTranslation} from 'react-i18next';
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {ResetPasswordModal} from "../resetPassword/ResetPasswordModal";

function UpdateDetailsModal() {
    const {user, refreshUser} = AuthData();
    const [showModal, setShowModal] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);
    const {t} = useTranslation();

    const resetModal = () => {
        setFirstName("");
        setLastName("");
        setErrorMessage("");
        setSuccessMessage("");
        setShowModal(false);
    };

    const toggleForgotPasswordModal = () => {
        resetModal();
        setShowForgotPasswordModal(!showForgotPasswordModal);
    };

    const handleUpdateDetails = async (event: React.FormEvent) => {
        event.preventDefault();
        if (user) {
            if (!firstName && !lastName) {
                if (!successMessage) {
                    setErrorMessage(t('main.updateDetailsModal.errorMessage.enterSomething'));
                }
                return;
            }
            try {
                const headers = {
                    Authorization: `Bearer ${user.token}`
                }
                const body: {email: string, firstName?: string, lastName?: string} = {email: user.email};
                if (firstName) {
                    body.firstName = firstName;
                }
                if (lastName) {
                    body.lastName = lastName;
                }
                await buildAxiosRequestWithHeaders("POST", "/update-user-details", headers, body);
                if (!errorMessage) {
                    setSuccessMessage(t('main.updateDetailsModal.errorMessage.successMessage'));
                    refreshUser(user.email);
                    setTimeout(resetModal, 2000);
                }
            } catch (error) {
                if (!successMessage) {
                    setErrorMessage(t('main.updateDetailsModal.errorMessage.errorUpdatingDetails'));
                }
            }
        } else {
            throw new Error(t('main.updateDetailsModal.errorMessage.userNotLoggedIn'));
        }
    };

    return (
        <>
            <button className="modal-update-details-button" onClick={() => setShowModal(true)}>Update Details</button>
            {showModal && (
                <div className="modal-overlay" onClick={resetModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className={"modal-form-container"}>
                            <div className={"modal-description"}>
                                <Trans i18nKey='main.updateDetailsModal.enterNewName'/><br/>
                                <Trans i18nKey='main.updateDetailsModal.orBoth'/>
                            </div>

                            <form className={"modal-form"} onSubmit={(e) => handleUpdateDetails(e)}>
                                <div className={"modal-form-group"}>
                                <label className={"modal-form-label"}>
                                        <input className={"modal-form-input"}
                                               type="text"
                                               placeholder={t('main.updateDetailsModal.enterFirstName')}
                                               value={firstName}
                                               onChange={(e) => setFirstName(e.target.value)}
                                        />
                                    </label>
                                </div>
                                <div className={"modal-form-group"}>
                                    <label className={"modal-form-label"}>
                                        <input className={"modal-form-input"}
                                               type="text"
                                               placeholder={t('main.updateDetailsModal.enterLastName')}
                                               value={lastName}
                                               onChange={(e) => setLastName(e.target.value)}
                                        />
                                    </label>
                                </div>
                                {errorMessage && <div className="modal-form-message" style={{color: '#ea1818'}}>{errorMessage}</div>}
                                {successMessage && <div className="modal-form-message" style={{color: 'green'}}>{successMessage}</div>}
                                <div className="button-container">
                                    <button
                                        className="modal-form-submit-button"
                                        type="submit"
                                    >{t('main.updateDetailsModal.confirm')}
                                    </button>
                                    <button
                                        className="modal-form-submit-button"
                                        onClick={() => setShowModal(false)}>{t('main.updateDetailsModal.cancel')}</button>
                                </div>
                            </form>
                            <div className={"separator"}></div>
                            <div className={"sign-up-login-container"}>
                                <button className={"modal-login-reset-signup"} onClick={toggleForgotPasswordModal}>
                                    {t('main.updateDetailsModal.Reset')}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {showForgotPasswordModal && (
                <ResetPasswordModal
                    setForgotPasswordModal={setShowForgotPasswordModal}/>
            )}
        </>
    );
}

export default UpdateDetailsModal;