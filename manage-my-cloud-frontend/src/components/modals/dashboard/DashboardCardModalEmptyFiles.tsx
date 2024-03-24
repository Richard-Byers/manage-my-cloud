import React from "react";

interface DashboardCardModalEmptyFilesProps {
    message: string;
}

export const DashboardCardModalEmptyFiles: React.FC<DashboardCardModalEmptyFilesProps> = ({message}) => {
    return(
        <div>
            <p>{message}</p>
        </div>
    );
}