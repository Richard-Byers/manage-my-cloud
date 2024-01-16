import {createContext, useContext, useState} from "react"
import AppRouting from "./AppRouting";
import {buildAxiosRequest} from "../helpers/AxiosHelper";
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../constants/RouteConstants";
import { useGoogleLogin } from '@react-oauth/google';

interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    token: string;
    accountType: string | null;
}

interface AuthContextProps {
    user: User | null;
    login: (email: string, password: string) => Promise<User>;
    googleLogin: () => void;
    logout: () => void;
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
    const navigate = useNavigate();
    const [user, setUser] = useState<User | null>(null);

    const login = async (email: string, password: string): Promise<User> => {
        try {
            const response = await buildAxiosRequest("POST", "/login", { email, password });
            const userData = response.data;
            setUser(userData);
            localStorage.setItem('token', userData.token);
            return userData;
        } catch (error) {
            throw error;
        }
    };

    const googleLogin = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            console.log(codeResponse);
            const authCode = codeResponse.code; 

            // Send the code to the server
            try {
                const response = await buildAxiosRequest("POST", "/storetoken", { authCode });
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

    const logout = () => {
        setUser(null);
        localStorage.removeItem('token');
        navigate(ROUTES.LANDING);
    };

    return (
        <AuthContext.Provider value={{ user, login, googleLogin, logout }}>
            <>
                <AppRouting />
            </>
        </AuthContext.Provider>
    );
};
