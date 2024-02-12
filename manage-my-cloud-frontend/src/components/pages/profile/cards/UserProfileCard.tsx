import React from "react";
import './UserProfileCard.css';
import profileLogo from '../../../images/profile_picture.png';
import {AuthData} from "../../../routing/AuthWrapper";
import { useTranslation } from 'react-i18next';
import ProfileImgButton from "./ProfileImgButton";

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
                    className='user-profile-card-data-label'>{t('main.userProfileCard.firstName')}:{user?.firstName}</div>
                <div className='user-profile-card-data-label'>{t('main.userProfileCard.lastName')}:</div>
                <div
                    className='user-profile-card-data-label'>{t('main.userProfileCard.emailAddress')}:{user?.email}</div>
                <div className='user-profile-card-data-label'>
                    {t('main.userProfileCard.password')}: ********
                </div>
            </div>
            <div className={'profile-card-buttons'}>
                <button className='save-changes-btn'>{t('main.userProfileCard.updateProfileDetails')}</button>
                <button className='logout-btn' onClick={logout}>{t('main.userProfileCard.logout')}</button>
            </div>
        </div>
    );
}

export default UserProfileCard;
