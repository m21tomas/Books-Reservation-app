import React, { useEffect, useState } from "react";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import apiEndpoint from "../services/endpoint";
import logo from "../images/books01414.jpg";
import Cookies from "js-cookie";
import { useUser } from "../services/userProvider";
import jwt_decode from "jwt-decode";
import "../App.css";

const UserNavBar = () => {
  const user = useUser();
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState(null);
  useEffect(() => {
    console.log("UserNavBar mounted");
    // Add any other logging or logic you want to perform when the component mounts
    return () => {
      console.log("UserNavBar unmounted");
    };
  }, []);

  useEffect(() => {
    if (user && user.jwt) {
      setCurrentUser(jwt_decode(user.jwt).sub);
    }
  }, [user, user.jwt]);

  return (
    <Navbar
      bg="light"
      expand="lg"
      style={{ paddingTop: "0px", paddingBottom: "0px" }}
    >
      <Container style={{ marginLeft: "0px", paddingLeft: "0px" }}>
        <div onClick={() => navigate("/")} style={{ marginRight: "1em" }}>
          <img className=" nav-img" src={logo} alt="logo" loading="lazy" />
          <img className=" nav-img" src={logo} alt="logo" loading="lazy" />
        </div>

        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link href="#home">Home</Nav.Link>
            <Nav.Link href="#link">Link</Nav.Link>
          </Nav>
        </Navbar.Collapse>

        <div style={{ padding: "1em" }}>{currentUser}</div>
        {user && user.jwt ? (
          <Button
          variant="outline-secondary"
          className="logout-button"
          onClick={async () => {
            try {
              const response = await fetch(`${apiEndpoint}/api/auth/logout`, {
                method: "GET",
                credentials: "include", // Include credentials for cookie handling
              });
        
              if (response.status === 200) {
                user.setJwt(null);
                Cookies.remove("jwt", {
                  domain: "localhost",
                  path: "/",
                  expires: 1,
                });
                navigate("/");
                console.log("After navigate");
              } else {
                console.error("Logout error(inner):", response.statusText);
              }
            } catch (error) {
              console.error("Logout error:(outer)", error);
            }
          }}
        >
          Logout
        </Button>
        ) : (
          <></>
        )}
      </Container>
    </Navbar>
  );
};

export default UserNavBar;
