package SphereGameDemo;

import java.util.Random;

public class ElementalAffinity {

	//set up elements as an array of element items. Each item tracks the affinities etc
	//then set up spheres subspheres and bonds using elemental affinities and base stats
	//come up with a leveling algorithm and a base stats algorithm

	public static final int AFFINITY_I = 0;
	public static final int RESISTANCE_I = 1;
	public static final int SPEED_I = 2;
	public static final int STRIKE_I = 3;
	public static final int INTEGRITY_I = 4;
	public static final int GRAVITAS_I = 5;
	public static final int ABSORPTION_I = 6;
	public static final int REGENERATION_I = 7;
	public static final int ATTUNEMENT_I = 8;
	public static final int AURA_I = 9;

	public static final int SOLAR = 0;
	public static final int LUNAR = 1;
	public static final int STELLAR = 2;
	public static final int VOID = 3;
	public static final int ETHER = 4;
	public static final int NEBULA = 5;
	public static final int NEUTRON = 6;

	public static final int WATER = 7;
	public static final int FIRE = 8;
	public static final int WIND = 9;
	public static final int EARTH = 10;
	public static final int METAL = 11;
	public static final int WOOD = 12;
	public static final int THUNDER = 13;
	public static final int CRYSTAL = 14;

	public static final int LIGHT = 15;
	public static final int SHADOW = 16;
	public static final int STABILITY = 17; //eternity
	public static final int DECAY = 18;
	public static final int MATTER = 19; //flesh
	public static final int SPIRIT = 20;
	public static final int ENERGY = 21;
	public static final int LIFE = 22;

	public static final int DESIRE = 23;
	public static final int REVULSION = 24; //horror
	public static final int FEAR = 25;
	public static final int INSTINCT = 26;
	public static final int ANGER = 27;
	public static final int TRANQUILITY = 28; //Peace
	public static final int SORROW = 29;
	public static final int BLISS = 30;

	public static final int FORCE = 31;
	public static final int GUARD = 32; //Barrier
	public static final int DESTINY = 33; //Fortune
	public static final int LUCK = 34;
	public static final int WILL = 35;
	public static final int DREAM = 36;
	public static final int VISION = 37;

	public static final int CHAOS = 38;
	public static final int LASTELEMENT = 39; //this number should also be the same as the number of elements
	private static final int NIL_ELEMENT = -1;

	public final static String [] ELEMNAMES = 
	{"Solar","Lunar","Stellar","Void","Ether","Nebula","Neutron","Water","Fire","Wind","Earth","Metal","Wood","Thunder","Crystal","Light","Shadow","Eternity","Decay","Flesh","Spirit","Energy","Life","Desire","Horror","Fear","Instinct","Anger","Peace","Sorrow","Bliss","Force","Barrier","Destiny","Fortune","Will","Dream","Vision","Chaos"};
	
	public final static int [] STAR_NAMES = {0,1,2,3,4,5,6};
	public final static int [] PHASE_NAMES = {7,8,9,10,11,12,13,14};
	public final static int [] FORCE_NAMES = {15,16,17,18,19,20,21,22};
	public final static int [] EMOTION_NAMES = {23,24,25,26,27,28,29,30};
	public final static int [] ZEIT_NAMES = {31,32,33,34,35,36,37};
	public final static int [] CONQ_NAMES = {0,1,2,31,32,33,34,35,36,37,23};
	public final static int [] HEAVEN_NAMES = {0,2,6,13,15,17,22,30,28};
	public final static int [] CHAOS_NAMES = 
	{0,1,2,3,4,5,6,
		7,8,9,10,11,12,13,14,
		15,16,17,18,19,20,21,22,
		23,24,25,26,27,28,29,30,
		31,32,33,34,35,36,37,38
	};
	
	private int [] [] myStatTable;
	private int [] myAffinity; //raw power
	private int [] myResistance; //deflection of attacks using element
	private int [] mySpeed; //speed of attacks using this element
	private int [] myStrike; //accuracy of elements using this element
	private int [] myAbsorption; //percentage transformation of element into integrity
	private int [] myGravitas; //mana; needs to be elemental because it can be boosted by bonds and interactions
	private int [] myIntegrity; //same for regeneration
	private int [] myRegeneration; //same for regeneration; but at base it is zero without abilities etc
	private int [] myAttunement; //amount gained by boosts; proportional to aff+res
	private int [] myAura; //amount that boosts others; proportional to aff+res

	public final static int STATNO = 10; //the number of stats

	public ElementalAffinity () {
		myAffinity = new int [LASTELEMENT];
		myResistance = new int [LASTELEMENT];
		mySpeed = new int [LASTELEMENT];
		myStrike = new int [LASTELEMENT];
		myIntegrity = new int [LASTELEMENT];
		myGravitas = new int [LASTELEMENT];
		myAbsorption = new int [LASTELEMENT];
		myRegeneration = new int [LASTELEMENT];
		myAttunement = new int [LASTELEMENT];
		myAura = new int [LASTELEMENT];

		myStatTable = new int [STATNO] [];
		myStatTable [AFFINITY_I] = myAffinity;
		myStatTable [RESISTANCE_I] = myResistance;
		myStatTable [SPEED_I] = mySpeed;
		myStatTable [STRIKE_I] = myStrike;
		myStatTable [INTEGRITY_I] = myIntegrity;
		myStatTable [GRAVITAS_I] = myGravitas;
		myStatTable [ABSORPTION_I] = myAbsorption;
		myStatTable [REGENERATION_I] = myRegeneration;
		myStatTable [ATTUNEMENT_I] = myAttunement;
		myStatTable [AURA_I] = myAura;

		initializeStats();
	}

	public void initializeStats () {
		for (int i=0;i<LASTELEMENT-1;i++){
			myStatTable [AFFINITY_I] [i]	=0;
			myStatTable [RESISTANCE_I] [i]	=0;
			myStatTable [SPEED_I] [i]	=0;
			myStatTable [STRIKE_I] [i]	=0;
			myStatTable [INTEGRITY_I] [i]	=0;
			myStatTable [GRAVITAS_I] [i]	=0;
			myStatTable [ABSORPTION_I] [i]	=0;
			myStatTable [REGENERATION_I] [i]	=0;
			myStatTable [ATTUNEMENT_I] [i]	=0;
			myStatTable [AURA_I] [i]	=0;
		}
	}
	//might not even write this method in favor of the following one
	public void generateRandomStats (int powerLevel) {
		/*
		 * points for affinity and res distributed from same pool a=1
		 * points for strike distributed at a=1, in areas with affinity
		 * points for speed distributed from macropool with aff/res and absorption at a=1
		 * absorption is distributed at npoints = nlevelcubed from macropool
		 * 
		 * pseudo formula
		 * 
		 * level * 10 = points
		 * 
		 * decide on random distribution into three fragments:
		 *  generate absorption at 0-10% points
		 *  generate speed and aff/res distrib
		 *  generate aff/res distrib
		 *  generate elemental distribution
		 *  generate strike points = aff/res points, and then distribute
		 * 
		 * 
		 */
	}

	//another algorithm takes up to five elements and a powerlevel and distributes points
	//nil element is used to signal that less elements are used
	public void generateElementalStats (int element0, int element1, int element2, int element3, int element4, int powerlevel) {
		//ensure that all elements are legal
		if (element0>=LASTELEMENT)
			element0=NIL_ELEMENT;
		if (element1>=LASTELEMENT)
			element1=NIL_ELEMENT;
		if (element2>=LASTELEMENT)
			element2=NIL_ELEMENT;
		if (element3>=LASTELEMENT)
			element3=NIL_ELEMENT;
		if (element4>=LASTELEMENT)
			element4=NIL_ELEMENT;

		int points = powerlevel * 10;
		Random rand = new Random();

		int absorption = rand.nextInt((int)(points*.1));
		int strike = rand.nextInt ((int)(points*.5))+(int)(points*.5);
		strike = (int) (strike + strike * ((rand.nextInt(9))/100)); //fluctuate the values randomly
		int gravitas = rand.nextInt ((int)(points*.7))+(int)(points*.25);
		gravitas = tweakInt (gravitas, rand);
		int integrity = rand.nextInt ((int)(points*.7))+(int)(points*.25);
		integrity = tweakInt (integrity, rand);
		int newpool = points-absorption-(gravitas/4)-(integrity/4);
		newpool = tweakInt (newpool, rand);
		int affres = (int) (newpool*.1+rand.nextInt((int)(newpool*.8)));
		affres = tweakInt (affres, rand);
		int speed = newpool-affres;
		speed = tweakInt (speed, rand);
		int affinity = (int) (affres * rand.nextDouble());
		int resistance = affres - affinity;
		int attunement = (int) (.4*strike + .6*affres);
		attunement = tweakInt (attunement, rand);
		int aura = (int) (.4*strike + .6*affres);
		aura = tweakInt (aura, rand);
		//100 points is the threshold for whether points are sufficient for distribution into more than 2 elements
		//if the resulting amount of points is less than 50, only 2 elements will be used
		//most wild spheres will be generated with 100 points
		if (points<100)
			element2 = -1;

		//elements are null = distribute into force
		if (element0<0){
			myStatTable [AFFINITY_I][FORCE] = affinity;
			myStatTable [RESISTANCE_I][FORCE] = resistance;
			myStatTable [SPEED_I][FORCE] = speed;
			myStatTable [STRIKE_I][FORCE] = strike;
			myStatTable [INTEGRITY_I][FORCE] = integrity;
			myStatTable [GRAVITAS_I][FORCE] = gravitas;
			myStatTable [AURA_I][FORCE] = aura;
			myStatTable [ATTUNEMENT_I][FORCE] = attunement;
		}
		//only one true element = don't partition values
		if (element0>-1 && element1<0){
			myStatTable [AFFINITY_I][element0] = affinity;
			myStatTable [RESISTANCE_I][element0] = resistance;
			myStatTable [SPEED_I][element0] = speed;
			myStatTable [STRIKE_I][element0] = strike;
			myStatTable [INTEGRITY_I][element0] = integrity;
			myStatTable [GRAVITAS_I][element0] = gravitas;
			myStatTable [ABSORPTION_I][element0] = (int)Math.sqrt(absorption);
			myStatTable [AURA_I][element0] = aura;
			myStatTable [ATTUNEMENT_I][element0] = attunement;
		}
		//otherwise, partition values
		if (element0>-1 && element1 >-1){
			//counter determines how many elements are valid
			int counter = 2;
			if (element2 >-1){
				counter++;
				if (element3 > -1){
					counter++;
					if (element4 > -1){
						counter++;
					}
				}
			}

			double [] divisions0 = new double [STATNO];
			double [] divisions1 = new double [STATNO];
			double [] divisions2 = new double [STATNO];
			double [] divisions3 = new double [STATNO];
			double [] divisions4 = new double [STATNO];

			double [] fraction = new double [counter];
			for (int i=0;i<divisions0.length;i++){
				fraction = divideArray (fraction);
				divisions0 [i] = fraction [0];
				divisions1 [i] = fraction [1];
				if (counter > 2) {
					divisions2 [i] = fraction [2];
					if (counter > 3) {
						divisions3 [i] = fraction [3];
						if (counter > 4){
							divisions4 [i] = fraction [4];	
						}
					} 
				}
			}


			//multiply the halves of the array and then distribute points

			myStatTable [AFFINITY_I][element0] = (int) (myStatTable [AFFINITY_I][element0] + affinity * divisions0 [AFFINITY_I]);
			myStatTable [RESISTANCE_I][element0] = (int) (myStatTable [RESISTANCE_I][element0] + resistance * divisions0 [RESISTANCE_I]);
			myStatTable [SPEED_I][element0] = (int) (myStatTable [SPEED_I][element0] + speed * divisions0 [SPEED_I]);
			myStatTable [STRIKE_I][element0] = (int) (myStatTable [STRIKE_I][element0] + strike * divisions0 [STRIKE_I]);
			myStatTable [INTEGRITY_I][element0] = (int) (myStatTable [INTEGRITY_I][element0] + integrity * divisions0 [INTEGRITY_I]);
			myStatTable [GRAVITAS_I][element0] = (int) (myStatTable [GRAVITAS_I][element0] + gravitas * divisions0 [GRAVITAS_I]);
			myStatTable [ABSORPTION_I][element0] = (int) (myStatTable [ABSORPTION_I][element0] + Math.sqrt(absorption) * divisions0 [ABSORPTION_I]);
			myStatTable [ATTUNEMENT_I][element0] = (int) (myStatTable [ATTUNEMENT_I][element0] + attunement * divisions0 [ATTUNEMENT_I]);
			myStatTable [AURA_I][element0] = (int) (myStatTable [AURA_I][element0] + aura * divisions0 [AURA_I]);
			
			myStatTable [AFFINITY_I][element1] = (int) (myStatTable [AFFINITY_I][element1] + affinity * divisions1 [AFFINITY_I]);
			myStatTable [RESISTANCE_I][element1] = (int) (myStatTable [RESISTANCE_I][element1] + resistance * divisions1 [RESISTANCE_I]);
			myStatTable [SPEED_I][element1] = (int) (myStatTable [SPEED_I][element1] + speed * divisions1 [SPEED_I]);
			myStatTable [STRIKE_I][element1] = (int) (myStatTable [STRIKE_I][element1] + strike * divisions1 [STRIKE_I]);
			myStatTable [INTEGRITY_I][element1] = (int) (myStatTable [INTEGRITY_I][element1] + integrity * divisions1 [INTEGRITY_I]);
			myStatTable [GRAVITAS_I][element1] = (int) (myStatTable [GRAVITAS_I][element1] + gravitas * divisions1 [GRAVITAS_I]);
			myStatTable [ABSORPTION_I][element1] = (int) (myStatTable [ABSORPTION_I][element1] + Math.sqrt(absorption) * divisions1 [ABSORPTION_I]);
			myStatTable [ATTUNEMENT_I][element1] = (int) (myStatTable [ATTUNEMENT_I][element1] + attunement * divisions0 [ATTUNEMENT_I]);
			myStatTable [AURA_I][element1] = (int) (myStatTable [AURA_I][element1] + aura * divisions0 [AURA_I]);
			
			if (counter > 2){
				myStatTable [AFFINITY_I][element2] = (int) (myStatTable [AFFINITY_I][element2] + affinity * divisions2 [AFFINITY_I]);
				myStatTable [RESISTANCE_I][element2] = (int) (myStatTable [RESISTANCE_I][element2] + resistance * divisions2 [RESISTANCE_I]);
				myStatTable [SPEED_I][element2] = (int) (myStatTable [SPEED_I][element2] + speed * divisions2 [SPEED_I]);
				myStatTable [STRIKE_I][element2] = (int) (myStatTable [STRIKE_I][element2] + strike * divisions2 [STRIKE_I]);
				myStatTable [INTEGRITY_I][element2] = (int) (myStatTable [INTEGRITY_I][element2] + integrity * divisions2 [INTEGRITY_I]);
				myStatTable [GRAVITAS_I][element2] = (int) (myStatTable [GRAVITAS_I][element2] + gravitas * divisions2 [GRAVITAS_I]);
				myStatTable [ABSORPTION_I][element2] = (int) (myStatTable [ABSORPTION_I][element2] + Math.sqrt(absorption) * divisions2 [ABSORPTION_I]);
				myStatTable [ATTUNEMENT_I][element2] = (int) (myStatTable [ATTUNEMENT_I][element2] + attunement * divisions0 [ATTUNEMENT_I]);
				myStatTable [AURA_I][element2] = (int) (myStatTable [AURA_I][element2] + aura * divisions0 [AURA_I]);
			}

			if (counter > 3){
				myStatTable [AFFINITY_I][element3] = (int) (myStatTable [AFFINITY_I][element3] + affinity * divisions3 [AFFINITY_I]);
				myStatTable [RESISTANCE_I][element3] = (int) (myStatTable [RESISTANCE_I][element3] + resistance * divisions3 [RESISTANCE_I]);
				myStatTable [SPEED_I][element3] = (int) (myStatTable [SPEED_I][element3] + speed * divisions3 [SPEED_I]);
				myStatTable [STRIKE_I][element3] = (int) (myStatTable [STRIKE_I][element3] + strike * divisions3 [STRIKE_I]);
				myStatTable [INTEGRITY_I][element3] = (int) (myStatTable [INTEGRITY_I][element3] + integrity * divisions3 [INTEGRITY_I]);
				myStatTable [GRAVITAS_I][element3] = (int) (myStatTable [GRAVITAS_I][element3] + gravitas * divisions3 [GRAVITAS_I]);
				myStatTable [ABSORPTION_I][element3] = (int) (myStatTable [ABSORPTION_I][element3] + Math.sqrt(absorption) * divisions3 [ABSORPTION_I]);
				myStatTable [ATTUNEMENT_I][element3] = (int) (myStatTable [ATTUNEMENT_I][element3] + attunement * divisions0 [ATTUNEMENT_I]);
				myStatTable [AURA_I][element3] = (int) (myStatTable [AURA_I][element3] + aura * divisions0 [AURA_I]);
			}

			if (counter > 4){
				myStatTable [AFFINITY_I][element4] = (int) (myStatTable [AFFINITY_I][element4] + affinity * divisions4 [AFFINITY_I]);
				myStatTable [RESISTANCE_I][element4] = (int) (myStatTable [RESISTANCE_I][element4] + resistance * divisions4 [RESISTANCE_I]);
				myStatTable [SPEED_I][element4] = (int) (myStatTable [SPEED_I][element4] + speed * divisions4 [SPEED_I]);
				myStatTable [STRIKE_I][element4] = (int) (myStatTable [STRIKE_I][element4] + strike * divisions4 [STRIKE_I]);
				myStatTable [INTEGRITY_I][element4] = (int) (myStatTable [INTEGRITY_I][element4] + integrity * divisions4 [INTEGRITY_I]);
				myStatTable [GRAVITAS_I][element4] = (int) (myStatTable [GRAVITAS_I][element4] + gravitas * divisions4 [GRAVITAS_I]);
				myStatTable [ABSORPTION_I][element4] = (int) (myStatTable [ABSORPTION_I][element4] + Math.sqrt(absorption) * divisions4 [ABSORPTION_I]);
				myStatTable [ATTUNEMENT_I][element4] = (int) (myStatTable [ATTUNEMENT_I][element4] + attunement * divisions0 [ATTUNEMENT_I]);
				myStatTable [AURA_I][element4] = (int) (myStatTable [AURA_I][element4] + aura * divisions0 [AURA_I]);
			}
		}
	}

	private int tweakInt (int n, Random rand){
		if (rand.nextDouble()>.5){
			return n + n*(rand.nextInt(11)/100);	
		} else {
			return n - n*(rand.nextInt(11)/100);
		}
	}
	//purpose is to divide a whole into random percentages
	public static double [] divideArray (double [] holder){
		Random rand = new Random();
		int [] points = new int [holder.length-1];
		//generation random points
		for (int i=0;i<points.length;i++){
			points [i] = rand.nextInt(10001);
		}

		sort (points);

		//tests
		//		for (int i=0;i<points.length;i++){
		//			System.out.println(points [i]);
		//		}
		//		System.out.println("******");
		//endtest

		//subtract each number from the previous number to get the percentage of points distrib'ed to it
		holder [0] = points [0];
		for (int i=1;i<points.length;i++){
			holder [i] = points [i] - points [i-1];
		}
		holder [holder.length-1] = 10000-points [points.length-1];

		for (int i=0;i<holder.length;i++){
			holder [i] = (holder [i] / 10000);
		}

		return holder;
	}

	private static void sort (int [] a){
		int i, j;
		int key;
		for (i=1;i<a.length;i++){
			key = a[i];

			for (j=i-1;j>=0 && key < a[j];j--){
				a [j+1] = a[j];
			}
			a[j+1]=key;
		}
	}

	public int [] getStatArray (int statIndex){
		if (statIndex < myStatTable.length){
			return myStatTable [statIndex];			
		} else {
			return null;
		}
	}

	public int [] getElementStats (int elementIndex) {
		if (elementIndex < LASTELEMENT) {
			int [] elementArray = new int [8];
			elementArray [AFFINITY_I] = myStatTable [AFFINITY_I][elementIndex];
			elementArray [RESISTANCE_I] = myStatTable [RESISTANCE_I][elementIndex];
			elementArray [SPEED_I] = myStatTable [SPEED_I][elementIndex];
			elementArray [STRIKE_I] = myStatTable [STRIKE_I][elementIndex];
			elementArray [INTEGRITY_I] = myStatTable [INTEGRITY_I][elementIndex];
			elementArray [GRAVITAS_I] = myStatTable [GRAVITAS_I][elementIndex];
			elementArray [ABSORPTION_I] = myStatTable [ABSORPTION_I][elementIndex];
			elementArray [REGENERATION_I] = myStatTable [REGENERATION_I][elementIndex];
			return elementArray;
		} else {
			return null;
		}
	}

	public void printElementalStats (int elementIndex){
		int [] mArray = getElementStats (elementIndex);
		System.out.println("Affinity is " + mArray [0]);	
		System.out.println("Resistance is " + mArray [1]);
		System.out.println("Speed is " + mArray [2]);
		System.out.println("Strike is " + mArray [3]);
		System.out.println("Integrity is " + mArray [4]);
		System.out.println("Gravitas is " + mArray [5]);
		System.out.println("Absorption is " + mArray [6]);
		System.out.println("Regen is " + mArray [7]);
	}
}
