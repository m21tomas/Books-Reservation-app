import React, { useState } from 'react';
import { useLocalState } from './useLocalStorage';
import { Navigate } from 'react-router-dom';
import apiEndpoint from '../services/endpoint';
import ajax from '../services/fetchService';

const PrivateRoute = ({children}) => {
    const [jwt, setJwt] = useLocalState("", "jwt");
    const [isLoading, setIsLoading] = useState(true);
    const [isValid, setIsValid] = useState(null);

    if(jwt) {
        ajax(`${apiEndpoint}/api/auth/validate?token=${jwt}`, "GET", jwt)
        .then((isValid) => {
            setIsValid(isValid);
            setIsLoading(false);
        });
    } else {
        return <Navigate to="/login" />;
    }

    return isLoading ? (<div>Loading...</div>) : 
    isValid === true ? (children) : <Navigate to="/login" />
};

export default PrivateRoute;