import React, {useState} from "react";
import "./AddConnectionsModal.css";
import LinkOneDrive from "../../pages/manage_connections/LinkOneDrive";
import LinkGoogleDrive from "../../pages/manage_connections/LinkGoogleDrive";
import CloseIcon from "@mui/icons-material/Close";

const AddConnectionsModal = () => {
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

                        <p>Link with one of our available providers below</p>

                        <LinkOneDrive/>
                        <LinkGoogleDrive/>
                    </div>
                </div>
            )}
        </>
    );
};

export default AddConnectionsModal;
