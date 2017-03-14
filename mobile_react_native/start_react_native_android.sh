#!/bin/bash
# this only starts the RN server
adb reverse tcp:8081 tcp:8081
react-native run-android

