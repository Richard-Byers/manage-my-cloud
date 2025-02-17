import React, {useState} from "react";
import {AuthData} from "../../routing/AuthWrapper";
import './DeleteAccountModal.css';
import "../Modal.css";
import {Trans, useTranslation} from 'react-i18next';
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";

function DeleteAccountModal() {
    const {user, logout} = AuthData();
    const [showModal, setShowModal] = useState(false);
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [isDeleting, setIsDeleting] = useState(false);
    const {t} = useTranslation();

    const resetModal = () => {
        setPassword("");
        setConfirmPassword("");
        setErrorMessage("");
        setSuccessMessage("");
        setIsDeleting(false);
        setShowModal(false);
    };


    const handleDeleteAccount = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsDeleting(true);

        if (user?.accountType !== "GOOGLE") {

            if (password === "" && confirmPassword === "") {
                setErrorMessage(t('main.deleteAccountModal.errorMessage.enterBothPasswords'));
                setIsDeleting(false);
                return;
            }

            if (password === "" || confirmPassword === "") {
                setErrorMessage(t('main.deleteAccountModal.errorMessage.fillBothPasswords'));
                setIsDeleting(false)
                return;
            }

            if (password !== confirmPassword) {
                setErrorMessage(t('main.deleteAccountModal.errorMessage.passwordsDoNotMatch'));
                setIsDeleting(false)
                return;
            }
        }

        if (user) {
            try {
                const headers = {
                    Authorization: `Bearer ${user.token}`
                }
                await buildAxiosRequestWithHeaders("DELETE", "/delete-user", headers, {
                    email: user.email,
                    password: password
                }).then((response) => {
                    if (response.status !== 200) {
                        setErrorMessage(t('main.deleteAccountModal.errorMessage.errorDeletingAccount'));
                        setIsDeleting(false)
                    } else {
                        setSuccessMessage(t('main.deleteAccountModal.errorMessage.accountDeletedSuccessfully'));

                        setTimeout(() => {
                            logout();
                            resetModal();
                        }, 2000);
                    }
                });
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
            <button className="actions-button" onClick={() => setShowModal(true)} id={"delete-account-button"}>Delete
                Account
            </button>
            {showModal && (
                <div className="modal-overlay" onClick={resetModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className={"modal-form-container"}>
                            <div className={"modal-description"}>
                                <Trans i18nKey='main.deleteAccountModal.modalDescription'/>
                                <br/>
                            </div>

                            <form className={"modal-form"} onSubmit={(e) => handleDeleteAccount(e)}>
                                <div className={"modal-form-group"}>
                                    <label className={"modal-form-label"}>
                                        <input className={"modal-form-input"}
                                               type="password"
                                               placeholder={t('main.deleteAccountModal.enterPassword')}
                                               value={password}
                                               onChange={(e) => {
                                                   setPassword(e.target.value)
                                                   setErrorMessage("")
                                               }}
                                               disabled={isDeleting}
                                        />
                                    </label>
                                </div>
                                <div className={"modal-form-group"}>
                                    <label className={"modal-form-label"}>
                                        <input className={"modal-form-input"}
                                               type="password"
                                               placeholder={t('main.deleteAccountModal.confirmPassword')}
                                               value={confirmPassword}
                                               onChange={(e) => {
                                                   setConfirmPassword(e.target.value)
                                                   setErrorMessage("")
                                               }}
                                               disabled={isDeleting}
                                        />
                                    </label>
                                </div>
                                {errorMessage &&
                                    <div className="modal-form-message" style={{color: '#ea1818'}}>{errorMessage}</div>}
                                {successMessage && <div className="modal-form-message"
                                                        style={{color: '#4caf50'}}>{successMessage}</div>}
                                <div className="button-container">
                                    <button
                                        className="modal-form-submit-button"
                                        type="submit"
                                        disabled={isDeleting}
                                    >{t('main.deleteAccountModal.confirm')}
                                    </button>
                                    <button
                                        className="modal-form-submit-button"
                                        onClick={() => {
                                            setShowModal(false)
                                            setErrorMessage("")
                                        }}
                                        disabled={isDeleting}
                                    >{t('main.deleteAccountModal.cancel')}</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default DeleteAccountModal;