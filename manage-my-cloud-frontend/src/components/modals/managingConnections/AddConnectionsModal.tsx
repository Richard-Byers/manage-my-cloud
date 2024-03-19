import React, {useState} from "react";
import "./AddConnectionsModal.css";
import LinkOneDrive from "../../pages/manage_connections/LinkOneDrive";
import LinkGoogleDrive from "../../pages/manage_connections/LinkGoogleDrive";
import CloseIcon from "@mui/icons-material/Close";
import ToolTip from "../../ui_components/ToolTip";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import {useTranslation} from "react-i18next";

const AddConnectionsModal = () => {
    const {t} = useTranslation();
    const [showModal, setShowModal] = useState(false);

    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const closeModal = () => {
        setShowModal(false);
    };

    return (
        <>
            <button className={"add-connections-modal-button"} onClick={toggleModal}>
                +
            </button>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="add-connections-modal" onClick={stopPropagation}>

                        <button className={"modal-close-button"} onClick={closeModal}>
                            <CloseIcon className="svg_icons"/>
                        </button>

                        <p className={"link-with-supported-providers-text"} id={"link-with-supported-providers-text"}>
                            {t("main.manageConnectionsPage.addConnectionModal.linkWithSupportedProviderText")}
                            <ToolTip
                                message={t("main.tooltip.manageConnections.addConnectionsModalText")}
                                children={<HelpOutlineIcon/>}/>
                        </p>

                        <LinkOneDrive/>
                        <LinkGoogleDrive/>
                    </div>
                </div>
            )}
        </>
    );
};

export default AddConnectionsModal;
