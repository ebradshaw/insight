import React, { Component } from 'react';
import ReactDataGrid from 'react-data-grid'
import Dimensions from 'react-dimensions'
import './ResultsTable.css'


class ResultsTable extends Component {


    constructor(props){
        super(props)

        const linkFormatter = (props) => <a target="_blank" href={props.value}>{props.value}</a>

        this.columns = [
            {key: 'url', name: "Request URL", width: 350, formatter: linkFormatter},
            {key: 'requestTime', name: "Request Time" },
            {key: 'stringsCreated', name: "Strings Created" },
            {key: 'memoryAllocated', name: "Memory Allocated" },
            {key: 'status', name: "Status" }
        ]

        this.getRowAt = this.getRowAt.bind(this)
    }

    getRowAt(i){
        return this.props.data[i]
    }

    render() {
        return (
            <ReactDataGrid
                columns={this.columns}
                rowGetter={this.getRowAt}
                rowsCount={this.props.data.length}
                minHeight={this.props.containerHeight} />
        )
    }
}

export default Dimensions()(ResultsTable)
