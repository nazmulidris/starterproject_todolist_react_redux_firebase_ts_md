#!/bin/bash
git subtree push --prefix web heroku master
# OR 
# git subtree push --prefix web heroku <branch>

# more info on adding remote for heroku to your git repo (locally)
# https://devcenter.heroku.com/articles/git#creating-a-heroku-remote
