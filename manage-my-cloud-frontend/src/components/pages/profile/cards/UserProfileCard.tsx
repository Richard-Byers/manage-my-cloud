import React from "react";
import './UserProfileCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import { useTranslation } from 'react-i18next';
import ProfileImgButton from "./ProfileImgButton";
import UpdateDetailsModal  from "../../../modals/profile/UpdateDetailsModal";

function UserProfileCard() {
    const {user, logout} = AuthData();
    const { t } = useTranslation();

    return (
        <div className={"user-profile-card-content"}>
            <div className={'profile-card-buttons'}>
                <ProfileImgButton />
            </div>
            <div className='user-info'>
                <div
                    className='user-profile-card-data-label'>{t('main.userProfileCard.firstName')} : {user?.firstName}</div>
                <div className='user-profile-card-data-label'>{t('main.userProfileCard.lastName')} : {user?.lastName}</div>
                <div
                    className='user-profile-card-data-label'>{t('main.userProfileCard.emailAddress')} : {user?.email}</div>

            </div>
            <div className={'profile-card-buttons'}>
                <UpdateDetailsModal />
                <button className='logout-btn' onClick={logout}>{t('main.userProfileCard.logout')}</button>
            </div>

        </div>
    );
}
export default UserProfileCard;
