/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server;

import database.Database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import server.api.ApiConstants;
import server.api.ApiFactory;
import server.maps.objects.User;
import service.ChannelServer;

/**
 *
 * @author Twdtwd
 */
public class QueueWorker extends Thread {

    public QueueWorker() {
        super();

        this.setName("QueueWorkerThread-0");
    }

    @Override
    public void run() {

        while (true) {
            try {
                getOnlineUsersForLumiere();
                Request request = new Request.Builder()
                        .url(ApiConstants.MESSAGE_URL + "list")
                        .header("Authorization", "Bearer " + ApiFactory.getFactory().getServerToken())
                        .build();

                Response response = ApiFactory.getFactory().getHttpClient().newCall(request).execute();

                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    QueueItem[] items = ApiFactory.getFactory().getGson().fromJson(resp, QueueItem[].class);

                    for (QueueItem item : items) {
                        if (processCommand(item)) {
                            request = new Request.Builder()
                                    .url(ApiConstants.MESSAGE_URL + item.message_id + ApiConstants.MESSAGE_PROCESS_URL)
                                    .header("Authorization", "Bearer " + ApiFactory.getFactory().getServerToken())
                                    .post(new FormBody.Builder().build()) // Just because the API requires a post type.
                                    .build();

                            Response temp = ApiFactory.getFactory().getHttpClient().newCall(request).execute();
                            temp.body().close();
                        } else {// if return false; delay it. 
                            request = new Request.Builder()
                                    .url(ApiConstants.MESSAGE_URL + item.message_id + ApiConstants.MESSAGE_DELAY_URL)
                                    .header("Authorization", "Bearer " + ApiFactory.getFactory().getServerToken())
                                    .post(new FormBody.Builder().build()) // Just because the API requires a post type.
                                    .build();

                            Response temp = ApiFactory.getFactory().getHttpClient().newCall(request).execute();
                            temp.body().close();
                        }
                    }
                }
                response.body().close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                return; // Just exit the thread if interupted.
            }
        }
    }

    private boolean processCommand(QueueItem item) throws SQLException {
        User chr = null;

        QueueCommand command = QueueCommand.valueOf(item.action);

        try (Connection con = Database.GetConnection()) {

            switch (command) {
                case GIVE_NX:
                    String payloadUnescape = item.payload.replaceAll("\\\\", "");
                    NXPayload payload = ApiFactory.getFactory().getGson().fromJson(payloadUnescape, NXPayload.class);
                    int accountId = payload.lumiere_id;
                    int amount = payload.nx_amount;

                    int[] charIdList = new int[16];
                    try (PreparedStatement ps = con.prepareStatement("SELECT id FROM `accounts` WHERE `authID` = ?")) {
                        ps.setInt(1, accountId);
                        ResultSet rs = ps.executeQuery();

                        if (!rs.next()) // The account doesn't exist in the game database yet.
                        {
                            return false;
                        }
                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    try (PreparedStatement ps = con.prepareStatement("SELECT `characters`.`id` FROM `characters` "
                            + "LEFT JOIN `accounts` ON `accounts`.`id` = `characters`.`accountid` "
                            + "WHERE `accounts`.`authID` = ?")) {
                        ps.setInt(1, accountId);
                        ResultSet rs = ps.executeQuery();

                        int i = 0;
                        while (rs.next()) {
                            charIdList[i] = rs.getInt("id");
                            i++;
                            if (i > 15) {
                                break;
                            }
                        }

                        rs.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    boolean online = false;

                    worldSearch:
                    { // TODO: Clean this up a bit..
                        for (ChannelServer w : ChannelServer.getAllInstances()) {
                            for (int id : charIdList) {
                                if (id == 0) {
                                    break;
                                }

                                chr = w.getPlayerStorage().getCharacterById(id);

                                if (chr != null) {
                                    break worldSearch;
                                }
                            }
                        }
                    }
                    if (chr != null) {
                        // Ok, so the player is online. Just give them the points and send them the message.
                        chr.modifyCSPoints(1, amount);
                        chr.dropMessage(5, "Thank you for voting for Cellion. " + amount + " NX has been dispatched to your account.");
                    } else {
                        // So the player is offline. Lets just update the database and give them the amount.
                        String type = "nxCredit";

                        try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `" + type + "` = `" + type + "` + ? WHERE `authID` = ?;")) {
                            ps.setInt(1, amount);
                            ps.setInt(2, accountId);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case SEND_NOTICE: // Not implemented.
                    break;
                case PIC_RESET:
                    String picPayloadStr = item.payload.replaceAll("\\\\", "");
                    Payload picPayload = ApiFactory.getFactory().getGson().fromJson(picPayloadStr, Payload.class);
                    try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `pic` = NULL WHERE `authID` = ?")) {
                        ps.setInt(1, picPayload.lumiere_id);
                        ps.execute();
                    }
                    break;
                case SET_EXP_MULTIPLIER:
                    //not implemented rn
                    break;
                case RELOAD_CS:
                    CashItemFactory.reload();
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private boolean getOnlineUsersForLumiere() throws IOException, SQLException {
        int playersOnline = 0;

        for (ChannelServer w : ChannelServer.getAllInstances()) {
            playersOnline += w.getPlayerStorage().getConnectedClients();
        }

        RequestBody body = new FormBody.Builder()
                .add("user_count", String.valueOf(playersOnline))
                .add("server_time", Instant.now().toString())
                .build();
        Request logOnlineUsers = new Request.Builder()
                .url(String.format(ApiConstants.HOOK_USER_ONLINE_URL, ApiConstants.PRODUCT_ID))
                .header("Authorization", "Bearer " + ApiFactory.getFactory().getServerToken())
                .post(body) // Just because the API requires a post type.
                .build();
        Response logOnlineUsersResp = ApiFactory.getFactory().getHttpClient().newCall(logOnlineUsers).execute();
        logOnlineUsersResp.body().close();

        return true;

    }

    public enum QueueCommand {

        GIVE_NX, PIC_RESET, DISCONNECT_USER,
        SEND_NOTICE, SET_EXP_MULTIPLIER, RELOAD_CS
    }

    private class QueueItem {

        public int message_id;
        public String action;
        public String payload;
        public String message;
        public String created_at;

    }

    private class NXPayload {

        public int lumiere_id;
        public String nx_type;
        public int nx_amount;
    }

    private class EventPayload {

        public int multiplier;
        public String notice;
    }

    private class NoticePayload { // Not implemented.

        public String notice_type;
        public String recipient; // Haven't decided whether or not to use character name, account name, or lumiere_id.
        public String notice_message;
    }

    private class Payload {

        public int lumiere_id;
    }

}
