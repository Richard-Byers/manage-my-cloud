import React, {useState} from "react";
import "./AddConnectionsModal.css";
import LinkOneDrive from "../../pages/manage_connections/LinkOneDrive";

interface AddConnectionsModalProps {
    oneDrive: boolean | undefined;
}

const AddConnectionsModal: React.FC<AddConnectionsModalProps> = ({oneDrive}) => {
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
                        <p>Link with one of our available providers below</p>
                        {!oneDrive ? (<LinkOneDrive/>) : null}
                    </div>
                </div>
            )}
        </>
    );
};

export default AddConnectionsModal;
