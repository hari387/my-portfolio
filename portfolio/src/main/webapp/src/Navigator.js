import React, { Component } from 'react';
import clsx from 'clsx';
import { withStyles, ThemeProvider, createMuiTheme, createTypography } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { Typography } from '@material-ui/core';

const navbarContent = {
	title: 'Waverly Place',
	articles: [
		{title: 'Article #1', date: '6/21/2020'},
		{title: 'Article #2', date: '6/15/2020'},
		{title: 'Article #3', date: '6/8/2020'},
		{title: 'Article #4', date: '6/1/2020'},
	],
};

const styles = theme => ({
	text: {
		color: theme.palette.primary.light,
	},
	item: {
		fontFamily: "'Comic Sans MS'"
	},
	title: {
		fontFamily: "'Niconne', cursive",
		fontSize: 40,
	},
	category:{
		fontSize:24,
		fontFamily: "'Comic Sans MS'"
	},
	divider:{
		marginBottom:theme.spacing(2)
	}
});

class Navigator extends Component {
	render() {
		const { classes, ...other } = this.props;

		return (
			<Drawer variant="permanent">
				<List disablePadding>
					<ListItem>
						<ListItemText>
							<div className={clsx(classes.title,classes.text)}>
								Portfolio
							</div>
						</ListItemText>
					</ListItem>
					<Divider className={classes.divider} />
					<ListItem>
						<div className={clsx(classes.text,classes.category)}>
							Recent Articles
						</div>
					</ListItem>
					{navbarContent.articles.map((article,index) => (
						<React.Fragment key={index}>
							<ListItem>
								<div className={clsx(classes.text,classes.item)}>
									{article.title}
								</div>
							</ListItem>
						</React.Fragment>
					))}
				</List>
			</Drawer>
		);
	}
}

export default withStyles(styles)(Navigator);
