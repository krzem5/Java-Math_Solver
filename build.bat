@echo off
cls
if exist build rmdir /s /q build
mkdir build
cd src
javac -d ../build com/krzem/math_solver/Main.java&&jar cvmf ../manifest.mf ../build/math_solver.jar -C ../build *&&goto run
cd ..
goto end
:run
cd ..
pushd "build"
for /D %%D in ("*") do (
	rd /S /Q "%%~D"
)
for %%F in ("*") do (
	if /I not "%%~nxF"=="math_solver.jar" del "%%~F"
)
popd
cls
java -jar build/math_solver.jar
:end
