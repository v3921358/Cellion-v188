/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.packet;

import net.OutPacket;
import net.Packet;

/**
 *
 * @author novak
 */
public class MapleTalkPacket {

    public enum MapleTalkOps {

        OnLoginResult(0x01),
        OnGuildChatMessage(0x12),
        OnFriendChatMessage(0x13),
        OnAliveReq(0x0D),
        OnEnterGuildChatRoomResult(0x0F),
        OnBlockGuildFriendChat(0x1C),
        ForceLogout(0x0B);

        private final int value;

        private MapleTalkOps(int mode) {
            this.value = mode;
        }

        public int getValue() {
            return value;
        }
    }

    public static Packet OnLoginResult(boolean unavailable) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnLoginResult.getValue());
        oPacket.Encode(unavailable);

        /*
            If the server is available, the client sends a join guild char request:
            int (nGuildId)
            int (chatInstance // charid/info) //unsure
            
        
         */
        return oPacket.ToPacket();
    }

    public static Packet OnGuildChatMessage() {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnGuildChatMessage.getValue());
        oPacket.EncodeInteger(0);//? seems to be charId/info
        oPacket.EncodeInteger(0);//? getGuildMemberNameByCharacterID
        oPacket.EncodeInteger(0);//v4
        oPacket.EncodeInteger(0);//v5
        oPacket.EncodeLong(0);//ReadTime
        oPacket.EncodeString("");//RawMessage
        oPacket.Encode(0);//LowDateTime

        return oPacket.ToPacket();
    }

    public static Packet OnFriendChatMessage() {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnFriendChatMessage.getValue());
        oPacket.EncodeInteger(0);//?
        oPacket.EncodeInteger(0);// SenderAccountId
        oPacket.EncodeInteger(0);// senderCharacterId
        oPacket.EncodeLong(0);//ReadTime
        oPacket.EncodeString("");//RawMessage
        oPacket.Encode(0);//LowDateTime

        return oPacket.ToPacket();
    }

    public static Packet OnAliveReq() {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnAliveReq.getValue());
        /*
            Client will respond with a message containing:
                Short(aliveResponseOpcode)
                bIsEncryptedByShanda = 0
         */
        return oPacket.ToPacket();
    }

    public static Packet OnEnterGuildChatRoomResult(boolean success) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnEnterGuildChatRoomResult.getValue());
        oPacket.Encode(success);

        /*
            client will respond with an outpacket including:
            int(accountid)
            int(charid)
            bIsEncryptedByShanda = 0
         */
        return oPacket.ToPacket();
    }

    public static Packet OnBlockGuildFriendChat(boolean block) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(MapleTalkOps.OnBlockGuildFriendChat.getValue());
        oPacket.Encode(block);
        oPacket.EncodeLong(0);//time?

        /*
            client will respond with an outpacket including:
            int(accountid)
            int(charid)
            bIsEncryptedByShanda = 0
         */
        return oPacket.ToPacket();
    }

    //Lmao
    public static Packet ForceLogout() {
        final OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(MapleTalkOps.ForceLogout.getValue());
        return oPacket.ToPacket();
    }

}
