package server.maps;

import java.io.IOException;
import provider.wz.nox.NoxBinaryReader;

/**
 *
 * @author Lloyd Korn
 */
public class MapleCoconut {

    int countFalling, countBombing, countStopped, countHit, timeExpand, timeMessage, timeFinish;
    String effectWin, effectLose, soundWin, soundLose, eventName, eventObjectName;

    public MapleCoconut(NoxBinaryReader data) throws IOException {
        this.countFalling = data.readInt();
        this.countBombing = data.readInt();
        this.countStopped = data.readInt();
        this.countHit = data.readInt();
        this.timeExpand = data.readInt();
        this.timeMessage = data.readInt();
        this.timeFinish = data.readInt();
        this.effectWin = data.readAsciiString();
        this.effectLose = data.readAsciiString();
        this.soundWin = data.readAsciiString();
        this.soundLose = data.readAsciiString();
        this.eventName = data.readAsciiString();
        this.eventObjectName = data.readAsciiString();
    }
}
