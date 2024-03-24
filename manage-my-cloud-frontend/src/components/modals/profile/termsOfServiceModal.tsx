import React, {useEffect, useState} from 'react';
import './termsOfServiceModal.css';
import CloseIcon from '@mui/icons-material/Close';
import ReactMarkdown from 'react-markdown';
import {useTranslation} from "react-i18next";

interface TermsOfServiceModalProps {
    show: boolean;
    handleClose: () => void;
}

function TermsOfServiceModal({show: showModal, handleClose: closeHandle}: TermsOfServiceModalProps) {
    const [show, setShow] = useState(showModal);
    const [termsOfServiceText, setTermsOfServiceText] = useState('');
    const {t} = useTranslation();

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
                    onClick={handleShow}
                    id={"terms-of-service-button"}>{t('main.termsOfServiceModal.viewTermsOfService')}</button>

            {show && (
                <div className={"modal-overlay"} onClick={handleClose}>
                    <div className="modal" onClick={stopPropagation}>

                        <button className={"modal-close-button"} onClick={handleClose}>
                            <CloseIcon className="svg_icons"/>
                        </button>

                        <div className="modal-body">
                            <ReactMarkdown>{termsOfServiceText}</ReactMarkdown>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default TermsOfServiceModal;