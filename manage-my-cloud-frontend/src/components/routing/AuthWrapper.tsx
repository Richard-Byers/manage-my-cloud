import {createContext, useContext, useEffect, useState} from "react"
import AppRouting from "./AppRouting";
import {buildAxiosRequest} from "../helpers/AxiosHelper";
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../constants/RouteConstants";
import { useGoogleLogin } from '@react-oauth/google';
import Cookies from 'universal-cookie'


interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    token: string;
    accountType: string | null;
    linkedAccounts: {
        linkedAccountsCount: number;
        oneDrive: boolean;
    }
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
            cookies.set('user', JSON.stringify(userData));
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
                const response = await buildAxiosRequest("POST", "/registergoogleuser", { authCode });
                const data = response.data;
                setUser(data);
                localStorage.setItem('token', data.token); // Set the token
                navigate("/profile")
            } catch (error) {
                // Handle the error
                console.error('Error:', error);
            }
        },
        flow: 'auth-code',
        scope: 'https://www.googleapis.com/auth/drive',
    });

    const refreshUser = async (email: string | undefined): Promise<void> => {
        try {
            const response = await buildAxiosRequest("POST", "/refresh-user", {email});
            const userData = response.data;
            setUser(userData);
            cookies.set('user', JSON.stringify(userData));
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
        <AuthContext.Provider value={{ user, login, googleLogin, refreshUser ,logout, loading }}>
            <>
                <AppRouting />
            </>
        </AuthContext.Provider>
    );
};
