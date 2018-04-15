/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.api;

/**
 * Used to make anonymous classes as call backs when the API classes return 
 * asynchronously.
 * @author Tyler
 */
public interface ApiCallback {
	public void onSuccess();
	public void onFail();
}
