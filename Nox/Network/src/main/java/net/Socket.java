/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Kaz Voeten
 */
public class Socket {

    public static final AttributeKey<Socket> SESSION_KEY = AttributeKey.valueOf("Session");
    public int uSeqSend, uSeqRcv;
    public int nCryptoMode = 1;
    public int nDecodeLen = -1;
    public boolean bEncryptData = true;
    private final ReentrantLock Lock;
    protected final Channel channel;

    public Socket(Channel channel, int uSeqSend, int uSeqRcv) {
        this.channel = channel;
        this.uSeqSend = uSeqSend;
        this.uSeqRcv = uSeqRcv;
        this.Lock = new ReentrantLock(true);
    }

    public void SendPacket(OutPacket msg) {
        channel.writeAndFlush(msg);
    }

    public void Close() {
        channel.close();
    }

    public String GetIP() {
        return channel.remoteAddress().toString().split(":")[0].substring(1);
    }
    
    public int GetPort() {
        return Integer.parseInt(channel.localAddress().toString().split(":")[1]);
    }

    public void Lock() {
        this.Lock.lock();
    }

    public void Unlock() {
        this.Lock.unlock();
    }
}
