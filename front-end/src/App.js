import { useState, useEffect } from "react";
import "./App.css";
import { Route, Routes } from "react-router-dom";
import UserDashboard from "./Dashboard/userDashboard";
import Homepage from "./Homepage";
import Login from "./Login";
import PrivateRoute from "./util/privateRoute";
import jwt_decode from 'jwt-decode'
import AdminDashboard from "./Dashboard/adminDashboard";
import { useUser } from "./services/userProvider";


function App() {
  const [roles, setRoles] = useState([]);
  const user = useUser();
  
  useEffect(() => {
    function getRolesFromJWT() {
      if (user.jwt) {
        const decodedJwt = jwt_decode(user.jwt);
        return decodedJwt.authorities;
      }
      return [];
    }
    setRoles(getRolesFromJWT());
  }, [user.jwt]);

  
  
  function pickDashboard (authArray){
    let dash = undefined;
    if(authArray.length === 1){
      authArray.map((item) => {
        return(item === "Administrator" ? dash = 0 : dash = 1)
        })
    }
    else{
      for (let i = 0; i < authArray.length; i++) {
        if(authArray[i] === "Administrator") {
          dash = 0;
          break;
        }
      }
    }

    if(dash === 0) return <AdminDashboard />
    else if(dash === 1) return <UserDashboard />
    else return <Homepage />
  }
  
  return ( 
    <Routes>
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
              {pickDashboard(roles)}
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Homepage />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}

export default App;
