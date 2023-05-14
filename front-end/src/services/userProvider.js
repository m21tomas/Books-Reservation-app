import React, { createContext, useContext, useState, useEffect } from "react";
import Cookies from "js-cookie";
import apiEndpoint from '../services/endpoint';
import ajax from '../services/fetchService';

const UserContext = createContext();

const UserProvider = ({ children }) => {
  const [jwt, setJwt] = useState(Cookies.get("jwt"));
  const [isValid, setIsValid] = useState(null);

  useEffect (() => {
    if(jwt){
      ajax(`${apiEndpoint}/api/auth/validate`, "get", jwt)
       .then((isValid) => {
           setIsValid(isValid);
       })
    }
  }, [jwt])

  const value = { jwt, setJwt, isValid, setIsValid };
  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
};

function useUser() {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error("useUser must be used within a UserProvider");
  }

  return context;
}

export { useUser, UserProvider };