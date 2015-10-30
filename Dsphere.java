package SphereGameDemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Dsphere implements Serializable{

	public final static int DEBUG_BONUS = 0; //for attack core dropping and other things
	public final static int EXP_MAX = 50;
	public final static int TURN_ATTRITION = 1;
	public final static int TURN_EXP = 1;
	public final static int ATTACK_EXP = 4;
	public final static int KILL_EXP = 5;
	public final static double ATTRITION_MULT = 0.98;
	public final static double SPHERE_LOOT_RATE = 0.05;
	public final static boolean ISMAGIC = true;
	public final static boolean IGNOREDEF = true;
	public final static int MAX_S_LEVEL = 40;
	
	public final static int INTEGRITY =0;
	public final static int OFFENSE =1;
	public final static int DEFENSE =2;
	public final static int MAGIC =3;
	public final static int PRIORITY =MAGIC+1;
	private final static int STATNO = PRIORITY+1;

	public final static String [] STATLIST = 
	{"Integrity","Force","Barrier","Gravitas","Priority"};
	public final static String [] ATKNAMES = 
	{"Basic","Assault","Wave","Spirit","Nullify",
		"Barrage","Critical","Subdue","Random"};
	public final static String [] ELEMNAMES = 
	{"Solar","Lunar","Stellar","Void","Ether","Nebula","Neutron","Water","Fire","Wind","Earth","Metal","Wood","Thunder","Crystal","Light","Shadow","Eternity","Decay","Flesh","Spirit","Energy","Life","Desire","Horror","Fear","Instinct","Anger","Peace","Sorrow","Bliss","Force","Barrier","Destiny","Fortune","Will","Dream","Vision","Chaos"};
	
	public String myName;
	public int level, exp;
	public int curr_int, curr_mag;
	public int [] stats;
	public int [] statBoosts;
	public DDice [] dice;
	public Ability ability;
	public int eleID;
	public int atkID;
	public int chaID;
	public boolean isCaptured;
//	public String [] mChList;

	public int expGain, attrition;
	
	private static Random rand;

	//everything specified constructor
	//level, statistics, dice,
	//ability, element, attack, chassis
	public Dsphere (int lvl, int [] st, DDice [] di, Ability ab, int eid, int kid, int cid){
		level = lvl;
		stats = st;
		dice = di;
		ability = ab;
		eleID = eid;
		atkID = kid;
		chaID = cid;
		exp = 0;
		resetInstancedStats();
		fullHeal();
		isCaptured = false;
		myName = "";
		if (rand ==null){
			rand = new Random();
		}
	}
	//constructor where stats are randomly genned
	//level, dice,
	//ability, element, attack, chassis
	public Dsphere (int lvl, DDice [] di, Ability ab, int eid, int kid, int cid){
		level = lvl;
		dice = di;
		stats = new int [dice.length];
		for (int i=0;i<stats.length;i++){
			stats[i]=0;
		}
		ability = ab;
		eleID = eid;
		atkID = kid;
		chaID = cid;
		exp = 0;
		initializeStats();
		resetInstancedStats();
		fullHeal();
		isCaptured = false;
		myName = "";
		if (rand ==null){
			rand = new Random();
		}
	}
	public Dsphere (Dsphere ds){
		level = ds.level;
		dice = ds.dice;
		stats = ds.stats;
		ability = ds.ability;
		eleID = ds.eleID;
		atkID = ds.atkID;
		chaID = ds.chaID;
		exp = 0;
		resetInstancedStats();
		fullHeal();
		isCaptured = false;
		myName = ds.myName;
		if (rand ==null){
			rand = new Random();
		}
	}
	/**
	 * Generate a Dsphere from a single int
	 */
	public Dsphere (int ref){
		if (rand ==null){
			rand = new Random();
		}

		level = rand.nextInt(Math.min(MAX_S_LEVEL, Math.max(ref/50, 5)));
		ref = ref-level*10;
		
		dice = new DDice [STATLIST.length];
		stats = new int [dice.length];
		for (int i=0;i<stats.length;i++){
			stats[i]=0;
		}
		int nref = ref;
		for (int i=0;i<dice.length;i++){
			dice[i]=new DDice (ref/3, rand);
			nref = nref-dice[i].calculateSPValue()*5;
		}
		ref = nref/10;
		initializeStats();
		ability = new Ability (rand.nextInt(Ability.POWERS.length),rand.nextInt(Ability.STARLIST.length));
		
		while (ability.starID<Ability.STARLIST.length-1 && ref >=10){
			ref=ref-10;
			ability.starID++;
		}
		distributeStats (ref);
		eleID = rand.nextInt(ElementalAffinity.LASTELEMENT);
		atkID = rand.nextInt(DAttack.ATKNAMES.length);
		chaID = rand.nextInt(Dchassis.regNames.length);
		exp = 0;
		resetInstancedStats();
		fullHeal();
		isCaptured = false;
		myName = "";
	}
	//different controlled method for generating higherlevel enemies
	public Dsphere (int lvl,int var,DDice[] di,int [] elems,int[]chlist){
		if (rand ==null){
			rand = new Random();
		}
		if (var<1)
			var=1;
		level = rand.nextInt(var)+lvl;
		dice = di;
		ability = new Ability (rand.nextInt(6),rand.nextInt(6));
		eleID = elems[rand.nextInt(elems.length)];
		atkID = rand.nextInt(Dsphere.ATKNAMES.length);
		chaID = chlist[rand.nextInt(chlist.length)];
		stats = new int [dice.length];
		for (int i=0;i<stats.length;i++){
			stats[i]=0;
		}
		exp = 0;
		initializeStats();
		resetInstancedStats();
		fullHeal();
		isCaptured = false;
		myName = "";
	}
	//this method generates the random dspheres looted from defeated enemy parties
	public static void addLootDsphere (ArrayList <Dsphere> ll,Dparty dp, Collective cl) {
		int tlv = 0;
		int cntr = 0;
		for (int i=0;i<dp.party.length;i++){
			if (dp.party[i]!=null){
				cntr++;
				tlv =tlv + dp.party[i].calculateLootVal();
			}
		}
		tlv = (int) (tlv * (1+0.1*cl.scavMastery[1]));
		if (rand==null){
			rand = new Random();
		}
		if (cntr>0 && rand.nextDouble()<SPHERE_LOOT_RATE+((double)cl.scavMastery[1])/100){
			ll.add(new Dsphere(tlv/(cntr*2)));
		}
	}
	private void initializeStatBoosts (){
		statBoosts = new int [stats.length];
		for (int i=0;i<STATNO;i++){
			statBoosts[i]=0;
		}
	}
	private void initializeStats (){
		for (int i=0;i<STATNO;i++){
			for (int j=0;j<=level;j++){
				stats [i]=stats[i]+dice[i].roll();
			}
		}
	}
	public void distributeStats (int val){
		while (val>0){
			int sid = rand.nextInt(stats.length);
			int raise = rand.nextInt(Math.max(1, val/5));
			if (raise <1)
				raise = 1;
			stats[sid]=stats [sid] + raise;
			val = val - raise;
		}
	}
	public int getStat (int id){
		return stats [id]+statBoosts[id];
	}
	public DDice getDice (int id){
		return dice [id];
	}
	public int getLevelRollCost (){
		int val = 0;
		for (int i=0;i<dice.length;i++){
			val = val + dice[i].calculateRollCost();
		}
		return val;
	}
	public int getLevelBoostCost (int lvgain) {
		return lvRecurse (lvgain + level);
	}
	private int lvRecurse (int check){
		if (check>0)
			return (check*(check+1))/2 + lvRecurse(check-1);
		else
			return 0;
	}
	public void setName (String name){
		myName = name;
	}
	//used for out of battle exp gain
	public void gainEXP (int gain){
		exp = exp + gain;
		while (exp >= EXP_MAX){
			exp = exp-EXP_MAX;
			gainLevel();
		}
	}
	//used for after battle exp gain
	//return true if the sphere levels
	public boolean addBattleEXP (double em){
		boolean leveled = false;
		exp = (int) (exp + expGain * em);
		expGain =0;
		while (exp >= EXP_MAX){
			exp = exp-EXP_MAX;
			gainLevel();
			leveled = true;
		}
		return leveled;
	}
	public void gainLevel (){
		//level gained protocol
		level++;
		for (int i=0;i<STATNO;i++){
			stats [i]=stats[i]+dice[i].roll();
		}
	}
	public void fullHeal () {
		curr_int = stats[INTEGRITY];
		curr_mag = stats[MAGIC];
	}
	public int takeDamage (Dsphere atk, Dparty ak, Dparty df, boolean ignoreDef, boolean isMagic){
		int dmg = -1;
		double damage = 0;
		double atkbonus = 0;
		if (rand.nextInt(101)<100+ak.cl.skilMastery[0]-df.cl.skilMastery[1]){
			if (isMagic){
				damage = atk.getStat(MAGIC)*(1+0.1*ak.cl.attMastery[2])*(1+0.1*ak.cl.elemMastery[atk.eleID]);
				//atkbonus is calculated with the bonus stat gain array
				atkbonus = ak.cl.attMastery[3]*0.1*atk.stats[MAGIC];
			} else {
				damage = atk.getStat(OFFENSE)*(1+0.1*ak.cl.attMastery[0])*(1+0.1*ak.cl.elemMastery[atk.eleID]);
				atkbonus = ak.cl.attMastery[0]*0.1*atk.stats[OFFENSE];
			}
			if (ignoreDef){
				dmg = (int) Math.max(1, (damage+atkbonus)*(rand.nextDouble()*.5+1)/30);
				curr_int = 	curr_int - dmg;	
			} else{
				dmg = (int) Math.max(1, (damage+atkbonus)*(atk.level+1)*(1+0.1*ak.cl.elemMastery[atk.eleID])*(rand.nextDouble()*2+1)/(getStat(DEFENSE)*(1+0.1*df.cl.elemMastery[atk.eleID])*(1+0.1*df.cl.attMastery[1])));
				curr_int = 	curr_int - dmg;
			}
		}
		return dmg;
	}
	public int estimateDamage (Dsphere atk, Dparty ak, Dparty df, boolean ignoreDef, boolean isMagic){
		int dmg = -1;
		double damage = 0;
		double atkbonus = 0;
		if (rand.nextInt(101)<100+ak.cl.skilMastery[0]-df.cl.skilMastery[1]){
			if (isMagic){
				damage = atk.stats[MAGIC]*(1+0.1*ak.cl.elemMastery[atk.eleID])*(1+0.1*ak.cl.attMastery[2]);
				atkbonus = ak.cl.attMastery[3]*0.1*atk.stats[MAGIC];
			} else {
				damage = atk.stats[OFFENSE]*(1+0.1*ak.cl.elemMastery[atk.eleID])*(1+0.1*ak.cl.attMastery[0]);
				atkbonus = ak.cl.attMastery[0]*0.1*atk.stats[OFFENSE];
			}
			if (ignoreDef){
				dmg = (int) Math.max(1, (damage+atkbonus)/3);
			} else{
				dmg = (int) Math.max(1, (damage+atkbonus)*(1+0.1*ak.cl.elemMastery[atk.eleID])*(atk.level+1)/(stats[DEFENSE]*(1+0.1*df.cl.elemMastery[atk.eleID])*(1+0.1*df.cl.attMastery[1])));
			}
		}
		return dmg;
	}
	/**
	 * Recalculates stats after attrition damage
	 */
	public void calculateAttrition(){
		double [] dstats = new double [stats.length];
		for (int i=0;i<dstats.length;i++){
			dstats[i]=stats[i];
		}
		while(attrition>0){
			attrition--;
			for (int i=0;i<dstats.length;i++){
				dstats[i]=dstats[i]*ATTRITION_MULT;
			}
		}
		for (int i=0;i<dstats.length;i++){
			stats[i]=(int) dstats[i];
		}
	}
	public void changeCurrInt (int val) {
		curr_int = curr_int + val;
		if (curr_int<0){
			curr_int = 0;
		}
	}
	public void changeCurrMag (int val) {
		curr_mag= curr_mag + val;
		if (curr_mag<0){
			curr_mag = 0;
		}
	}
	/**
	 * Resets expgain and attrition
	 */
	public void resetBattleStats (){
		expGain = 0;
		attrition = 0;
	}
	/**
	 * resets expgain and statboosts
	 */
	public void resetInstancedStats () {
		initializeStatBoosts ();
		resetBattleStats ();
	}
	/**
	 * resets statboosts
	 */
	public void resetStatBoosts () {
		initializeStatBoosts ();
	}
	public void refreshLevels (int amt){
		int [] nstats = new int [stats.length];
		for (int i=0;i<nstats.length;i++){
			nstats[i]=0;
		}
		for (int i=0;i<amt;i++){
			for (int j=0;j<nstats.length;j++){
				nstats[j] = nstats[j]+dice[j].roll();
			}
		}
		for (int i=0;i<stats.length;i++){
			if (nstats[i]>stats[i]){
				stats[i] = nstats[i];
			}
		}
	}
	//calculates the value of the boost power of the sphere's ability
	public int getAbilityBoostVal () {
		double rawStat=0;
		if (ability.powerID==0){
			rawStat = stats[OFFENSE];
		}
		if (ability.powerID==1){
			rawStat = stats[DEFENSE];
		}
		if (ability.powerID==2){
			rawStat = stats[MAGIC];
		}
		if (ability.powerID==3){
			rawStat = stats[PRIORITY];
		}
		if (ability.powerID==4){
			rawStat = stats[INTEGRITY];
		}
		if (ability.powerID==5){
			rawStat = stats[MAGIC];
		}
		return (int) (rawStat*.1*(ability.starID+1.0));
	}
	public boolean payAbilityCost (){
		boolean sufficientMana = false;
		//HARDCODED FORMULA
		int p60 = (int) ((ability.starID+1.0)*0.01*stats[MAGIC]);
		int cost = Math.min((ability.starID+1)*10, p60);
		if (cost<curr_mag){
			curr_mag = curr_mag-cost;
			sufficientMana = true;
		}
		return sufficientMana;
	}
	public boolean hasSufficientMana () {
		//HARDCODED FORMULA
		int p60 = (int) ((ability.starID+1.0)*0.01*stats[MAGIC]);
		int cost = Math.min((ability.starID+1)*10, p60);
		return cost<curr_mag;
	}
	public boolean isNotDisabled (){
		return curr_int >0 && !isCaptured;
	}
	public void setCaptured(boolean val){
		isCaptured = val;
	}
	public boolean isCaptured (){
		return isCaptured;
	}
	public double getIntPercentage (){
		return (double)curr_int/(double)stats[INTEGRITY];
	}
	public void gainTurnAttrition (){
		attrition=attrition+TURN_ATTRITION;
	}
	public void gainAttackExperience(){
		expGain = expGain + ATTACK_EXP;
	}
	public void gainKillExperience(){
		expGain = expGain + KILL_EXP;
	}
	public int calculateLootVal () {
		int lv = 0;
		for (int i=0;i<dice.length;i++){
			lv = lv + dice[i].calculateSPValue();
		}
		return level * lv;
	}
	public int calculateAtkCLoot(Dparty enemy){
		if (enemy!=null){
			if (rand.nextDouble()<0.05+0.01*enemy.cl.scavMastery[1]+DEBUG_BONUS){
				return atkID;
			} else{
				return -1;
			}
		} else {
			if (rand.nextDouble()<0.05+DEBUG_BONUS){
				return atkID;
			} else{
				return -1;
			}
		}
	}
	public void rerollName (int [] cidList){
		chaID = cidList[rand.nextInt(cidList.length)];
	}
	public String toString (){
		if (myName.length()>0){
			return myName;
		} else{
			return Collective.elemNames[eleID]+" "+Dchassis.chnames[chaID]+" Lv"+Integer.toString(level);			
		}
	}
	public String dumpAttributes () {
		String s="";
		s = s+toString()+"\n";
		for (int i=0;i<stats.length;i++){
			s = s +STATLIST[i]+": "+stats[i] +"\n";
		}
		for (int i=0;i<dice.length;i++){
			s = s +STATLIST[i]+"Dice : "+dice[i].toString() +"\n";
		}
		s = s +
			"Level: "+level+"\n"+
			"Experience: "+exp+"\n"+
			"Element: "+ELEMNAMES[eleID]+"\n"+
			"Chassis: "+Dchassis.chnames[chaID]+"\n"+
			"Ability: "+ability.toString()+"\n"+
			"Attack: "+DAttack.ATKNAMES[atkID];
		return s;
	}
}
