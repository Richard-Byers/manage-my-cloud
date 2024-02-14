import React, { useState } from "react";
import { AuthData } from "../../routing/AuthWrapper";
import './DeleteAccountModal.css';
import "../Modal.css";
import { useTranslation } from 'react-i18next';
import {buildAxiosRequest, buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";

function DeleteAccountModal() {
    const {user, logout} = AuthData();
    const [showModal, setShowModal] = useState(false);
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const { t } = useTranslation();

    const resetModal = () => {
        setPassword("");
        setConfirmPassword("");
        setErrorMessage("");
        setShowModal(false);
    };

    const handleDeleteAccount = async () => {
        if (password === "" && confirmPassword === "") {
            setErrorMessage(t('main.deleteAccountModal.errorMessage.enterBothPasswords'));
            return;
        }

        if (password === "" || confirmPassword === "") {
            setErrorMessage(t('main.deleteAccountModal.errorMessage.fillBothPasswords'));
            return;
        }

        if (password !== confirmPassword) {
            setErrorMessage(t('main.deleteAccountModal.errorMessage.passwordsDoNotMatch'));
            return;
        }

        if (user) {
            try {
                const headers = {
                    Authorization: `Bearer ${user.token}`
                }
                await buildAxiosRequestWithHeaders("DELETE", "/delete-user", headers, {email: user.email, password: password});
                alert(t('main.deleteAccountModal.errorMessage.accountDeletedSuccessfully'));
                logout();
                setShowModal(false);
            } catch (error) {
                const err = error as any;
                if (err.response && err.response.data === 'Invalid password') {
                    setErrorMessage(t('main.deleteAccountModal.errorMessage.invalidPassword'));
                } else {
                    setErrorMessage(t('main.deleteAccountModal.errorMessage.errorDeletingAccount'));
                }
            }
        } else {
            throw new Error(t('main.deleteAccountModal.errorMessage.userNotLoggedIn'));
        }
    };
    const closeModal = () => {
        setShowModal(false);
    };

    return (
        <>
            <button className="actions-button" onClick={() => setShowModal(true)}>Delete Account</button>
            {showModal && (
                <div className="modal-overlay" onClick={resetModal}>
                    <div className="modal delete-account-modal" onClick={e => e.stopPropagation()}>
                        <h2>Delete Account</h2>
                        <input
                            type="password"
                            placeholder={t('main.deleteAccountModal.enterPassword')}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder={t('main.deleteAccountModal.confirmPassword')}
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                        {errorMessage && <div className="error-message">{errorMessage}</div>}
                        <button
                            onClick={handleDeleteAccount}
                        >{t('main.deleteAccountModal.confirm')}
                        </button>
                        <button onClick={() => setShowModal(false)}>{t('main.deleteAccountModal.cancel')}</button>
                    </div>
                </div>
            )}
        </>
    );
}

export default DeleteAccountModal;