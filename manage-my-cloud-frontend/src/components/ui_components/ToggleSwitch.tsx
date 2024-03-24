import React, {useEffect, useState} from 'react';
import './ToggleSwitch.css';
interface ToggleSwitchProps {
    value: boolean;
    onChange: (newValue: boolean) => void;
}

const ToggleSwitch: React.FC<ToggleSwitchProps> = ({ value, onChange }) => {
    const [isToggled, setIsToggled] = useState(value);

    useEffect(() => {
        setIsToggled(value);
    }, [value]);

    const handleToggle = () => {
        onChange(!isToggled);
    };

    return (
        <div className={`toggle-switch ${isToggled ? 'toggled' : ''}`} onClick={handleToggle}>
            <div className="toggle-switch-handle"></div>
        </div>
    );
}

export default ToggleSwitch;