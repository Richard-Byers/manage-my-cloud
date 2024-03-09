import React, {useEffect, useState} from "react";
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';
import ProfileActionsCard from './cards/ProfileActionsCard';
import UserProfileCard from "./cards/UserProfileCard";
import ProfilePreferencesCard from "./cards/ProfilePreferencesCard";
import {useTranslation} from 'react-i18next';

const ProfilePage = () => {
    const {t} = useTranslation();
    const [activeCard, setActiveCard] = useState('userProfile');

    useEffect(() => {
        document.body.style.overflow = "hidden";
    }, []);

    const handleCardChange = (cardName: React.SetStateAction<string>) => {
        setActiveCard(cardName);
    }

    return (
        <>
            <Navbar/>
            <div className={"profile-page-content-grid"}>
                <div className={"main-card-container"}>
                    <div className={"profile-page-navigation-bar"}>
                        <button onClick={() => handleCardChange('userProfile')}>{t("main.profile.profileButton")}</button>
                        <button onClick={() => handleCardChange('profilePreferences')}>{t("main.profile.preferencesButton")}</button>
                        <button onClick={() => handleCardChange('profileActions')}>{t("main.profile.profileActionsButton")}</button>
                    </div>
                    {activeCard === 'userProfile' &&
                        <UserProfileCard/>
                    }
                    {activeCard === 'profilePreferences' &&
                        <ProfilePreferencesCard/>
                    }
                    {activeCard === 'profileActions' &&
                        <ProfileActionsCard/>
                    }
                </div>
            </div>
        </>
    )
};

export default ProfilePage;