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
        <div className='modal'>
            <div className='modal-content'>
                <div className='modal-body'>
                    <p>{errorMessage}</p>
                    <button onClick={handleClose}>Close</button>
                </div>
            </div>
        </div>
    );
}

export default ErrorModal;