import {createContext, useContext, useState} from "react"
import AppRouting from "./AppRouting";
import {buildAxiosRequest} from "../helpers/AxiosHelper";
import {useNavigate} from "react-router-dom";
import {ROUTES} from "../../constants/RouteConstants";

interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    token: string;
}

interface AuthContextProps {
    user: User | null;
    login: (email: string, password: string) => Promise<User>;
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

    const logout = () => {
        setUser(null);
        localStorage.removeItem('token');
        navigate(ROUTES.LANDING);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            <>
                <AppRouting />
            </>
        </AuthContext.Provider>
    );
};
