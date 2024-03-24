import "./ToolTip.css";
import React from "react";

interface ToolTipProps {
    message: string;
    children: React.ReactNode;
}

const ToolTip: React.FC<ToolTipProps> = ({message, children}) => {

    const [isVisible, setIsVisible] = React.useState(false);

    return (
        <div className={"tooltip-container"} onMouseEnter={() => setIsVisible(true)}
             onMouseLeave={() => setIsVisible(false)}>
            {children}
            {isVisible && <div className={"tooltip"}>
                {message.split('.').map((sentence, index) =>
                    <React.Fragment key={index}>
                        <p key={index}>
                            {sentence}
                        </p>
                    </React.Fragment>
                )}
            </div>}
        </div>
    );
}

export default ToolTip;