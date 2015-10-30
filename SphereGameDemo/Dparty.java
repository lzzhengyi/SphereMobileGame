package SphereGameDemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Dparty implements Serializable {

	public final static int PARTY_SIZE = 6;

	public Dsphere [] party;
	public Collective cl;
	
	private final static int CHAOS_LEVEL = 40;
	private final static String [] BLISS_LORDS = {"Zasheryl","Regulir","Artaniel","Firrehihl","Tyyrenia","Orgulion"};

	//when the party and owner are known
	public Dparty (Dsphere []py, Collective c){
		cl = c;
		party = py;
	}
	public Dparty (Dparty dp){
		party = new Dsphere [dp.party.length];
		cl = dp.cl;
		for (int i=0;i<party.length;i++){
			if (dp.party[i]!=null){
				party [i] = new Dsphere (dp.party[i]);
			}
		}
	}
	public Dparty (Collective c, int ref, boolean type){
		party = new Dsphere [PARTY_SIZE];
		cl = c;
		initializeRandomEnemy(ref);
		if (type){
			for (int i=0;i<party.length;i++){
				party[i].rerollName(Dchassis.rulerNames);
			}
		}
	}
	//when an owner is known and a deck must be generated
	public Dparty (Collective c, int index){
		cl = c;
		if (index==-99){
			initializeChaosEnemy (CHAOS_LEVEL);
		}
		if (index==-990){
			initializeChaosEnemy2 (CHAOS_LEVEL*3);
		}
		if (index==-991){
			initializeChaosEnemy2 (CHAOS_LEVEL*4);
		}
		if (index==-992){
			initializeChaosEnemy2 (CHAOS_LEVEL*5);
		}
		if (index==-993){
			initializeChaosEnemy2 (CHAOS_LEVEL*6);
		}
		if (index==-994){
			initializeChaosEnemy2 (CHAOS_LEVEL*8);
		}
		if (index==-995){
			initializeChaosEnemy2 (CHAOS_LEVEL*10);
		}
		if (index==-996){
			initializeChaosEnemy2 (CHAOS_LEVEL*12);
		}
		if (index==-997){
			initializeChaosEnemy2 (CHAOS_LEVEL*25);
		}
		if (index==-1){
			initializeDebugParty ();
		}
		if (index==0){
			initializeStarterSpheres ();
		}
		if (index==1){
			initializeRandomStarter ();
		}
		if (index==11){
			initializeRuinsParty(1);
		}
		if (index==12){
			initializeRuinsParty(200);
		}
		if (index==13){
			initializeRuinsParty(400);
		}
		if (index==14){
			initializeRuinsParty(800);
//			initializeRuinsParty(6000000);
		}
		if (index==15){
			initializeRuinsParty(1200);
		}
		if (index==16){
			initializeRuinsParty(1600);
		}
		if (index==17){
			initializeRuinsParty(2000);
		}
		if (index==18){
			initializeRuinsParty(2400);
		}
		if (index==19){
			initializeRuinsParty(3000);
		}
		if (index>19 && index < 100){
			initializeWildsParty(index-5);
		}
		if (index>99 && index < 1000){
			initializeChallengeParty(index-60);
		}
		if (index>999 && index < 1000000){
			initializeElemParty(index);
		}
		if (index>999999){
			initializeBlissParty(index);
		}
		/*
		 * add more methods for enemy parties
		 */
	}
	public void initializeRuinsParty(int ref){
		party = new Dsphere [6];
		for (int i=0;i<party.length;i++){
			party[i] = new Dsphere(ref);
			party[i].rerollName(Dchassis.ruinNames);
			while (party[i].level<ref/100){
				party[i].gainLevel();
			}
		}
	}
	//WARNING: PARTY GENERATION METHODS ARE MASSIVELY HARDCODED
	public void initializeWildsParty(int lvl){
		if (lvl<1)
			lvl = 1;
		lvl = lvl+(lvl-15)*6;
		Random rand = new Random ();
		party = new Dsphere [6];
		for (int j=0;j<party.length;j++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int i=0;i<da.length;i++){
				da[i] = new DDice (rand.nextInt(3)+2,rand.nextInt(12)+12,rand.nextInt(7),rand);
			}
			party[j]=new Dsphere (lvl,5,da,ElementalAffinity.EMOTION_NAMES,Dchassis.wildNames);
		}
	}
	public void initializeChallengeParty(int lvl){
		if (lvl<1)
			lvl = 1;
		cl.attMastery[0]=3;
		cl.attMastery[1]=3;
		cl.attMastery[2]=3;
		cl.attMastery[3]=3;
		cl.attMastery[4]=3;
		cl.skilMastery[0]=3;
		cl.skilMastery[1]=3;
		lvl = lvl+(lvl-40)*3;
		Random rand = new Random ();
		party = new Dsphere [6];
		for (int j=0;j<party.length;j++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int i=0;i<da.length;i++){
				da[i] = new DDice (rand.nextInt(2)+3,rand.nextInt(15)+12,rand.nextInt(9),rand);
			}
			party[j]=new Dsphere (lvl,10,da,ElementalAffinity.CONQ_NAMES,Dchassis.chalNames);
		}
	}
	public void initializeElemParty(int lvl){
		if (lvl<1)
			lvl = 1;
		for (int i=ElementalAffinity.WATER;i<ElementalAffinity.ELEMNAMES.length;i++){
			cl.elemMastery[i]=15;
		}
		cl.attMastery[0]=5;
		cl.attMastery[1]=5;
		cl.attMastery[2]=5;
		cl.attMastery[3]=5;
		cl.attMastery[4]=5;
		cl.skilMastery[0]=5;
		cl.skilMastery[1]=5;
		lvl = 70+(lvl-1000);
		Random rand = new Random ();
		party = new Dsphere [6];
		for (int j=0;j<party.length;j++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int i=0;i<da.length;i++){
				da[i] = new DDice (rand.nextInt(3)+3,rand.nextInt(15)+15,rand.nextInt(10),rand);
			}
			party[j]=new Dsphere (lvl,10,da,ElementalAffinity.PHASE_NAMES,Dchassis.eleChNames);
		}
	}
	public void initializeBlissParty(int lvl){
		if (lvl<1)
			lvl = 1;
		cl.elemMastery[ElementalAffinity.BLISS]=15;
		cl.attMastery[0]=10;
		cl.attMastery[1]=10;
		cl.attMastery[2]=10;
		cl.attMastery[3]=10;
		cl.attMastery[4]=10;
		cl.skilMastery[0]=10;
		cl.skilMastery[1]=10;
		lvl = lvl/10000+(lvl-1000000)*2;
		Random rand = new Random ();
		party = new Dsphere [6];
		for (int j=0;j<party.length;j++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int i=0;i<da.length;i++){
				da[i] = new DDice (rand.nextInt(4)+3,rand.nextInt(15)+15,rand.nextInt(12),rand);
			}
			party[j]=new Dsphere (lvl,10,da,ElementalAffinity.HEAVEN_NAMES,Dchassis.blisNames);
		}
	}
	public void initializeDebugParty () {
		Random rand = new Random ();
		cl.changeScorePoints(10000000);
		cl.elemMastery[ElementalAffinity.BLISS]=15;
		cl.scavMastery[0]=15;
		cl.scavMastery[1]=15;
		cl.scavMastery[2]=15;
		cl.attMastery[5]=15;
		cl.skilMastery[0]=15;
		DDice d1 = new DDice (5,30,12,rand);
		DDice [] da = new DDice [Dsphere.STATLIST.length];
		for (int i=0;i<da.length;i++){
			da[i] = d1;
		}
		party = new Dsphere [6];
		for (int i=0;i<party.length;i++){
			party[i]= new Dsphere (400,da,new Ability (i,5),
					ElementalAffinity.BLISS,2,4);
			party[i].myName=BLISS_LORDS [i];
		}
	}
	public void initializeStarterSpheres () {
		Random rand = new Random ();
		cl.changeScorePoints(1000);
		DDice d1 = new DDice (2,8,2,rand);
		DDice [] da = new DDice [Dsphere.STATLIST.length];
		party = new Dsphere [6];
		for (int i=0;i<da.length;i++){
			da[i] = d1;
		}
		for (int i=0;i<party.length;i++){
			party[i] = new Dsphere (4,da,new Ability (i,5),
					ElementalAffinity.WATER+i,i+1,2);
		}
	}
	public void initializeRandomStarter (){
		Random rand = new Random ();
		cl.changeScorePoints(1000);
		party = new Dsphere [6];
		for (int i=0;i<party.length;i++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int j=0;j<da.length;j++){				
				da[j] = new DDice (rand.nextInt(3)+1,rand.nextInt(7)+5,rand.nextInt(5),rand);
			}
			party[i] = new Dsphere (rand.nextInt(3)+3,da,new Ability (rand.nextInt(Ability.POWERS.length),5),
					ElementalAffinity.WATER+rand.nextInt(6),rand.nextInt(DAttack.ATKNAMES.length),2);
		}
	}
	public void initializeChaosEnemy (int lvl){
		if (lvl<1)
			lvl = 1;
		Random rand = new Random ();
		DDice [] da = new DDice [Dsphere.STATLIST.length];
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(2)+1,rand.nextInt(20)+1,rand.nextInt(10),rand);
		}

		//		Dsphere s1 = new Dsphere (rand.nextInt(20),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
		//				rand.nextInt(ElementalAffinity.LASTELEMENT),Dsphere.ATKNAMES.length-1,rand.nextInt(Dchassis.chnames.length));

		Dsphere s0 = new Dsphere (rand.nextInt(lvl),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(5)+1,rand.nextInt(30)+1,rand.nextInt(10),rand);
		}
		Dsphere s1 = new Dsphere (rand.nextInt(lvl),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(5)+1,rand.nextInt(30)+1,rand.nextInt(10),rand);
		}
		Dsphere s2 = new Dsphere (rand.nextInt(lvl),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(5)+1,rand.nextInt(30)+1,rand.nextInt(10),rand);
		}
		Dsphere s3 = new Dsphere (rand.nextInt(lvl),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(5)+1,rand.nextInt(30)+1,rand.nextInt(10),rand);
		}
		Dsphere s4 = new Dsphere (rand.nextInt(lvl),da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		for (int i=0;i<da.length;i++){
			da[i] = new DDice (rand.nextInt(5)+1,rand.nextInt(30)+1,rand.nextInt(10),rand);
		}
		Dsphere s5 = new Dsphere (rand.nextInt(20)+1,da,new Ability (rand.nextInt(6),rand.nextInt(6)),
				rand.nextInt(ElementalAffinity.LASTELEMENT),rand.nextInt(Dsphere.ATKNAMES.length),rand.nextInt(Dchassis.chnames.length));
		party = new Dsphere [6];
		party [0] = s0;
		party [1] = s1;
		party [2] = s2;
		party [3] = s3;
		party [4] = s4;
		party [5] = s5;
	}
	public void initializeChaosEnemy2 (int lvl){
		if (lvl<1)
			lvl = 1;
		cl.elemMastery[ElementalAffinity.CHAOS]=15;
		cl.attMastery[0]=15;
		cl.attMastery[1]=15;
		cl.attMastery[2]=15;
		cl.attMastery[3]=15;
		cl.attMastery[4]=15;
		cl.skilMastery[0]=15;
		cl.skilMastery[1]=15;
		Random rand = new Random ();
		party = new Dsphere [6];
		for (int j=0;j<party.length;j++){
			DDice [] da = new DDice [Dsphere.STATLIST.length];
			for (int i=0;i<da.length;i++){
				da[i] = new DDice (rand.nextInt(4)+3,rand.nextInt(15)+20,rand.nextInt(15),rand);
			}
			party[j]=new Dsphere (lvl,10,da,ElementalAffinity.CHAOS_NAMES,Dchassis.chaosNames);
		}
	}
	public void initializeRandomEnemy (int ref){
		party = new Dsphere [6];
		for (int i=0;i<party.length;i++){
			party[i] = new Dsphere(ref);
		}
	}
	public int calculateIntegrity () {
		int j = 0;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				j=j+party[i].getStat(Dsphere.INTEGRITY);
			}
		}
		return j;
	}
	public void fullHealParty () {
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].fullHeal();
			}
		}
	}
	public boolean healPartyInt (int hval) {
		boolean healed = false;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].curr_int<party[i].stats[Dsphere.INTEGRITY]){
				party[i].curr_int=(int) (party[i].curr_int+hval*(1+cl.attMastery[3]*0.03));
				healed = true;
			}
		}
		return healed;
	}
	public boolean healPartyMag (int hval, Dsphere user) {
		boolean healed = false;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i]!=user && party[i].curr_mag<party[i].stats[Dsphere.MAGIC]){
				party[i].curr_mag=(int) (party[i].curr_mag+hval*(1+cl.attMastery[3]*0.03));
				healed = true;
			}
		}
		return healed;
	}
	//sid is the stat to change, rval is the value to change it
	public void changePartyStats (int sid, int rval) {
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].stats[sid]=party[i].stats[sid]+rval;
				if (party[i].stats[sid]<0){
					party[i].stats[sid]=0;
				}
			}
		}
	}
	/**
	 * sid is the stat to change, rval is the value to change it
	 */
	public void changePartyStatBoosts (int sid, int rval) {
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].statBoosts[sid]=party[i].statBoosts[sid]+rval;
			}
		}
	}
	public void resolveAttrition () {
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].calculateAttrition();
			}
		}
	}
	public void gainTurnEXP () {
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].expGain++;
			}
		}
	}
	//hardcoded formula: player boost + highest sphere boost
	public double findExpMult () {
		double d = 0;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].ability.powerID==Ability.EXP_AURA){
				d = Math.max(d, party[i].ability.starID*0.2);
			}
		}
		d = d + cl.attMastery[5]*0.1+1;
		return d;
	}
	//add exp and return list of the leveled
	public ArrayList <Dsphere> resolveBattleEXP () {
		ArrayList <Dsphere> leveled = new ArrayList <Dsphere>();
		double em = findExpMult();
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].addBattleEXP(em)){
				leveled.add(party[i]);
			}
		}
		return leveled;
	}
	/*****
	 * Remove exhausted spheres (integrity = 0)
	 */
	public void pruneParty (){
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].curr_int<=0){
				party[i]=null;
				System.out.println("Sphere destroyed!");
			}
		}
	}
	/*****
	 * 	Calculate and return the SP value of exhausted spheres in party
	 */
	public int calculateSPLoot (){
		int lv = 0;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].curr_int<=0){
				lv = lv+party[i].calculateLootVal();
			}
		}
		return lv;
	}
	/*****
	 * Returns a list of spheres to be awarded to the player
	 */
	public ArrayList <Dsphere> calculateDSLoot(){
		ArrayList <Dsphere> dl = new ArrayList <Dsphere>();
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].curr_int>0){
				dl.add(party[i]);
			}
		}
		return dl;
	}
	/*****
	 * Returns a list of attack cores to be awarded to the player
	 */
	public int[] calculateAtkCLoot(Dparty enemy){
		int[] dl = new int[DAttack.ATKNAMES.length];
		for (int i=0;i<dl.length;i++){
			dl[i]=0;
		}
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party[i].curr_int<=0){
				int r = party[i].calculateAtkCLoot(enemy);
				if (r>-1){
					dl[r]++;
				}
			}
		}
		return dl;
	}
	/**
	 * Returns true if any spheres still have >0 integrity
	 */
	public boolean checkActive () {
		boolean active = false;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party [i].isNotDisabled()){
				active=true;
			}
		}
		return active;
	}
	/*****
	 * Returns true if any spheres are not null
	 */
	public boolean isValid () {
		boolean active = false;
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				active=true;
			}
		}
		return active;
	}
	public Dsphere getRandomPartyMember () {
		Random rand = new Random();
		Dsphere ds = null;
		if (checkActive()){
			while (ds==null){
				int slot = rand.nextInt(party.length);
				if (party[slot]!=null && party [slot].isNotDisabled()){
					ds = party[slot];
				}
			}
		}
		return ds;
	}
	public Dsphere getHighestDamage (Dsphere atker, Dparty atkparty, boolean ignoreDef, boolean isMagic){
		Dsphere ds = null;
		int dmg = 0;
		if (checkActive()){
			for (int i=0;i<party.length;i++){
				if (party[i]!=null && party [i].isNotDisabled()){
					int mdmg = party[i].estimateDamage(atker, atkparty, this, ignoreDef, isMagic);
					if (ds==null || mdmg>dmg){
						ds=party[i];
						dmg = mdmg;
					}
				}
			}
		}
		return ds;
	}
	public Dsphere getLowestIntegrity (){
		Dsphere ds = null;
		if (checkActive()){
			for (int i=0;i<party.length;i++){
				if (party[i]!=null && party [i].isNotDisabled() && ds==null || ds.stats[Dsphere.INTEGRITY]>party[i].stats[Dsphere.INTEGRITY]){
					ds=party[i];
				} 
			}
		}
		return ds;
	}
	/*****
	 * This method both heals the party and resets exp/attrition and statboosts to 0
	 */
	public void resetPartyInstancedStats(){
		fullHealParty();
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].resetInstancedStats();
			}
		}
	}
	/*****
	 * This method resets statboosts to 0
	 */
	public void resetPartyStatChanges(){
		for (int i=0;i<party.length;i++){
			if (party[i]!=null){
				party[i].resetStatBoosts();
			}
		}
	}
	/*****
	 * return a list of spheres with magic sufficient to cast
	 */
	public Dsphere [] findHasMagic () {
		int scnt = 0; //count of spheres with gravitas
		//count those with magic
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party [i].isNotDisabled() && party [i].hasSufficientMana()){
				scnt++;
			}
		}
		Dsphere [] hasMagic = new Dsphere [scnt];
		scnt=0;//reset scnt to act as the counter for spheres added to hasMAgic
		for (int i=0;i<party.length;i++){
			if (party[i]!=null && party [i].isNotDisabled() && party [i].hasSufficientMana()){
				hasMagic [scnt]=party[i];
				scnt++;
			}
		}
		return hasMagic;
	}
}
