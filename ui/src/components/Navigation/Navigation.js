import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar'
import NavItem from 'react-bootstrap/lib/NavItem'
import Nav from 'react-bootstrap/lib/Nav'

class Navigation extends Component {
  render() {
    return (
        <Navbar>
            <Navbar.Header>
                <Navbar.Brand>
                    Insight UI
                </Navbar.Brand>
                <Navbar.Toggle />
                <Navbar.Collapse>
                    <Nav>
                        <NavItem href="#metrics">Metrics</NavItem>
                    </Nav>
                </Navbar.Collapse>
            </Navbar.Header>
        </Navbar>
    )
  }
}

export default Navigation;
