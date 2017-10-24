import React, { Component } from 'react';
import Metric from './Metric'
import './Metrics.css'

const avg = (data, field) => data.reduce((val, entry) => val += entry[field], 0) / (data.length || 1)


class Metrics extends Component {
  render() {
    const { data } = this.props
    const totalRequests = data.length
    const avgRequestTime = Math.round(avg(data, "requestTime"))
    const avgStrings  = Math.round(avg(data, "stringsCreated"))
    const avgMemory = Math.round(avg(data, "memoryAllocated") / 1000)

    return (
        <div className="metrics">
            <Metric label="Total Requests" content={totalRequests}/>
            <div className="metrics-spacer" />
            <Metric label="Avg Request Time" content={avgRequestTime + " ms"}/>
            <div className="metrics-spacer" />
            <Metric label="Avg Memory Allocated" content={avgMemory + " kb"} />
            <div className="metrics-spacer" />
            <Metric label="Avg Strings Created" content={avgStrings}/>
        </div>
    )

  }
}

export default Metrics;
