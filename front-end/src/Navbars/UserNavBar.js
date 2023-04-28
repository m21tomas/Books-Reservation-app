import React from "react";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import logo from "../images/books01414.jpg";
import { useLocalState } from "../util/useLocalStorage";
import jwt_decode from 'jwt-decode'
import "../App.css";

const UserNavBar = () => {
  const [jwt, setJwt] = useLocalState("", "jwt");
  const navigate = useNavigate();
  var decoded = jwt_decode(jwt);

  return (
    <Navbar bg="light" expand="lg" style={{paddingTop: "0px", paddingBottom: "0px"}}>

      <Container style={{marginLeft: "0px", paddingLeft: "0px"}}> 
        <div onClick={() => navigate("/")} style={{marginRight: "1em"}}>
          <img
            className=" nav-img"
            src={logo}
            alt="logo"
            loading="lazy"
          />
          <img
            className=" nav-img"
            src={logo}
            alt="logo"
            loading="lazy"
          />
        </div>
        
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link href="#home">Home</Nav.Link>
            <Nav.Link href="#link">Link</Nav.Link>
          </Nav>
        </Navbar.Collapse>

        <div style={{padding: "1em"}}>
          {decoded.sub}
        </div>

        <Button variant="outline-secondary"
                className="logout-button"
                onClick={() => {setJwt(null); navigate("/login")}}    
        >
           Logout
        </Button>
      </Container>
    </Navbar>
  );
};

export default UserNavBar;
