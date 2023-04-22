import React, { useRef } from "react";
import { useLocalState } from "../util/useLocalStorage";
import { Navigate } from "react-router-dom";
import apiEndpoint from "../services/endpoint";

const Login = () => {
  const [jwt, setJwt] = useLocalState("", "jwt");
  const usrRef = useRef();
  const passRef = useRef();

  function sendLoginRequest() {
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
        if (response.status === 200)
          return Promise.all([response.json(), response.headers]);
        else return Promise.reject("Invalid login attempt");
      })
      .then(([body, headers]) => {
        console.log(body)
        setJwt(headers.get("authorization"));
        window.location.href = "dashboard";
      })
      .catch((message) => {
        alert(message);
      });
    usrRef.current.value = "";
    passRef.current.value = "";
  }
  return (
    <>
      <div>
        <label htmlFor="username">Username</label>
        <input ref={usrRef} type="text" id="username" />
      </div>
      <div>
        <label htmlFor="password">Password</label>
        <input ref={passRef} type="text" id="password" />
      </div>
      <div>
        <button id="submit" type="button" onClick={() => sendLoginRequest()}>
          Login
        </button>
      </div>
    </>
  );
};

export default Login;
