package SphereGameDemo;

import java.io.Serializable;
import java.util.ArrayList;

public class DGameState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//this class keeps track of anything that needs to be unlocked
	//like powers, privileges, levels, etc
	//since nothing is implemented, there is nothing to track
	public final static int ATK_CORE_MAX = 99;
	
	public ArrayList <Dsphere> myDspheres; //*saved
	public ArrayList <DDice> myUpgradeDice;
	public ArrayList <Ability> myAbilityCores;
	public int [] myAttackCores;
	public Collective myPlayer; //*saved
	public Dparty myParty; //*saved
	
	//triggers follow if they are implemented
	
	public DGameState (Collective c, Dparty d, ArrayList <Dsphere> ds,
			ArrayList <DDice> dd, ArrayList <Ability> ac, int [] ak) {
		myDspheres= ds;
		myUpgradeDice = dd;
		myAbilityCores = ac;
		myAttackCores = ak;
		myPlayer = c;
		myParty = d;
	}
	public void updateParty (Dparty p2){
		myParty = p2;
	}
	public void updateDSpheres (ArrayList <Dsphere> nd){
		myDspheres = nd;
	}
	public void updateDDice (ArrayList <DDice> nd){
		myUpgradeDice = nd;
	}
	public void updateAbilityCores (ArrayList <Ability> ac){
		myAbilityCores = ac;
	}
	public void updateAttackCores (int [] ac){
		myAttackCores = ac;
	}
	public void addAttackCore(int id, int num){
		myAttackCores[id]=myAttackCores[id]+num;
		if (myAttackCores[id]>ATK_CORE_MAX){
			myAttackCores[id]=ATK_CORE_MAX;
		}
	}
	public String [] toStringDsphere () {
		String [] lst = new String [myDspheres.size()];
		for (int i=0;i<lst.length;i++){
			lst [i] = myDspheres.get(i).toString();
		}
		return lst;
	}
	public String [] toStringDDice () {
		String [] lst = new String [myUpgradeDice.size()];
		for (int i=0;i<lst.length;i++){
			lst [i] = myUpgradeDice.get(i).toString();
		}
		return lst;
	}
	public String [] toStringAbilityCore () {
		String [] lst = new String [myAbilityCores.size()];
		for (int i=0;i<lst.length;i++){
			lst [i] = myAbilityCores.get(i).toString();
		}
		return lst;
	}
	public String [] toStringAttackCore () {
		String [] lst = new String [myAttackCores.length];
		for (int i=0;i<lst.length;i++){
			lst [i] = Dsphere.ATKNAMES[i]+" "+myAttackCores[i];
		}
		return lst;
	}
}
