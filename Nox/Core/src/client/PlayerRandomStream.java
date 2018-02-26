package client;

import net.OutPacket;
import server.Randomizer;

public class PlayerRandomStream {

    private transient long seed1, seed2, seed3;
    private static final PlayerRandomStream pStream = new PlayerRandomStream();

    public static final PlayerRandomStream GetInstance() {
        return pStream;
    }

    public PlayerRandomStream() {
        final int v4 = 5;
        this.CRand32__Seed(Randomizer.nextLong(), 1170746341 * v4 - 755606699, 1170746341 * v4 - 755606699);
    }

    public final void CRand32__Seed(final long s1, final long s2, final long s3) {
        seed1 = s1 | 0x100000;
        seed2 = s2 | 0x1000;
        seed3 = s3 | 0x10;
    }

    public final long CRand32__Random() {
        long v8 = ((this.seed1 & 0xFFFFFFFE) << 12) ^ ((this.seed1 & 0x7FFC0 ^ (this.seed1 >> 13)) >> 6);
        long v9 = 16 * (this.seed2 & 0xFFFFFFF8) ^ (((this.seed2 >> 2) ^ this.seed2 & 0x3F800000) >> 23);
        long v10 = ((this.seed3 & 0xFFFFFFF0) << 17) ^ (((this.seed3 >> 3) ^ this.seed3 & 0x1FFFFF00) >> 8);
        return (v8 ^ v9 ^ v10) & 0xffffffffL; // to be confirmed, I am not experienced in converting signed > unsigned
    }

    public final void connectData(final OutPacket oPacket) {
        long v5 = CRand32__Random();
        long s2 = CRand32__Random();
        long v6 = CRand32__Random();

        CRand32__Seed(v5, s2, v6);

        oPacket.EncodeInteger((int) v5);
        oPacket.EncodeInteger((int) s2);
        oPacket.EncodeInteger((int) v6);
    }

    public long Random() {
        this.seed1 = ((((this.seed1 >> 6) & 0x3FFFFFF) ^ (this.seed1 << 12)) & 0x1FFF) ^ ((this.seed1 >> 19) & 0x1FFF) ^ (this.seed1 << 12);
        this.seed2 = ((((this.seed2 >> 23) & 0x1FF) ^ (this.seed2 << 4)) & 0x7F) ^ ((this.seed2 >> 25) & 0x7F) ^ (this.seed2 << 4);
        this.seed3 = ((((this.seed3 << 17) ^ ((this.seed3 >> 8) & 0xFFFFFF)) & 0x1FFFFF) ^ (this.seed3 << 17)) ^ ((this.seed3 >> 11) & 0x1FFFFF);
        return (this.seed1 ^ this.seed2 ^ this.seed3) & 0xFFFFFFFFL;
    }

    public static final long GetRandom() {
        return GetInstance().Random();
    }
}
