import React from 'react';
import './Connection.css';
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../constants/ConnectionConstants";
import RemoveConnectionModal from "../../modals/managingConnections/RemoveConnectionModal";

interface ConnectionProps {
    connectionProvider: string,
    isConnected: boolean | number
}

const Connection: React.FC<ConnectionProps> = ({connectionProvider, isConnected}) => {


    return (
        <div className={"connection-container"}>
            <img src={CONNECTION_LOGOS[connectionProvider]} alt={`Logo for ${connectionProvider}`}/>
            <p>{CONNECTION_TITLE[connectionProvider]}</p>
            <RemoveConnectionModal connectionProvider={CONNECTION_TITLE[connectionProvider]}/>
        </div>
    )
}

export default Connection;