package SphereGameDemo;

public class DDestination {

	/*
	 * DDestination is merely a reference for id, name, and party of a location
	 * 
	 * Actual DDestination objects are initialized elsewhere: probably destination panel
	 */
	
	public int dID; //unique id that corresponds to a collective
	public int zoneID; //id that corresponds to a world (ruins, challenge, etc; 0-6);
	//^ might be optional
	public String myName;
	
	public DDestination (int id, int zid, String name) {
		dID = id;
		zoneID = zid;
		myName = name;
	}
	
	public Dparty generateDparty () {
		/*
		 * the following code is an example of the structure
		 * for generating enemy collectives
		 * 
		 * 
		 */
		Collective c = new Collective ("Chaos",0);
		if (dID == -99){
			c = new Collective ("Chaos",0);
			return new Dparty (c,dID);
		}
		if (dID <= -990){
			c = new Collective ("Chaos",0);
			return new Dparty (c,dID);
		}
		if (dID >10 && dID < 20){
			c = new Collective ("Scavenger",9);
			return new Dparty (c,dID);
		}
		if (dID >19 && dID < 100){
			c = new Collective ("Hunter",9);
			return new Dparty (c,dID);
		}
		if (dID >99 && dID < 1000){
			c = new Collective ("ArchLord",0);
			return new Dparty (c,dID);
		}
		if (dID >999 && dID < 1000000){
			c = new Collective ("Element Lord",0);
			return new Dparty (c,dID);
		}
		if (dID > 999999 ){
			c = new Collective ("Archoseraphel",0);
			return new Dparty (c,dID);
		}
		if (dID==-1){
			c = new Collective ("Magistra",6);
			return new Dparty (c,dID);
		}
		return new Dparty (c,-99);
	}
	public Dparty generateDpartyMirror(Dparty initial){
		Collective c = initial.cl;
		if (dID == -11){
			int cost = 0;
			for (int i=0;i<initial.party.length;i++){
				if (initial.party[i]!=null){
					cost = cost + initial.party[i].calculateLootVal();	
				}
			}
			return new Dparty (c,cost/(2*initial.party.length),false);
		}
		if (dID == -12){
			int cost = 0;
			for (int i=0;i<initial.party.length;i++){
				if (initial.party[i]!=null){
					int val = initial.party[i].calculateLootVal();
					if (val>cost){
						cost = val;
					}
				}

			}
			return new Dparty (c,cost/(2),false);
		}
		return new Dparty (initial);
	}
	public String toString () {
		return myName;
	}
}
