The Wayback Machine - https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no:80/~kenth/tm/

_SnowBro Software presents..._

Tile Molester
=============

![](data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAcIAAADwAQMAAACpE/YUAAAAK3RFWHRDcmVhdGlvbiBUaW1lAHRvIDI5IG1haSAyMDAzIDE3OjI3OjA5ICswMTAw1+zjOgAAAAd0SU1FB9MFHQ8gEl+M1sQAAAAJcEhZcwAACvAAAArwAUKsNJgAAAAEZ0FNQQAAsY8L/GEFAAAABlBMVEUAAAD///+l2Z/dAAADzUlEQVR42u3YTW8bRRgH8DGusqoUvB+AqiMVzsCRQ+jkxgnlK/RWjpF6III2Y+ght1ocEVQ+8CmQKsWRpUYc6HLgQMXB6y7KHrPuIjyRx/Nndtdu3TRRZh5UUMTMYeV5+dmzM7PjZ5aBmAbsMkt2fmq7yFaTKZocb3KzIIMMMsgggwwyyCCDvJzyzHRBZCzrq27aRss2QQYZZJBBBhlkkEEGGeSbl+z89D94wy3JUlCl7MEIkuR7KGjyhyGGNPnNGB2a/O2zuzdp8sl7H2pJknnUVjRZ5iho86mAsZEU+YdEZki/eSSQ02Teo8osp/Y2m6Aw+J0ghxr6fZI8MBIdknxqV9ADf7nJ+LeZwGPzjrfc5Nfslv2MJDe4/ay/85W3bvENCai3/eU2txDmqrfc3r5ey6G33NmpJQjyo0Zm/nKDKu/ebCT+Rbkr1mhyXYqvaTKSYkCTa8AvJLluIxJFkldaVLlu6zVZGpLstKkyfll1KeQxWSbLqp/8ZITesupnP8l4tKw68pRrZMnaPbK8v6h67isZ85GvpHXuJGV9feW8IhZVE2+5v6jS3rKPM9Obk3OytDfXo0mzyz4gSh7FNIlYgCjf7VPljz2q/H6LKkcxUeqtPaIcRDm1t8MpUc7LkihPTh8ZPCSIcvYfyPIfSEaVevnyw1cOT1SfJrOWEUSZgbibZIcYEEcowcGpSlfZ1y97K3zkRKimfZd7SiX+rApMlTHcS6KQTUF7pGMfCfzFFwW5r8yrq7K5L1WkMXrx33axPD5YyGtFVIq46b2TRDW4VZTSSdtJcvvFknKQxrZNbe7TtCXKz9O2u8T9WsblgEmtlrGSk0yrfAtXuoyrTca0u1Q7wy4TRczuPVizpTfcZfZcdCOo7ifZuKq4ngpX2Zvu2VaP7/y6O62mpxN3gbHT2PJDcIwencDUfW/DqF7fRWo5eGuCJ18YeYLRU/slQPJMuMgJ0lgXrYfg9oCW1u0P3e7TNtvnx+VMY7aIGfYcR8h2T8/7U11itrrjXyxtF7+aGHkktuy2LX2kghG3IRM8iqar0aNDNB7DbKH/EMqGDPn58syk1Sj52BRxYjvgJ+0oHd3DIB6tbn+OMirmdt+0d5l4SnPHxguM79dT5CWVmtstVwisTIubnFihmZCroYqbrB4Wxe0ZP/OV/foYYFYHyE3av+1xI7mn1M0bELsmpaesxoVXG30LnrJ6uOymqyJQ5OR0oZMsgNU14CHrkEhQZD2bMUnapaNo0g7PAUhSyddPoW5Si9fjRscnO05BkzhsU2XKqfKMFOSF8m8xOPKtURCz+gAAAABJRU5ErkJggg==)

  

\[[News](#NEWS)\] \[[About](#ABOUT)\] \[[Downloads](#DOWNLOADS)\] \[[Gallery](#GALLERY)\] \[[Documentation](#DOCS)\] \[[Contact](#CONTACT)\]

  

* * *

  

### News

**11.01.2005**: I've compiled TM using the new JDK1.5.0. It now appears to work on both Windows XP and (Mandrake)Linux without strange errors. I originally developed TM on Windows 98 SE, and TM 0.15c didn't have any of the XML parsing errors etc. that it has on XP and Linux (otherwise I would have fixed it long time ago). Anyways, I've released the new version: [0.16](#DOWNLOADS).  

**28.06.2004**: Wow, an update. Not much to get excited about though, unless you run linux. I finally mustered up the strength for the ordeal that was to make Tile Molester work under linux. Quite a few have made me aware of the problem, so now you may rejoice. [Here](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/tmlinux.png)'s how it could look in MDK9.2, for example. Get the new release from the [Downloads](#DOWNLOADS) section.  

**22.11.2003**: Together with my friend EFX I discovered that quite a few NES games store their graphics as a series of _columns_ of tiles, as opposed to a series of _rows_, which is the common way of storing graphics. In order to view graphics stored in this manner properly, the tiles must be ordered top-to-bottom, left-to-right (as opposed to left-to-right, top-to-bottom, which is the conventional tile order) when drawing them. Fortunately, this can easily be achieved with Tile Molester by manipulating the _block size_ from the View -> Block Size -> Custom... dialog. The preferred size for most games is 1 column, 16 rows. To see the dramatic improvement in the viewability of graphics for a select few games, take a look at these screens:

*   [Kirby's Adventure](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/Kirby_1x16.png)
*   [Gremlins 2](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/Gremlins2_1x16.png)
*   [Batman - Return of the Joker](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/Batman-ROTJ_1x16.png)
*   [Blaster Master](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/BlasterMaster_1x16.png)
*   [Kabuki Quantum Fighter](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/Kabuki_1x8.png)

Which games use this scheme seems to be company-dependent. I haven't investigated whether the same storage technique is used on other console systems as well.  

**09.09.2003**: Mainly because of a busy school and work schedule (and a different emulation-related project I've been working on for some time now), I won't be able to do much with TM this fall. Even though TM is officially at version 0.15a, it's more like version 0.90 according to the original plan I had with the program anyway. What's mainly missing is a good set of tutorials that showcase all aspects and possibilities of TM's functionality. If anyone feels up to the task, by all means...  

Oh yeah, there's a new version of the Java environment out; maybe TM will work better now for those who had problems before...?  

**28.07.2003**: I finally got around to uploading the [source code](#DOWNLOADS) to version 0.15a. There's also a new version of the [TM specification](#DOWNLOADS) with support for (uncompressed) Magic Engine savestate palettes. Lastly, an [Italian translation](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/lang/language_it_IT.properties) of TM has been added.  

**12.06.2003**: A new version of TM is out -- version 0.15a to be exact. There's not too much new; some bugs have been discovered lately by my nice beta tester Rob, and I felt that some of them were serious enough to warrant a new release. There's also official support for Genecyst savestate palettes now, as well as for FCEUltra savestate palettes, and a new tile codec (3bpp linear) has been added. So be sure to grab it from the [Downloads Section](#DOWNLOADS).  

**12.06.2003**: Any oldskool people out there who use (or at least _used to_ use) DeluxePaint? Then you might get a kick out of [this](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/DPaint.png). :) Now you can \_finally\_ design your own user interface icons for this great program... A dream come true.  

I've been looking into the Turaco ini file format, used by arcade emulators to describe graphics formats for individual games (thanks to the honourable C. Doty for the info). This gave me the neat idea of creating a new type of template tile codec that takes a Turaco ini file as input and creates a TM codec for it. Instant arcade game support. We'll see how it goes...  

**11.06.2003**:  

*   First of all, thanks for all the nice feedback these last few days.
*   Tile Molester can now load palettes from Kega/Genecyst/Gens savestates (\*.gs?) (and directly from SMD/BIN files if you're a bit more hardcore). Thanks to Charles Doty for giving the offset of CRAM within .gs files. Download the new specification file from the [Downloads Section](#DOWNLOADS).
*   Thanks to "xdaniel", who graciously sent some PSX TIM image files, I've been able to verify that TM can view some of these formats quite well. Check [this](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/PSXLogo.png) and [this](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/pics/CT_PSX.png) for two examples, showing 4bpp and 8bpp images respectively. The info on TIM is due to [Klarth](https://web.archive.org/web/20060101100845/http://rpgd.emulationworld.com/klarth)'s document. The palette data begins at offset 20 in the file, so to view a 4bpp or 8bpp TIM in correct colors, select Palette->Import From->This File, then enter offset 20 and set the format to 15bpp BGR, Intel byte order (little endian). If anyone has ripped 16bpp/24bpp TIM files I'd appreciate to hear about it.
*   I've had some questions about how to deal with games that have compressed graphics. Currently TM doesn't support any form of compression; this would require an extension to the plug-in system that can deal with compression on a per-game basis. It's pretty interesting, but would require that a separate decompressor/recompressor be written for each game, which is a time-consuming job. (Locate the graphics, reverse- engineer the decompression algorithm, write a Java-equivalent algorithm, write an optimal recompression algorithm...)
*   "Vag" reports that he's spotted some graphics in Amiga games using TM. Cool! I decided to have a look myself, and quickly found lots of 1bpp and 2bpp linear graphics in "Defender of the Crown" and "Speedball 2".

**05.06.2003**: Turns out the offset for palette data in ZSNES save states is wrong in the specification file for TM v0.1a; so if you've had problems importing palettes from ZST files, this is the reason. Until there's a new version of Tile Molester, you can download the updated specification file separately [here](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/tmspec.xml) (right-click and save). Simply overwrite the one that's in your TM folder.  

**03.06.2003**: Added a small [graphics formats reference](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/formatref.htm) which lists the graphics formats that are known to be used by various systems. Also added a [Spanish translation](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/lang/language_sp_LA.properties), thanks to Magimaster2!  

**01.06.2003**: Some people have had problems running Tile Molester because JAR files aren't associated with the Java runtime environment on their machines; typically it is associated with compression programs instead. (It seems this applies mostly (only?) to Windows, WinZip users.) If you don't want to change the associativity settings, you can 1) run Tile Molester from the commandline, by standing in the Tile Molester root directory and execute the command java -jar tm.jar, or 2) put the preceding command in a batch file and save it in the Tile Molester root directory. Double-clicking on that file should then do the trick.  

**29.05.2003**: First public version of Tile Molester has been released, after roughly a month of development. Thanks to Rob for doing some alpha-testing. Other than him and myself, nobody has tried the program, so I'm very interested in receiving feedback on how it's working (or not working) for everyone. Thanks.  

* * *

  

### About

Tile Molester is a multi-format, user-extensible graphics data editor that lets you create, view and edit graphics in arbitrary binary files, with a particular focus on binaries for _game consoles_. Personally I use it for Nintendo (NES) and GameBoy Advance game development at this time, but a large range of other formats are supported, including (but not limited to) those for the following consoles:  

*   GameBoy
*   Sega Master System, Game Gear
*   Sega Genesis, Mega Drive, 32X
*   Super Nintendo
*   Turbo Grafx-16, PC Engine
*   NeoGeo Pocket
*   Virtual Boy
*   WonderSwan
*   X68000
*   Nintendo 64

  

Tile Molester uses XML to achieve a "pluggable" design, where every file format and graphics format is defined separate from the program; new formats can be added and existing formats customized by the user without requiring any changes to the program itself.  

The program is written entirely in [Java](https://web.archive.org/web/20060101100845/http://java.sun.com/), so it can be run on any platform that has a Java Runtime Environment installed. See the Documentation for more information. The full source code is also available under the GNU Public License.  

    _Tile Molester_ is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    _Tile Molester_ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    [GNU General Public License](https://web.archive.org/web/20060101100845/http://www.gnu.org/licenses/gpl.html) for more details.

  

* * *

  

### Downloads

[Tile Molester v0.16 \[Bin\]](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/tilemolester-0.16.zip)  

[Tile Molester v0.16 \[Src\]](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/tilemolester-0.16.src.tar.gz)  

If you don't have J2SE 1.5.0 installed, click [here](https://web.archive.org/web/20060101100845/http://java.sun.com/j2se/1.5.0/download.html) to download it.  

* * *

  

### Gallery

Click [here](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/gallery.htm) to see some screenshots of glorious moments.  

* * *

  

### Documentation

Click [here](https://web.archive.org/web/20060101100845/http://www.stud.ntnu.no/~kenth/tm/docs/help.htm) to view the online help files.  

  

* * *

  

### Contact

If you have bug reports, suggestions or other contributions, you may submit them [here](https://web.archive.org/web/20060101100845/mailto:kenth%20at%20stud.ntnu.no).

  

_© Kent Hansen 2003, 2004, 2005_