/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.api;

/**
 *
 * @author Tyler
 */
public final class ApiConstants {
	public static String PRODUCT_ID = "115";
	public static final String BASE_LUMIERE_API_URL = "https://api.cellion.org";
	public static String LUMIERE_CLIENT_ID = "115";
	public static String LUMIERE_CLIENT_SECRET = "EQ6h715mW1%!dlRwGOb2E7!#G47l1oDx";
	public static final String LUMIERE_SDK_VERSION = "0.1"; //currently unused
	public static final String DISCORD_NOTIFICATION_URL = "https://discordapp.com/api/webhooks/271060725873508352/EAvU-IIWz_OEFwi794xkUMgEI5Q7DLDVgpSf2WMi_ddBdk2Z9Ss0coUNOSeTrAG96Ole";
	public static final String HIPCHAT_COLOR = "green";
	public static final boolean HIPCHAT_NOTIFY = true;
	
	public static final String LUMIERE_FEATURED_URL = BASE_LUMIERE_API_URL + "/cms/100/all/featured";
	public static final String LUMIERE_PING_URL = BASE_LUMIERE_API_URL + "/info";
	public static final String LUMIERE_AUTH_URL = BASE_LUMIERE_API_URL + "/oauth/token";
	public static final String LUMIERE_USER_ACCESS_LOG_URL = BASE_LUMIERE_API_URL + "/log/access";
	public static final String LUMIERE_USER_INFO_URL = BASE_LUMIERE_API_URL + "/users/me";
	public static final String LUMIERE_USER_INFO_EX_URL = BASE_LUMIERE_API_URL + "/users/%s";
	public static final String LUMIERE_USER_ACCESS_CHECK_URL = LUMIERE_USER_INFO_EX_URL + "/access?product_id=%s";
	public static final String LUMIERE_USER_GRANT_LP_ACCESS = "/activate_purchase";
	public static final String LUMIERE_BEST_ITEMS_URL = BASE_LUMIERE_API_URL + "/shop/100/bestitems";

	public static final String LUMIERE_MESSAGE_URL = BASE_LUMIERE_API_URL + "/messages/";
	public static final String LUMIERE_MESSAGE_LIST_URL = BASE_LUMIERE_API_URL + "/list";
	public static final String LUMIERE_MESSAGE_PROCESS_URL = "/process";
	public static final String LUMIERE_MESSAGE_DELAY_URL = "/delay";
	public static final String LUMIERE_MESSAGE_TRASH_URL = "/trash";
        
	public static final String LUMIERE_HOOK_USER_ONLINE_URL = BASE_LUMIERE_API_URL + "/hooks/%s/user_count";
        
	public static final String GET_LP_URL = BASE_LUMIERE_API_URL + "/users/%s/lumierepoints";
	public static final String PURCHASE_ITEM_LP_URL = BASE_LUMIERE_API_URL + "/shop/%s/purchase/U%d/%d";
	
	public static final String JIRA_ISSUE_URL = "http://jira.twdtwd.com/rest/api/2/issue";
	public static final String JIRA_USERNAME = "Twdtwd";
	public static final String JIRA_PASSWORD = "kx6z9mpJEN6W";
	public static final String JIRA_PROJECT = "10000";
	public static final String JIRA_ISSUE_TYPE = "10001";
	
	public static long LUMIERE_FEATURE_REFRESH_RATE = 1 * 60 * 60 * 1000;
}

