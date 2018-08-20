package provider.data.loaders;

import java.util.ArrayList;

/**
 * @author Steven
 *
 */
public class MapleAndroidBuilder {
	
	private int id;
	private ArrayList<Integer> hair;
	private ArrayList<Integer> face;
	private ArrayList<Integer> skin;

	
	/**
	 * This is the builder class where all the android data is stored for each android type
	 * id - Is for the different types of androids which is located in "Etc.wz/Android"
	 */
	public MapleAndroidBuilder(){}
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the hair
	 */
	public ArrayList<Integer> getHair() {
		return hair;
	}
	/**
	 * @param hair the hair to set
	 */
	public void setHair(ArrayList<Integer> hair) {
		this.hair = hair;
	}
	/**
	 * @return the face
	 */
	public ArrayList<Integer> getFace() {
		return face;
	}
	/**
	 * @param face the face to set
	 */
	public void setFace(ArrayList<Integer> face) {
		this.face = face;
	}
	/**
	 * @return the skin
	 */
	public ArrayList<Integer> getSkin() {
		return skin;
	}
	/**
	 * @param skin the skin to set
	 */
	public void setSkin(ArrayList<Integer> skin) {
		this.skin = skin;
	}
	
	
}
