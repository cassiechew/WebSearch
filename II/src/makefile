JFLAGS = -g -d ./out/
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Index.java \
		Search.java \
		indexingModule/DocumentHandler.java \
		indexingModule/InvIndexGenerator.java \
		queryModule/BM25.java \
		queryModule/QueryDocumentHandler.java \
		queryModule/QueryProcessing.java \
		queryModule/RSJ.java \
		queryModule/TSV.java \
		util/Accumulator.java \
		util/Compressor.java \
		util/Document.java \
		util/DocumentFactory.java \
		util/LexMapping.java \
		util/MapMapping.java \
		util/SkipTags.java \
		util/SwitchTags.java \
		util/TermSelectionValue.java \
		util/strategy/Strategy.java \
		util/strategy/VariableByte.java \
		util/strategy/Standard.java \
		util/strategy/AStrategy.java

default: classes

dir: mkdir -p out

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class