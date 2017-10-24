import React, { Component } from 'react';

class Metric extends Component {
  render() {
    let { label, content } = this.props
    return <div className="metric-container">
                <div className="metric-label">{label}</div>
                <div className="metric-content">{content}</div>
           </div>
  }
}

export default Metric;
