package SphereGameDemo;

import java.util.ArrayList;

public class SaveState {

	public Collective myPlayer;
	public Dparty myParty;
	public ArrayList <Dsphere> myInventory;
	public DGameState myGameState;
	
	/*
	 * the save routine for SGDpanel should eventually
	 * output an object of this type; this object can then be
	 * "read" to reproduce the game state
	 */
	public SaveState (){
		
	}
	public void initializeData () {

	}
	public void storeData (Collective c, Dparty d, ArrayList <Dsphere> a, DGameState dgs){
		myPlayer = c;
		myParty = d;
		myInventory = a;
		myGameState = dgs;
	}
}
