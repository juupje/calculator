latex() {
	for f in *.tex
	do
		pdflatex "$f"
		xdg-open "${f%.*}.pdf"
	done
	if ["$2" == "D"]
	then
		dot
	fi
}

dot() {
	for f in *.dot
	do
		dot -Tsvg "$f" -o "${f%.*}.svg"
		xdg-open "${f%.*}.svg"
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
else
	if [ -f "$1" ]; then
		echo "test"
		if [ ${1: -4} == ".dot" ]; then
			dot -Tsvg "$1" -o "${1%.*}.svg"
			xdg-open "${1%.*}.svg"
		elif [ ${1: -4} == ".tex" ]; then
			pdflatex "$1"
			xdg-open "${1%.*}.pdf"
		fi
	else
		echo "File not found"
	fi
fi
