import React from "react";
import './DashboardPageButtons.css';
import {useTranslation} from "react-i18next";

const DashboardPageButtons = () => {

    const {t} = useTranslation();

    return (
        <div className={"dashboard-button-container"}>
            <button className={"dashboard-button"}>{t('main.dashboard.dashboardPageButtons.deleteDuplicates')}</button>
            <button className={"dashboard-button"}>{t('main.dashboard.dashboardPageButtons.deleteRecommended')}</button>
        </div>
    )
};

export default DashboardPageButtons;