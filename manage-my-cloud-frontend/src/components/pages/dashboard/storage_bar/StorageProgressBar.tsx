import React from 'react';
import './StorageProgressBar.css';

interface StorageProgressBarProps {
    used: number;
    total: number;
}

const StorageProgressBar: React.FC<StorageProgressBarProps> = ({ used, total }) => {
    const percentageUsed = Math.round((used / total) * 100);
    let color;

    if (percentageUsed <= 50) {
        color = 'green';
    } else if (percentageUsed <= 75) {
        color = 'orange';
    } else {
        color = 'red';
    }

    return (
        <div className="storage-progress-bar" id={"storage-used-progress-bar"}>
            <span className="progress-bar-label">{percentageUsed}%</span>
            <div className="progress-bar-fill" style={{ width: `${percentageUsed}%`, backgroundColor: color }}></div>
        </div>
    );
};

export default StorageProgressBar;