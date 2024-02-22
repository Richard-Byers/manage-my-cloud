import React, {useState} from "react";
import "./AddConnectionsModal.css";
import LinkOneDrive from "../../pages/manage_connections/LinkOneDrive";
import LinkGoogleDrive from "../../pages/manage_connections/LinkGoogleDrive";
import {Success} from "../../helpers/Success";
import CloseIcon from "@mui/icons-material/Close";

interface AddConnectionsModalProps {
    oneDrive: boolean | undefined;
    googleDrive: boolean | undefined;
}

const AddConnectionsModal: React.FC<AddConnectionsModalProps> = ({oneDrive, googleDrive}) => {
    const [showModal, setShowModal] = useState(false);

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
                    <div className="add-connections-modal">

                        <button className={"modal-close-button"} onClick={closeModal}>
                            <CloseIcon className="svg_icons"/>
                        </button>

                        {oneDrive && googleDrive ?
                            <div className={"all-drives-linked-container"}>
                                <button className={"modal-close-button"} onClick={closeModal}>
                                    <CloseIcon className="svg_icons" sx={{stroke:"none", fill:"none"}}/>
                                </button>
                                <p>All supported drives have been linked</p>
                                <Success/>
                            </div>
                            :
                            <p>Link with one of our available providers below</p>}

                        {oneDrive ? null : <LinkOneDrive/>}
                        {googleDrive ? null : <LinkGoogleDrive/>}
                    </div>
                </div>
            )}
        </>
    );
};

export default AddConnectionsModal;
