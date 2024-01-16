import React, {useEffect} from "react";
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';
import ProfileActionsCard from './cards/ProfileActionsCard';
import UserProfileCard from "./cards/UserProfileCard";
import ProfilePreferencesCard from "./cards/ProfilePreferencesCard";

const ProfilePage = () => {

    useEffect(() => {
        document.body.style.overflow = "hidden";
    }, []);

    return (
        <div>
            <Navbar/>
            <div className={"profile-page-content-grid"}>
                <div className={"profile-page-title-container"}>
                    Profile
                </div>
                <div className={"user-profile-card-container"}>
                    <UserProfileCard/>
                </div>
                <div className={"profile-actions-card-container"}>
                    <ProfileActionsCard/>
                </div>
                <div className={"profile-preferences-card-container"}>
                    <ProfilePreferencesCard/>
                </div>
            </div>
        </div>
    )
};

export default ProfilePage;