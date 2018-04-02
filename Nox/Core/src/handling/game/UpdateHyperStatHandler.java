package handling.game;

import client.MapleClient;
import client.MapleSpecialStats.MapleHyperStats;
import client.MapleSpecialStats.MapleSpecialStatUpdateType;
import constants.ServerConstants;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.LogHelper;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn (LaiLaiNoob/LightPepsi is a faggot)
 */
public class UpdateHyperStatHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String statStr = iPacket.DecodeString();
        int requestType = iPacket.DecodeInteger();
        int requestValue = iPacket.DecodeInteger();

        MapleSpecialStatUpdateType stat = MapleSpecialStatUpdateType.getFromString(statStr);
        MapleCharacter chr = c.getPlayer();

        switch (stat) {
            case UpdateHonor: {
                c.write(CWvsContext.updateSpecialStat(stat, requestType, requestValue, c.getPlayer().getHonourNextExp()));
                break;
            }
            case UpdateHyperSkills: {
                c.write(CWvsContext.updateSpecialStat(stat, requestType, requestValue, chr.getRemainingHSp(requestValue)));
                break;
            }
            case RequiredHyperStatNext: { // Required hyper stat amount
                c.write(CWvsContext.updateSpecialStat(stat, requestType, requestValue, MapleHyperStats.getRequiredHyperStatSP(requestType - 1)));
                break;
            }
            case GetHyperStatDistributionPerLevel: {
                c.write(CWvsContext.updateSpecialStat(stat, requestType, requestValue, MapleHyperStats.getHyperStatDistribution(requestType)));
                break;
            }
            default:
                if (ServerConstants.DEVELOPER_DEBUG_MODE) { // Don't log when not in debug mode, someone may spam this and cause out of disk space
                    LogHelper.UNCODED.get().info(
                            String.format("[UpdateHyperStatHandler] %s [ChrID: %d; AccId %d] has requested a special update hyper stat of type: %s, mode: %d", chr.getName(), chr.getId(), c.getAccID(), statStr, requestValue)
                    );
                }
                break;
        }
    }
}
