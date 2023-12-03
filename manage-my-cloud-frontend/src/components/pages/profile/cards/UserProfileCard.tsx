import React from "react";
import './UserProfileCard.css';
import profileLogo from '../../../images/profile_picture.png';
import {AuthData} from "../../../routing/AuthWrapper";

function UserProfileCard() {
    const {user, logout} = AuthData();

    return (
        <div className="card">
            <div className={"user-profile-card-content"}>
                <div className={'user-profile-picture'}>
                    <img className={'profile-picture'} src={profileLogo} alt={'profile logo'}/>
                </div>
                <div className='user-info'>
                    <div className='user-profile-card-fname'>First Name: {user?.firstName}</div>
                    <div className='user-profile-card-lname'>Last Name: {user?.lastName}</div>
                    <div className='user-profile-card-email'>Email Address: {user?.email}</div>
                    <div className='user-profile-card-password'>
                        Password: ********
                    </div>
                </div>
                <div className={'profile-card-buttons'}>
                    <button className='save-changes-btn'>Update Profile Details</button>
                    <button className='logout-btn' onClick={logout}>Log Out</button>
                </div>
            </div>
        </div>
    )
        ;
}

export default UserProfileCard;
