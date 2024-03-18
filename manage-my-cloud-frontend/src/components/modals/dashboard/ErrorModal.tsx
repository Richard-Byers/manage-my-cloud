import React from 'react';
import './ErrorModal.css';

interface ErrorModalProps {
    showModal: boolean;
    handleClose: () => void;
    errorMessage: string;
}

const ErrorModal: React.FC<ErrorModalProps> = ({ showModal, handleClose, errorMessage }) => {
    if (!showModal) {
        return null;
    }

    return (
        <div className='error-modal'>
            <div className='error-modal-content'>
                <div className='error-modal-body'>
                    <p>{errorMessage}</p>
                    <button onClick={handleClose}>Manage Connections</button>
                </div>
            </div>
        </div>
    );
}

export default ErrorModal;