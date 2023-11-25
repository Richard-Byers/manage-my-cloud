import Navbar from "../../nav/Navbar";
import React from "react";

const ManageConnectionsPage = () => {
    return (
        <div>
            <Navbar/>
            <h1>Dashboard Page</h1>
            <a href="http://localhost:8080/oauth/authorise" style={{ textDecoration: 'none' }}>
                            <button className={"modal-login-google-button"}>
                                Log in using Google
                            </button>
                            </a>
        </div>
    )
};

export default ManageConnectionsPage;