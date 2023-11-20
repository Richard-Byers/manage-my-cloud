import React, { useEffect } from "react";
import UserProfileCard from './cards/UserProfileCard';
import CardsContainer from './cards/CardsContainer';
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';

const ProfilePage = () => {
    useEffect(() => {
        document.body.style.overflow = "hidden";
      }, []);

    return (
        <div >
            <Navbar/>
            <div className="profile-title">
                <h1>Profile</h1>
                </div>
            <UserProfileCard/>
            <CardsContainer/>
        </div>
    )
};


export default ProfilePage;