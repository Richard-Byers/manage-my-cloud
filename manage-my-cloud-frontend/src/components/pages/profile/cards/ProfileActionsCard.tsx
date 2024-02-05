import React from "react";
import './ProfileActions.css';
import './Card.css';
import { useTranslation } from 'react-i18next';

function ProfileActionsCard() {
    const { t } = useTranslation();

    return (
            <div className="card-content">
                <div className={"card-title"}>
                    {t('main.profileActionsCard.accountActions')}
                </div>
                <button className="actions-button">{t('main.profileActionsCard.deleteAccount')}</button>
                <button className="actions-button">{t('main.profileActionsCard.requestData')}</button>
                <button className="actions-button">{t('main.profileActionsCard.viewTermsOfService')}</button>
                <button className="actions-button">{t('main.profileActionsCard.downloadTermsOfService')}</button>
            </div>
    );
}

export default ProfileActionsCard;
