echo off
echo NUL>_.class&&del /s /f /q *.class
cls
javac com/krzem/math_solver/Main.java&&java com/krzem/math_solver/Main
start /min cmd /c "echo NUL>_.class&&del /s /f /q *.class"