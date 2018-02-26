/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author kaz_v
 */
public class LoginAuthorization {

    /**
     * Storing login authentication information
     */
    private final String ipAddress;
    private final String tempIP;
    private final int channel;
    private final long randomLoginAuthCookie;

    public LoginAuthorization(String ipAddress, String tempIP, int channel, long randomLoginAuthCookie) {
        this.ipAddress = ipAddress;
        this.tempIP = tempIP;
        this.channel = channel;
        this.randomLoginAuthCookie = randomLoginAuthCookie;
    }

    public String getIPAddress() {
        return ipAddress;
    }

    public String getTempIP() {
        return tempIP;
    }

    public int getChannel() {
        return channel;
    }

    public long getRandomLoginAuthCookie() {
        return randomLoginAuthCookie;
    }
}
