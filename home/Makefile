
all: \
	copernica1.w200.jpg \
	copernica2.w200.jpg

%.w200.jpg:	%.png Makefile
		pngtopnm < $< \
		| pnmscale -width 200 \
		| pnmtojpeg -optimize > $@
