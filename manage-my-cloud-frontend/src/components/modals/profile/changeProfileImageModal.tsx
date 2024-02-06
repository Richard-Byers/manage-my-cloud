import React, {useState} from "react";
import "./changeProfileImageModal.css";

import Spinner from 'react-spinner-material';
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {AuthData} from "../../routing/AuthWrapper";


interface ChangeProfileImageModalProps {
    setIsModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
}



const ChangeProfileImageModal: React.FC<ChangeProfileImageModalProps> = ({ setIsModalOpen }) => {
    const [imageInput, setImageInput] = useState<{ image: File | null }>({
        image: null,


    });
    const [isLoading, setIsLoading] = useState(false);

    const {user, refreshUser} = AuthData();
    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const image = event.target.files ? event.target.files[0] : null;
        setImageInput((prevState) => ({...prevState, image}));

    };

    const [message, setMessage] = useState("");
    const headers = {
        Authorization: `Bearer ${user?.token}`
    }

    const handleImageUpload = (e: React.FormEvent) => {
        e.preventDefault();

        const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

        if (!imageInput.image) {
            setMessage("No image selected. Please select an image to upload.");
            return;
        }

        if (imageInput.image.size > MAX_FILE_SIZE) {
            setMessage("File size exceeds the 2MB limit. Please select a smaller file.");
            return;
        }

        setIsLoading(true);

        const formData = new FormData();
        formData.append('image', imageInput.image as Blob);
        formData.append('email', user?.email as string);

        buildAxiosRequestWithHeadersForImageUpload('POST', '/updateProfileImg', headers, formData)
            .then(() => {
                setIsLoading(false); // Stop the loading animation
                setMessage("Update successful"); // Set the success message
            })
            .catch(error => {
                console.error('Error:', error);
                setIsLoading(false);
            });
    };

    const buildAxiosRequestWithHeadersForImageUpload = (method: string, url: string, headers: any, data: any) => {
        if (data instanceof FormData) {
            headers['Content-Type'] = 'multipart/form-data';
        }

        return buildAxiosRequestWithHeaders(method, url, headers, data);
    };


    const stopPropagation = (e: React.MouseEvent<HTMLDivElement>) => {
        e.stopPropagation();
    };

    const closeModal = () => {
        setIsModalOpen(false);
        refreshUser(user?.email);
    };

    return (
        <>
            <div className="modal-overlay" onClick={closeModal}>
                <div className="ProfileModal" onClick={stopPropagation}>
                    <div className={"profile-modal-description"}>
                        {isLoading ? "Upload a new profile image" :
                            <span className={message === "Update successful" ? "success-message" : "error-message"}>
                            {message}
                        </span>
                        }
                    </div>

                    <form className={"profile-modal-form"} onSubmit={handleImageUpload}>
                        <label className={"profile-modal-form-label"}>
                            <input className={"profile-modal-form-input"}
                                   type="file"
                                   accept="image/*"
                                   onChange={handleImageChange}/>
                        </label>
                        <button className={"profile-modal-form-submit-button"} type="submit">
                            Upload Image
                        </button>
                    </form>
                    {isLoading && <Spinner />} {/* Render the spinner when loading */}
                </div>
            </div>
        </>
    );
}


export default ChangeProfileImageModal;