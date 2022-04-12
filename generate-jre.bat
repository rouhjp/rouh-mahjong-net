@echo off
setlocal

.\jdk-17\bin\jdeps --list-deps --ignore-missing-deps .\build\libs\rouh-mahjong-net-1.0-SNAPSHOT-all.jar | findstr java > tmp.txt
for /f %%x in (tmp.txt) do (
    call set dep-list=%%dep-list%%,%%x
)
set dep-list=%dep-list:~1%
rem echo dep-list=%dep-list%

del tmp.txt
if exist ".\build\product\jre-min" rd /s /q ".\build\product\jre-min"

.\jdk-17\bin\jlink --compress=2 --module-path "jdk-17/bin/jmods" --add-modules %dep-list% --output build\product\jre-min
