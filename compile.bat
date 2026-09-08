@echo off
cd /d "%~dp0"
javac -cp "lib\jansi-2.4.1.jar" *.java
echo Build finished.
