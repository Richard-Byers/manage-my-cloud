import React, {useEffect, useState} from "react";
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';
import ProfileActionsCard from './cards/ProfileActionsCard';
import UserProfileCard from "./cards/UserProfileCard";
import ProfilePreferencesCard from "./cards/ProfilePreferencesCard";
import {useTranslation} from 'react-i18next';
import {AuthData} from "../../routing/AuthWrapper";

const ProfilePage = () => {
    const {t} = useTranslation();
    const [activeCard, setActiveCard] = useState('userProfile');
    const {user, refreshUser} = AuthData();

    useEffect(() => {
        document.body.style.overflow = "hidden";
    
        if (user && user.email) {
            refreshUser(user.email);
        }
    }, []);

    const handleCardChange = (cardName: React.SetStateAction<string>) => {
        setActiveCard(cardName);
    }

    return (
        <>
            <Navbar/>
            <div className={"profile-page-content-grid"}>
                <div className={"main-card-container"}>
                    <div className={"profile-page-navigation-bar"} id={"profile-navigation-menu"}>
                        <button onClick={() => handleCardChange('userProfile')}
                                id={"user-profile-button"}>{t("main.profile.profileButton")}</button>
                        <button
                            onClick={() => handleCardChange('profilePreferences')}
                            id={"user-preferences-button"}>{t("main.profile.preferencesButton")}</button>
                        <button
                            onClick={() => handleCardChange('profileActions')}
                            id={"user-profile-actions-button"}>{t("main.profile.profileActionsButton")}</button>
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