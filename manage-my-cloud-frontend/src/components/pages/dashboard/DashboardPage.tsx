import Navbar from "../../nav/Navbar";
import React, { useEffect, useState } from 'react';

const ManageConnectionsPage = () => {

    return (
        <div>
            <Navbar/>
            <h1>Dashboard Page</h1>
            <a href="http://localhost:8080/getDetails" style={{ textDecoration: 'none' }}>
                            <button className={"modal-login-google-button"}>
                                Click to return details of cloud
                            </button>
                            </a>
        </div>
    )
};

export default ManageConnectionsPage;