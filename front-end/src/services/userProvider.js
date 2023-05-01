import React, { createContext, useContext, useState } from "react";
import Cookies from "js-cookie";
const UserContext = createContext();

const UserProvider = ({ children }) => {
  const [jwt, setJwt] = useState(Cookies.get("jwt"));
  // document.cookie = `name=jwt; value=${Cookies.get("jwt")}; 
          //                    domain=${Cookies.get("Domain")};
          //                    path=${Cookies.get("Path")};
          //                    max-age=${Cookies.get("Max-Age")};
          //                    expires=${Cookies.get("Expires")}`;

  const value = { jwt, setJwt };
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