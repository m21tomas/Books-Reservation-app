import React from "react";
import UserNavBar from "../Navbars/UserNavBar";
import { useUser } from "../services/userProvider";
import jwt_decode from "jwt-decode";
import { Container, Table } from "react-bootstrap";

const UserDashboard = () => {
  const user = useUser();
  var decoded = jwt_decode(user.jwt);
  console.log("expire opoch time: ", decoded.exp * 1000)
  const dateTime = new Date(decoded.exp*1000);
  console.log("Date and time: ", dateTime.toISOString())
  console.log("Date and time: ", dateTime.toLocaleString('lt-LT', { timeZone: 'Europe/Vilnius' }))
  console.log("Timezone offset: ", dateTime.getTimezoneOffset())
  console.log("toIsoString: ", toIsoString(dateTime));

  const loginDate = new Intl.DateTimeFormat("lt-LT", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  }).format(decoded.iat * 1000);
  const expiryDate = new Intl.DateTimeFormat("lt-LT", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  }).format(decoded.exp * 1000);

  function toIsoString(date) {
    var tzo = -date.getTimezoneOffset(),
        dif = tzo >= 0 ? '+' : '-',
        pad = function(num) {
            return (num < 10 ? '0' : '') + num;
        };
  
    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate()) +
        'T' + pad(date.getHours()) +
        ':' + pad(date.getMinutes()) +
        ':' + pad(date.getSeconds()) +
        dif + pad(Math.floor(Math.abs(tzo) / 60)) +
        ':' + pad(Math.abs(tzo) % 60);
  }


  return (
    <>
      <UserNavBar />
      <Container>
        <div className="mt-3">
          <h1>User dashboard</h1>
          <Table style={{ width: "40%" }}>
            <colgroup>
              <col span="1" style={{ width: "32%" }} />
              <col span="1" style={{ width: "68%" }} />
            </colgroup>
            <tbody>
              <tr>
                <td>Username:</td>
                <td>{decoded.sub}</td>
              </tr>
              <tr>
                <td>Role:</td>
                <td>{decoded.authorities}</td>
              </tr>
              <tr>
                <td>Login date:</td>
                <td>{loginDate}</td>
              </tr>
              <tr>
                <td>Expiration date:</td>
                <td>{expiryDate}</td>
              </tr>
            </tbody>
          </Table>
          <Table className="beta" style={{ width: "40%" }}>
            <colgroup>
              <col span="1" style={{ width: "24%" }} />
              <col span="1" style={{ width: "76%" }} />
            </colgroup>
            <tbody>
              <tr>
                <td>Username:</td>
                <td>{decoded.sub}</td>
              </tr>
              <tr>
                <td>Role:</td>
                <td>{decoded.authorities.join(", ")}</td>
              </tr>
              <tr>
                <td>Login date:</td>
                <td>{loginDate}</td>
              </tr>
              <tr>
                <td>Expiration date:</td>
                <td>{expiryDate}</td>
              </tr>
            </tbody>
          </Table>
        </div>
      </Container>
    </>
  );
};

export default UserDashboard;
