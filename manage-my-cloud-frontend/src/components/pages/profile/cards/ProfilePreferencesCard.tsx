import React, { useState, useEffect } from "react";
import './ProfilePreferencesCard.css';
import { useTranslation } from 'react-i18next';
import ToggleSwitch from "../../../ui_components/ToggleSwitch";
import Select from 'react-select';
import { buildAxiosRequestWithHeaders } from "../../../helpers/AxiosHelper";
import { AuthData } from "../../../routing/AuthWrapper";

function ProfilePreferencesCard() {
    const { t } = useTranslation();
    const user = AuthData()?.user;

    // Define state variables for each dropdown
    const [createdAfter, setCreatedAfter] = useState('1');
    const [lastEdited, setLastEdited] = useState('1');
    const [deleteVideos, setDeleteVideos] = useState(true);
    const [deleteImages, setDeleteImages] = useState(true);
    const [deleteDocuments, setDeleteDocuments] = useState(true);
    const [deleteEmails, setDeleteEmails] = useState(true);


    const headers = {
        'Authorization': `Bearer ${user?.token}`
    };


    const weekOptions = [
        { value: 'anytime', label: '\u00A0Anytime\u00A0' },
        { value: '1', label: '\u00A01 Week\u00A0\u00A0' },
        { value: '2', label: '\u00A02 Weeks\u00A0' },
        { value: '3', label: '1 Months' },
        { value: '4', label: '2 Months' },
        { value: '5', label: '6 Months' },
        { value: '6', label: '\u00A0\u00A01 Year\u00A0\u00A0\u00A0' },

    ];
    useEffect(() => {
        buildAxiosRequestWithHeaders('GET', `/get-preferences/${user?.email}`, headers, {})
            .then((response: { data: any; }) => {
                const settings = response.data;
                setDeleteVideos(settings.deleteVideos);
                setDeleteImages(settings.deleteImages);
                setDeleteDocuments(settings.deleteDocuments);
                setDeleteEmails(settings.deleteEmails);
                setCreatedAfter(mapDaysToWeeks(settings.deleteItemsCreatedAfterDays));
                setLastEdited(mapDaysToWeeks(settings.deleteItemsNotChangedSinceDays));
            })
            .catch((error: any) => {
                console.error('There has been a problem with your fetch operation:', error);
            });
    }, []);

    const mapDaysToWeeks = (days: number) => {
        switch (days) {
            case 7:
                return '1';
            case 14:
                return '2';
            case 28:
                return '3';
            case 56:
                return '4';
            case 168:
                return '5';
            case 365:
                return '6';
            default:
                return 'anytime';
        }
    }

    const handleToggleChange = (toggleName: string, newValue: boolean) => {
        switch (toggleName) {
            case 'deleteVideos':
                setDeleteVideos(newValue);
                break;
            case 'deleteImages':
                setDeleteImages(newValue);
                break;
            case 'deleteDocuments':
                setDeleteDocuments(newValue);
                break;
            case 'deleteEmails':
                setDeleteEmails(newValue);
                break;
            default:
                break;
        }
    };

    const updatePreferences = () => {
        const data = {
            deleteVideos,
            deleteImages,
            deleteDocuments,
            deleteEmails,
            deleteItemsCreatedAfterDays: mapWeeksToDays(parseInt(createdAfter)),
            deleteItemsNotChangedSinceDays: mapWeeksToDays(parseInt(lastEdited))
        };

        // Update the RecommendationSettings in the database
        buildAxiosRequestWithHeaders('POST', `/preference-update?email=${user?.email}`, headers, data)
            .catch(error => {
                console.error('There has been a problem with your fetch operation:', error);
            });
    };

    const mapWeeksToDays = (weeks: number) => {
        switch (weeks) {
            case 1:
                return 7;
            case 2:
                return 14;
            case 3:
                return 28;
            case 4:
                return 56;
            case 5:
                return 168;
            case 6:
                return 365;
            default:
                return 0;
        }
    }

    return (
        <div className="card-content">
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.deleteVideos')}:</label>
                <ToggleSwitch value={deleteVideos}
                              onChange={(newValue: boolean) => handleToggleChange('deleteVideos', newValue)}/>
            </div>
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.deleteImages')}:</label>
                <ToggleSwitch value={deleteImages}
                              onChange={(newValue: boolean) => handleToggleChange('deleteImages', newValue)}/>
            </div>
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.deleteDocuments')}:</label>
                <ToggleSwitch value={deleteDocuments}
                              onChange={(newValue: boolean) => handleToggleChange('deleteDocuments', newValue)}></ToggleSwitch>
            </div>
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.deleteEmails')}:</label>
                <ToggleSwitch value={deleteEmails}
                              onChange={(newValue: boolean) => handleToggleChange('deleteEmails', newValue)}/>
            </div>
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.createdAfter')}:</label>
                <Select
                    options={weekOptions}
                    value={weekOptions.find(option => option.value === createdAfter)}
                    onChange={(selectedOption) => selectedOption && setCreatedAfter(selectedOption.value)}
                    menuPortalTarget={document.body}
                    styles={{
                        menuPortal: base => ({...base, zIndex: 9999}),
                        menu: provided => ({...provided, maxHeight: 200, overflow: 'auto'})
                    }}
                />
            </div>
            <div className="toggle-container">
                <label>{t('main.profilePreferencesCard.weeksSinceLastEdited')}:</label>
                <Select
                    options={weekOptions}
                    value={weekOptions.find(option => option.value === lastEdited)}
                    onChange={(selectedOption) => selectedOption && setLastEdited(selectedOption.value)}
                    menuPortalTarget={document.body}
                    styles={{
                        menuPortal: base => ({...base, zIndex: 9999}),
                        menu: provided => ({...provided, maxHeight: 200, overflow: 'auto'})
                    }}
                />
            </div>
            <div className="button-container">
                <button onClick={updatePreferences}>Update Preferences</button>
            </div>
        </div>
    );
}

export default ProfilePreferencesCard;