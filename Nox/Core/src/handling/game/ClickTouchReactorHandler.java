package handling.game;

import client.MapleClient;
import constants.GameConstants;
import scripting.provider.ReactorScriptManager;
import server.MapleInventoryManipulator;
import server.maps.objects.MapleReactor;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ClickTouchReactorHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final int oid = iPacket.DecodeInt();
        final boolean touched = iPacket.GetRemainder() == 0 || iPacket.DecodeByte() > 0; //the byte is probably the state to set it to
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        if (c.getPlayer().isIntern()) {
            c.getPlayer().dropMessage(5, "[Debug] Reactor ID : " + oid);
        }

        if (!touched || reactor == null || !reactor.isAlive() || reactor.getTouch() == 0) {
            return;
        }
        if (reactor.getTouch() == 2) {
            ReactorScriptManager.getInstance().act(c, reactor); //not sure how touched boolean comes into play
        } else if (reactor.getTouch() == 1 && !reactor.isTimerActive()) {
            if (reactor.getReactorType() == 100) {
                final int itemid = reactor.getReactItem().getLeft();
                if (c.getPlayer().haveItem(itemid, reactor.getReactItem().getRight())) {
                    if (reactor.getArea().contains(c.getPlayer().getTruePosition())) {
                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, reactor.getReactItem().getRight(), true, false);
                        reactor.hitReactor(c);
                    } else {
                        c.getPlayer().dropMessage(5, "You are too far away.");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You don't have the item required.");
                }
            } else {
                //just hit it
                reactor.hitReactor(c);
            }
        }
    }

}
