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
        <div className={"profile-page-main"}>
            <Navbar/>
            <div className={"main-card-container"}>
                <div className={"profile-page-navigation-bar"}>
                    <button onClick={() => handleCardChange('userProfile')}>Profile</button>
                    <button onClick={() => handleCardChange('profilePreferences')}>Preferences</button>
                    <button onClick={() => handleCardChange('profileActions')}>Advanced</button>
                </div>
                {activeCard === 'userProfile' &&
                    <div className={"user-profile-card-container"}>
                        <UserProfileCard/>
                    </div>
                }
                {activeCard === 'profilePreferences' &&
                    <div className={"profile-preferences-card-container"}>
                        <ProfilePreferencesCard/>
                    </div>
                }
                {activeCard === 'profileActions' &&
                    <div className={"profile-actions-card-container"}>
                        <ProfileActionsCard/>
                    </div>
                }
            </div>
        </div>
    )
};

export default ProfilePage;