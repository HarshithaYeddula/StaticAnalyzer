@echo off
if "%CATALINA_HOME%" == "" (
    echo Please setup environment variable CATALINA_HOME pointing to Tomcat's base directory
) else (
    if exist target/fences-0.1.war (
        cd target
        echo Renaming jar file
        ren fences-0.1.war fences.war
        cd ..
        rmdir "%CATALINA_HOME%/webapps/fences" /s /q
        del "%CATALINA_HOME%/webapps/fences.war"
        cd target
        move fences.war "%CATALINA_HOME%/webapps"
        cd ..
        echo Deployed war to Tomcat
        echo Start Tomcat Server for web service to run
    ) else (
        echo Run build.bat first
    )
)