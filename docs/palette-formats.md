# Palette file formats (Tile Molester)

Palette import filters are defined in `tmspec.xml` under `<palettefilters>`. Each filter maps a file extension to a **color format** (color codec), byte **offset**, entry **count**, and optional **endianness**.

## Windows palette (`*.pal`, RIFF)

Tile Molester treats Microsoft RIFF palette files as **256 colors** starting at **byte offset 24**, decoded with color format **RIFF** (32-bit BGRx per entry, big-endian byte order in the file filter).

### File layout

| Offset | Size | Content |
|--------|------|---------|
| 0 | 4 | ASCII `RIFF` |
| 4 | 4 | File size − 8 (little-endian `uint32`) |
| 8 | 4 | ASCII `PAL ` (note trailing space) |
| 12 | 4 | `data` chunk size (little-endian `uint32`) |
| 16 | 2 | Version, usually `0x0300` |
| 18 | 2 | `wNumEntries`, usually 256 |
| 20 | 4 | Reserved (often zero) |
| **24** | 1024 | 256 × 4-byte palette entries |

Each palette entry is a Windows `PALETTEENTRY`-style quad:

| Byte | Field |
|------|--------|
| 0 | Blue |
| 1 | Green |
| 2 | Red |
| 3 | Reserved (often 0) |

The **RIFF** color codec maps these bytes to ARGB using masks `FF000000`, `00FF0000`, `0000FF00` on the 32-bit value (blue in the high byte of the 24-bit color, then green, then red).

### References

- [LOGPALETTE / PALETTEENTRY](https://learn.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-logpalette) (Microsoft Docs)
- Classic `.PAL` files are a RIFF wrapper around palette data, not raw 768-byte RGB.

### Other `.pal` handling

The filter `Raw palette (*.col, *.pal, *.bak)` uses **CF00** (15bpp BGR 555) from file offset **0** with **256** entries. That path is for raw ROM dumps, not RIFF Windows palettes. Use **Windows Palette (*.pal)** when the file begins with `RIFF`.

## Other supported imports (summary)

| Description | Extension | Codec | Size | Offset | Endianness |
|-------------|-----------|-------|------|--------|------------|
| CSV `0xRRGGBB` | `.csv` | CF01 (24bpp RGB) | variable | 0 | — |
| Raw palette | `.col`, `.pal`, `.bak` | CF00 (15bpp BGR 555) | 256 | 0 | little (default) |
| Tile Layer Pro | `.tpl` | CF01 | 256 | 4 | big |
| Windows Palette | `.pal` | RIFF | 256 | 24 | big |
| FCEUltra save state | `.fc?` | CF02 (NES indexed) | 32 | 4276 | — |
| Genesis save state | `.gs?` | CF05 (9bpp Genesis) | 64 | 274 | little |
| NESticle save state | `.st?` | CF02 | 32 | 22791 | — |
| ZSNES save state | `.zs?` | CF00 | 256 | 1560 | little |

Color formats themselves are defined under `<colorformats>` in `tmspec.xml`.
