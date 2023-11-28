import Navbar from "../../nav/Navbar";
import React from "react";
import HorizontalCards from "./cards/HorizontalCards";
import './DashboardPage.css';

const ButtonComponent = () => {
    return (
      <div className='delete-btns'>
        <button className="delete-duplicates-btn">
          Delete Duplicates
        </button>
        <button className="delete-recommended-btn">
          Delete Recommended
        </button>
      </div>
    );
  };

const ManageConnectionsPage = () => {
    return (
        <div>
            <Navbar/>
            <div className="dashboard-page-title">
            <h1>Dashboard</h1>
            </div>
            <HorizontalCards/>
            <button className="refresh-drives-btn">
                Refresh Drives
            </button>
            <ButtonComponent></ButtonComponent>
            <div style={{ borderTop: '3px solid white', textAlign: 'center', marginTop:'10vh', width:'50%', margin: '0 auto' }}>
    <div className="text-under-line" style={{ width: '100%', textAlign: 'center', display: 'flex', flexDirection: 'column' }}>
        <p>If you would like to add or remove drives</p>
        <p style={{marginTop:'-1vh'}}>please head to <span className="manage-connections">Manage Connections</span></p>
    </div>
</div>
        </div>
        
    )
};

export default ManageConnectionsPage;