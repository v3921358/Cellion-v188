package tools.packet;

import client.MapleCharacterCreationUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import service.SendPacketOpcode;
import provider.data.HexTool;
import net.OutPacket;

import server.CashCategory;
import server.CashItem;
import server.CashItemFactory;
import server.CashShop;
import server.maps.objects.User;
import tools.Pair;
import static tools.packet.PacketHelper.getTime;

public class CSPacket {

    private static final byte Operation_Code = 0x66;//66
    private static final byte[] CashShopPacket_1 = HexTool.getByteArrayFromHexString("04 01 08 00 09 3D 00 10 30 3D 00 9C B8 0F 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 77 E2 F5 05 B1 35 4D 00 01 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 B8 6F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 40 BB EF 66 42 D3 01 90 65 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 3C 00 00 00 01 00 00 00 00 00 3C 00 02 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 10 30 3D 00 16 54 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 C9 F1 FA 02 35 9D 4E 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 6A 04 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 10 30 3D 00 15 54 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 9C F1 FA 02 58 95 4E 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 FD 06 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 10 30 3D 00 B4 7C 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 19 F1 F5 05 96 EB 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 08 9D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 40 BB EF 66 42 D3 01 78 50 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 00 00 1A F1 F5 05 6D 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 1B F1 F5 05 6E 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 1C F1 F5 05 6C 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 24 E6 F5 05 E8 17 50 00 01 00 00 00 70 17 00 00 00 00 00 00 00 00 00 00 06 00 00 00 5A 00 00 00 02 00 00 00 64 E5 F5 05 71 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 69 E5 F5 05 76 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 65 94 96 03 20 50 53 00 01 00 00 00 C4 09 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 10 30 3D 00 B4 7C 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 1D F1 F5 05 93 EB 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 A8 61 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 40 BB EF 66 42 D3 01 6C 2A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 00 00 1A F1 F5 05 6D 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 1E F1 F5 05 6E 81 1B 00 01 00 00 00 88 13 00 00 08 07 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 24 E6 F5 05 E8 17 50 00 01 00 00 00 70 17 00 00 00 00 00 00 00 00 00 00 06 00 00 00 5A 00 00 00 02 00 00 00 64 E5 F5 05 71 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 69 E5 F5 05 76 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 40 94 96 03 50 E3 4E 00 01 00 00 00 34 08 00 00 EC 04 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 17 E1 F5 05 C4 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 18 E1 F5 05 C5 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 10 30 3D 00 B4 7C 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 1F F1 F5 05 94 EB 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 A8 61 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 40 BB EF 66 42 D3 01 6C 2A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 00 00 1B F1 F5 05 6E 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 20 F1 F5 05 6F 81 1B 00 01 00 00 00 88 13 00 00 08 07 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 24 E6 F5 05 E8 17 50 00 01 00 00 00 70 17 00 00 00 00 00 00 00 00 00 00 06 00 00 00 5A 00 00 00 02 00 00 00 64 E5 F5 05 71 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 69 E5 F5 05 76 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 40 94 96 03 50 E3 4E 00 01 00 00 00 34 08 00 00 EC 04 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 17 E1 F5 05 C4 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 18 E1 F5 05 C5 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 10 30 3D 00 B4 7C 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 21 F1 F5 05 95 EB 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 A8 61 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 40 BB EF 66 42 D3 01 6C 2A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 00 00 1C F1 F5 05 6C 4E 4C 00 01 00 00 00 AC 26 00 00 00 19 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 22 F1 F5 05 70 81 1B 00 01 00 00 00 88 13 00 00 08 07 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 24 E6 F5 05 E8 17 50 00 01 00 00 00 70 17 00 00 00 00 00 00 00 00 00 00 06 00 00 00 5A 00 00 00 02 00 00 00 64 E5 F5 05 71 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 69 E5 F5 05 76 31 4F 00 01 00 00 00 E8 03 00 00 BC 02 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 40 94 96 03 50 E3 4E 00 01 00 00 00 34 08 00 00 EC 04 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 17 E1 F5 05 C4 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 18 E1 F5 05 C5 61 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 10 30 3D 00 7C 32 10 00 35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67 CB EC F5 05 21 E9 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 E8 35 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 C0 F2 9D 66 37 D3 01 00 00 D7 C6 E6 3C D3 01 68 10 00 00 00 00 00 00 01 00 00 00 00 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 00 00 CC EC F5 05 A2 E7 0F 00 01 00 00 00 38 18 00 00 08 07 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 CD EC F5 05 4E 35 10 00 01 00 00 00 68 10 00 00 B0 04 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00 CE EC F5 05 75 5F 10 00 01 00 00 00 48 0D 00 00 B0 04 00 00 00 00 00 00 01 00 00 00 00 00 00 00 02 00 00 00".replace("35 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 75 6D 62 72 61 63 6F 2F 31 32 38 30 33 2F 6D 6C 6C 6B 6B 6A 70 6F 69 69 2E 6A 70 67", "26 00 68 74 74 70 3a 2f 2f 72 65 78 69 6f 6e 2e 78 79 7a 2f 73 6f 75 72 63 65 73 2f 43 61 73 68 53 68 6f 70 2e 6a 70 67".toUpperCase()));
    private static final byte[] CashShopPacket_2 = HexTool.getByteArrayFromHexString("05 01 04 C0 C6 2D 00 D0 ED 2D 00 E4 DE 0F 00 00 00 09 E4 F5 05 79 3D 4D 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 B0 04 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 B0 04 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 78 00 78 00 01 00 01 00 00 00 78 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 E4 DE 0F 00 00 00 0B E4 F5 05 7A 3D 4D 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 98 08 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 98 08 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 78 00 78 00 01 00 01 00 00 00 78 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 74 E0 0F 00 00 00 7C FE FD 02 81 3A 54 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 3C 00 3C 00 01 00 00 00 00 00 3C 00 02 00 00 00 B2 01 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 10 E0 0F 00 00 00 B6 F0 F5 05 AF 63 54 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 24 13 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 24 13 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 3C 00 3C 00 01 00 01 00 00 00 3C 00 02 00 00 00 77 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
    private static final byte[] CashShopPacket_3 = HexTool.getByteArrayFromHexString("06 01 04 C0 C6 2D 00 E0 14 2E 00 58 06 10 00 00 00 CE A1 98 00 B0 20 57 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 C4 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 C4 09 00 00 00 00 00 00 01 00 00 00 0A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 59 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 BC 06 10 00 00 00 06 FE FD 02 AA C9 51 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 DC 05 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 DC 05 00 00 00 00 00 00 01 00 00 00 01 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 A9 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 B0 08 10 00 00 00 D4 E2 F5 05 90 40 4D 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 6C 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 6C 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 C4 90 0F 00 00 00 BF C3 C9 01 84 E7 4C 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 AC 26 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 26 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 1C 03 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
    private static final byte[] CashShopPacket_4 = HexTool.getByteArrayFromHexString("08 01 04 C0 C6 2D 00 F0 3B 2E 00 CC E2 0F 00 00 00 9F A4 98 00 00 3F 4D 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 3C 00 3C 00 01 00 01 00 00 00 01 00 02 00 00 00 C1 02 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 20 07 10 00 00 00 13 FE FD 02 F0 DA 52 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 F4 1A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 F4 1A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 1B 03 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 E8 07 10 00 00 00 1A E6 F5 05 14 47 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 E8 03 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E8 03 00 00 00 00 00 00 01 00 00 00 01 00 00 00 01 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 E8 07 10 00 00 00 1B E6 F5 05 40 1B 54 00 01 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 E8 03 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E8 03 00 00 00 00 00 00 01 00 00 00 01 00 00 00 01 00 01 00 01 00 01 00 00 00 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    public static OutPacket disableCS() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.PingCheckResult_ClientToGame.getValue());
        oPacket.Fill(0, 5);

        return oPacket;
    }

    public static OutPacket SetCashShopBannerPicture() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP.getValue());

        oPacket.Encode(CashShopPacket_1);
        return oPacket;
    }

    public static OutPacket CS_Top_Items() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP.getValue());

        oPacket.Encode(CashShopPacket_2);
        return oPacket;
    }

    public static OutPacket CS_Special_Item() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP.getValue());

        oPacket.Encode(CashShopPacket_3);
        return oPacket;
    }

    public static OutPacket CS_Featured_Item() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP.getValue());

        oPacket.Encode(CashShopPacket_4);
        return oPacket;
    }

    //v173:
    //0B 01 04 40 42 0F 00 B4 69 0F 00 38 B8 0F 00 00 00 87 2C 9A 00 AC AE 4F 00 01 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 48 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 48 0D 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 3C 00 3C 00 01 00 00 00 00 00 3C 00 02 00 00 00 D9 07 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 42 0F 00 B4 69 0F 00 38 B8 0F 00 00 00 88 2C 9A 00 AC AE 4F 00 01 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 D0 84 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 D0 84 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 3C 00 3C 00 01 00 00 00 00 00 3C 00 02 00 00 00 A8 02 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 42 0F 00 B4 69 0F 00 15 54 10 00 00 00 9C F1 FA 02 58 95 4E 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 01 07 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 42 0F 00 B4 69 0F 00 16 54 10 00 00 00 C9 F1 FA 02 35 9D 4E 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E4 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 00 01 00 01 00 00 00 00 00 01 00 02 00 00 00 6E 04 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
    public static OutPacket changeCategory(int category) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        oPacket.EncodeByte(11);
        CashItemFactory cif = CashItemFactory.getInstance();
        oPacket.EncodeByte(cif.getCategoryItems(category).size() > 0 ? 1 : 3);
        oPacket.EncodeByte(cif.getCategoryItems(category).size());
        for (CashItem i : cif.getCategoryItems(category)) {
            addCSItemInfo(oPacket, i);
        }

        return oPacket;
    }

    public static OutPacket addFavorite(boolean remove, int itemSn) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        oPacket.EncodeByte(remove ? 0x10 : 0x0E); //16 remove
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(itemSn);

        return oPacket;
    }

    public static OutPacket Like(int item) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        oPacket.EncodeByte(15);
        oPacket.EncodeByte(1);//todo add db row

        return oPacket;
    }

    public static OutPacket Favorite(User chr) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        oPacket.EncodeByte(18);
        oPacket.EncodeByte(chr.getWishlistSize() > 0 ? 1 : 3);
        oPacket.EncodeByte(chr.getWishlistSize());
        CashItemFactory cif = CashItemFactory.getInstance();
        oPacket.EncodeByte(chr.getWishlistSize() > 0 ? 1 : 3);
        oPacket.EncodeByte(chr.getWishlistSize());
        for (int i : chr.getWishlist()) {
            CashItem ci = cif.getAllItem(i);
//        for (CashItem i : cif.getMenuItems(301)) {//TODO create and load form favorites?
            addCSItemInfo(oPacket, ci);
        }
        return oPacket;
    }

    public static void addCSItemInfo(OutPacket oPacket, CashItem item) {
        oPacket.EncodeInt(item.getCategory());
        oPacket.EncodeInt(item.getSubCategory()); //4000000 + 10000 + page * 10000
        oPacket.EncodeInt(item.getParent()); //1000000 + 70000 + page * 100 + item on page
        oPacket.EncodeString(item.getImage()); //jpeg img url
        oPacket.EncodeInt(item.getSN());
        oPacket.EncodeInt(item.getItemId());
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(item.getFlag());//1 =event 2=new = 4=hot
        oPacket.EncodeInt(0);//1 = package?
        oPacket.EncodeInt(0);//changes - type?
        oPacket.EncodeInt(item.getPrice());
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeLong(PacketHelper.MAX_TIME);
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeLong(PacketHelper.MAX_TIME);
        oPacket.EncodeInt(item.getDiscountPrice()); //after discount
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(item.getQuantity());
        oPacket.EncodeInt(item.getExpire());
        oPacket.EncodeShort(1); //buy
        oPacket.EncodeShort(0); //gift (broken so nty)
        oPacket.EncodeShort(1); //cart
        oPacket.EncodeInt(0); //like? (as it increments the numlikes which is int, it's possible. (nexon be weird tho)
        oPacket.EncodeShort(0); //favorite (list unfinished so disabled rn)
        oPacket.EncodeInt(item.getGender());//gender female 1 male 0 nogender 2
        oPacket.EncodeInt(item.getLikes()); //likes
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        List pack = CashItemFactory.getInstance().getPackageItems(item.getSN());
        if (pack == null) {
            oPacket.EncodeInt(0);
        } else {
            oPacket.EncodeInt(pack.size());
            for (int i = 0; i < pack.size(); i++) {
                oPacket.EncodeInt(100000677);//item.getSN()); //should be pack item sn
                oPacket.EncodeInt(1072443);//((Integer) pack.get(i)).intValue());
                oPacket.EncodeInt(1);//1
                oPacket.EncodeInt(3600); //pack item usual price
                oPacket.EncodeInt(2880); //pack item discounted price
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(2);
            }
        }
    }

    public static OutPacket warpCS(MapleClient c) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.SetCashShop.getValue());
        int[][][] packages = {
            {{5533004}, {20800281, 20800258, 20800261, 20800262, 20800266, 20800268}},
            {{5533028}, {140800209, 140800205, 140800206, 140800207, 140800208}},
            {{5533012}, {21100152, 21100153, 21100154, 21100155}},
            {{5533013}, {100000424, 100000425, 100000426, 100000427, 100000428, 100000429, 100000430, 100000431, 100000432, 100000433, 100000434, 100000435, 100000436, 100000437, 100000438}},
            {{5533021}, {20800317, 20800318, 20800319, 20800320, 20800321}},
            {{5533030}, {140800210, 140800211, 140800212, 140800213, 140800214}},
            {{5533006}, {100000172, 100000173, 100000174, 100000175, 100000176}},
            {{5533014}, {20000462, 20000463, 20000464, 20000465, 20000466, 20000467, 20000468, 20000469}},
            {{5533022}, {21100177, 21100178, 21100179}},
            {{5533007}, {20000536, 20000537, 20000538, 20000539, 20000540}},
            {{5533039}, {140100626, 140100627, 140100628, 140100629, 140100630, 140100631, 140100632, 140100633}},
            {{5533023}, {20000625, 20000626, 20000627, 20000628, 20000629}},
            {{5533000}, {20000462, 20000463, 20000464, 20000465, 20000466, 20000467, 20000468, 20000469}},
            {{5533024}, {140100535, 140100536, 140100537, 140100538}},
            {{5533008}, {21100149, 21100150, 21100151}},
            {{5533001}, {20800259, 20800260, 20800263, 20800264, 20800265, 20800267}},
            {{5533009}, {20000543, 20000544, 20000545, 20000546, 20000547}},
            {{5533017}, {10002766, 10002767, 10002768, 10002769}},
            {{5533041}, {140100474, 140100634, 140100475, 140100476, 140100477, 140100478}},
            {{5533002}, {20800620, 20800621, 20800622, 20800623, 20800624}},
            {{5533026}, {140100547, 140100548, 140100549, 140100550, 140100551, 140100552}},
            {{5533034}, {140100445, 140100446, 140100355, 140100448, 140100449}},
            {{5533018}, {100000019, 100000004, 100000005, 100000006, 100000007, 100000008, 100000009, 100000010, 100000011, 100000012, 100000013, 100000014, 100000015, 100000016, 100000017, 100000018}},
            {{5533003}, {20001141, 20001142, 20001143, 20001144, 20001145, 20001146, 20001147}},
            {{5533011}, {100000578, 100000579, 100000580, 100000581, 100000582, 100000583}},
            {{5533035}, {100000988, 100000989, 100000990, 100000991, 100000992, 100000993, 100000994, 100000995, 100000996, 100000997}}
        };
        PacketHelper.addCharacterInfo(oPacket, c.getPlayer());
        oPacket.EncodeByte(1);//beta bool
        oPacket.EncodeInt(0); // if > 0, int * x
        oPacket.EncodeShort(0); // if > 0, int - new sub that is huge.

        oPacket.EncodeInt(packages.length);
        for (int[][] package1 : packages) {
            oPacket.EncodeInt(package1[0][0]); // pkg id
            oPacket.EncodeInt(package1[1].length);
            for (int j = 0; j < package1[1].length; j++) {
                oPacket.EncodeInt(package1[1][j]); // SN
            }
        }

        // Best: Implement if u feel like it
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 5; k++) {
                    oPacket.EncodeInt(i);//nCategory
                    oPacket.EncodeInt(j);//nGender
                    oPacket.EncodeInt(0);//nCommoditySN
                }
            }
        }
        /*
        for (Best[][] aaBest : ShopInfo.aBest) {
            for (Best[] aBest : aaBest) {
                for (Best pBest : aBest) {
                    oPacket.EncodeInt(pBest.nCategory);
                    oPacket.EncodeInt(pBest.nGender);
                    oPacket.EncodeInt(pBest.nCommoditySN);
                }
            }
        }
         */

        oPacket.EncodeShort(0); // aStock: Encode per size
        /*
        oPacket.EncodeShort(ShopInfo.aStock.size());
        for (Stock pStock : ShopInfo.aStock) {
            pStock.Encode(oPacket);
        }
         */

        oPacket.EncodeShort(0); // aLimitGoods: Encode per size
        /*
        oPacket.EncodeShort(ShopInfo.aLimitGoods.size());
        for (LimitGoods pLimit : ShopInfo.aLimitGoods) {
            pLimit.Encode(oPacket);
        }
         */
        oPacket.EncodeShort(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(c.getPlayer().getLevel());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        return oPacket;
    }

    public static OutPacket loadCategories() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CASH_SHOP.getValue());

        oPacket.EncodeByte(3);
        oPacket.EncodeByte(1);
        CashItemFactory cif = CashItemFactory.getInstance();
        oPacket.EncodeByte(cif.getCategories().size()); //categories size
        for (CashCategory cat : cif.getCategories()) {
            //id: base = 1000000; favorite = +1000000; category = +10000; subcategory = +100 subsubcategory = +1
            oPacket.EncodeInt(cat.getId());
            oPacket.EncodeString(cat.getName());
            oPacket.EncodeInt(cat.getParentDirectory());
            oPacket.EncodeInt(cat.getFlag());
            oPacket.EncodeInt(cat.getSold()); //1 = sold out
        }
        oPacket.EncodeInt(0);
        return oPacket;
    }

    public static OutPacket showNXMapleTokens(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_UPDATE.getValue());
        oPacket.EncodeInt(chr.getCSPoints(1)); // NX Credit
        oPacket.EncodeInt(chr.getCSPoints(2)); // MPoint
        oPacket.EncodeInt(chr.getCSPoints(3)); // Maple Rewards
        oPacket.EncodeInt(chr.getCSPoints(4)); // Nx Prepaid

        return oPacket;
    }

    public static OutPacket showMesos(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_MESO_UPDATE.getValue());
        oPacket.Fill(0, 2);
        oPacket.EncodeByte(4);
        oPacket.Fill(0, 5);
        oPacket.EncodeLong(chr.getMeso());
        return oPacket;
    }

    public static OutPacket LimitGoodsCountChanged() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code);
        oPacket.EncodeInt(0); // SN
        oPacket.EncodeInt(0); // Count
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket getCSInventory(MapleClient c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(2); // 5 = Failed + transfer //was3
        CashShop mci = c.getPlayer().getCashInventory();
        oPacket.EncodeByte(0);
        oPacket.EncodeShort(mci.getItemsSize());
        if (mci.getItemsSize() > 0) {
            int size = 0;
            for (Item itemz : mci.getInventory()) {
                addCashItemInfo(oPacket, itemz, c.getAccID(), 0);
                if (InventoryConstants.isPet(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    size++;
                }
            }
        }
        if (c.getPlayer().getCashInventory().getInventory().size() > 0) {
            oPacket.EncodeInt(0);
        }
        oPacket.EncodeShort(c.getPlayer().getStorage().getSlots());
        oPacket.EncodeShort(MapleCharacterCreationUtil.getCharacterSlots(c.getAccID(), c.getWorld()));
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(c.getPlayer().getStorage().getSlots());
        return oPacket;
    }

    public static OutPacket getCSGifts(MapleClient c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(5); // 7 = Failed + transfer//was8
        List<Pair<Item, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        oPacket.EncodeShort(mci.size());
        for (Pair<Item, String> mcz : mci) { // 70 Bytes, need to recheck.
            oPacket.EncodeLong(mcz.getLeft().getUniqueId());
            oPacket.EncodeInt(mcz.getLeft().getItemId());
            oPacket.EncodeString(mcz.getLeft().getGiftFrom(), 13);
            oPacket.EncodeString(mcz.getRight(), 73);
        }

        return oPacket;
    }

    public static OutPacket doCSMagic() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(3); // 7 = Failed + transfer//6
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket sendWishList(User chr, boolean update) {

        OutPacket oPacket = new OutPacket((short) 0x6E);
        oPacket.Encode(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
//        oPacket.encode(Operation_Code + (/*update ? 15 : */8)); // 9 = Failed + transfer, 16 = Failed.
//        int[] list = chr.getWishlist();
//        for (int i = 0; i < 10; i++) {
//            oPacket.EncodeInt(list[i] != -1 ? list[i] : 0);
//        }

        return oPacket;
    }

    public static OutPacket showBoughtCSItem(Item item, int sn, int accid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(13);
        addCashItemInfo(oPacket, item, accid, sn);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket showBoughtCSItem(int itemid, int sn, int uniqueid, int accid, int quantity, String giftFrom, long expire) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(13);
        addCashItemInfo(oPacket, uniqueid, accid, itemid, sn, quantity, giftFrom, expire);
        return oPacket;
    }

    public static OutPacket showBoughtCSItemFailed(final int mode, final int sn) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 18);
        oPacket.EncodeByte(mode); // 0/1/2 = transfer, Rest = code
        if (mode == 29 || mode == 30) { // Limit Goods update. this item is out of stock, and therefore not available for sale.
            oPacket.EncodeInt(sn);
        } else if (mode == 69) { // You cannot make any more purchases in %d.\r\nPlease try again in (%d + 1).
            oPacket.EncodeByte(1);	// Hour?	
        } else if (mode == 85) { // %s can only be purchased once a month.
            oPacket.EncodeInt(sn);
            oPacket.EncodeLong(System.currentTimeMillis());
        }

        return oPacket;
    }

    public static OutPacket showBoughtCSPackage(Map<Integer, Item> ccc, int accid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(0xA3); //use to be 7a
        oPacket.EncodeByte(ccc.size());
        int size = 0;
        for (Entry<Integer, Item> sn : ccc.entrySet()) {
            addCashItemInfo(oPacket, sn.getValue(), accid, sn.getKey().intValue());
            if (InventoryConstants.isPet(sn.getValue().getItemId()) || GameConstants.getInventoryType(sn.getValue().getItemId()) == MapleInventoryType.EQUIP) {
                size++;
            }
        }
        if (ccc.size() > 0) {
            oPacket.EncodeInt(size);
            for (Item itemz : ccc.values()) {
                if (InventoryConstants.isPet(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(oPacket, itemz);
                }
            }
        }
        oPacket.EncodeShort(0);

        return oPacket;
    }

    public static OutPacket sendGift(int price, int itemid, int quantity, String receiver, boolean packages) {
        // [ %s ] \r\nwas sent to %s. \r\n%d NX Prepaid \r\nwere spent in the process.

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + (packages ? 74 : 25)); // 74 = Similar structure to showBoughtCSItemFailed
        oPacket.EncodeString(receiver);
        oPacket.EncodeInt(itemid);
        oPacket.EncodeShort(quantity);
        if (packages) {
            oPacket.EncodeShort(0); //maplePoints
        }
        oPacket.EncodeInt(price);

        return oPacket;
    }

    public static OutPacket showCouponRedeemedItem(Map<Integer, Item> items, int mesos, int maplePoints, MapleClient c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 19);
        oPacket.EncodeByte(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(oPacket, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        oPacket.EncodeInt(maplePoints);
        oPacket.EncodeInt(0); // Normal items size
        //for (Pair<Integer, Integer> item : items2) {
        //    oPacket.EncodeInt(item.getRight()); // Count
        //    oPacket.EncodeInt(item.getLeft());  // Item ID
        //}
        oPacket.EncodeInt(mesos);

        return oPacket;
    }

    public static OutPacket showCouponGifted(Map<Integer, Item> items, String receiver, MapleClient c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 21); // 22 = Failed. [Mode - 0/2 = transfer, 15 = invalid 3 times]
        oPacket.EncodeString(receiver); // Split by ;
        oPacket.EncodeByte(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(oPacket, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        oPacket.EncodeInt(0); // (amount of receiver - 1)

        return oPacket;
    }

    public static OutPacket increasedInvSlots(int inv, int slots) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 26);
        oPacket.EncodeByte(inv);
        oPacket.EncodeShort(slots);

        return oPacket;
    }

    public static OutPacket increasedStorageSlots(int slots, boolean characterSlots) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + (characterSlots ? 30 : 28)); // 32 = Buy Character. O.O
        oPacket.EncodeShort(slots);

        return oPacket;
    }

    public static OutPacket increasedPendantSlots() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 34); // 35 = Failed
        oPacket.EncodeShort(0); // 0 = Add, 1 = Extend
        oPacket.EncodeShort(100); // Related to time->Low/High fileTime
        // The time limit for the %s slot \r\nhas been extended to %d-%d-%d %d:%d.

        return oPacket;
    }

    public static OutPacket confirmFromCSInventory(Item item, short pos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(38); // 37 = Failed//36 8A
        oPacket.EncodeByte(1); // bCashShopRequestSent
        oPacket.EncodeShort(pos);
        PacketHelper.addItemInfo(oPacket, item);
        oPacket.EncodeInt(0); // aSN.size
        oPacket.EncodeByte(0); // CashItemInfo

        return oPacket;
    }

    public static OutPacket confirmToCSInventory(Item item, int accId, int sn) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(40); // 40 = e38
        addCashItemInfo(oPacket, item, accId, sn, false);
        return oPacket;
    }

    public static OutPacket cashItemDelete(int uniqueid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 40); // 41 = Failed //42 is delete
        oPacket.EncodeLong(uniqueid); // or SN?

        return oPacket;
    }

    public static OutPacket rebateCashItem() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 67); // 41 = Failed
        oPacket.EncodeLong(0); // UniqueID
        oPacket.EncodeInt(0); // MaplePoints accumulated
        oPacket.EncodeInt(0); // For each: 8 bytes.

        return oPacket;
    }

    public static OutPacket sendBoughtRings(boolean couple, Item item, int sn, int accid, String receiver) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + (couple ? 69 : 79));
        addCashItemInfo(oPacket, item, accid, sn);
        oPacket.EncodeString(receiver);
        oPacket.EncodeInt(item.getItemId());
        oPacket.EncodeShort(1); // Count

        return oPacket;
    }

    public static OutPacket receiveFreeCSItem(Item item, int sn, int accid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 87); // 105 = Buy Name Change, 107 = Transfer world
        addCashItemInfo(oPacket, item, accid, sn);

        return oPacket;
    }

    public static OutPacket cashItemExpired(int uniqueid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 42);
        oPacket.EncodeLong(uniqueid);

        return oPacket;
    }

    public static OutPacket showBoughtCSQuestItem(long price, short quantity, byte position, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 75); // 76 = Failed.
        oPacket.EncodeInt(1); // size. below gets repeated for each.
        oPacket.EncodeInt(quantity);
        oPacket.EncodeInt(itemid);

        return oPacket;
    }

    public static OutPacket updatePurchaseRecord() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 94); // 95 = Failed. //94
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1); // boolean

        return oPacket;
    }

    public static OutPacket sendCashRefund(final int cash) {
        // Your refund has been processed. \r\n(%d NX Refund)

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 97);
        oPacket.EncodeInt(0); // Item Size.->For each 8 bytes.
        oPacket.EncodeInt(cash); // NX

        return oPacket;
    }

    public static OutPacket sendRandomBox(int uniqueid, Item item, short pos) { // have to revise this

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 99); // 100 = Failed
        oPacket.EncodeLong(uniqueid);
        oPacket.EncodeInt(1302000);
        PacketHelper.addItemInfo(oPacket, item);
        oPacket.EncodeShort(0);
        oPacket.EncodeInt(0); // Item Size.->For each 8 bytes.

        return oPacket;
    }

    public static OutPacket sendCashGachapon(final boolean cashItem, int idFirst, Item item, int accid) { // Xmas Surprise, Cash Shop Surprise

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 109); // 110 = Failed.		
        oPacket.EncodeLong(idFirst); //uniqueid of the xmas surprise itself
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(cashItem ? 1 : 0);
        if (cashItem) {
            addCashItemInfo(oPacket, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }
        oPacket.EncodeInt(item.getItemId());
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket sendTwinDragonEgg(boolean test1, boolean test2, int idFirst, Item item, int accid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 111); // 112 = Failed.		
        oPacket.EncodeByte(test1 ? 1 : 0);
        oPacket.EncodeByte(test2 ? 1 : 0);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(2);
        oPacket.EncodeInt(3);
        oPacket.EncodeInt(4);
        if (test1 && test2) {
            addCashItemInfo(oPacket, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }

        return oPacket;
    }

    public static OutPacket sendBoughtMaplePoints(final int maplePoints) {
        // You've received %d Maple Points.

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 113);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(maplePoints);

        return oPacket;
    }

    public static OutPacket receiveGachaStamps(final boolean invfull, final int amount) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.GACHAPON_STAMPS.getValue());
        oPacket.EncodeByte(invfull ? 0 : 1);
        if (!invfull) {
            oPacket.EncodeInt(amount);
        }

        return oPacket;
    }

    public static OutPacket freeCashItem(final int itemId) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.FREE_CASH_ITEM.getValue());
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    public static OutPacket showXmasSurprise(boolean full, int idFirst, Item item, int accid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_SURPRISE.getValue());
        oPacket.EncodeByte(full ? 212 : 213);
        if (!full) {
            oPacket.EncodeLong(idFirst); //uniqueid of the xmas surprise itself
            oPacket.EncodeInt(0);
            addCashItemInfo(oPacket, item, accid, 0); //info of the new item, but packet shows 0 for sn?
            oPacket.EncodeInt(item.getItemId());
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(1);
        }

        return oPacket;
    }

    public static OutPacket showOneADayInfo(boolean show, int sn) { // hmmph->Buy regular item causes invalid pointer

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ONE_A_DAY.getValue());
        oPacket.EncodeInt(100); //idk-related to main page
        oPacket.EncodeInt(100000); // idk-related to main page
        oPacket.EncodeInt(1); // size of items to buy, for each, repeat 3 ints below.
        oPacket.EncodeInt(20121231); // yyyy-mm-dd
        oPacket.EncodeInt(sn);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket playCashSong(int itemid, String name) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.StalkResult.getValue());
        oPacket.EncodeInt(itemid);
        oPacket.EncodeString(name);
        return oPacket;
    }

    public static OutPacket useAlienSocket(boolean start) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ALIEN_SOCKET_CREATOR.getValue());
        oPacket.EncodeByte(start ? 0 : 2);

        return oPacket;
    }

    public static OutPacket ViciousHammer(boolean start, int hammered) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetCashShopInitialItem.getValue());
        oPacket.EncodeByte(start ? 0x42 : 0x46);
        oPacket.EncodeInt(0);
        if (start) {
            oPacket.EncodeInt(hammered);
        }

        return oPacket;
    }

    public static OutPacket getLogoutGift() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TryMigrateCashShop.getValue());

        return oPacket;
    }

    public static OutPacket GoldenHammer(byte mode, int success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GOLDEN_HAMMER.getValue());

        oPacket.EncodeByte(mode);
        oPacket.EncodeInt(success);

        /*
         * success = 1:
         * mode:
         * 3 - 2 upgrade increases\r\nhave been used already.
         */
        return oPacket;
    }

    public static OutPacket changePetFlag(int uniqueId, boolean added, int flagAdded) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetBuff.getValue());

        oPacket.EncodeLong(uniqueId);
        oPacket.EncodeByte(added ? 1 : 0);
        oPacket.EncodeShort(flagAdded);

        return oPacket;
    }

    public static OutPacket changePetName(User chr, String newname, int slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetNameChanged.getValue());

        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(0);
        oPacket.EncodeString(newname);
        oPacket.EncodeByte(slot);

        return oPacket;
    }

    public static OutPacket OnMemoResult(final byte act, final byte mode) {

        //04 // The note has successfully been sent 
        //05 00 // The other character is online now. Please use the whisper function. 
        //05 01 // Please check the name of the receiving character. 
        //05 02 // The receiver's inbox is full. Please try again. 
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MemoResult.getValue());
        oPacket.EncodeByte(act);
        if (act == 5) {
            oPacket.EncodeByte(mode);
        }

        return oPacket;
    }

    public static OutPacket showNotes(final ResultSet notes, final int count) throws SQLException {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MemoResult.getValue());
        oPacket.EncodeByte(3);
        oPacket.EncodeByte(count);
        for (int i = 0; i < count; i++) {
            oPacket.EncodeInt(notes.getInt("id"));
            oPacket.EncodeString(notes.getString("from"));
            oPacket.EncodeString(notes.getString("message"));
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            oPacket.EncodeByte(notes.getInt("gift"));
            notes.next();
        }

        return oPacket;
    }

    /**
     * This packet spawns a chalkboard above the players head
     *
     * @param int charid - character Id
     * @param String msg = the message
     *
     * @return oPacket
     */
    public static OutPacket useChalkboard(int charid, String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserADBoard.getValue());
        oPacket.EncodeInt(charid);
        oPacket.EncodeBool((msg == null || msg.length() <= 0));
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket OnMapTransferResult(User chr, byte vip, boolean delete) {

        // 31 00 05/08 00 // You cannot go to that place.
        // 31 00 06 00 // (null) is currently difficult to locate, so the teleport will not take place.
        // 31 00 09 00 // It's the map you're currently on.
        // 31 00 0A 00 // This map is not available to enter for the list.
        // 31 00 0B 00 // Users below level 7 are not allowed to go out from Maple Island.
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MapTransferResult.getValue());
        oPacket.EncodeByte(delete ? 2 : 3);
        oPacket.EncodeByte(vip);
        if (vip == 1) {
            int[] map = chr.getRegRocks();
            for (int i = 0; i < 5; i++) {
                oPacket.EncodeInt(map[i]);
            }
        } else if (vip == 2) {
            int[] map = chr.getRocks();
            for (int i = 0; i < 10; i++) {
                oPacket.EncodeInt(map[i]);
            }
        } else if (vip == 3 || vip == 5) {
            int[] map = chr.getHyperRocks();
            for (int i = 0; i < 13; i++) {
                oPacket.EncodeInt(map[i]);
            }
        }

        return oPacket;
    }

    public static void addCashItemInfo(OutPacket oPacket, Item item, int accId, int sn) {
        addCashItemInfo(oPacket, item, accId, sn, true);
    }

    public static void addCashItemInfo(OutPacket oPacket, Item item, int accId, int sn, boolean isFirst) {
        addCashItemInfo(oPacket, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst); //owner for the lulz
    }

    public static void addCashItemInfo(OutPacket oPacket, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire) {
        addCashItemInfo(oPacket, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
    }

    public static void addCashItemInfo(OutPacket oPacket, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst) {
        oPacket.EncodeLong(uniqueid > 0 ? uniqueid : 0);
        oPacket.EncodeInt(accId);
        oPacket.EncodeInt(0); // dwCharacterID (Neckson just sends 0)
        oPacket.EncodeInt(itemid);
        oPacket.EncodeInt(sn);
        oPacket.EncodeShort(quantity);//quantity
        oPacket.EncodeString(sender, 13); //owner for the lulzlzlzl
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeInt(0); // nPaybackRate
        oPacket.EncodeLong(0); // dDiscountRate (idk if u handle doubles well)
        oPacket.EncodeInt(0); // dwOrderNo
        oPacket.EncodeInt(0); // dwProductNo
        oPacket.EncodeBool(false); // bRefundable
        oPacket.EncodeByte(0); // nSourceFlag
        oPacket.EncodeByte(0); // nStoreBank

        // CashItemOption
        oPacket.EncodeLong(uniqueid);
        oPacket.EncodeLong(getTime(-2)); //ftExpireDate
        oPacket.EncodeInt(0); //nGrade
        for (int i = 0; i < 3; i++) { // aOption
            oPacket.EncodeInt(0);
        }
    }

    public static OutPacket sendCSFail(int err) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CS_OPERATION.getValue());
        oPacket.EncodeByte(Operation_Code + 22);
        oPacket.EncodeByte(err);
        // 1: Request timed out.\r\nPlease try again.
        // 3: You don't have enough cash.
        // 4: You can't buy someone a cash item gift if you're under 14.
        // 5: You have exceeded the allotted limit of price\r\nfor gifts.
        // 10: Please check and see if you have exceeded\r\nthe number of cash items you can have.
        // 11: Please check and see\r\nif the name of the character is wrong,\r\nor if the item has gender restrictions.
        // 44/69: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
        // 22: Due to gender restrictions, the coupon \r\nis unavailable for use.
        // 17: This coupon was already used.
        // 16: This coupon has expired.
        // 18: This coupon can only be used at\r\nNexon-affiliated Internet Cafe's.\r\nPlease use the Nexon-affiliated Internet Cafe's.
        // 19: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been used.
        // 20: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been expired.
        // 14: Please check and see if \r\nthe coupon number is right.
        // 23: This coupon is only for regular items, and \r\nit's unavailable to give away as a gift.
        // 24: This coupon is only for MapleStory, and\r\nit cannot be gifted to others.
        // 25: Please check if your inventory is full or not.
        // 26: This item is only available for purchase by a user at the premium service internet cafe.
        // 27: You are sending a gift to an invalid recipient.\r\nPlease check the character name and gender.
        // 28: Please check the name of the receiver.
        // 29: Items are not available for purchase\r\n at this hour.
        // 30: The item is out of stock, and therefore\r\nnot available for sale.
        // 31: You have exceeded the spending limit of NX.
        // 32: You do not have enough mesos.
        // 33: The Cash Shop is unavailable\r\nduring the beta-test phase.\r\nWe apologize for your inconvenience.
        // 34: Check your PIC password and\r\nplease try again.
        // 37: Please verify your 2nd password and\r\ntry again.
        // 21: This is the NX coupon number.\r\nRegister your coupon at www.nexon.net.
        // 38: This coupon is only available to the users buying cash item for the first time.
        // 39: You have already applied for this.
        // 47: You have exceeded the maximum number\r\nof usage per account\for this account.\r\nPlease check the coupon for detail.
        // 49: The coupon system will be available soon.
        // 50: This item can only be used 15 days \r\nafter the account's registration.
        // 51: You do not have enough Gift Tokens \r\nin your account. Please charge your account \r\nwith Nexon Game Cards to receive \r\nGift Tokens to gift this item.
        // 52: Due to technical difficulties,\r\nthis item cannot be sent at this time.\r\nPlease try again.
        // 53: You may not gift items for \r\nit has been less than two weeks \r\nsince you first charged your account.
        // 54: Users with history of illegal activities\r\n may not gift items to others. Please make sure \r\nyour account is neither previously blocked, \r\nnor illegally charged with NX.
        // 55: Due to limitations, \r\nyou may not gift this item as this time. \r\nPlease try again later.
        // 56: You have exceeded the amount of time \r\nyou can gift items to other characters.
        // 57: This item cannot be gifted \r\ndue to technical difficulties. \r\nPlease try again later.
        // 58: You cannot transfer \r\na character under level 20.
        // 59: You cannot transfer a character \r\nto the same world it is currently in.
        // 60: You cannot transfer a character \r\ninto the new server world.
        // 61: You may not transfer out of this \r\nworld at this time.
        // 62: You cannot transfer a character into \r\na world that has no empty character slots.
        // 63: The event has either ended or\r\nthis item is not available for free testing.
        // 6: You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
        // 7: That character could not be found in this world.\r\nGifts can only be sent to character\r\nin the same world.
        // 8: This item has a gender restriction.\r\nPlease confirm the gender of the recipient.
        // 9: The gift cannot be sent because\r\nthe recipient's Inventory is full.
        // 64: This item cannot be purchased \r\nwith MaplePoints.
        // 65: Sorry for inconvinence. \r\nplease try again.
        // 67: This item cannot be\r\npurchased by anyone under 7.
        // 68: This item cannot be\r\nreceived by anyone under 7.
        // 66: You can no longer purchase or gift that Item of the Day.
        // 70: NX use is restricted.\r\nPlease change your settings in the NX Security Settings menu\r\nin the Nexon Portal My Info section.
        // 74: This item is not currently for sale.
        // 81: You have too many Cash Items.\r\nPlease clear 1 Cash slot and try again.
        // 90: You have exceeded the purchase limit for this item.\r\nYou cannot buy anymore.
        // 88: This item is non-refundable.
        // 87: Items cannot be refunded if\r\n7 days have passed from purchase.
        // 89: Refund cannot be processed, as some of the items in this\r\npackage have been used.
        // 86: Refund is currently unavailable.
        // 91: You cannot name change.\r\na character under level 10.
        // default: Due to an unknown error,\r\nthe request for Cash Shop has failed.

        return oPacket;
    }

    public static OutPacket enableCSUse() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PingCheckResult_ClientToGame.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket getBoosterPack(int f1, int f2, int f3) { //item IDs

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BOOSTER_PACK.getValue());
        oPacket.EncodeByte(0xD7);
        oPacket.EncodeInt(f1);
        oPacket.EncodeInt(f2);
        oPacket.EncodeInt(f3);

        return oPacket;
    }

    public static OutPacket getBoosterPackClick() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BOOSTER_PACK.getValue());
        oPacket.EncodeByte(0xD5);

        return oPacket;
    }

    public static OutPacket getBoosterPackReveal() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BOOSTER_PACK.getValue());
        oPacket.EncodeByte(0xD6);

        return oPacket;
    }

    public static OutPacket sendMesobagFailed(final boolean random) {

        OutPacket oPacket = new OutPacket(random ? SendPacketOpcode.MesoGiveFailed.getValue() : SendPacketOpcode.MesoGiveFailed.getValue());

        return oPacket;
    }

    public static OutPacket sendMesobagSuccess(int mesos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MesoGiveSucceeded.getValue());
        oPacket.EncodeInt(mesos);
        return oPacket;
    }

    public static OutPacket sendRandomMesobagSuccess(int size, int mesos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RandomMesoGiveSucceeded.getValue());
        oPacket.EncodeByte(size); // 1 = small, 2 = adequete, 3 = large, 4 = huge
        oPacket.EncodeInt(mesos);

        return oPacket;
    }
}
