import React, { Component } from 'react';
import FormControl from 'react-bootstrap/lib/FormControl'

class SearchBar extends Component {

  constructor(props){
    super(props)
    this.handleChange = this.handleChange.bind(this)
  }

  handleChange(e) {
    let { onChange } = this.props
    onChange(e.target.value)
  }

  render() {
    let { value } = this.props
    return(
          <FormControl
            type="text"
            value={value}
            placeholder="Filter by request path..."
            onChange={this.handleChange}
          />
    )
  }
}

export default SearchBar;
