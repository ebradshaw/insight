import React from 'react';
import ReactDOM from 'react-dom';
import App from './components/App/App';
import registerServiceWorker from './registerServiceWorker';
import 'bootswatch/flatly/bootstrap.css'
import './index.css'

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
