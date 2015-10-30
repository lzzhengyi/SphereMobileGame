package SphereGameDemo;

import java.util.ArrayList;
import java.util.Random;

public class DAttack {

	//an arraylist of names, 
	//the indices are used to correlate names
	//and coded actions
	//probably unneeded
	public final static String [] ATKNAMES = 
	{"Basic","Assault","Wave","Spirit","Nullify",
		"Barrage","Critical","Subdue","Random"};
	
	public final static int BASIC_A = 0;
	public final static int ASSAULT_A = BASIC_A+1;
	public final static int WAVE_A = ASSAULT_A+1;
	public final static int SPIRIT_A = WAVE_A+1;
	public final static int NULLIFY_A = SPIRIT_A+1;
	public final static int BARRAGE_A = NULLIFY_A+1;
	public final static int CRITICAL_A = BARRAGE_A+1;
	public final static int SUBDUE_A = CRITICAL_A+1;
	public final static int RANDOM_A = SUBDUE_A+1;
	
	public final static int TARGET_TYPE = 0;
	public final static int ELEMENT = 1;
	public final static int PRIORITY = 2;
	public final static int BASE_POWER = 3;
	public final static int BASE_ACCURACY = 4;
	public final static int NUM_HITS = 5;
	public final static int PURCHASE = 6;
	
	public final static int RANDOM_SINGLE = 0; //one random enemy
	public final static int WEAKEST_SINGLE = 1; //lowest percent max hp left
	public final static int MOST_SINGLE = 2; //deals most damage to target
	public final static int TARGETS_RANDOM = 3;
	public final static int ENEMY_ALL = 4;
	public final static int TARGETS_ALL = 5;

	
	public final static String [] N_ATK_NAMES = {"Strike", "Finisher","Assaulter",
		"Wave","Blast","Miscellany"};
	
	public final static int MAGICAL = 0;
	public final static int IGNORES_DEF = 1;
	public final static int TARGET_COLLECTIVE = 2;
	public final static int NULLIFY = 3;
	
	//need effect index constants

	public final static int SUBDUE = 0;
	
	//store: status effects, misc attack stats, any attack changes associated with the attack
	int [] myEffects, myTraits, myBoosts;
	boolean [] mySwitches; 
	String myName;
	
	public DAttack (int target_type, int element, int [] effects, int [] boosts, int priority, int base_power,
			int base_accuracy, int num_hits, boolean magical, boolean ignoredef,
			boolean targets_collective, boolean nullify, int purchase) {
		myEffects = effects;
		myBoosts = boosts; //boosts are handled as an int amount for each stat, times two, one for allies and one for enemies
		myTraits = new int []{target_type, element, priority, base_power, base_accuracy,num_hits, purchase};
		mySwitches = new boolean[]{magical, ignoredef, targets_collective, nullify};
		myName = "";
		/*
		 * ->Attack name: magical (arcane)
		 *  + elemental+effect type (nullification, subduing) 
		 *  target type (wave, barrage, random-miscellany, 
		 *  explosion (targetall)) + 
		 *  ignoredef (assaulter, critical, piercer)
		 */
		if (!mySwitches[TARGET_COLLECTIVE]){
			if (mySwitches[MAGICAL]){
				myName = myName + " arcane";
			}
			if (mySwitches[IGNORES_DEF]){
				myName = myName + " piercing";
			}
			if (mySwitches[NULLIFY]){
				myName = myName + " nullifying";
			}
			if (element>-1){
				myName = myName + " "+ElementalAffinity.ELEMNAMES[element];
			}
			//update when fleshed out effects
			if (myEffects [SUBDUE]>0){
				myName = myName + " Subduing";
			}
			myName = myName + " "+N_ATK_NAMES[target_type];
			myName = myName + " "+myTraits[BASE_POWER]+"."+myTraits[BASE_ACCURACY]+".x"+myTraits[NUM_HITS];
		}
	}
	//generate a DAttack from a single number
	public DAttack (int ref){
		Random rand = new Random();
		
	}
	public ArrayList <Dsphere> findAttackTargets (Dsphere attacker, Dparty atk, Dparty def){
		ArrayList <Dsphere> spheres = new ArrayList <Dsphere>();
		return spheres;
	}
	public ArrayList <Dsphere> findBoostTargets (Dsphere attacker, Dparty atk, Dparty def){
		ArrayList <Dsphere> spheres = new ArrayList <Dsphere>();
		return spheres;
	}
	public ArrayList <Dsphere> findHealTargets (Dsphere attacker, Dparty atk, Dparty def){
		ArrayList <Dsphere> spheres = new ArrayList <Dsphere>();
		return spheres;
	}
	public void resolveAttackTargets (ArrayList <Dsphere> targets,Dsphere attacker, Dparty atk, Dparty def){

	}
}
