Ryan Chew (s3714984)
Jin Zeng (s3688213)


/* INDEXING */

To compile this program, run the compile script

    %. ./compile

The files will be compiled into an out folder in this directory.

Usage: java Index [-p|--print] [-s|-stoplist <src>] [-t, --time]
                [-c, --compress <strategy>] [-h, --help] <source file>
Creates an inverted index of the supplied document
Options:
  -p, --print            Prints the cleaned text
  -s, --stoplist         Uses the supplied stoplist for processing
  -t, --time             Times the excecution time
  -c, --compress         Use variable byte compression
  -h, --help             Prints this help message and exits

Strategies:
  none [default]  ->     No compression
  varbyte         ->     Variable Byte Compression

Upon compression, the lexicon and inverted list file will be generated with a suffix
to denote the compression type.

The code relating to compression is in the folder

    % util/strategy

The related files are:

    % VariableByte.java

This file contains the code used to perform the compression. See report for more details.


/* Ranked Search */

Usage: java Search  -BM25 -q <query-label> -n <num-results> -l <lexicon> -i <invlists>
            -m <map> -a <noDocsToAdd> <noTermsToAdd> [-s <stoplist>] <queryterm-1> [<queryterm-2> ...  <queryterm-N>]


  -BM25                 Specifies that the BM25 similarity function is to be used.
  -q <query-label>      An integer that identifies the current query. For example, you might indicate that the current
                            query label is “1133”. This item is only used for output formatting (see below).
  -n <num-results>      An integer number specifying the number of top-ranked doc- uments that should be returned as
                            an answer.
  -l <lexicon>          The inverted index lexicon file
  -i <invlists>         The inverted list file
  -m <map>              The mapping table from internal document numbers to actual document identifiers.
  -s <stoplist>         An optional argument which may indicate a stoplist;
                            note that if the inverted index was created with stopping, then queries should be stopped in exactly the
                            same way.

  -a <noDocsToAdd=1> <noTermsToAdd> <documentSourceFile> is to flag for advanced search features. The first two arguments
        should be numbers, noDocsToAdd shoule be less than or equal to num-results, and the sourcefile should be the
        original document file used in indexing. Automatic query expansion.

  <queryterm-1> [<queryterm-2> ... <queryterm-N>] are a variable number of query terms (but at least one query term)
        that the program will receive as command- line arguments. Each of these terms should be looked up in the
        lexicon, and the appropriate inverted list data fetched from the inverted list file.
