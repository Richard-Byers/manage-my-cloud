import React, {useState} from "react";
import "./changeProfileImageModal.css";
import {useTranslation} from 'react-i18next';
import Spinner from 'react-spinner-material';
import {buildAxiosRequestWithHeaders} from "../../helpers/AxiosHelper";
import {AuthData} from "../../routing/AuthWrapper";
import "../Modal.css";
import CloseIcon from "@mui/icons-material/Close";

interface ChangeProfileImageModalProps {
    setIsModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const ChangeProfileImageModal: React.FC<ChangeProfileImageModalProps> = ({setIsModalOpen}) => {
    const [imageInput, setImageInput] = useState<{ image: File | null }>({
        image: null,

    });

    const [isLoading, setIsLoading] = useState(false);

    const {user, refreshUser} = AuthData();
    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setMessage("")
        const image = event.target.files ? event.target.files[0] : null;
        setImageInput((prevState) => ({...prevState, image}));
    };

    const [message, setMessage] = useState("");
    const headers = {
        Authorization: `Bearer ${user?.token}`
    }
    const {t} = useTranslation();
    const handleImageUpload = (e: React.FormEvent) => {
        e.preventDefault();
        const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

        if (!imageInput.image) {
            setMessage(t('main.changeProfileImage.noImageSelected'));
            return;
        }

        if (imageInput.image.size > MAX_FILE_SIZE) {
            setMessage(t('main.changeProfileImage.fileSizeError'));
            return;
        }

        setIsLoading(true);

        const formData = new FormData();
        formData.append('image', imageInput.image as Blob);
        formData.append('email', user?.email as string);

        buildAxiosRequestWithHeadersForImageUpload('POST', '/update-profile-Img', headers, formData)
            .then(() => {
                setIsLoading(false); // Stop the loading animation
                setMessage(t('main.changeProfileImage.success'));
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
                <div className="modal" onClick={stopPropagation}>
                    <button className={"modal-close-button"} onClick={closeModal}>
                        <CloseIcon className="svg_icons"/>
                    </button>
                    <form className={"profile-modal-form"} onSubmit={handleImageUpload}>
                        {isLoading ? t('main.changeProfileImage.uploading') :
                            <span
                                className={message === t('main.changeProfileImage.success') ? "success-message" : "error-message"}>
                            {message}
                        </span>
                        }
                        <label className={"profile-modal-form-label"}>
                            <input className={"profile-modal-form-input"}
                                   type="file"
                                   accept="image/*"
                                   onChange={handleImageChange}/>
                        </label>
                        <button className={"profile-modal-form-submit-button"} type="submit">
                            {t('main.changeProfileImage.uploadImage')}
                        </button>
                    </form>
                    {isLoading && <Spinner/>} {/* Render the spinner when loading */}
                </div>
            </div>
        </>
    );
}
export default ChangeProfileImageModal;