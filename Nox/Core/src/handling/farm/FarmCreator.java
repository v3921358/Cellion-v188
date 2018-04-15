package handling.farm;

import client.MapleCharacterCreationUtil;
import client.MapleClient;
import server.farm.MapleFarm;
import net.InPacket;
import tools.packet.FarmPacket;
import net.ProcessPacket;

public final class FarmCreator implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String name = iPacket.DecodeString();
        if (!MapleCharacterCreationUtil.canCreateChar(name, c.isGm())) {
            return;
        }
        MapleFarm farm = MapleFarm.getDefault(35549721, c, name);
        farm.setLevel(1);
        c.setFarm(farm);
        c.SendPacket(FarmPacket.updateQuestInfo(1111, 1, "A1/Z/"));
        //c.write(FarmPacket.updateFarmInfo(c, true));
        //c.write(CField.createPacketFromHexString("68 03 19 72 1E 02 00 00 00 00 00 00 00 00 00 00 00 00 0A 00 65 73 6D 69 66 61 72 6D 7A 7A 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 FF FF FF FF 00"));
        c.SendPacket(FarmPacket.farmPacket4());
        c.SendPacket(FarmPacket.updateQuestInfo(30000, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30003, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30007, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30011, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30015, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30019, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30023, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30027, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30045, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30050, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30055, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30060, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30065, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30070, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30076, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30080, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30081, 0, "A1/"));
        c.SendPacket(FarmPacket.updateQuestInfo(30082, 0, "A1/"));
    }

}
