@echo off
echo Starting build...
call mvn package
echo.
echo Generating coverage report
call jacoco:report
echo.
echo Open 'target/site/jacoco/index.html' to view the coverage report
echo.
echo To start the web service run the 'run.bat' batch file 