import React from 'react';
import './ErrorModal.css';

interface ErrorModalProps {
    showModal: boolean;
    handleClose: () => void;
    errorMessage: string;
    buttonName: string;
}

const ErrorModal: React.FC<ErrorModalProps> = ({ showModal, handleClose, errorMessage, buttonName }) => {
    if (!showModal) {
        return null;
    }

    return (
        <div className='error-modal'>
            <div className='error-modal-content'>
                <div className='error-modal-body'>
                    <p>{errorMessage}</p>
                    <button onClick={handleClose}>{buttonName}</button>
                </div>
            </div>
        </div>
    );
}

export default ErrorModal;