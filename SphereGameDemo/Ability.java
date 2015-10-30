package SphereGameDemo;

import java.io.Serializable;

public class Ability implements Serializable {

	/*
	 * note: ability names cannot be longer than 6 characters
	 */
	public final static String [] STARLIST = {"0","1","2","3","4","5"};
//	public final static String [] STARLIST = {"o","<>","<|","[]","#","*"};
	public final static String [] POWERS = {"Might","Shield","Burst","Blitz","Regen","Forge"};
	public final static String [] POWERS_ALL = {
		"Might","Shield","Burst","Blitz","Regen","Forge",
		"Aether","Dspark","Flux"};
	
	public final static int EXP_AURA = 5;
	public final static int BOOST_AURA_INDEX = 4; //the index where boost aura powers end
	public final static int BASIC_AURA_INDEX = 6; //the index where boost aura powers end
	
	public int powerID, starID;
	//an arraylist of names, 
	//the indices are used to correlate names
	//and coded boosts
	/**
	 * Ability (powerID, starID)
	 */
	public Ability (int pid, int sid) {
		powerID = pid;
		starID = sid;
	}
	public String toString (){
		return POWERS_ALL [powerID]+" "+STARLIST[starID];
	}
	public int calculateSPValue (){
		return (starID+2) * 12;
	}
}
