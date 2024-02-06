import React from 'react';
import './LoadingSpinner.css';

const LoadingSpinner: React.FC = () => {
    return(
        <div className="spinner-container">
            <div className="lds-default">
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
                <div></div>
            </div>
        </div>
    )
};

export default LoadingSpinner;