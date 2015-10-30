package SphereGameDemo;

import java.util.ArrayList;

public class Dchassis {

	public static ArrayList <String> cnames;
	public static final String [] chnames = 
	{"Lord","Sphinx","Sprite","Orb","Angel",
		"Mermaid","Ghost","Wolf","Unicorn","Elf",
		"Phoenix","Dragon","Monolith","Giant","Guardian",
		"Beast","Warrior","Eminence","Manticore","Siren",
		"Fury","Abomination","Monstrosity","Antinumin","Succubus",
		"Fell Lord","Prosecutor","Incubus","Daemon","Warreek",
		"Terror","Messakuro","Condemner","Souleater","Loreburner",
		"Accuser","Executor","Impaler","Extinguisher","Hexakus",
		"Lifestealer","Cambion","Mordemarch","Majinn","Smiler",
		"Lanternal","Rustwork","Wreckling","Rupture","FlashBolt",
		"Axlehub","Pipejam","Elocutor","Spiral","Widgit",
		"Rotor","Circuit","Rubble","Scavenger","Telenigma",
		"Wirefray","Disunity","Impulse","Lenscrack","Forlornite"};
	public static final int [] regNames =
	{0,1,2,3,4,
		5,6,7,8,9,
		10,11,12,13,14,
		15,16,17,18,19};
	public static final int [] chaosNames =
	{20,21,22,23,24,
		25,26,27,28,29,
		30,31,32,33,34,
		35,36,37,38,39,
		40,41,42,43,44};
	public static final int [] ruinNames =
	{45,46,47,48,49,
		50,51,52,53,54,
		55,56,57,58,59,
		60,61,62,63,64};
	public static final int [] wildNames = {
			1,2,5,6,7,8,9,10,11,13,15,16,18,58,45,64,20
	};
	public static final int [] chalNames = {
		0,1,2,3,4,
		5,6,7,8,9,
		10,11,12,13,14,
		15,16,17,18,19,20,21,22,23,
		45,46,47,48,49,
		50,51,52,53,54,
		55,56,57,58,59,
		60,61,62,63,64
	};
	public static final int [] eleChNames = {
		0,3,
		6,9,
		10,11,12,14,
		17,
		21,22,23
	};
	public static final int [] blisNames = {
		4,14,0,35,4,4,4,4,4,4,14,14,14,14,4,0,0
	};
	public static final int [] rulerNames = {
		0,14,25
	};
	public Dchassis (){
		cnames = new ArrayList <String>();
		for (int i=0;i<chnames.length;i++){
			cnames.add(chnames[i]);
		}
	}
	
}
