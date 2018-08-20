package handling.game;

import client.ClientSocket;
import client.SpecialStats.MapleHyperStats;
import client.SpecialStats.MapleSpecialStatUpdateType;
import constants.ServerConstants;
import net.InPacket;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn (LaiLaiNoob/LightPepsi is a faggot)
 */
public class UpdateHyperStatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        String statStr = iPacket.DecodeString();
        int requestType = iPacket.DecodeInt();
        int requestValue = iPacket.DecodeInt();

        MapleSpecialStatUpdateType stat = MapleSpecialStatUpdateType.getFromString(statStr);
        User chr = c.getPlayer();

        switch (stat) {
            case UpdateHonor: {
                c.SendPacket(WvsContext.updateSpecialStat(stat, requestType, requestValue, c.getPlayer().getHonourNextExp()));
                break;
            }
            case UpdateHyperSkills: {
                c.SendPacket(WvsContext.updateSpecialStat(stat, requestType, requestValue, chr.getRemainingHSp(requestValue)));
                break;
            }
            case RequiredHyperStatNext: { // Required hyper stat amount
                c.SendPacket(WvsContext.updateSpecialStat(stat, requestType, requestValue, MapleHyperStats.getRequiredHyperStatSP(requestType - 1)));
                break;
            }
            case GetHyperStatDistributionPerLevel: {
                c.SendPacket(WvsContext.updateSpecialStat(stat, requestType, requestValue, MapleHyperStats.getHyperStatDistribution(requestType)));
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
