@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot
set PATH=C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot\bin;%PATH%
cd /d C:\Users\yavuz\Desktop\Brutality-Forge
call "%~dp0gradlew.bat" compileJava --no-daemon --console=plain


