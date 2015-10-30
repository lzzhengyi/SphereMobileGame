package SphereGameDemo;

import java.io.Serializable;
import java.util.ArrayList;

public class Collective implements Serializable {
	public final static int EXP_MAX = 20000;
	public final static int TEMP_EXP_BONUS = 600;
	public final static int LVL_MAX = 15;
	public final static double LVL_JUMP = 1.5;
	
	public final static String [] elemNames = {"Solar","Lunar","Stellar","Void","Ether","Nebula","Neutron","Water","Fire","Wind","Earth","Metal","Wood","Thunder","Crystal","Light","Shadow","Eternity","Decay","Flesh","Spirit","Energy","Life","Desire","Horror","Fear","Instinct","Anger","Peace","Sorrow","Bliss","Force","Barrier","Destiny","Fortune","Will","Dream","Vision","Chaos"};	
	public final static String [] attNames = {"Force","Barrier","Gravitas","Regeneration","Priority","Temperance"};
	public final static String [] skilNames = {"Strike","Evade"};
	public final static String [] scavNames = {"ScoreKeeper","SphereFinder","ForgeMaster"};
	public final static int ATT_CNT = 6;
	public final static int SKIL_CNT = 2;
	public final static int SCAV_CNT = 3;
	public final static int ELEM_M = 0;
	public final static int ATTR_M = 1;
	public final static int SKIL_M = 2;
	public final static int SCAV_M = 3;
	
	public int [] elemMastery;
	public int [] elemEXP;
	public int [] attMastery;
	public int [] attEXP;
	public int [] skilMastery;
	public int [] skilEXP;
	public int [] scavMastery;
	public int [] scavEXP;
	
	public int scorepoints;
	public ArrayList <Dsphere> dslist; //unneeded; the inventory
	public String [] intlist; //unneeded; the string version of the inventory
	public int chassisID;
	public String myName;
	
	//for the new player and generic collective
	public Collective (String name, int cid){
		myName = name;
		chassisID = cid;
		scorepoints = 0;
		dslist = new ArrayList <Dsphere> ();
		elemMastery = new int [ElementalAffinity.LASTELEMENT];
		for (int i=0;i<elemMastery.length;i++){
			elemMastery [i]=0;
		}
		elemEXP = new int [ElementalAffinity.LASTELEMENT];
		for (int i=0;i<elemEXP.length;i++){
			elemEXP [i]=0;
		}
		attMastery = new int [ATT_CNT];
		for (int i=0;i<attMastery.length;i++){
			attMastery [i]=0;
		}
		attEXP = new int [ATT_CNT];
		for (int i=0;i<attEXP.length;i++){
			attEXP [i]=0;
		}
		skilMastery = new int [SKIL_CNT];
		for (int i=0;i<skilMastery.length;i++){
			skilMastery [i]=0;
		}
		skilEXP = new int [SKIL_CNT];
		for (int i=0;i<skilEXP.length;i++){
			skilEXP [i]=0;
		}
		scavMastery = new int [SCAV_CNT];
		for (int i=0;i<scavMastery.length;i++){
			scavMastery [i]=0;
		}
		scavEXP = new int [SCAV_CNT];
		for (int i=0;i<scavEXP.length;i++){
			scavEXP [i]=0;
		}
	}
	//for a specifically powerful collective
	public Collective (String name, int cid, int [] em, int [] am, int skm [], int [] svm){
		myName = name;
		chassisID = cid;
		scorepoints = 0;
		dslist = new ArrayList <Dsphere> ();
		elemMastery = em;
		elemEXP = new int [ElementalAffinity.LASTELEMENT];
		for (int i=0;i<elemEXP.length;i++){
			elemEXP [i]=0;
		}
		attMastery = am;
		attEXP = new int [ATT_CNT];
		for (int i=0;i<attEXP.length;i++){
			attEXP [i]=0;
		}
		skilMastery = skm;
		skilEXP = new int [SKIL_CNT];
		for (int i=0;i<skilEXP.length;i++){
			skilEXP [i]=0;
		}
		scavMastery = svm;
		scavEXP = new int [SCAV_CNT];
		for (int i=0;i<scavEXP.length;i++){
			scavEXP [i]=0;
		}
	}
	public void changeScorePoints (int change){
		scorepoints = scorepoints + change;
	}
	public void gainStatEXP (int stat, int index, int change){
		if (stat == ELEM_M){
			elemEXP[index]=elemEXP[index]+change;
			while (elemMastery[index]<LVL_MAX && elemEXP[index]>=(calcEXPtoLevel(elemMastery[index]))){
				elemEXP[index]=elemEXP[index]-calcEXPtoLevel(elemMastery[index]); //expmastery decreases points to level up
				elemMastery[index]++;
			}
		}
		if (stat == ATTR_M){
			attEXP[index]=attEXP[index]+change;
			while (attMastery[index]<LVL_MAX && attEXP[index]>=(calcEXPtoLevel(attMastery[index]))){
				attEXP[index]=attEXP[index]-calcEXPtoLevel(attMastery[index]);
				attMastery[index]++;
			}
		}
		if (stat == SKIL_M){
			skilEXP[index]=skilEXP[index]+change;
			while (skilMastery[index]<LVL_MAX && skilEXP[index]>=(calcEXPtoLevel(skilMastery[index]))){
				skilEXP[index]=skilEXP[index]-calcEXPtoLevel(skilMastery[index]);
				skilMastery[index]++;
			}
		}
		if (stat == SCAV_M){
			scavEXP[index]=scavEXP[index]+change;
			while (scavMastery[index]<LVL_MAX && scavEXP[index]>=(calcEXPtoLevel(scavMastery[index]))){
				scavEXP[index]=scavEXP[index]-calcEXPtoLevel(scavMastery[index]);
				scavMastery[index]++;
			}
		}
	}
	public int calcEXPtoLevel (int level){
		if (level == 0){
			return EXP_MAX-attMastery[5]*TEMP_EXP_BONUS;
		} else {
			return (int) (LVL_JUMP * calcEXPtoLevel (level-1)); 	
		}
	}
	/**
	 * Updates the stored string list of spheres
	 * Currently unused
	 */
	public void updateInventory (){
		ArrayList <String> nameList = new ArrayList <String> ();
		for (int i=0;i<dslist.size();i++){
			nameList.add(dslist.get(0).toString());
		}
		intlist = (String[]) nameList.toArray();
	}
}
