import React, { MouseEvent } from 'react';
import '../Modal.css';
import './SuccessModal.css';
import { useTranslation } from 'react-i18next';

interface SuccessModalProps {
    show: boolean;
    onClose: () => void;
}

function SuccessModal({ show, onClose }: SuccessModalProps) {
    const { t } = useTranslation();
    if (!show) {
        return null;
    }

    const stopPropagation = (e: MouseEvent) => {
        e.stopPropagation();
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={stopPropagation}>
                <button className="modal-close-button" onClick={onClose}>x</button>
                <h2 style={{color: 'green'}}>{t('main.successModal.title')}</h2>
            </div>
        </div>
    );
}

export default SuccessModal;