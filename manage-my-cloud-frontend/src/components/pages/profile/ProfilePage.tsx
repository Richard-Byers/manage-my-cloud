import React, {useEffect} from "react";
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';
import ProfileActionsCard from './cards/ProfileActionsCard';
import UserProfileCard from "./cards/UserProfileCard";
import ProfilePreferences from "./cards/ProfilePreferencesCard";

const ProfilePage = () => {

    useEffect(() => {
        document.body.style.overflow = "hidden";
    }, []);

    return (
        <div>
            <Navbar/>
            <div className={"profile-page-content"}>
                <div className={"profile-page-title"}>
                    Profile
                </div>
                <div className={"card-container"}>
                    <UserProfileCard/>
                    <ProfileActionsCard/>
                    <ProfilePreferences/>
                </div>
            </div>
        </div>
    )
};

export default ProfilePage;