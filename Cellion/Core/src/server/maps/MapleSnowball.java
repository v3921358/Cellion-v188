package server.maps;

import java.io.IOException;
import provider.wz.nox.NoxBinaryReader;

/**
 *
 * @author Lloyd Korn
 */
public class MapleSnowball {

    int xMin, xMax, damageSnowBall, damageSnowMan0, damageSnowMan1, snowManHP,
            snowManWait, recoveryAmount, speed, Section1, Section2, Section3;

    public MapleSnowball(NoxBinaryReader data) throws IOException {
        this.xMin = data.readInt();
        this.xMax = data.readInt();
        this.damageSnowBall = data.readInt();
        this.damageSnowMan0 = data.readInt();
        this.damageSnowMan1 = data.readInt();
        this.snowManHP = data.readInt();
        this.snowManWait = data.readInt();
        this.recoveryAmount = data.readInt();
        this.speed = data.readInt();
        this.Section1 = data.readInt();
        this.Section2 = data.readInt();
        this.Section3 = data.readInt();
    }
}
