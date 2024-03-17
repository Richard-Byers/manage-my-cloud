import {AuthData} from "../routing/AuthWrapper";
import {buildAxiosRequestWithHeaders} from "./AxiosHelper";

export class TokenUpdater {
    static async checkAndUpdateToken() {
        const {user, refreshUser} = AuthData();
        const userEmail = user?.email;
        const headers = {
            Authorization: `Bearer ${user?.token}`
        };
        if (userEmail && user?.linkedAccounts.linkedDriveAccounts.some(account => account.accountType === 'OneDrive')) {
            try {
                const response = await buildAxiosRequestWithHeaders('POST', `/onedrive-refresh-access-token`, headers, {email: userEmail});
                if (response.status === 200) {
                    for (let i = 0; i < response.data.length; i++) {
                        if (response.data[i].status === 200) {
                            refreshUser(user?.email);
                        }
                    }
                }
            } catch (error) {
                console.error('Failed to refresh access token:', error);
            }
        }
    };
}