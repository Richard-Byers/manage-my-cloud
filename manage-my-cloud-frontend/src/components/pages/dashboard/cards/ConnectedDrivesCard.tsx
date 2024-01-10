import React from 'react';
import './ConnectedDrivesCard.css';
import GoogleDriveLogo from '../../../images/dashboard/GoogleDriveLogo.png';
import OneDriveLogo from '../../../images/dashboard/OneDriveLogo.png';
import DropboxLogo from '../../../images/dashboard/DropboxLogo.png';


function CardContainer() {
    return (
        <div className="container">
            <div className="item">
                <img className="g-drive-logo" src={GoogleDriveLogo} alt="Google Drive Logo"/>
                <div className="item-drive-name">
                    <h2 style={{borderBottom: '0.2vh solid black', paddingBottom: '1vh', marginLeft: '2vw'}}><span>Drive Name:</span>
                        <span style={{marginLeft: '5vw'}}>Google Drive</span></h2>
                </div>
                <div className='item-storage-used'>
                    <h2 style={{marginLeft: '2vw'}}><span>Storage Used:</span>
                        <span style={{marginLeft: '5vw'}}>10.1GB/15GB</span></h2>
                </div>
            </div>
            <div className="item">
                <img className="dropbox-logo" src={DropboxLogo} alt="Dropbox Logo"/>
                <div className="item-drive-name">
                    <h2 style={{borderBottom: '0.2vh solid black', paddingBottom: '1vh', marginLeft: '2vw'}}><span>Drive Name:</span>
                        <span style={{marginLeft: '7vw'}}>Dropbox</span></h2>
                </div>
                <div className='item-storage-used'>
                    <h2 style={{marginLeft: '2vw'}}><span>Storage Used:</span>
                        <span style={{marginLeft: '5vw'}}>10.1GB/15GB</span></h2>
                </div>
            </div>
            <div className="item">
                <img className="onedrive-logo" src={OneDriveLogo} alt="OneDrive Logo"/>
                <div className="item-drive-name">
                    <h2 style={{borderBottom: '0.2vh solid black', paddingBottom: '1vh', marginLeft: '3vw'}}><span>Drive Name:</span>
                        <span style={{marginLeft: '5.5vw'}}>Onedrive</span></h2>
                </div>
                <div className='item-storage-used'>
                    <h2 style={{marginLeft: '2vw'}}><span>Storage Used:</span>
                        <span style={{marginLeft: '5vw'}}>10.1GB/15GB</span></h2>
                </div>
            </div>
        </div>
    );
}

export default CardContainer