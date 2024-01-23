import React from 'react';
import './Connection.css';
import {CONNECTION_LOGOS, CONNECTION_TITLE} from "../../../constants/ConnectionConstants";

interface ConnectionProps {
    connectionProvider: string,
    isConnected: boolean | number
}

const Connection: React.FC<ConnectionProps> = ({connectionProvider, isConnected}) => {


    return (
        <div className={"connection-container"}>
            <img src={CONNECTION_LOGOS[connectionProvider]} alt={`Logo for ${connectionProvider}`}/>
            <p>{CONNECTION_TITLE[connectionProvider]}</p>
        </div>
    )
}

export default Connection;