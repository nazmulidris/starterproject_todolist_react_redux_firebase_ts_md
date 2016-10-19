#!/bin/bash
git subtree push --prefix web heroku master
# OR 
# git subtree push --prefix web heroku <branch>

# more info on adding remote for heroku to your git repo (locally)
# https://devcenter.heroku.com/articles/git#creating-a-heroku-remote

# more info on deploying just folders 
# http://stackoverflow.com/questions/7539382/how-can-i-deploy-push-only-a-subdirectory-of-my-git-repo-to-heroku
