import React from "react";
import './UserProfileCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import {useTranslation} from 'react-i18next';
import ProfileImgButton from "./ProfileImgButton";
import UpdateDetailsModal from "../../../modals/profile/UpdateDetailsModal";

function UserProfileCard() {
    const {user, logout} = AuthData();
    const {t} = useTranslation();

    return (
        <div className={"card-content"}>
            <div className={'profile-card-buttons'}>
                <ProfileImgButton/>
            </div>
            <span className={"preference-section-span"}>Personal Details</span>
            <div className='user-info'>
                <div
                    className='user-profile-card-data-label' id={"profile-firstname"}>{t('main.userProfileCard.firstName')} : {user?.firstName}</div>
                <div
                    className='user-profile-card-data-label' id={"profile-lastname"}>{t('main.userProfileCard.lastName')} : {user?.lastName}</div>
                <div
                    className='user-profile-card-data-label' id={"profile-email"}>{t('main.userProfileCard.emailAddress')} : {user?.email}</div>

            </div>
            <span className={"preference-section-span"}>Profile Actions</span>
            <div className={'profile-card-buttons'}>
                <UpdateDetailsModal/>
                <button className='logout-btn' id={"profile-logout-button"} onClick={logout}>{t('main.userProfileCard.logout')}</button>
            </div>

        </div>
    );
}

export default UserProfileCard;
