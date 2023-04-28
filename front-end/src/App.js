import "./App.css";
import { Route, Routes } from "react-router-dom";
import UserDashboard from "./Dashboard/userDashboard";
import Homepage from "./Homepage";
import Login from "./Login";
import PrivateRoute from "./util/privateRoute";
import jwt_decode from 'jwt-decode'
import AdminDashboard from "./Dashboard/adminDashboard";
import { useLocalState } from "./util/useLocalStorage";

function App() {
  const [jwt, setJwt] = useLocalState("", "jwt");
  console.log("jwt: ", jwt)
  var decoded = undefined
  if(jwt){
    decoded = jwt_decode(jwt);
  } 
  
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
            {
              decoded !== undefined ? pickDashboard(decoded.authorities) : console.log("decoded: ", decoded)
            }
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Homepage />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}

export default App;
