package handling.cashshop;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import database.DatabaseConnection;
import provider.data.HexTool;
import net.OutPacket;
import tools.packet.PacketHelper;

public class CSMenuItem {

    private static final List<CSMenuItem> pictureItems = new LinkedList<>();

    public static void loadFromDb() {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (ResultSet rs = con.prepareStatement("SELECT * FROM cs_picture").executeQuery()) {
                while (rs.next()) {
                    pictureItems.add(new CSMenuItem(
                            rs.getInt("category"),
                            rs.getInt("subcategory"),
                            rs.getInt("parent"),
                            rs.getString("image"),
                            rs.getInt("sn"),
                            rs.getInt("itemid"),
                            rs.getByte("flag"),
                            rs.getInt("originalPrice"),
                            rs.getInt("salePrice"),
                            rs.getInt("quantity"),
                            rs.getInt("duration"),
                            rs.getInt("likes")));
                }
            }
        } catch (SQLException ex) {
        }

    }
    private int c, sc, p, i, sn, id, op, sp, qty, dur, likes;
    private final String img;
    private final byte flag;

    private CSMenuItem(int c, int sc, int p, String img, int sn, int id, byte flag, int op, int sp, int qty, int dur, int likes) {
        this.c = c;
        this.sc = sc;
        this.p = p;
        this.img = img;
        this.sn = sn;
        this.id = id;
        this.flag = flag;
        this.op = op;
        this.sp = sp;
        this.qty = qty;
        this.dur = dur;
        this.likes = likes;
    }

    public static void writeData(CSMenuItem csmi, OutPacket oPacket) {
        oPacket.EncodeInteger(csmi.c);
        oPacket.EncodeInteger(csmi.sc);
        oPacket.EncodeInteger(csmi.p);
        oPacket.EncodeString(csmi.img); // TODO add check if cat != 4 write empty string
        oPacket.EncodeInteger(csmi.sn);
        oPacket.EncodeInteger(csmi.id);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(csmi.flag);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0); // this one changes
        oPacket.EncodeInteger(csmi.op);
        oPacket.Encode(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        oPacket.EncodeLong(PacketHelper.MAX_TIME);
        oPacket.Encode(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        oPacket.EncodeLong(PacketHelper.MAX_TIME);
        oPacket.EncodeInteger(csmi.sp);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(csmi.qty);
        oPacket.EncodeInteger(csmi.dur);
        oPacket.Encode(HexTool.getByteArrayFromHexString("01 00 01 00 01 00 00 00 01 00 02 00 00 00")); // flags maybe
        oPacket.EncodeInteger(csmi.likes);
        oPacket.Fill(0, 20);
    }
}
