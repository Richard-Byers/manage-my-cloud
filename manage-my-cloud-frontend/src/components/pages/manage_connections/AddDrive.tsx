// AddDrive.jsx
import React, { useState } from "react";
import "./AddDrive.css";
import googleLogo from "../../images/login/google.png";

interface AddDriveBtnProps {
    onDriveAdded: (drive: { id: number; name: string; email: string }) => void;
}
const AddDriveBtn: React.FC<AddDriveBtnProps> = ({ onDriveAdded }) => {
    const [isModalOpen, setModalOpen] = useState(false);
    const [userDetails, setUserDetails] = useState({
        name: '',
        email: '',
    });
    const [showSquares, setShowSquares] = useState<{ id: number; name: string; email: string }[]>([]);

    const handleButtonClick = () => {
        setModalOpen(true);
    };

    const handleModalClose = () => {
        setModalOpen(false);
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setUserDetails((prevDetails) => ({
            ...prevDetails,
            [name]: value,
        }));
    };

    const handleFormSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const newDrive = { id: Date.now(), name: userDetails.name, email: userDetails.email };
        setShowSquares((prevSquares) => [newDrive, ...prevSquares]);
        setModalOpen(false);
        setUserDetails({ name: "", email: "" });

        // Call the onDriveAdded function from the parent component
        onDriveAdded(newDrive);
    };

    return (
        <div className="addDriveContainer">
            <div className="squaresContainer">
                {showSquares.map((square) => (
                    <div key={square.id} className="square">
                        <p>{` Email: ${square.email}`}</p>
                    </div>
                ))}
            </div>

            <button className="addDrive" onClick={handleButtonClick}>
                <div className="plus-sign">
                </div>
                <p>Add Drive</p>
            </button>

            {isModalOpen && (
                <div className="modal">
                    <div className="modal-content">
            <span className="close" onClick={handleModalClose}>
                &times;
            </span>
                        <form onSubmit={handleFormSubmit}>
                            <div className="form-group">
                                <label htmlFor="Email">Email:</label>
                                <input
                                    type="text"
                                    id="email"
                                    name="Account Email"
                                    value={userDetails.name}
                                    onChange={handleInputChange}
                                    placeholder="Enter your name"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="password">Password:</label>
                                <input
                                    type="text"
                                    id="password"
                                    name="password"
                                    value={userDetails.email}
                                    onChange={handleInputChange}
                                    placeholder="Enter your email"
                                />
                            </div>
                            <button type="submit">Add Drive</button>
                            <div className="google-login">
                                {/* Google Login Button (non-functional) */}
                                <button className="google-button" disabled>
                                    <img className={"modal-login-google-logo"} src={googleLogo} alt={"Google Logo"}/>
                                    Add with Google
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

        </div>
    );
};

export default AddDriveBtn;

