package netty;

import crypto.CAESCipher;
import net.Packet;
import net.InPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstraction for Netty channels that contains some attribute keys
 * for important resources used by the client during encryption, 
 * decryption, and general functions. <B>Note: Some methods cannot be
 * overridden by descendents due to the nature of the functionality they 
 * provide</B>
 * 
 * @author Zygon
 */
public class CClientSocket {
    
    /**
     * Attribute key for MapleCrypto related to this Client.
     */
    public static final AttributeKey<CAESCipher> CRYPTO_KEY = AttributeKey.valueOf("A");
    /**
     * Attribute key for this NettyClient object.
     */
    public static final AttributeKey<CClientSocket> CLIENT_KEY = AttributeKey.valueOf("C");
    
    /**
     * Send seed or IV for one of the cryptography stages.
     */
    private int uSeqSend;
    /**
     * Receive seed or IV for one of the cryptography stages.
     */
    private int uSeqRcv;
    /**
     * Defines which encryption method to be used in the encode/decoder.
     */
    private int nCryptoMode = 1;
    /**
     * Stored length used for packet decryption. This is used for
     * storing the packet length for the next packet that is readable.
     * Since TCP sessions ensure that all data arrives to the server in order,
     * we can decode packet data in the correct order.
     */
    private int nSavedLen = -1;
    /**
     * Channel object associated with this specific client. Used for all
     * I/O operations regarding a MapleStory game session.
     */
    protected final Channel ch;
    
    /**
     * Lock regarding the encoding of packets to be sent to remote 
     * sessions.
     */
    private final ReentrantLock lock;
    
    /**
     * PacketReader object for this specific session since this can help
     * scaling compared to keeping PacketWriter for each session.
     */
    private final InPacket decoder;
    
    /**
     * Empty constructor for child class implementation.
     */
    private CClientSocket() {
        ch = null;
        lock = null;
        decoder = null;
    }
    
    /**
     * Construct a new NettyClient with the corresponding Channel that
     * will be used to write to as well as the send and recv seeds or IVs.
     * @param c the channel object associated with this client session.
     * @param uSendSeq the send seed or IV.
     * @param uRcvSeq the recv seed or IV.
     */
    public CClientSocket(Channel c, int uSendSeq, int uRcvSeq) {
        ch = c;
        uSeqSend = uSendSeq;
        uSeqRcv = uRcvSeq;
        decoder = new InPacket();
        lock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
    }
    
    /**
     * Gets the PacketDecoder object associated with this NettyClient.
     * @return a packet decoder.
     */
    public final InPacket GetDecoder() {
        return decoder;
    }
    
    /**
     * Gets the stored length for the next packet to be read. Used as
     * a decoding state variable to determine when it is ok to proceed with
     * decoding a packet.
     * @return stored length for next packet.
     */
    public final int GetStoredLength() {
        return nSavedLen;
    }
    
    /**
     * Sets the stored length for the next packet to be read.
     * @param val length of the next packet to be read.
     */
    public final void SetStoredLength(int val) {
        nSavedLen = val;
    }
    
    /**
     * Gets the current send seed or IV.
     * @return send IV.
     */
    public final int GetSeqSend() {
        return uSeqSend;
    }
    
    /**
     * Gets the current recv seed or IV.
     * @return recv IV.
     */
    public final int GetSeqRcv() {
        return uSeqRcv;
    }

    /**
     * Sets the send seed or IV for this session.
     * @param alpha the new send IV.
     */
    public final void SetSeqSend(int alpha) {
        uSeqSend = alpha;
    }

    /**
     * Sets the recv seed or IV for this session.
     * @param delta  the new recv IV.
     */
    public final void SetSeqRcv(int delta) {
        uSeqRcv = delta;
    }
    
    /**
     * Gets the current recv seed or IV.
     * @return recv IV.
     */
    public final int GetCryptoMode() {
        return nCryptoMode;
    }

    /**
     * Sets the send seed or IV for this session.
     * @param alpha the new send IV.
     */
    public final void SetCryptoMode(int alpha) {
        nCryptoMode = alpha;
    }
    
    /**
     * Writes a packet message to the channel. Gets encoded later in the
     * pipeline.
     * @param msg the packet message to be sent. 
     */
    public void write(Packet msg) {
        ch.writeAndFlush(msg);
    }
    
    /**
     * Closes this channel and session.
     */
    public void close() {
        ch.close();
    }
    
    /**
     * Gets the remote IP address for this session.
     * @return the remote IP address.
     */
    public String GetIP() {
        return ch.remoteAddress().toString().split(":")[0].substring(1);
    }
    
    /**
     * Locks this instance to prevent multiple encoding 
     * states to be possible at the same time. 
     * If allowed, the sendSeq would mutate to an unusable 
     * value and the session would be dropped as a result.
     */
    public final void Lock() {
        lock.lock();
    }
    
    /**
     * Releases the encoding state for this specific send IV.
     */
    public final void Unlock() {
        lock.unlock();
    }
}
