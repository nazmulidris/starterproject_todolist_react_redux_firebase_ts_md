/**
 * this is the main entry point for the web app
 */

import React from 'react';
import ReactDOM from 'react-dom';
import {App} from './ui/app';

/** Material UI requirement */
import injectTapEventPlugin from 'react-tap-event-plugin';

/**
 * Material UI requirement
 * Needed for onTouchTap
 * more info - http://stackoverflow.com/a/34015469/988941
 */
injectTapEventPlugin();

ReactDOM.render(
  <App />,
  document.getElementById('app')
);
