import React, { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Container, Row, Form } from "react-bootstrap";
import apiEndpoint from "../services/endpoint";
import { useUser } from "../services/userProvider";
import NavBar from "../Navbars/UserNavBar";
import jwt_decode from "jwt-decode";
import Cookies from "js-cookie";

const Login = () => {
  const user = useUser();
  const navigate = useNavigate();
  const usrRef = useRef();
  const passRef = useRef();
  const [errorMsg, setErrorMsg] = useState(null);

 
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

  function sendLoginRequest() {
    setErrorMsg("");
    const reqBody = {
      username: usrRef.current.value,
      password: passRef.current.value,
    };

    fetch(`${apiEndpoint}/api/auth/login`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "post",
      body: JSON.stringify(reqBody),
    })
      .then((response) => {
        console.log(reqBody);
        console.log(response.headers);
        if (response.status === 200) return response.text();
        else if (response.status === 401 || response.status === 403) {
          setErrorMsg("Invalid username or password");
        } else {
          setErrorMsg(
            "Something went wrong, try again later or reach out to m21tomas@gmail.com"
          );
        }
      })
      .then((data) => {
        if (data) {
          user.setJwt(data);
          //document.cookie=`${data}; name=jwt`;
          let decoded = jwt_decode(data);
          const expire = new Date(decoded.exp*1000);
          Cookies.set('jwt', data, {domain: 'localhost', path: '/', expires: new Date(toIsoString(expire))})
          navigate("/dashboard");
        }
      });
    usrRef.current.value = "";
    passRef.current.value = "";
  }

  return (
    <>
      <NavBar />
      <Container>
        <Row className="justify-content-center mt-5">
          <Col md="8" lg="6">
            <Form.Group className="mb-3" controlId="username">
              <Form.Label className="fs-4">Username</Form.Label>
              <Form.Control
                type="text"
                size="lg"
                placeholder="username"
                ref={usrRef}
              />
            </Form.Group>
          </Col>
        </Row>

        <Row className="justify-content-center">
          <Col md="8" lg="6">
            <Form.Group className="mb-3" controlId="password">
              <Form.Label className="fs-4">Password</Form.Label>
              <Form.Control
                type="password"
                size="lg"
                placeholder="Type in your password"
                ref={passRef}
              />
            </Form.Group>
          </Col>
        </Row>

        {errorMsg ? (
          <Row className="justify-content-center mb-4">
            <Col md="8" lg="6">
              <div className="" style={{ color: "red", fontWeight: "bold" }}>
                {errorMsg}
              </div>
            </Col>
          </Row>
        ) : (
          <></>
        )}

        <Row className="justify-content-center">
          <Col
            md="8"
            lg="6"
            className="mt-2 d-flex flex-column gap-5 flex-md-row justify-content-md-between"
          >
            <Button
              id="submit"
              type="button"
              size="lg"
              onClick={() => sendLoginRequest()}
            >
              Login
            </Button>
            <Button
              variant="secondary"
              type="button"
              size="lg"
              onClick={() => {
                navigate("/");
              }}
            >
              Exit
            </Button>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default Login;
