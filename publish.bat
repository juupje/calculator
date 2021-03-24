@echo off
if "%1"=="L" GOTO latex
if "%1"=="D" GOTO dot

::Assume the first argument is the filename
if exist "%1" (
	if "%~x1" == ".dot" (
		dot -Tsvg "%1" -o "%~n1.svg"
		start %~n1.svg
		EXIT /B
	)
	if "%~x1" == ".tex" (
		pdflatex "%1"
		start %~n1.pdf
		EXIT /B
	)
) else (
	echo File not found
)

:latex
for %%f in (*.tex) do (
pdflatex "%%f"
start %%~nf.pdf
)
if "%2"=="D" GOTO dot
EXIT /B

:dot
for %%f in (*.dot) do (
dot -Tsvg "%%f" -o "%%~nf.svg"
start %%~nf.svg
)
if "%2"=="L" GOTO latex
EXIT /B