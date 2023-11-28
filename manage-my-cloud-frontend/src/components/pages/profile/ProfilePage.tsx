import React, { useEffect } from "react";
import './ProfilePage.css';
import Navbar from '../../nav/Navbar';
import {Avatar} from '@mui/material';
import Card from './cards/UserProfileCard';
import StackedCards from './cards/StackedCards';
import {AuthData} from "../../routing/AuthWrapper";

const ProfilePage = () => {

    useEffect(() => {
        document.body.style.overflow = "hidden";
      }, []);

    return (
        <div >
            <Navbar/>
            <Card/>
            <StackedCards/>
        </div>
    )
};

export default ProfilePage;