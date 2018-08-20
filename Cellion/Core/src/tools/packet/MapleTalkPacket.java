/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.packet;

import net.OutPacket;

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

        private final short value;

        private MapleTalkOps(int mode) {
            this.value = (short) mode;
        }

        public short getValue() {
            return value;
        }
    }

    public static OutPacket OnLoginResult(boolean unavailable) {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnLoginResult.getValue());
        oPacket.EncodeBool(unavailable);

        /*
            If the server is available, the client sends a join guild char request:
            int (nGuildId)
            int (chatInstance // charid/info) //unsure
            
        
         */
        return oPacket;
    }

    public static OutPacket OnGuildChatMessage() {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnGuildChatMessage.getValue());
        oPacket.EncodeInt(0);//? seems to be charId/info
        oPacket.EncodeInt(0);//? getGuildMemberNameByCharacterID
        oPacket.EncodeInt(0);//v4
        oPacket.EncodeInt(0);//v5
        oPacket.EncodeLong(0);//ReadTime
        oPacket.EncodeString("");//RawMessage
        oPacket.EncodeByte(0);//LowDateTime

        return oPacket;
    }

    public static OutPacket OnFriendChatMessage() {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnFriendChatMessage.getValue());
        oPacket.EncodeInt(0);//?
        oPacket.EncodeInt(0);// SenderAccountId
        oPacket.EncodeInt(0);// senderCharacterId
        oPacket.EncodeLong(0);//ReadTime
        oPacket.EncodeString("");//RawMessage
        oPacket.EncodeByte(0);//LowDateTime

        return oPacket;
    }

    public static OutPacket OnAliveReq() {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnAliveReq.getValue());
        /*
            Client will respond with a message containing:
                Short(aliveResponseOpcode)
                bIsEncryptedByShanda = 0
         */
        return oPacket;
    }

    public static OutPacket OnEnterGuildChatRoomResult(boolean success) {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnEnterGuildChatRoomResult.getValue());
        oPacket.EncodeBool(success);

        /*
            client will respond with an outpacket including:
            int(accountid)
            int(charid)
            bIsEncryptedByShanda = 0
         */
        return oPacket;
    }

    public static OutPacket OnBlockGuildFriendChat(boolean block) {
        OutPacket oPacket = new OutPacket(MapleTalkOps.OnBlockGuildFriendChat.getValue());
        oPacket.EncodeBool(block);
        oPacket.EncodeLong(0);//time?

        /*
            client will respond with an outpacket including:
            int(accountid)
            int(charid)
            bIsEncryptedByShanda = 0
         */
        return oPacket;
    }

    //Lmao
    public static OutPacket ForceLogout() {
        OutPacket oPacket = new OutPacket(MapleTalkOps.ForceLogout.getValue());
        return oPacket;
    }

}
