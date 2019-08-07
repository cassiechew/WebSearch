JFLAGS = -g -d ./out/
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Index.java \
		Compressed_Index.java \
		indexing/DocumentHandler.java \
		indexing/InvIndexGenerator.java\
		util/Compressor.java \
		util/Document.java \
		util/DocumentFactory.java \
		util/LexMapping.java \
		util/SkipTags.java \
		util/SwitchTags.java \
		util/strategy/Strategy.java \
		util/strategy/VariableByte.java

default: classes

dir: mkdir -p out

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class