import React, {useState} from "react";
import {AuthData} from "../../routing/AuthWrapper";
import "./RemoveConnectionModal.css";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import CloseIcon from "@mui/icons-material/Close";
import ToolTip from "../../ui_components/ToolTip";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import {useTranslation} from "react-i18next";

interface RemoveConnectionModalProps {
    connectionProvider: string
    driveEmail: string
}

const RemoveConnectionModal: React.FC<RemoveConnectionModalProps> = ({connectionProvider, driveEmail}) => {

    const {user, refreshUser} = AuthData();
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

    const handleUnlink = () => {

        const headers = {
            Authorization: `Bearer ${user?.token}`
        }

        buildAxiosRequestWithHeaders('DELETE', `/unlink-drive?email=${user?.email}&provider=${connectionProvider}&driveEmail=${driveEmail}`, headers, {}).then(() => {
            refreshUser(user?.email);
        })
    }

    return (
        <>
            <button className={"remove-connections-unlink-button"} id={"unlink-drive-button"}
                    onClick={toggleModal}>Unlink
            </button>
            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="remove-connections-modal" onClick={stopPropagation}>

                        <button className={"modal-close-button"} onClick={closeModal}>
                            <CloseIcon className="svg_icons"/>
                        </button>

                        <p className={"unlink-confirmation-text"} id={"unlink-confirmation-text"}>
                            Unlink the {driveEmail} {connectionProvider} account?
                        </p>
                        <button className={"no-button"} onClick={closeModal}>No</button>
                        <button className={"yes-button"} id={"confirm-unlink"} onClick={handleUnlink}>Yes</button>
                    </div>
                </div>
            )}
        </>
    )

}

export default RemoveConnectionModal;