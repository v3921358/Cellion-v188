/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.Client;
import handling.login.LoginPasswordHandler;
import server.api.FeatureList.FeatureAPI;
import server.api.data.BestCashShopItems;
import server.api.data.Ping;
import server.api.data.Token;
import server.api.data.UserAccess;
import server.api.data.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CLogin;

/**
 * Class used to handle external API interactions. It has the global references to OkHttpClient and Gson.
 *
 * @author Tyler
 */
public class ApiFactory {

    private static ApiFactory instance;
    private final OkHttpClient client;
    private final Gson gson;
    private String apiToken;
    private long tokenExpireTime;
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private FeatureList features = new FeatureList();

    private ApiFactory() {
        client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Used to get the ApiFactory singleton.
     *
     * @return Singleton ApiFactory Instance.
     */
    public static ApiFactory getFactory() {
        if (instance == null) {
            instance = new ApiFactory();
        }

        return instance;
    }

    public boolean retrieveFeatures(ApiCallback callback, String url) {
        if ((System.currentTimeMillis() - features.lastUpdated) >= ApiConstants.LUMIERE_FEATURE_REFRESH_RATE) {

            Request request = new Request.Builder()
                    .url(url).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call arg0, IOException arg1) {
                    if (callback != null) {
                        callback.onFail();
                    }
                }

                @Override
                public void onResponse(Call arg0, Response arg1) throws IOException {
                    if (callback != null) {

                        BufferedReader reader = null;

                        StringBuffer buffer = new StringBuffer();
                        try {
                            try {
                                URL url = new URL(ApiConstants.FEATURED_URL);
                                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                                int read;
                                char[] chars = new char[1024];
                                while ((read = reader.read(chars)) != -1) {
                                    buffer.append(chars, 0, read);
                                }
                                features.featureList = gson.fromJson(buffer.toString(), FeatureList.FeatureAPI[].class);
                                features.lastUpdated = System.currentTimeMillis();
                            } finally {
                                if (reader != null) {
                                    reader.close();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        callback.onSuccess();
                    }
                }

            });
            return false;
        }
        return true;
    }

    public FeatureAPI[] getFeatures() {
        return features.featureList;
    }

    /**
     * Contacts the API /info endpoint and confirms that everything is in working order.
     *
     * @param callback
     */
    public void ping(ApiCallback callback) throws ApiRuntimeException {
        Request request = new Request.Builder()
                .url(String.format(ApiConstants.PING_URL))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Ping data = gson.fromJson(response.body().string(), Ping.class);
                    LogHelper.CONSOLE.get().info("Account API version " + data.getPlatform().getVersion() + "-" + data.getPlatform().getEnvironment() + ". ");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    if (callback != null) {
                        callback.onFail();
                    }
                }

                response.body().close();
            }
        });
    }

    /**
     * Contacts the authentication server and gets a token for the server to use. This token will have full access to the API, and will be
     * used for server related functions.
     *
     * @throws java.io.IOException If the request runs into an issue, pass through the IOException.
     */
    public void getServerAuthToken() throws IOException, ApiRuntimeException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", ApiConstants.CLIENT_ID)
                .add("client_secret", ApiConstants.CLIENT_SECRET)
                .add("scope", "*")
                .build();

        Request request = new Request.Builder()
                .url(ApiConstants.AUTH_URL)
                .post(body)
                .build();

        Response response = getHttpClient().newCall(request).execute();

        if (response.isSuccessful()) {
            TokenResponseData data = getGson().fromJson(response.body().string(), TokenResponseData.class);
            apiToken = data.getAccessToken();
            tokenExpireTime = System.currentTimeMillis() + data.getExpiresIn() * 1000;
        }

        // Stop server from starting up to tell the user to consider enable DEVMODE.
        if (apiToken == null) {
            throw new ApiRuntimeException(ApiErrorCode.NULL_SERVER_TOKEN);
        }

        response.body().close();
    }

    /**
     * Contacts the authentication server and gets a token for the user to use. This token will have limited access to the API, and will be
     * used for user initiated functions.
     *
     * @throws java.io.ApiException If the request runs into an issue, pass through the ApiException.
     */
    public void getUserAuthToken(Client c, String username, String password, ApiCallback callback) {
        System.out.println("Resolving Auth Token.");

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "password")
                .add("client_id", ApiConstants.CLIENT_ID)
                .add("client_secret", ApiConstants.CLIENT_SECRET)
                .add("username", username)
                .add("password", password)
                .add("scope", "*")
                .build();

        Request request = new Request.Builder()
                .url(ApiConstants.AUTH_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                System.out.println("Failed mate");
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 500 || response.code() == 429) {
                    c.SendPacket(CLogin.getLoginFailed(6));
                    return;
                }

                System.out.println(response.code());

                Token data = getGson().fromJson(response.body().string(), Token.class);
                data.setSuccess(true);
                getUserDetailsFromToken(c, username, data);
            }
        });
    }

    public void checkUserAccessLevel(int userid, ApiCallback callback) {

        Request request = new Request.Builder()
                .url(String.format(ApiConstants.USER_ACCESS_CHECK_URL, userid, ApiConstants.PRODUCT_ID))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + this.getServerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 500 || response.code() == 429) {
                    if (callback != null) {
                        callback.onFail();
                    }
                    return;
                }

                UserAccess data = gson.fromJson(response.body().string(), UserAccess.class);

                if (data == null || data.access_code == null || !data.access_code.equals("P100")) {
                    if (callback != null) {
                        callback.onFail();
                    }
                    return;
                }
                if (callback != null) {
                    callback.onSuccess();
                }
            }
        });
    }

    public void postUserAccessLog(boolean success, int userid, String ipaddress) {
        ApiFactory.getFactory().postUserAccessLog(success, userid, ipaddress, new ApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        });

    }

    public void postUserAccessLog(boolean success, String username, String ipaddress) {
        ApiFactory.getFactory().postUserAccessLog(success, username, ipaddress, new ApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        });

    }

    public void getUserDetailsFromToken(Client c, String username, Token data) {
        if (!data.getSuccess() || data.getAccessToken() == null) {
            if (data.getError().equals("invalid_credentials")) { // Bad user credentials
                if (c != null) {
                    postUserAccessLog(false, username, c.GetIP());
                }
                c.SendPacket(CLogin.getLoginFailed(4));
                return;
            }
            if (data.getError() == "invalid_client" || data.getError() == "invalid_scope" || data.getError() == "unsupported_grant_type") { // Bad client id/secret or invalid scopes or malformed config grant
                c.SendPacket(CLogin.getLoginFailed(8));
                return;
            }
        }

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Authorization", data.getTokenType() + " " + data.getAccessToken())
                .url(ApiConstants.USER_INFO_URL)
                .build();

        ApiFactory.getFactory().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                c.SendPacket(CLogin.getLoginFailed(6));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UserInfo data = getGson().fromJson(response.body().string(), UserInfo.class);
                LoginPasswordHandler.checkLumiereAccount(c, data);
            }
        });
    }

    public void postUserLPActivated(int userid, ApiCallback callback) {
        RequestBody body = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(String.format(ApiConstants.USER_INFO_EX_URL + ApiConstants.USER_GRANT_LP_ACCESS, userid))
                .header("Authorization", "Bearer " + this.getServerToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.onSuccess();
                }
                response.body().close();
            }
        });
    }

    public void postUserAccessLog(boolean success, int userid, String ipaddress, ApiCallback callback) {
        RequestBody body = new FormBody.Builder()
                .add("user_id", String.valueOf(userid))
                .add("result", String.valueOf(success))
                .add("ip_address", ipaddress)
                .build();

        Request request = new Request.Builder()
                .url(ApiConstants.USER_ACCESS_LOG_URL)
                .header("Authorization", "Bearer " + this.getServerToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.onSuccess();
                }
                response.body().close();
            }
        });

    }

    public void postUserAccessLog(boolean success, String username, String ipaddress, ApiCallback callback) {
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("result", String.valueOf(success))
                .add("ip_address", ipaddress)
                .build();

        Request request = new Request.Builder()
                .url(ApiConstants.USER_ACCESS_LOG_URL)
                .header("Authorization", "Bearer " + this.getServerToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.onSuccess();
                }
                response.body().close();
            }
        });

    }

    /**
     * Gets the current server token. If it's expired, it fetches a new one.
     *
     * @return API Token String
     */
    public String getServerToken() {
        if (isServerTokenExpired()) {
            try {
                getServerAuthToken();
            } catch (IOException ex) {
                ex.printStackTrace();
                apiToken = null;
            }
            return apiToken;
        } else {
            return apiToken;
        }
    }

    /**
     * Checks to see if the server token is expired.
     *
     * @return true if the token is expired, false if it's active.
     */
    public boolean isServerTokenExpired() {
        return System.currentTimeMillis() > tokenExpireTime;
    }

    /**
     * Returns the instance of OkHttpClient. This is an external library used to make and handle all API calls and call backs.
     *
     * @return OkHttpClient instance
     */
    public OkHttpClient getHttpClient() {
        return client;
    }

    /**
     * Returns the instance of Gson. Gson is an external library used to convert java objects to and from JSON.
     *
     * @return
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Used to make an API call to the Jira server and create an issue based on the {@code name} and {@code desc}. All external calls are
     * asynchronous. Use the ApiCallback anonymous class to handle if it succeeds or fails.
     *
     * @param name The name for the Jira issue.
     * @param desc The description for the new Jira issue.
     * @param callback Anonymous class to provide call back functionality.
     */
    public void createJiraIssue(String name, String desc, ApiCallback callback) {
        String json = gson.toJson(new JiraCreateIssueData(name, desc), JiraCreateIssueData.class);

        RequestBody body = RequestBody.create(JSON, json);
        String auth = Credentials.basic(ApiConstants.JIRA_USERNAME, ApiConstants.JIRA_PASSWORD);
        Request request = new Request.Builder()
                .url(ApiConstants.JIRA_ISSUE_URL)
                .header("Authorization", auth)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                response.body().close();
            }
        });
    }

    /**
     * Sends a notice to the HipChat server in the Rien Support channel. All call backs are asynchronous. Use the ApiCallback anonymous
     * class to handle if it succeeds or fails.
     *
     * @param notice The notice to send to the HipChat server.
     * @param callback The ApiCallback to call on succeed or fail.
     */
    public void sendHipChatNotice(String notice, ApiCallback callback) {
        String json = gson.toJson(new HipchatSendNoticeData(notice));

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(ApiConstants.DISCORD_NOTIFICATION_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
                response.body().close();
            }
        });
    }

    public void checkBirthday(User mc, int birthday, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(String.format(ApiConstants.USER_INFO_EX_URL, mc.getClient().getAuthID()))
                .header("Authorization", "Bearer " + this.getServerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                callback.onFail();
            }
//

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    UserInfo data = getGson().fromJson(response.body().string(), UserInfo.class);
                    String dateOfBirth = data.getDob().replaceAll("-", "");
                    if (dateOfBirth.equals(Integer.toString(birthday))) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        callback.onFail();
                    }
                } else {
                    callback.onFail();
                }

                response.body().close();
            }
        });
    }

    public void getCellionPoints(User mc, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(String.format(ApiConstants.GET_CP_URL, mc.getClient().getAuthID()))
                .header("Authorization", "Bearer " + this.getServerToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }
//

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    UserResponseData data = gson.fromJson(response.body().string(), UserResponseData.class);
                    mc.setMapleRewards(data.getLumierePoints());
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    if (callback != null) {
                        callback.onFail();
                    }
                }

                response.body().close();
            }
        });
    }

    public void purchaseItemWithLP(User mc, int serialNumber, ApiCallback callback) {
        RequestBody body = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(String.format(ApiConstants.PURCHASE_ITEM_CP_URL, "300", mc.getClient().getAuthID(), serialNumber))
                .header("Authorization", "Bearer " + this.getServerToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException throwable) {
                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    PurchaseResponseData data = gson.fromJson(resp, PurchaseResponseData.class);
                    if (data.isSuccess()) {
                        mc.setMapleRewards(data.getPurchaseInfo().getCurrentLPTotal());
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (data.getError() != null) {
                            // So there was an error, check the string and provide feedback to the client.
                            switch (data.getError()) {
                                case "no_money":
                                    mc.dropMessage(1, "You currently don't have enough Points to buy this item.");
                                    break;
                                case "unknown_error":
                                    mc.dropMessage(1, "There was an error looking up the details for this purchase.\r\n\r\nPlease try again later.");
                                    break;
                                case "unexpected_error_purchase0":
                                    mc.dropMessage(1, "There was an error while checking your Points.\r\n\r\nPlease try again later.");
                                    break;
                                case "unexpected_error_purchase1":
                                    mc.dropMessage(1, "There was an error while purchasing the item.\r\n\r\nPlease try again later.");
                                    break;
                            }

                            if (callback != null) {
                                callback.onFail();
                            }
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFail();
                    }
                }

                response.body().close();
            }
        });
    }
}
