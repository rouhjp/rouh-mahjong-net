@echo off
setlocal

.\jdk-17.0.1\bin\jdeps --list-deps --ignore-missing-deps .\build\libs\rouh-mahjong-net-1.0-SNAPSHOT-all.jar | findstr java > tmp.txt
for /f %%x in (tmp.txt) do (
    call set dep-list=%%dep-list%%,%%x
)
set dep-list=%dep-list:~1%
rem echo dep-list=%dep-list%

del tmp.txt
if exist ".\build\jre-min" rd /s /q ".\build\jre-min"
rem if exist ".\jre-min" rd /s /q ".\jre-min"

.\jdk-17.0.1\bin\jlink --compress=2 --module-path "jdk-17.0.1/bin/jmods" --add-modules %dep-list% --output build\jre-min
