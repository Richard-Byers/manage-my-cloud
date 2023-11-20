import React from 'react';
import RequestUserDataCard from './RequestUserDataCard';
import DownloadTermsOfServiceCard from './DownloadTermsOfServiceCard';
import DeleteAccountCard from './DeleteAccountCard';
import './CardsContainer.css';

const CardsContainer = () => {
    return (
        <div className="cards-container">
            <DownloadTermsOfServiceCard />
            <RequestUserDataCard />
            <DeleteAccountCard />
        </div>
    );
}

export default CardsContainer;