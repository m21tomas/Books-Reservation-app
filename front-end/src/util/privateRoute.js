import { useUser } from "../services/userProvider";
import { Navigate } from "react-router-dom";

const PrivateRoute = (props) => {
  const user = useUser();
  const { children } = props;

  if (user.isValid === null && user.jwt) return <div>Loading...</div>;
  else if (user.isValid) return children;
  else if (!user.isValid) return <Navigate to="/login" />;
};

export default PrivateRoute;
