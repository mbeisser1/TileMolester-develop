package tm.tilecodecs;

/**
 * Fixed 3 bpp linear tile layout (codec id LN98).
 * Packs eight 3-bit palette indices into three bytes per row.
 **/
public class _3BPPLinearTileCodec extends TileCodec {

    /**
     * Registers the built-in 3 bpp linear codec.
     **/
    public _3BPPLinearTileCodec() {
        super("LN98", 3, "3bpp linear");
    }

    /**
     * Decodes one 3 bpp linear 8×8 tile (three bytes per row).
     * @param bits file buffer
     * @param ofs start offset of the tile
     * @param stride row padding (multiplied by bytes-per-row internally)
     * @return 64 palette indices (0–7)
     **/
    public int[] decode(byte[] bits, int ofs, int stride) {
        int pos=0;
        int b1, b2, b3;
        stride *= bytesPerRow;
        for (int i=0; i<8; i++) {
            // do one row
            b1 = bits[ofs++] & 0xFF; // byte 1: 0001 1122
            b2 = bits[ofs++] & 0xFF; // byte 2: 2333 4445
            b3 = bits[ofs++] & 0xFF; // byte 3: 5566 6777
            pixels[pos++] = (b1 >> 5) & 7;
            pixels[pos++] = (b1 >> 2) & 7;
            pixels[pos++] = ((b1 & 3) << 1) | ((b2 >> 7) & 1);
            pixels[pos++] = (b2 >> 4) & 7;
            pixels[pos++] = (b2 >> 1) & 7;
            pixels[pos++] = ((b2 & 1) << 2) | ((b3 >> 6) & 3);
            pixels[pos++] = (b3 >> 3) & 7;
            pixels[pos++] = b3 & 7;
            ofs += stride;
        }
        return pixels;
    }

    /**
     * Encodes one 3 bpp linear 8×8 tile into three bytes per row.
     * @param pixels 64 palette indices (0–7)
     * @param bits destination buffer
     * @param ofs start offset of the tile
     * @param stride row padding (multiplied by bytes-per-row internally)
     **/
    public void encode(int[] pixels, byte[] bits, int ofs, int stride) {
        int pos = 0;
        int b1, b2, b3;
        stride *= bytesPerRow;
        for (int i=0; i<8; i++) {
            // do one row
            b1 = (pixels[pos++] & 7) << 5;
            b1 |= (pixels[pos++] & 7) << 2;
            b1 |= (pixels[pos] & 6) >> 1;
            b2 = (pixels[pos++] & 1) << 7;
            b2 |= (pixels[pos++] & 7) << 4;
            b2 |= (pixels[pos++] & 7) << 1;
            b2 |= (pixels[pos] & 4) >> 2;
            b3 = (pixels[pos++] & 3) << 6;
            b3 |= (pixels[pos++] & 7) << 3;
            b3 |= (pixels[pos++] & 7);
            bits[ofs++] = (byte)b1; // byte 1: 0001 1122
            bits[ofs++] = (byte)b2; // byte 2: 2333 4445
            bits[ofs++] = (byte)b3; // byte 3: 5566 6777
            ofs += stride;
        }
    }

}
