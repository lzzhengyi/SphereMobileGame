package SphereGameDemo;

import java.util.ArrayList;

public class DAttackList {

	public ArrayList <DAttack> attackList;
	public final static String [] ATKNAMES = 
	{"Basic","Assault","Wave","Spirit","Nullify",
		"Barrage","Critical","Subdue","Random"};
	public final static int NO_ELEMENT = -1;
	public final static int [] EFFECTLESS = {-1};
	public final static int [] BOOSTLESS = {0,0,0,0,0,0,0,0,0,0,0,0};
	
	/*
	 * 	public DAttack (int target_type, int element, 
	 * int [] effects, int [] boosts, int priority, 
	 * int base_power,
			int base_accuracy, int num_hits, boolean magical,
			 boolean ignoredef,
			boolean targets_collective, boolean nullify, 
			int purchase) {
		myEffects = effects;
	 */
	public DAttackList () {
		attackList = new ArrayList <DAttack>();
		
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		//other attacks not yet implemented
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
		attackList.get(attackList.size()-1).myName = "Basic";
		attackList.add(new DAttack (DAttack.TARGETS_RANDOM,NO_ELEMENT,
				EFFECTLESS, BOOSTLESS, 0,1,100,1,false,false,false,false,1));
	}
	
}
