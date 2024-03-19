import React from 'react';
import './Connection.css';
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../constants/ConnectionConstants";
import RemoveConnectionModal from "../../modals/managingConnections/RemoveConnectionModal";

interface ConnectionProps {
    accountType: string,
    accountEmail: string
}

const Connection: React.FC<ConnectionProps> = ({accountType, accountEmail}) => {

    return (
        <div className={"connection-container"} id={`${accountType}-container`}>
            <img src={CONNECTION_LOGOS[accountType]} alt={`Logo for ${accountType}`}/>
            <p className={"linked-drive-information-text"} id={"linked-drive-type"}>{CONNECTION_TITLE[accountType]}</p>
            <p className={"linked-drive-information-text"} id={"linked-drive-email"}>{accountEmail}</p>
            <RemoveConnectionModal connectionProvider={CONNECTION_TITLE[accountType]} driveEmail={accountEmail}/>
        </div>
    )
}

export default Connection;