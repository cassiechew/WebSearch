Ryan Chew (s3714984)
Jin Zeng (s3688213)

To compile this program, run the makefile

    %. make

Usage: Index [-p|--print] [-s|-stoplist <src>] [-t, --time]
                [-c, --compress <strategy>] [-h, --help] <source file>
Creates an inverted index of the supplied document
Options:
  -p, --print            Prints the cleaned text
  -s, --stoplist         Uses the supplied stoplist for processing
  -t, --time             Times the excecution time
  -c, --compress         Use variable byte compression
  -h, --help             Prints this help message and exits

Strategies:");
  varbyte         ->     Variable Byte Compression


This program includes an option for compression. However, the Compressed_Index version has the
variable byte compression by default.