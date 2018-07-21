@echo off
if "%1"=="L" GOTO latex
if "%2"=="L" GOTO latex
if "%1"=="D" GOTO dot
if "%2"=="D" GOTO dot

:latex
for %%f in (*.tex) do (
pdflatex "%%f"
start %%~nf.pdf
)
if "%2"=="D" GOTO dot
EXIT /B

:dot
for %%f in (*.dot) do (
dot -Tps2 "%%f" -o "%%~nf.ps"
ps2pdf "%%~nf.ps"
del "%%~nf.ps"
start %%~nf.pdf
)
if "%2"=="L" GOTO latex
EXIT /B