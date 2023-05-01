import React, { useState } from 'react';
import { useUser } from '../services/userProvider';
import { Navigate } from 'react-router-dom';
import apiEndpoint from '../services/endpoint';
import ajax from '../services/fetchService';

const PrivateRoute = (props) => {
    const user = useUser();
    const [isLoading, setIsLoading] = useState(true);
    const [isValid, setIsValid] = useState(null);
    const { children } = props;

    if(user && user.jwt) {
        ajax(`${apiEndpoint}/api/auth/validate`, "get", user.jwt)
        .then((isValid) => {
            setIsValid(isValid);
            setIsLoading(false);
        })
    } else {
        console.log("PrivateRoute does not have user, navigating to /login")
        return <Navigate to="/login" />;
    }

    return isLoading ? (<div>Loading...</div>) : 
    isValid === true ? (children) : <Navigate to="/login" />
};

export default PrivateRoute;