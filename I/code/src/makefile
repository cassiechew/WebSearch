JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Index.java \
		Compressed_Index.java \
		indexing/*.java \
		util/*.java \
		util/strategy/*.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class