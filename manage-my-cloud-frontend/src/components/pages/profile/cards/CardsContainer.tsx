import React from 'react';
import RequestUserDataCard from './RequestUserDataCard';
import DownloadTermsOfServiceCard from './DownloadTermsOfServiceCard';
import DeleteAccountCard from './DeleteAccountCard';
import PreferencesCard from './PreferencesCard';
import MemberSinceCard from './MemberSinceCard';
import './CardsContainer.css';

const CardsContainer = () => {
    return (
        <div className="cards-container">
            <DownloadTermsOfServiceCard />
            <RequestUserDataCard />
            <DeleteAccountCard />
            <PreferencesCard />
            <MemberSinceCard />
        </div>
    );
}

export default CardsContainer;