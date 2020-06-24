import React, { Component } from 'react';

import theme from 'theme';
import { ThemeProvider } from '@material-ui/core/styles';

import Navigator from 'Navigator';

class App extends Component {
	render() {
		return (
			<React.Fragment>
				<ThemeProvider theme={theme}>
					<Navigator />
				</ThemeProvider>
			</React.Fragment>
		);
	}
}

export default App;
