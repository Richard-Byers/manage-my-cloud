import React from 'react';
import './LoadingProgress.css';

interface LoadingProgressProps {
    filled: number,
}

const LoadingProgress: React.FC<LoadingProgressProps> = ({filled}) => {
    const radius = 90; // Increase this to make the circle bigger
    const strokeWidth = 10; // This should match the stroke-width in your CSS
    const svgSize = radius * 2 + strokeWidth;
    const circumference = 2 * Math.PI * radius;
    const offset = circumference - (filled / 100 * circumference);

    return (
        <div className={"loading-progress-container"}>
            <svg className="progress-ring" width={240} height={240}>
                <circle className="progress-ring__circle-background" stroke="grey" fill="transparent" r={radius} cx={120} cy={120} />
                <circle className="progress-ring__circle progress-ring__circle--progress" fill="transparent" r={radius} cx={120} cy={120} strokeDasharray={circumference} strokeDashoffset={offset} />
            </svg>
            <span className="progress-percent">{filled}%</span>
        </div>
    );
}

export default LoadingProgress;