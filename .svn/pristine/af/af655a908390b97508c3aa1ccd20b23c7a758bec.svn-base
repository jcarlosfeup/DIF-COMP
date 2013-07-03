all: javac

retest:javac test

test:

	rm -f testes/result.txt
	testes/scripts/testes_unitarios.py

clean:
	rm -f *.class
	rm -f *.dot

javac: javacc
	javac *.java

javacc: jjtree
	javacc/bin/javacc DIF.jj

jjtree:
	javacc/bin/jjtree DIF.jjt


