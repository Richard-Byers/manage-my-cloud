import {useState} from "react";
import './WelcomeModal.css';
import logo from '../images/managemycloudlogo.svg';
import {AuthData} from "../routing/AuthWrapper";
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';

const WelcomeModal = () => {

    const [showModal, setShowModal] = useState(true);
    const {refreshUser, user} = AuthData();

    const handleCloseModal = () => {
        setShowModal(false);
        refreshUser(user?.email)
    }

    return (
        <>
            {showModal ?
                <div className={"modal-overlay"}>
                    <div className={"welcome-modal"}>
                        <div className={"welcome-container"} id={"welcome-container"}>
                        <span className={"welcome-container-text-container"} id={"welcome-container-text-container"}>
                            <h2>Welcome to Manage My Cloud</h2>
                            <br/>
                            <p>To get started, you'll need to navigate to the <strong>Manage Connections Page</strong> by clicking the button located in the navigation bar.</p>
                            <br/>
                            <p>Keep an eye out for this icon <HelpOutlineIcon/> located throughout our website, when hovering or clicking it we will provide you with insights into our features.</p>
                            <br/>
                            <p>When you're finished reading, click the <strong>Got it!</strong> button to close this message.</p>
                        </span>
                            <button onClick={handleCloseModal} className={"welcome-button"} id={"welcome-button"}>
                                Got it!
                            </button>
                        </div>
                        <div className={"welcome-modal-art"} id={"welcome-modal-art"}>
                            <img src={logo} alt={"Welcome modal art"}/>
                        </div>
                    </div>
                </div>
                : null}
        </>
    )
}

export default WelcomeModal;