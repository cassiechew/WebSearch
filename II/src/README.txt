Ryan Chew (s3714984)
Jin Zeng (s3688213)

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