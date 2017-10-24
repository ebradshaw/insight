import React, { Component } from 'react';
import Navigation from '../Navigation/Navigation'
import Footer from '../Footer/Footer'
import SearchBar from '../SearchBar/SearchBar'
import Metrics from '../Metrics/Metrics'
import ResultsTable from '../ResultsTable/ResultsTable'
import Grid from 'react-bootstrap/lib/Grid'

import './App.css'

class App extends Component {

  constructor(props){
    super(props)
    this.refreshData = this.refreshData.bind(this)
    this.getFilteredData = this.getFilteredData.bind(this)
    this.state = {
        data: [],
        filter: ""
    }
  }

  componentDidMount() {
    this.mounted = true
    this.refreshData()
  }

  componentWillUnmount() {
    this.mounted = false
  }

  refreshData() {
    fetch('api/metrics')
        .then((res) => res.json())
        .then((data) => this.setState({ data: data.reverse() }))
    if(this.mounted) {
        setTimeout(this.refreshData, 1000)
    }
  }

  getFilteredData(){
    let { data, filter } = this.state
    return data.filter(entry => entry.url.includes(filter))
  }

  render() {
    let data = this.getFilteredData()
    let { filter } = this.state

    return <div className="app">
                <Navigation />
                <Grid>
                    <SearchBar value={filter} onChange={filter => this.setState({ filter }) } />
                    <div className="row-spacer" />
                    <Metrics data={data} />
                    <div className="row-spacer" />
                    <div style={{flex:"1 0 0%", overflowY:"auto"}}>
                        <ResultsTable data={data} />
                    </div>
                    <div className="row-spacer" />
                </Grid>
                <Footer/>
           </div>
  }
}

export default App;
