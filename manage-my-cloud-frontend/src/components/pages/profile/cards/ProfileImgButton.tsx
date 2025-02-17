import React, {useState} from "react";
import "./ProfileImgButton.css";
import ChangeProfileImageModal from "../../../modals/profile/changeProfileImageModal";
import {AuthData} from "../../../routing/AuthWrapper";
import CloseIcon from "@mui/icons-material/Close";

const ProfileImgButton: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const {user} = AuthData();
    const openModal = () => {
        if (user?.accountType === 'google') {
            return;
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };


    const profileImage = localStorage.getItem('profileImage') || "manage-my-cloud-frontend/src/components/images/profile_picture.png";

    return (
        <>
            <button className="profile-img-button" id={"profile-img-button"} onClick={openModal} style={{backgroundImage: `url(${profileImage})`}}>
            </button>
            {isModalOpen && <ChangeProfileImageModal setIsModalOpen={setIsModalOpen} />}
        </>
    );
};

export default ProfileImgButton;