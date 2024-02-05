import React, {useState} from "react";
import './UserProfileCard.css';
import {AuthData} from "../../../routing/AuthWrapper";
import ProfileImgButton from "./profileImgButton";

function UserProfileCard() {
    const { user, logout } = AuthData();



    return (
        <div className={"user-profile-card-content"}>
            <div className='user-profile-card-header'>

            </div>
            <div className={'profile-card-buttons'}>
                <ProfileImgButton />
            </div>

            <div className='user-info'>
                <div className='user-profile-card-data-label'>First Name: {user?.firstName}</div>
                <div className='user-profile-card-data-label'>Last Name: {user?.lastName}</div>
                <div className='user-profile-card-data-label'>Email Address: {user?.email}</div>
                <div className='user-profile-card-data-label'>
                    Password: ********
                </div>
            </div>
            <div className={'profile-card-buttons'}>
                <button className='save-changes-btn'>Update Profile Details</button>
                <button className='logout-btn' onClick={logout}>Log Out</button>
            </div>
        </div>
    );
}

export default UserProfileCard;