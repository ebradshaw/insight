import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar'

class Footer extends Component {
  render() {
    return (
        <Navbar fixedBottom>
            <Navbar.Header>
                <Navbar.Text>
                    Elliott Bradshaw - 2017
                </Navbar.Text>
            </Navbar.Header>
        </Navbar>
    )
  }
}

export default Footer;
