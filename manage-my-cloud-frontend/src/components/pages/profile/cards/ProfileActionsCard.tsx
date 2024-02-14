import './ProfileActions.css';
import './Card.css';
import { useTranslation } from 'react-i18next';
import DeleteAccountModal from "../../../modals/profile/DeleteAccountModal";
import {buildAxiosRequest, buildAxiosRequestWithHeaders} from "../../../helpers/AxiosHelper";
import { AuthData } from '../../../routing/AuthWrapper';

function ProfileActionsCard() {
    const { t } = useTranslation();
    const {user, logout} = AuthData();

    const handleUserDataRequest = () => {
        if (user) {
            const email = user.email;
            const headers = {
                Authorization: `Bearer ${user.token}`
            };
            buildAxiosRequestWithHeaders('POST', `/data-request?email=${email}`, headers, {})
                .then(response => {
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute('download', 'user-data.txt');
                    document.body.appendChild(link);
                    // Add confirmation dialog
                    if (window.confirm('Do you want to download the file?')) {
                        link.click();
                    }
                })
                .catch(error => {
                    console.error('There has been a problem with your fetch operation:', error);
                    if (error.response) {
                        console.error('Response data:', error.response.data);
                        console.error('Response status:', error.response.status);
                        console.error('Response headers:', error.response.headers);
                    } else if (error.request) {
                        console.error('Request:', error.request);
                    } else {
                        console.error('Error message:', error.message);
                    }
                });
        }
    }

    return (
        <div className="card-content">
            <div className={"card-title"}>
                {t('main.profileActionsCard.accountActions')}
            </div>
            <DeleteAccountModal />
            <button className="actions-button" onClick={handleUserDataRequest}>{t('main.profileActionsCard.requestData')}</button>
            <button className="actions-button">{t('main.profileActionsCard.viewTermsOfService')}</button>
            <button className="actions-button">{t('main.profileActionsCard.downloadTermsOfService')}</button>
        </div>
    );
}

export default ProfileActionsCard;