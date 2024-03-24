import {createContext, useContext, useEffect, useState} from "react"
import AppRouting from "./AppRouting";
import {buildAxiosRequest, buildAxiosRequestWithHeaders} from "../helpers/AxiosHelper";
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../constants/RouteConstants";
import {useGoogleLogin} from '@react-oauth/google';
import Cookies from 'universal-cookie'
import {jwtDecode} from 'jwt-decode';


interface Account {
    accountEmail: string;
    accountType: string;
}

interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    token: string;
    refreshToken: string;
    accountType: string | null;
    profileImage: Uint8Array | null;
    linkedAccounts: {
        linkedAccountsCount: number;
        linkedDriveAccounts: Account[];
    }
    firstLogin: boolean;
}

interface AuthContextProps {
    user: User | null;
    login: (email: string, password: string) => Promise<User>;
    googleLogin: () => void;
    logout: () => void;
    refreshUser: (email: string | undefined) => void;
    loading: boolean;
}

const AuthContext = createContext<AuthContextProps | undefined>(undefined);

export const AuthData = (): AuthContextProps => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthWrapper = () => {
    const cookies = new Cookies();
    const navigate = useNavigate();
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedUser = cookies.get('user')
        if (storedUser) {
            setUser(storedUser);
        }
        setLoading(false);
    }, []);

    const login = async (email: string, password: string): Promise<User> => {
        try {
            const response = await buildAxiosRequest("POST", "/login", {email, password});
            const userData = response.data;
            setUser(userData);
            const { profileImage, ...userDataWithoutImage } = userData; // Exclude profileImage
            cookies.set('user', JSON.stringify(userDataWithoutImage)); // Save user data without image in the cookie
            localStorage.setItem('profileImage', `data:image/jpeg;base64,${profileImage}`); // Store profile image URL in local storage
            return userData;
        } catch (error) {
            throw error;
        }
    };

    const googleLogin = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            // Get the code from the response
            const authCode = codeResponse.code;

            // Send the code to the server
            try {
                const response = await buildAxiosRequest("POST", "/registergoogleuser", {authCode});
                const data = response.data;
                setUser(data);
                cookies.set('user', JSON.stringify(data));
                navigate(ROUTES.DASHBOARD)
            } catch (error) {
                // Handle the error
                console.error('Error:', error);
            }
        },
        flow: 'auth-code',
        scope: 'https://www.googleapis.com/auth/drive',
    });

    useEffect(() => {
        const refreshToken = async () => {
            // Get the user data from the cookie
            let storedUser = cookies.get('user');

            if (storedUser && storedUser.refreshToken) {
                console.log("Refreshing token");
                try {
                    const response = await buildAxiosRequest("POST", "/refresh-token", { token: storedUser.refreshToken });
                    const newToken = response.data.accessToken;
                    const newRefreshToken = response.data.refreshToken;

                    setUser((prevUser) => {
                        if (prevUser) {
                            return {
                                ...prevUser,
                                token: newToken,
                                refreshToken: newRefreshToken
                            };
                        } else {
                            // Don't update the user state if there's no user
                            return prevUser;
                        }
                    });

                    if (storedUser) {
                        // Parse the user data from JSON to an object
                        storedUser = JSON.parse(JSON.stringify(storedUser));
                    
                        // Update the token and refreshToken
                        storedUser.token = newToken;
                        storedUser.refreshToken = newRefreshToken;
                    
                        // Store the updated user data back in the cookie
                        cookies.set('user', JSON.stringify(storedUser));
                    }
                } catch (error) {
                    console.error(error);
                }
            }
        };

        // Call the function once on component mount
        refreshToken();

        // Then call it every 10 minutes
        const intervalId = setInterval(refreshToken, 10 * 60 * 1000); // 10 minutes in milliseconds

        // Clear the interval when the component is unmounted
        return () => clearInterval(intervalId);
    }, []); 

    const isTokenExpired = (token: String) => {
        try {
            const decodedToken = jwtDecode(token as string);
    
            if (!decodedToken || !decodedToken.exp) {
                return true;
            }
    
            const dateNow = new Date();
            const tokenExpirationDate = decodedToken.exp * 1000; // Convert to milliseconds
    
            return tokenExpirationDate < dateNow.getTime();
        } catch (error) {
            console.error(error);
            return true;
        }
    };

    const refreshUser = async (email: string | undefined): Promise<void> => {
        try {
            let token = user?.token;
    
            if (isTokenExpired(token as string) || !token) {
                const response = await buildAxiosRequest("POST", "/refresh-token", { token: user?.refreshToken });
                const newToken = response.data.accessToken;
                const newRefreshToken = response.data.refreshToken;
    
                setUser((prevUser) => {
                    if (prevUser) {
                        return {
                            ...prevUser,
                            token: newToken,
                            refreshToken: newRefreshToken
                        };
                    } else {
                        // Don't update the user state if there's no user
                        return prevUser;
                    }
                });
    
                let storedUser = cookies.get('user');
                if (storedUser) {
                    // Parse the user data from JSON to an object
                    storedUser = JSON.parse(JSON.stringify(storedUser));
                
                    // Update the token and refreshToken
                    storedUser.token = newToken;
                    storedUser.refreshToken = newRefreshToken;
                    token = newToken;
                
                    // Store the updated user data back in the cookie
                    cookies.set('user', JSON.stringify(storedUser));
                }
            }
    
            const headers = {
                Authorization: `Bearer ${token}`
            }
            const response = await buildAxiosRequestWithHeaders("POST", "/refresh-user", headers, {email});
            const userData = response.data;
            setUser(userData);
            const { profileImage, ...userDataWithoutImage } = userData; // Exclude profileImage
            cookies.set('user', JSON.stringify(userDataWithoutImage)); // Save user data without image in the cookie
            localStorage.setItem('profileImage', `data:image/jpeg;base64,${profileImage}`); // Store profile image URL in local storage
        } catch (error) {
            throw error;
        }
    };

    const logout = () => {
        setUser(null);
        cookies.remove('user');
        navigate(ROUTES.LANDING);
    };

    return (
        <AuthContext.Provider value={{user, login, googleLogin, refreshUser, logout, loading}}>
            <>
                <AppRouting/>
            </>
        </AuthContext.Provider>
    );
};
