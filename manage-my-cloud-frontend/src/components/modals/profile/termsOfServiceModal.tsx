import React, { useState, useEffect } from 'react';
import './termsOfServiceModal.css';
import CloseIcon from '@mui/icons-material/Close';
import ReactMarkdown from 'react-markdown';
import {useTranslation} from "react-i18next";

interface TermsOfServiceModalProps {
    show: boolean;
    handleClose: () => void;
}

function TermsOfServiceModal({ show: showModal, handleClose: closeHandle }: TermsOfServiceModalProps) {
    const [show, setShow] = useState(showModal);
    const [termsOfServiceText, setTermsOfServiceText] = useState('');
    const { t } = useTranslation();

    useEffect(() => {
        if (show) {
            fetch('/termsOfService.txt')
                .then(response => response.text())
                .then(text => setTermsOfServiceText(text));
        }
    }, [show]);
    const handleClose = () => {
        setShow(false);
        closeHandle();
    };

    const handleShow = () => {
        setShow(true);
    };

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    return (
        <>
            <button className="actions-button"
                    onClick={handleShow} id={"terms-of-service-button"}>{t('main.termsOfServiceModal.viewTermsOfService')}</button>

            {show && (
                <div className="modal-overlay terms-of-service-modal" onClick={handleClose}>
                    <div className="modal modal-dialog" onClick={stopPropagation}>
                        <div className="modal-header">
                            <h5 className="modal-title">{t('main.termsOfServiceModal.termsOfServiceTitle')}</h5>

                        </div>
                        <div className="modal-body">
                            <ReactMarkdown>{termsOfServiceText}</ReactMarkdown>
                        </div>
                        <div className="modal-footer">

                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default TermsOfServiceModal;