
m68k
====

This is my Motorola 680x0 emulator written in Java. This is the designed to be a modular cpu component for my Java based Amiga emulator project *Miggy*.  This project was originally hosted on Google code but I'm now moving it to GitHub.

Emulation
---------

Currently m68k emulates the 68000 cpu found in the Amiga and other 16-bit era machines. There is no reason why this couldn't be extended to support the rest of the 680x0 family and this was a consideration of the design.

Building
--------

There is an Apache Ant build file included.  Just run ant from the project directory.


Running
-------

There is a simple cpu monitor shell to enable testing/debugging. This can be invoked by running the following at the command prompt:

	$ java -cp m68k.jar m68k.Monitor


Feedback, Comments, Bugs etc.
-----------------------------

Please give me your feedback and comments and report any bugs via my GitHub project.


License
-------
The code is released under the Open Source BSD License.


Contributors
------------
Many thanks to [Wolfgang Lenerz](https://github.com/flockermush) and @fedex81 for their contributions.
