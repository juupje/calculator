latex() {
	for f in *.tex
	do
		pdflatex "$f"
		xdg-open "$(basename -- $f).pdf"
	done
	if ["$2" == "D"]
	then
		dot
	fi
}

dot() {
	for f in *.dot
	do
		dot -Tps2 "$f" -o "$(basename -- $f).ps"
		ps2pdf "$(basename -- $f).ps"
		del "$(basename -- $f).ps"
		xdg-open "$(basename -- $f).pdf"
	done
	if ["$2" == "L"]
	then
		latex
	fi
}

if [ "$1" == "L" ]
then
	latex
elif [ "$1" == "D" ]
then
	dot
fi