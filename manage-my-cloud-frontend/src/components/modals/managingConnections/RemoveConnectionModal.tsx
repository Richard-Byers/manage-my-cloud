import React, {useState} from "react";
import {AuthData} from "../../routing/AuthWrapper";
import "./RemoveConnectionModal.css";
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import CloseIcon from "@mui/icons-material/Close";

interface RemoveConnectionModalProps {
    connectionProvider: string
}

const RemoveConnectionModal: React.FC<RemoveConnectionModalProps> = ({connectionProvider}) => {

    const {user, refreshUser} = AuthData();
    const [showModal, setShowModal] = useState(false);

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

        buildAxiosRequestWithHeaders('DELETE', `/unlink-drive?email=${user?.email}&provider=${connectionProvider}`, headers, {}).then(() => {
            refreshUser(user?.email);
        })
    }

    return (
        <>
            <button className={"remove-connections-unlink-button"} onClick={toggleModal}>Unlink</button>
            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="remove-connections-modal">

                        <button className={"modal-close-button"} onClick={closeModal}>
                            <CloseIcon className="svg_icons"/>
                        </button>

                        <p>Are you sure you wish to unlink {connectionProvider} account?</p>
                        <button className={"no-button"} onClick={closeModal}>No</button>
                        <button className={"yes-button"} onClick={handleUnlink}>Yes</button>
                    </div>
                </div>
            )}
        </>
    )

}

export default RemoveConnectionModal;