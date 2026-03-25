@echo off
cd /d C:\Users\yavuz\Desktop\Brutality-Forge
gradlew.bat compileJava > build_output.txt 2>&1
type build_output.txt
