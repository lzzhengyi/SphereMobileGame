package SphereGameDemo;

public class AbilityCore {

	public final static String [] STARLIST = {"o","<>","<|","[]","#","*"};
	public final static String [] POWERS = {"Might","Shield","Burst","Blitz","Regen","Forge"};
	
	public int powerID, starID;
//this class is redundant and needs to be deleted
	public AbilityCore (int pid, int sid) {
		powerID = pid;
		starID = sid;
	}
	public String toString (){
		return POWERS [powerID]+" "+STARLIST[starID];
	}
}
