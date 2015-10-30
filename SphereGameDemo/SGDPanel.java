package SphereGameDemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SGDPanel extends JPanel {

	public static final int SPHEREVIEWER = 0;
	public static final int BATTLELOG = 1;
	public static final int SHOWHELP = 2;
	public static final int SHOWABOUT = 3;

	public DGameState dgs; //save everything here (player info, party, inventories)
	//interface components
	public PartySelectPanel psp;
	public InventoryPanel ivp;
	public DestinationSelectPanel dsp;
	public SphereDisplayPanel sdp;
	public PlayerDisplayPanel pdp;
	public ShopDisplayPanel shp;
	public BattleDisplayPanel bdp;

	public SphereStatFrame ssf;
	public BattleLogFrame blf;
	public RefreshLevelFrame rlf;
	public PurchaseSphereLevelFrame pslf;
	public PurchasePlayerLevelFrame pplf;
	public UnitPurchaseFrame upf;
	public CustomizeSphereFrame csf;
	public ShowHelpFrame shf;
	public ShowAboutFrame saf;
	//interface global variables
	public Dsphere selectedSphere; //this is the sphere that is displayed and deleted
	public Ability selectedACore;
	public DDice selectedDice;
	public int selectedATKInd;
	public int selectedDestination;

	public boolean gameRunning; //need to add gameRunning checks to all methods
	public boolean battleRunning; //need to add battleRunning checks to most gameRunning checks

	public Random rand;

	public SGDPanel (){

		gameRunning = false;
		battleRunning = false;
		rand = new Random();
		selectedDestination = -2; //to ensure null destination
		selectedATKInd = -1; //to ensure null attack selected

		/*
		 * This part initializes viewing frames
		 */

		ssf = new SphereStatFrame();
		blf = new BattleLogFrame();
		rlf = new RefreshLevelFrame(this);
		pslf = new PurchaseSphereLevelFrame(this);
		pplf = new PurchasePlayerLevelFrame(this);
		upf = new UnitPurchaseFrame (this);
		csf = new CustomizeSphereFrame();
		shf = new ShowHelpFrame();
		saf = new ShowAboutFrame();
		/*
		 * this part initializes panels
		 */
		//experimenting with gridbaglayout
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		gbl.columnWeights = new double[] {.25,.6,0.15};
		//		gbl.rowWeights = new double[] {0.1,0.10,0.6,0.20};
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		//generic border information
		Border rbb = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		TitledBorder ebd;

		//adding and bordering the panels
		//PartySelectPanel 
		psp = new PartySelectPanel(this);
		ebd = BorderFactory.createTitledBorder(rbb, "Party Select");
		psp.setBorder(ebd);
		//InventoryPanel 
		ivp = new InventoryPanel (this);
		ebd = BorderFactory.createTitledBorder(rbb, "Inventory");
		ivp.setBorder(ebd);
		//DestinationSelectPanel 
		dsp = new DestinationSelectPanel (this);
		ebd = BorderFactory.createTitledBorder(rbb, "Destination");
		dsp.setBorder(ebd);
		//SphereDisplayPanel 
		sdp = new SphereDisplayPanel ();
		ebd = BorderFactory.createTitledBorder(rbb, "Sphere Stats");
		sdp.setBorder(ebd);
//		sdp.setMaximumSize (new Dimension (100,90));
		//PlayerDisplayPanel 
		pdp = new PlayerDisplayPanel (this);
		ebd = BorderFactory.createTitledBorder(rbb, "Player");
		pdp.setBorder(ebd);
		//ShopDisplayPanel 
		shp = new ShopDisplayPanel(this);
		ebd = BorderFactory.createTitledBorder(rbb, "Shop");
		shp.setBorder(ebd);
		//BattleDisplayPanel 
		bdp = new BattleDisplayPanel (); 
		ebd = BorderFactory.createTitledBorder(rbb, "");
		bdp.setBorder(ebd);
		bdp.setMaximumSize (new Dimension (412,555));
		bdp.setMinimumSize (new Dimension (300,450));
		//		shp.setMaximumSize (new Dimension (200,555));

		//not sure how this works; fine tune when time is available
		c.weightx=1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight=1;
		c.gridwidth=1;
		//		c.ipady = 150;
		add(psp,c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight=3;
		//		c.ipady = 150;
		add(ivp,c);
		c.gridx = 0;
		c.gridy = 4;
		//		c.gridheight=1;
		c.ipadx = 40;
//		c.ipady = 40;
		add(sdp,c);
		c.gridx=1;
		c.gridy=4;
		c.gridwidth=3;
		c.ipadx = 200;
		add(dsp,c);
		c.gridwidth=4;
		c.gridx=1;
		c.gridy=0;
		//		c.ipady = 100;
		//		c.ipadx = 100;
		c.weightx=1;
		add(pdp,c);
		c.gridx=4;
		c.gridy=1;
		c.gridheight=4;
		c.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
		add(shp,c);
		c.gridx=1;
		c.gridy=1;
		c.gridwidth=3;
		c.gridheight=3;
		//		c.ipady = 400;
		//		c.ipadx = 400;
		add(bdp,c);
		/*
		 * further action depends on the buttons
		 * the player presses
		 */
	}

	//this is most likely the uberpanel
	//within this panel, create more panels for:
	/*
	 * battle display (center)
	 * party selection (left) v
	 * sphere inventory (right) v
	 * destination selection (top) v
	 * shop (alternate panel)
	 * player stats (top)
	 * sphere stats (bottom or after their names) v
	 */
	public void startNewGame (String pname, boolean setstart){
		/*
		 * normal code = prompt name (if name is debug, go to debug mode)
		 * depending on selected options, give starter deck, random orbs, etc
		 */
		if (pname.equals("Magistra")){
			startDebugMode();
		} else {
			if (!setstart){
				startRandomMode (pname);
			} else {
				startSetMode (pname);
			}
		}
//		if (!gameRunning){
//			gameRunning = true;
//		}
//		if (!ssf.initialized){
//			ssf.initializeFrame();
//		}
		updateAllLabels();
	}
	public void startDebugMode (){
		gameRunning = true;
		Collective magistra = new Collective ("Magistra",0);

		dgs = new DGameState (
				magistra,new Dparty (magistra, -1),new ArrayList <Dsphere>(),
				new ArrayList <DDice>(), new ArrayList <Ability>(),
				new int [Dsphere.ATKNAMES.length]);
	}
	//for the initial linked elemental sprite party
	public void startSetMode (String pname){
		gameRunning = true;
		Collective magistra = new Collective (pname,0);

		dgs = new DGameState (
				magistra,new Dparty (magistra, 0),new ArrayList <Dsphere>(),
				new ArrayList <DDice>(), new ArrayList <Ability>(),
				new int [Dsphere.ATKNAMES.length]);
	}
	public void startRandomMode (String pname){
		gameRunning = true;
		Collective magistra = new Collective (pname,0);

		dgs = new DGameState (
				magistra,new Dparty (magistra, 1),new ArrayList <Dsphere>(),
				new ArrayList <DDice>(), new ArrayList <Ability>(),
				new int [Dsphere.ATKNAMES.length]);
	}
	public void loadState (DGameState d){
		gameRunning = true;
		dgs = d;
		updateAllLabels();
	}
	public void saveState (){
		try {
			FileOutputStream fileOut =
				new FileOutputStream("savestate.ser");
			ObjectOutputStream out =
				new ObjectOutputStream(fileOut);
			out.writeObject(dgs);
			out.close();
			fileOut.close();
		}catch(IOException i)
		{
			i.printStackTrace();
		}
	}
	//contains every label update method in the program
	public void updateAllLabels (){
		pdp.updateLabels();
		psp.updateAll();
		ssf.updateDisplay();
		pplf.updatePPLabels ();
		upf.updateLabels();
		csf.updateLabels();

		ivp.updateSpheres (dgs.toStringDsphere());
		ivp.updateDice (dgs.toStringDDice());
		ivp.updateAuras (dgs.toStringAbilityCore());
		ivp.updateCores (dgs.toStringAttackCore());
	}
	public void updateInventory(){
		ssf.updateDisplay();
		ivp.updateSpheres (dgs.toStringDsphere());
	}
	public void changeSelectedSphere (Dsphere neo) {
		selectedSphere = neo;
		sdp.updateDisplay ();
	}
	//when the program is closed, this closes all interface frames
	public void disposeSubframes(){
		ssf.dispose();
		blf.dispose();
		rlf.dispose();
		pslf.dispose();
		pplf.dispose();
		upf.dispose();
		csf.dispose();
		shf.dispose();
		saf.dispose();
	}
	//unused
	public void promptContinue () {
		Scanner scan = new Scanner (System.in);
		String s = scan.nextLine();	
	}
	//to aid in reading user number input and preventing crashes
	public static int processIntInput (String curr){
		try {
			String res = "0";
			for (int i=0;i<curr.length();i++){
				String ch = ""+curr.charAt(i);
				if (Character.isDigit(curr.charAt(i))){
					res=res+ch;
				}
			}
			return Integer.parseInt(res);
		} catch (Exception e){
			System.out.println("Bad Input");
		}
		return -1;
	}
	//required for SGDFrame interactions
	public void hideFrame (int frameno){
		if (frameno == SPHEREVIEWER){
			ssf.setVisible(false);
		}
		if (frameno == BATTLELOG){
			blf.setVisible(false);
		}
		if (frameno == SHOWHELP){
			shf.setVisible(false);
		}
		if (frameno == SHOWABOUT){
			saf.setVisible(false);
		}
	}
	public void showFrame (int frameno){
		if (frameno == SPHEREVIEWER){
			ssf.setVisible(true);
		}
		if (frameno == BATTLELOG){
			blf.setVisible(true);
		}
		if (frameno == SHOWHELP){
			shf.setVisible(true);
		}
		if (frameno == SHOWABOUT){
			saf.setVisible(true);
		}
	}
	public void disableAll () {
		psp.setEnabled (false);
		ivp.setEnabled (false);
		dsp.setEnabled (false);
		sdp.setEnabled (false);
		pdp.setEnabled (false);
		shp.setEnabled (false);
		bdp.setEnabled (false);
	}
	public void enableAll () {
		psp.setEnabled (true);
		ivp.setEnabled (true);
		dsp.setEnabled (true);
		sdp.setEnabled (true);
		pdp.setEnabled (true);
		shp.setEnabled (true);
		bdp.setEnabled (true);
	}
	//use setEnabled(boolean) to enable or disable
	private class PartySelectPanel extends JPanel implements ActionListener {

		/*
		 * two items:
		 * first label of the current party member's name
		 * second combobox of all spheres
		 */
		SGDPanel sgd;

		JComboBox box0,box1, box2, box3,box4,box5;
		JLabel dyn0;
		JLabel dyn1;
		JLabel dyn2;
		JLabel dyn3;
		JLabel dyn4;
		JLabel dyn5;

		JButton cb0,cb1,cb2,cb3,cb4,cb5; //clear
		JButton rb0,rb1,rb2,rb3,rb4,rb5; //rename
		JButton sb0,sb1,sb2,sb3,sb4,sb5; //set

		public PartySelectPanel (SGDPanel s){
			sgd = s;
			JTabbedPane dtp = new JTabbedPane();

			JPanel dp0 = new JPanel ();
			dp0.setPreferredSize(new Dimension(270,90));
			JPanel dp1 = new JPanel ();
			JPanel dp2 = new JPanel ();
			JPanel dp3 = new JPanel ();
			JPanel dp4 = new JPanel ();
			JPanel dp5 = new JPanel ();
			dtp.addTab("Member 01",null,dp0,"");
			dtp.addTab("Member 02",null,dp1,"");
			dtp.addTab("Member 03",null,dp2,"");
			dtp.addTab("Member 04",null,dp3,"");
			dtp.addTab("Member 05",null,dp4,"");
			dtp.addTab("Member 06",null,dp5,"");

			JLabel label0 = new JLabel (" M 1: ");
			JLabel label1 = new JLabel (" M 2: ");
			JLabel label2 = new JLabel (" M 3: ");
			JLabel label3 = new JLabel (" M 4: ");
			JLabel label4 = new JLabel (" M 5: ");
			JLabel label5 = new JLabel (" M 6: ");

			dyn0 = new JLabel (" * ");
			dyn1 = new JLabel (" * ");
			dyn2 = new JLabel (" * ");
			dyn3 = new JLabel (" * ");
			dyn4 = new JLabel (" * ");
			dyn5 = new JLabel (" * ");

			box0 = new JComboBox ();
			box1 = new JComboBox ();
			box2 = new JComboBox ();
			box3 = new JComboBox ();
			box4 = new JComboBox ();
			box5 = new JComboBox ();

			cb0 = new JButton("Clear");
			cb1 = new JButton("Clear");
			cb2 = new JButton("Clear");
			cb3 = new JButton("Clear");
			cb4 = new JButton("Clear");
			cb5 = new JButton("Clear");
			rb0 = new JButton("Rename");
			rb1 = new JButton("Rename");
			rb2 = new JButton("Rename");
			rb3 = new JButton("Rename");
			rb4 = new JButton("Rename");
			rb5 = new JButton("Rename");
			sb0 = new JButton("Set");
			sb1 = new JButton("Set");
			sb2 = new JButton("Set");
			sb3 = new JButton("Set");
			sb4 = new JButton("Set");
			sb5 = new JButton("Set");

			box0.addActionListener(this);
			box1.addActionListener(this);
			box2.addActionListener(this);
			box3.addActionListener(this);
			box4.addActionListener(this);
			box5.addActionListener(this);

			cb0.addActionListener(this);
			cb1.addActionListener(this);
			cb2.addActionListener(this);
			cb3.addActionListener(this);
			cb4.addActionListener(this);
			cb5.addActionListener(this);

			rb0.addActionListener(this);
			rb1.addActionListener(this);
			rb2.addActionListener(this);
			rb3.addActionListener(this);
			rb4.addActionListener(this);
			rb5.addActionListener(this);

			sb0.addActionListener(this);
			sb1.addActionListener(this);
			sb2.addActionListener(this);
			sb3.addActionListener(this);
			sb4.addActionListener(this);
			sb5.addActionListener(this);

			JPanel int0 = new JPanel();
			JPanel inti0 = new JPanel();
			int0.add (label0);
			int0.add (dyn0);
			dp0.add (int0);
			dp0.add (box0);
			dp0.add (inti0);
			inti0.add (cb0);
			inti0.add (rb0);
			inti0.add (sb0);
			dp0.setLayout(new BoxLayout(dp0, BoxLayout.PAGE_AXIS));

			JPanel int1 = new JPanel();
			JPanel inti1 = new JPanel();
			int1.add (label1);
			int1.add (dyn1);
			dp1.add (int1);
			dp1.add (box1);
			dp1.add (inti1);
			inti1.add (cb1);
			inti1.add (rb1);
			inti1.add (sb1);
			dp1.setLayout(new BoxLayout(dp1, BoxLayout.PAGE_AXIS));

			JPanel int2 = new JPanel();
			JPanel inti2 = new JPanel();
			int2.add (label2);
			int2.add (dyn2);
			dp2.add (int2);
			dp2.add (box2);
			dp2.add (inti2);
			inti2.add (cb2);
			inti2.add (rb2);
			inti2.add (sb2);
			dp2.setLayout(new BoxLayout(dp2, BoxLayout.PAGE_AXIS));

			JPanel int3 = new JPanel();
			JPanel inti3 = new JPanel();
			int3.add (label3);
			int3.add (dyn3);
			dp3.add (int3);
			dp3.add (box3);
			dp3.add (inti3);
			inti3.add (cb3);
			inti3.add (rb3);
			inti3.add (sb3);
			dp3.setLayout(new BoxLayout(dp3, BoxLayout.PAGE_AXIS));

			JPanel int4 = new JPanel();
			JPanel inti4 = new JPanel();
			int4.add (label4);
			int4.add (dyn4);
			dp4.add (int4);
			dp4.add (box4);
			dp4.add (inti4);
			inti4.add (cb4);
			inti4.add (rb4);
			inti4.add (sb4);
			dp4.setLayout(new BoxLayout(dp4, BoxLayout.PAGE_AXIS));

			JPanel int5 = new JPanel();
			JPanel inti5 = new JPanel();
			int5.add (label5);
			int5.add (dyn5);
			dp5.add (int5);
			dp5.add (box5);
			dp5.add (inti5);
			inti5.add (cb5);
			inti5.add (rb5);
			inti5.add (sb5);
			dp5.setLayout(new BoxLayout(dp5, BoxLayout.PAGE_AXIS));

			add (dtp);
		}
		//index1 = party slot, index2 = inventory slot
		public void changePartyMember (int index1, int index2){
			if (sgd.dgs.myParty.party[index1]!=null && sgd.dgs.myDspheres.size()>index2){
				Dsphere temp = sgd.dgs.myParty.party[index1];
				sgd.dgs.myParty.party[index1]=sgd.dgs.myDspheres.remove(index2);
				sgd.dgs.myDspheres.add(0, temp);
			}else {
				sgd.dgs.myParty.party[index1]=sgd.dgs.myDspheres.remove(index2);

			}
			updateAll();
		}
		public void updatePartyLabels (){
			if (sgd.dgs.myParty.party[0]!=null){
				dyn0.setText(sgd.dgs.myParty.party[0].toString());
			}else {
				dyn0.setText("empty");
			}
			if (sgd.dgs.myParty.party[1]!=null){
				dyn1.setText(sgd.dgs.myParty.party[1].toString());
			}else {
				dyn1.setText("empty");
			}
			if (sgd.dgs.myParty.party[2]!=null){
				dyn2.setText(sgd.dgs.myParty.party[2].toString());
			}else {
				dyn2.setText("empty");
			}
			if (sgd.dgs.myParty.party[3]!=null){
				dyn3.setText(sgd.dgs.myParty.party[3].toString());
			}else {
				dyn3.setText("empty");
			}
			if (sgd.dgs.myParty.party[4]!=null){
				dyn4.setText(sgd.dgs.myParty.party[4].toString());
			}else {
				dyn4.setText("empty");
			}
			if (sgd.dgs.myParty.party[5]!=null){
				dyn5.setText(sgd.dgs.myParty.party[5].toString());
			}else {
				dyn5.setText("empty");
			}
		}
		public void updateComboBoxes (){
			resetList(box0);
			resetList(box1);
			resetList(box2);
			resetList(box3);
			resetList(box4);
			resetList(box5);
		}
		private void resetList (JComboBox box){
			box.removeAllItems();
			String [] lt = sgd.dgs.toStringDsphere();
			for (int i=0;i<lt.length;i++){
				box.addItem(lt[i]);
			}
		}
		public void updateAll () {
			sgd.updateInventory();
			updatePartyLabels();
			updateComboBoxes();
			ssf.updateDisplay();
		}
		public void actionPerformed(ActionEvent e) {
			if (gameRunning && !battleRunning){
				//selection in a combobox = change of sphere in party tab
				if (e.getSource()==sb0 && !battleRunning){
					changePartyMember (0,box0.getSelectedIndex());
				}
				if (e.getSource()==sb1 && !battleRunning){
					changePartyMember (1,box1.getSelectedIndex());
				}
				if (e.getSource()==sb2 && !battleRunning){
					changePartyMember (2,box2.getSelectedIndex());
				}
				if (e.getSource()==sb3 && !battleRunning){
					changePartyMember (3,box3.getSelectedIndex());
				}
				if (e.getSource()==sb4 && !battleRunning){
					changePartyMember (4,box4.getSelectedIndex());
				}
				if (e.getSource()==sb5 && !battleRunning){
					changePartyMember (5,box5.getSelectedIndex());
				}

				//clears a sphere from the party into the inventory
				if (e.getSource()==cb0 && sgd.dgs.myParty.party[0]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[0]);
					sgd.dgs.myParty.party[0]=null;
					updateAll();
				}
				if (e.getSource()==cb1 && sgd.dgs.myParty.party[1]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[1]);
					sgd.dgs.myParty.party[1]=null;
					updateAll();
				}
				if (e.getSource()==cb2 && sgd.dgs.myParty.party[2]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[2]);
					sgd.dgs.myParty.party[2]=null;
					updateAll();
				}
				if (e.getSource()==cb3 && sgd.dgs.myParty.party[3]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[3]);
					sgd.dgs.myParty.party[3]=null;
					updateAll();
				}
				if (e.getSource()==cb4 && sgd.dgs.myParty.party[4]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[4]);
					sgd.dgs.myParty.party[4]=null;
					updateAll();
				}
				if (e.getSource()==cb5 && sgd.dgs.myParty.party[5]!=null && !battleRunning){
					sgd.dgs.myDspheres.add(0, sgd.dgs.myParty.party[5]);
					sgd.dgs.myParty.party[5]=null;
					updateAll();
				}

				if (e.getSource()==rb0 && sgd.dgs.myParty.party[0]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[0].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[0]);
						updatePartyLabels();
					}
				}
				if (e.getSource()==rb1 && sgd.dgs.myParty.party[1]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[1].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[1]);
						updatePartyLabels();
					}
				}
				if (e.getSource()==rb2 && sgd.dgs.myParty.party[2]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[2].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[2]);
						updatePartyLabels();
					}
				}
				if (e.getSource()==rb3 && sgd.dgs.myParty.party[3]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[3].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[3]);
						updatePartyLabels();
					}
				}
				if (e.getSource()==rb4 && sgd.dgs.myParty.party[4]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[4].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[4]);
						updatePartyLabels();
					}
				}
				if (e.getSource()==rb5 && sgd.dgs.myParty.party[5]!=null){
					String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
					if (nname !=null){
						sgd.dgs.myParty.party[5].myName=nname;
						sgd.changeSelectedSphere(sgd.dgs.myParty.party[5]);
						updatePartyLabels();
					}
				}
			}
		}
	}
	class InventoryPanel extends JPanel implements ListSelectionListener {

		//two large text fields I think

		/*
		 * use tabs to organize:
		 * first jlist is upgrade dice
		 * second jlist is ability auras
		 * -show core descriptions 
		 * third jlist is sphere list
		 * fourth jlist is attack cores
		 * -this is a list of attack types and number possessed
		 * -at the bottom is the core assigner
		 */
		public final static int ATK_CORE_VAL = 250;
		SGDPanel sgd;
		JList sp, ud, aa, ak; //sphere upgradedice ability attack
		JPanel dp0,dp1,dp2,dp3;
		JTabbedPane dtp;
		JButton reprocess, sellDice, sellCores, sellAttack;

		public InventoryPanel (SGDPanel s) {
			scrapListener sl = new scrapListener();
			sgd = s;
			dtp = new JTabbedPane();

			dp0 = new JPanel ();
			dp1 = new JPanel ();
			dp2 = new JPanel ();
			dp3 = new JPanel ();

			/*
			 * add particular components here
			 */
			sp = new JList ();
			sp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			sp.setLayoutOrientation(JList.VERTICAL);
			sp.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane lsp = new JScrollPane(sp);
			lsp.setPreferredSize (new Dimension (200,300));
			dp0.add (lsp);
			reprocess = new JButton("Scrap Selected");
			reprocess.addActionListener (sl);
			dp0.add (reprocess);

			ud = new JList ();
			ud.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			ud.setLayoutOrientation(JList.VERTICAL);
			ud.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane dsp = new JScrollPane(ud);
			dsp.setPreferredSize (new Dimension (200,300));
			dp1.add (dsp);
			sellDice = new JButton("Sell Selected");
			sellDice.addActionListener (sl);
			dp1.add (sellDice);

			aa = new JList ();
			aa.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			aa.setLayoutOrientation(JList.VERTICAL);
			aa.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane asp = new JScrollPane(aa);
			asp.setPreferredSize (new Dimension (200,300));
			dp2.add (asp);
			sellCores = new JButton("Sell Selected");
			sellCores.addActionListener (sl);
			dp2.add (sellCores);

			ak = new JList ();
			ak.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			ak.setLayoutOrientation(JList.VERTICAL);
			ak.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane ksp = new JScrollPane(ak);
			ksp.setPreferredSize (new Dimension (200,300));
			dp3.add (ksp);
			sellAttack = new JButton("Sell Selected");
			sellAttack.addActionListener (sl);
			dp3.add (sellAttack);

			dp0.setPreferredSize(new Dimension(260,350));
			dp1.setPreferredSize(new Dimension(260,350));
			dp2.setPreferredSize(new Dimension(260,350));
			dp3.setPreferredSize(new Dimension(260,350));					

			//finish initializing inventory
			/*
			 * inventory consists of Jlist, sell/scrap button
			 */


			dtp.addTab("Spheres",null,dp0,"");
			dtp.addTab("Dice",null,dp1,"");
			dtp.addTab("Auras",null,dp2,"");
			dtp.addTab("Cores",null,dp3,"");

			add (dtp);
		}
		public void updateSpheres (String [] nlist) {
			//			dtp.remove (0);
			//			dp0 = new JPanel ();
			dp0.removeAll();
			sp = new JList (nlist);
			sp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			sp.setLayoutOrientation(JList.VERTICAL);
			sp.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane lsp = new JScrollPane(sp);
			lsp.setPreferredSize (new Dimension (200,300));
			dp0.add (lsp);
			dp0.add (reprocess);
			validate();
			repaint();
		}
		public void updateDice (String [] nlist) {
			dp1.removeAll();
			ud = new JList (nlist);
			ud.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			ud.setLayoutOrientation(JList.VERTICAL);
			ud.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane lsp = new JScrollPane(ud);
			lsp.setPreferredSize (new Dimension (200,300));
			dp1.add (lsp);
			dp1.add (sellDice);
			validate();
			repaint();
		}
		public void updateAuras (String [] nlist) {
			dp2.removeAll();
			aa = new JList (nlist);
			aa.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			aa.setLayoutOrientation(JList.VERTICAL);
			aa.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane lsp = new JScrollPane(aa);
			lsp.setPreferredSize (new Dimension (200,300));
			dp2.add (lsp);
			dp2.add (sellCores);
			validate();
			repaint();
		}
		public void updateCores (String [] nlist) {
			dp3.removeAll();
			ak = new JList (nlist);
			ak.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			ak.setLayoutOrientation(JList.VERTICAL);
			ak.getSelectionModel().addListSelectionListener(
					this);
			JScrollPane lsp = new JScrollPane(ak);
			lsp.setPreferredSize (new Dimension (200,300));
			dp3.add (lsp);
			dp3.add (sellAttack);
			validate();
			repaint();
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource()==sp.getSelectionModel()){
				selectedSphere = sgd.dgs.myDspheres.get(sp.getSelectionModel().getMinSelectionIndex());
				sgd.sdp.updateDisplay();
				ssf.updateDisplay();
			}
			if (e.getSource()==ud.getSelectionModel()){
				selectedDice = sgd.dgs.myUpgradeDice.get(ud.getSelectionModel().getMinSelectionIndex());
			}	
			if (e.getSource()==aa.getSelectionModel()){
				selectedACore = sgd.dgs.myAbilityCores.get(aa.getSelectionModel().getMinSelectionIndex());
			}	
			if (e.getSource()==ak.getSelectionModel()){
				selectedATKInd = ak.getSelectionModel().getMinSelectionIndex();
			}	
		}
		//this was badly coded, should have integrated into main private class
		private class scrapListener implements ActionListener {

			public scrapListener () {

			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==reprocess){
					if (selectedSphere != null && dgs.myDspheres.contains(selectedSphere)){
						int loot = selectedSphere.calculateLootVal();
						if (loot>ShopDisplayPanel.SPHERE_TAX_LEVEL){
							loot = (int) ((loot + 1.0-1.0) * ShopDisplayPanel.SALE_TAX);
						}
						dgs.myPlayer.changeScorePoints(loot);
						int lac = selectedSphere.calculateAtkCLoot(dgs.myParty);
						if (lac>-1){
							dgs.addAttackCore(lac, 1);
						}
						dgs.myDspheres.remove(selectedSphere);
						selectedSphere =null;
						updateAllLabels();
					}
				}
				if (e.getSource()==sellDice){
					if (selectedDice != null && dgs.myUpgradeDice.contains(selectedDice)){
						dgs.myPlayer.changeScorePoints(selectedDice.calculateSPValue());
						dgs.myUpgradeDice.remove(selectedDice);
						selectedDice=null;
						updateAllLabels();
					}
				}
				if (e.getSource()==sellCores){
					if (selectedACore != null && dgs.myAbilityCores.contains(selectedACore)){
						dgs.myPlayer.changeScorePoints(selectedACore.calculateSPValue());
						dgs.myAbilityCores.remove(selectedACore);
						selectedACore=null;
						updateAllLabels();
					}
				}
				if (e.getSource()==sellAttack){
					if (selectedATKInd>-1 && selectedATKInd<dgs.myAttackCores.length){
						if (dgs.myAttackCores[selectedATKInd]>0){
							dgs.myAttackCores[selectedATKInd]--;
							dgs.myPlayer.changeScorePoints(ATK_CORE_VAL);
							selectedATKInd = -1;
							updateAllLabels();
						}
					}
				}
			}
		}
	}
	private class DestinationSelectPanel extends JPanel implements ActionListener {

		/*
		 * the game state preserves the current destination id
		 * each tab displays a combobox of unlocked areas and a button to switch to that place
		 * button is often disabled eg when in battle
		 */
		public final static int RUINS = 10;
		public final static int WILDS = 20;
		public final static int CHALLENGE = 100;
		public final static int MIRROR = -10;
		public final static int ELEM = 1000;
		public final static int BLISS = 1000000;
		public final static int CHAOS = -99;
		public final static String PREFIX = "Destination: ";

		JComboBox box0,box1, box2, box3,box4,box5, box6;
		SGDPanel sgd;
		JButton travel;
		JLabel destDyn;
		ArrayList <DDestination> ruinL,wildL,chalL,mirrL,elemL,blisL,chaoL;

		public DestinationSelectPanel (SGDPanel s) {
			sgd = s;
			ruinL = new ArrayList <DDestination> ();
			ruinL.add(new DDestination(11,0,"Undercity Wastes"));
			ruinL.add(new DDestination(12,0,"Trash Hall"));
			ruinL.add(new DDestination(13,0,"Aluminum Cavern"));
			ruinL.add(new DDestination(14,0,"Bottomless Pit"));
			ruinL.add(new DDestination(15,0,"Silent Fall"));
			ruinL.add(new DDestination(16,0,"Scrap Mountain"));
			ruinL.add(new DDestination(17,0,"Babel Quarry"));
			ruinL.add(new DDestination(18,0,"Lightning Rod"));
			ruinL.add(new DDestination(19,0,"WreckLord Throne"));
			wildL = new ArrayList <DDestination> ();
			wildL.add(new DDestination(20,1,"Overland Wastes"));
			wildL.add(new DDestination(21,1,"Dwindling Fork"));
			wildL.add(new DDestination(22,1,"Unspanned Canyon"));
			wildL.add(new DDestination(23,1,"Barren Pass"));
			wildL.add(new DDestination(24,1,"Cobbled Highway"));
			wildL.add(new DDestination(25,1,"Crumbling Temple"));
			wildL.add(new DDestination(26,1,"Underland Road"));
			wildL.add(new DDestination(27,1,"Crest Lightway"));
			wildL.add(new DDestination(28,1,"Misty Foglands"));
			wildL.add(new DDestination(29,1,"Ghostly Shores"));
			wildL.add(new DDestination(30,1,"Ruined Castle"));
			chalL = new ArrayList <DDestination> ();
			chalL.add(new DDestination(100,2,"Cloud Firmament"));
			chalL.add(new DDestination(101,2,"Destiny Bridge"));
			chalL.add(new DDestination(102,2,"Diamond Gate"));
			chalL.add(new DDestination(103,2,"Heaven Abyss"));
			chalL.add(new DDestination(104,2,"Rainbow Highway"));
			chalL.add(new DDestination(105,2,"Starry Sky"));
			chalL.add(new DDestination(106,2,"Meteor Palace"));
			chalL.add(new DDestination(107,2,"Glass Labyrinth"));
			chalL.add(new DDestination(108,2,"Shattered Causeway"));
			chalL.add(new DDestination(109,2,"Empyreal Pillar"));
			chalL.add(new DDestination(110,2,"Illusion Palace"));
			mirrL = new ArrayList <DDestination> ();
			mirrL.add(new DDestination(-10,3,"Perfect Mirror"));
			mirrL.add(new DDestination(-11,3,"Warped Mirror"));
			mirrL.add(new DDestination(-12,3,"Concave Mirror"));
			elemL = new ArrayList <DDestination> ();
			elemL.add(new DDestination(1000,4,"First Ascent"));
			elemL.add(new DDestination(1010,4,"Second Ascent"));
			elemL.add(new DDestination(1015,4,"Interlude"));
			elemL.add(new DDestination(1020,4,"Third Ascent"));
			elemL.add(new DDestination(1030,4,"Final Ascent"));
			elemL.add(new DDestination(1040,4,"Pinnacle"));
			blisL = new ArrayList <DDestination> ();
			blisL.add(new DDestination(1000000,5,"Orbis Gates"));
			blisL.add(new DDestination(1000001,5,"Solar Pedestal"));
			blisL.add(new DDestination(1000002,5,"Elysia"));
			blisL.add(new DDestination(1000003,5,"Primordial Garden"));
			blisL.add(new DDestination(1000004,5,"Hall of Memories"));
			blisL.add(new DDestination(1000005,5,"Court of Judgement"));
			blisL.add(new DDestination(1000006,5,"Tower of Bliss"));
			blisL.add(new DDestination(1000007,5,"Celestial Seat"));
			blisL.add(new DDestination(-1,5,"Magistra"));
			chaoL = new ArrayList <DDestination> ();
			chaoL.add(new DDestination(-99,6,"Chaos"));
			chaoL.add(new DDestination(-990,6,"Pandemonium"));
			chaoL.add(new DDestination(-991,6,"Mkrzrkvr"));
			chaoL.add(new DDestination(-992,6,"Ogggklxu"));
			chaoL.add(new DDestination(-993,6,"Vilrghlu"));
			chaoL.add(new DDestination(-994,6,"Purgatory"));
			chaoL.add(new DDestination(-995,6,"Penitence"));
			chaoL.add(new DDestination(-996,6,"Abyss"));
			chaoL.add(new DDestination(-997,6,"Nameless"));
			/*
			 * 
			 */
			JPanel overpanel = new JPanel();
//			JScrollPane jsp = new JScrollPane (overpanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add (overpanel);

			JTabbedPane dtp = new JTabbedPane();

			JPanel dp0 = new JPanel ();
			dp0.setPreferredSize(new Dimension(450,50));
			JPanel dp1 = new JPanel ();
			JPanel dp2 = new JPanel ();
			JPanel dp3 = new JPanel ();
			JPanel dp4 = new JPanel ();
			JPanel dp5 = new JPanel ();
			JPanel dp6 = new JPanel ();
			dtp.addTab("Ruins",null,dp0,"");
			dtp.addTab("Wilds",null,dp1,"");
			dtp.addTab("Challenge",null,dp2,"");
			dtp.addTab("Mirror",null,dp3,"");
			dtp.addTab("Elemental",null,dp4,"");
			dtp.addTab("Bliss",null,dp5,"");
			dtp.addTab("Chaos",null,dp6,"");

			overpanel.add (dtp);

			destDyn = new JLabel ("Destination: *");
			JPanel destDis = new JPanel ();

			travel = new JButton ("Battle!");
			travel.addActionListener (this);

			box0 = new JComboBox (ruinL.toArray());
			box1 = new JComboBox (wildL.toArray());
			box2 = new JComboBox (chalL.toArray());
			box3 = new JComboBox (mirrL.toArray());
			box4 = new JComboBox (elemL.toArray());
			box5 = new JComboBox (blisL.toArray());
			box6 = new JComboBox (chaoL.toArray());

			box0.addActionListener (this);
			box1.addActionListener (this);
			box2.addActionListener (this);
			box3.addActionListener (this);
			box4.addActionListener (this);
			box5.addActionListener (this);
			box6.addActionListener (this);

			dp0.add (box0);
			dp1.add (box1);
			dp2.add (box2);
			dp3.add (box3);
			dp4.add (box4);
			dp5.add (box5);
			dp6.add (box6);

			destDis.add (destDyn);
			destDis.add (travel);
			destDis.setLayout(new BoxLayout(destDis, BoxLayout.Y_AXIS));
			overpanel.add (destDis);
			overpanel.setPreferredSize(new Dimension(470,150));
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==travel && sgd.gameRunning && !sgd.battleRunning){
				if (sgd.selectedDestination == CHAOS){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,chaoL.get(box6.getSelectedIndex()).generateDparty());						
					}
				}
				if (sgd.selectedDestination == MIRROR){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,mirrL.get(box3.getSelectedIndex()).generateDpartyMirror(sgd.dgs.myParty));
					}
				}
				if (sgd.selectedDestination>=RUINS && sgd.selectedDestination < WILDS){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,ruinL.get(box0.getSelectedIndex()).generateDparty());
					}
				}
				if (sgd.selectedDestination>=WILDS && sgd.selectedDestination < CHALLENGE){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,wildL.get(box1.getSelectedIndex()).generateDparty());
					}
				}
				if (sgd.selectedDestination>=CHALLENGE && sgd.selectedDestination < ELEM){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,chalL.get(box2.getSelectedIndex()).generateDparty());
					}
				}
				if (sgd.selectedDestination>=ELEM && sgd.selectedDestination<BLISS){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,elemL.get(box4.getSelectedIndex()).generateDparty());
					}
				}
				if (sgd.selectedDestination>=BLISS){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,blisL.get(box5.getSelectedIndex()).generateDparty());
					}
				}
				if (sgd.selectedDestination==-1){
					if (dgs.myParty.isValid()){
						sgd.bdp.initializeBattle(sgd.dgs.myParty,blisL.get(box5.getSelectedIndex()).generateDparty());
					}
				}
			}
			if (e.getSource()==box0){
				sgd.selectedDestination = ruinL.get(box0.getSelectedIndex()).dID;
				destDyn.setText (PREFIX+ruinL.get(box0.getSelectedIndex()).myName);
			}
			if (e.getSource()==box1){
				sgd.selectedDestination = wildL.get(box1.getSelectedIndex()).dID;
				destDyn.setText (PREFIX+wildL.get(box1.getSelectedIndex()).myName);
			}
			if (e.getSource()==box2){
				sgd.selectedDestination = chalL.get(box2.getSelectedIndex()).dID;
				destDyn.setText (PREFIX+chalL.get(box2.getSelectedIndex()).myName);
			}
			if (e.getSource()==box3){
				sgd.selectedDestination = MIRROR;
				destDyn.setText (PREFIX+"MIRROR");
			}
			if (e.getSource()==box4){
				sgd.selectedDestination = elemL.get(box4.getSelectedIndex()).dID;
				destDyn.setText (PREFIX+elemL.get(box4.getSelectedIndex()).myName);
			}
			if (e.getSource()==box5){
				sgd.selectedDestination = blisL.get(box5.getSelectedIndex()).dID;
				destDyn.setText (PREFIX+blisL.get(box5.getSelectedIndex()).myName);
			}
			if (e.getSource()==box6){
				sgd.selectedDestination = CHAOS;
				destDyn.setText (PREFIX+"CHAOS");
			}
		}

	}
	private class SphereDisplayPanel extends JPanel implements ActionListener {

		/*
		 * how to display the essential attributes of a sphere?
		 * Name
		 * Element
		 * Chassis
		 * Level
		 * Exp
		 * AbilityJComboboxes:
		 * -Stats
		 * -Dice
		 * Attack
		 */
		JLabel sndyn;
		JLabel sedyn;
		JLabel scdyn;
		JLabel sldyn;
		JLabel sxdyn;
		JLabel sadyn0;
		JLabel sadyn1;
		JLabel atdyn;
		JLabel abdyn;

		JComboBox sbox;
		JComboBox dbox;

		public SphereDisplayPanel () {
//			setMaximumSize (new Dimension (200,90));

			JTabbedPane dtp = new JTabbedPane();

			JPanel dp0 = new JPanel ();
			dp0.setPreferredSize(new Dimension(200,90));
			JPanel dp1 = new JPanel ();
			JPanel dp2 = new JPanel ();
			JPanel dp3 = new JPanel ();
			dtp.addTab("Info",null,dp0,"");
			dtp.addTab("Level",null,dp1,"");
			dtp.addTab("Stats",null,dp2,"");
			dtp.addTab("Ability",null,dp3,"");

			JLabel snlabel = new JLabel (" Name: ");
			JLabel selabel = new JLabel (" Element: ");
			JLabel sclabel = new JLabel (" Chassis: ");
			JLabel sllabel = new JLabel (" Level: ");
			JLabel sxlabel = new JLabel (" Experience: ");
			JLabel salabel0 = new JLabel (" Stats: ");
			JLabel salabel1 = new JLabel (" Dice: ");
			JLabel atlabel = new JLabel (" Attack: ");
			JLabel ablabel = new JLabel (" Ability: ");

			sndyn = new JLabel ("*");
			sedyn = new JLabel ("*");
			scdyn = new JLabel ("*");
			sldyn = new JLabel ("*");
			sxdyn = new JLabel ("*");
			sadyn0 = new JLabel ("*");
			sadyn1 = new JLabel ("*");
			atdyn = new JLabel ("*");
			abdyn = new JLabel ("*");

			sbox = new JComboBox (Dsphere.STATLIST);
			dbox = new JComboBox (Dsphere.STATLIST);

			sbox.addActionListener(this);
			dbox.addActionListener(this);

			//info
			dp0.setLayout (new GridLayout (3,2));
			dp0.add (snlabel);
			dp0.add (sndyn);
			dp0.add (selabel);
			dp0.add (sedyn);
			dp0.add (sclabel);
			dp0.add (scdyn);

			//level
			dp1.setLayout (new GridLayout (2,2));
			dp1.add (sllabel);
			dp1.add (sldyn);
			dp1.add (sxlabel);
			dp1.add (sxdyn);

			//stats
			dp2.setLayout (new GridLayout (2,3,10,0));
			dp2.add (salabel0);
			dp2.add (sbox);
			dp2.add (sadyn0);
			dp2.add (salabel1);
			dp2.add (dbox);
			dp2.add (sadyn1);

			//ability
			dp3.setLayout (new GridLayout (2,2));
			dp3.add (atlabel);
			dp3.add (atdyn);
			dp3.add (ablabel);
			dp3.add (abdyn);

			add (dtp);
		}
		public void updateDisplay () {
			sndyn.setText (selectedSphere.toString());
			sedyn.setText (Dsphere.ELEMNAMES[selectedSphere.eleID]);
			scdyn.setText (Dchassis.chnames[selectedSphere.chaID]);
			sldyn.setText (Integer.toString(selectedSphere.level));
			sxdyn.setText (Integer.toString(selectedSphere.exp));
			sadyn0.setText (Integer.toString(selectedSphere.stats[sbox.getSelectedIndex()]));
			sadyn1.setText (selectedSphere.dice[dbox.getSelectedIndex()].toString());
			atdyn.setText (Dsphere.ATKNAMES[selectedSphere.atkID]);
			abdyn.setText (selectedSphere.ability.toString());
		}
		public void updateDisplayedStat () {
			if (selectedSphere != null){
				sadyn0.setText (Integer.toString(selectedSphere.stats[sbox.getSelectedIndex()]));	
			}
		}
		public void updateDisplayedDice () {
			if (selectedSphere != null){
				sadyn1.setText (selectedSphere.dice[dbox.getSelectedIndex()].toString());				
			}
		}
		//combobox listener that updates the stats and dice
		public void actionPerformed(ActionEvent e) {
			if (gameRunning){
				if (e.getSource()==sbox){
					updateDisplayedStat();
				}
				if (e.getSource()==dbox){
					updateDisplayedDice();
				}
			}
		}
	}
	private class PlayerDisplayPanel extends JPanel implements ActionListener {

		/*
		 * only important player traits are:
		 * name (constant)
		 * scorepoints (updates via specific events)
		 * masteries (updates via combobox)
		 * 
		 * mastery displayed will be visible via a label and a combobox
		 * this simple interface can be upgraded later assuming time allows
		 */
		SGDPanel sgd;

		JLabel pndyn;
		JLabel spdyn;
		JLabel evdyn;
		JLabel avdyn;
		JLabel svdyn;
		JLabel fvdyn;

		JComboBox ebox;
		JComboBox abox;
		JComboBox skbox;
		JComboBox scbox;

		public PlayerDisplayPanel (SGDPanel s) {
			sgd = s;
			setLayout(new GridLayout(6,3,20,0));
			JLabel pnlabel = new JLabel ("Name: ");
			JLabel splabel = new JLabel ("Scorepoints: ");
			JLabel emlabel = new JLabel ("Elem Mastery: ");
			JLabel amlabel = new JLabel ("Attrib Mastery: ");
			JLabel smlabel = new JLabel ("Skill Mastery: ");
			JLabel fmlabel = new JLabel ("Finder Mastery: ");

			pndyn = new JLabel ("*");
			spdyn = new JLabel ("*");
			evdyn = new JLabel ("*"); //changes depending on the value of mastery selected
			avdyn = new JLabel ("*");
			svdyn = new JLabel ("*");
			fvdyn = new JLabel ("*");

			JLabel leblanc = new JLabel ("      ");
			JLabel leblanc2 = new JLabel ("      ");

			ebox = new JComboBox (Collective.elemNames);
			abox = new JComboBox (Collective.attNames);
			skbox = new JComboBox (Collective.skilNames);
			scbox = new JComboBox (Collective.scavNames);

			ebox.addActionListener(this);
			abox.addActionListener(this);
			skbox.addActionListener(this);
			scbox.addActionListener(this);

			add (pnlabel);
			add (leblanc);
			add (pndyn);
			add (splabel);
			add (leblanc2);
			add (spdyn);

			add (emlabel);
			add (ebox);
			add (evdyn);
			add (amlabel);
			add (abox);
			add (avdyn);
			add (smlabel);
			add (skbox);
			add (svdyn);
			add (fmlabel);
			add (scbox);
			add (fvdyn);
		}
		public void updateLabels () {
			pndyn.setText (sgd.dgs.myPlayer.myName);
			spdyn.setText (Integer.toString(sgd.dgs.myPlayer.scorepoints));
			evdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemMastery[ebox.getSelectedIndex()]));
			avdyn.setText (Integer.toString(sgd.dgs.myPlayer.attMastery[abox.getSelectedIndex()]));
			svdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilMastery[skbox.getSelectedIndex()]));
			fvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavMastery[scbox.getSelectedIndex()]));
		}
		public void updateEMastery(){
			evdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemMastery[ebox.getSelectedIndex()]));
		}
		public void updateAMastery(){
			avdyn.setText (Integer.toString(sgd.dgs.myPlayer.attMastery[abox.getSelectedIndex()]));
		}
		public void updateSkMastery(){
			svdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilMastery[skbox.getSelectedIndex()]));
		}
		public void updateScMastery(){
			fvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavMastery[scbox.getSelectedIndex()]));
		}
		public void actionPerformed(ActionEvent e) {
			if (gameRunning){
				if (e.getSource()==ebox){
					updateEMastery();
				}
				if (e.getSource()==abox){
					updateAMastery();
				}
				if (e.getSource()==skbox){
					updateSkMastery();
				}
				if (e.getSource()==scbox){
					updateScMastery();
				}
			}
		}
	}
	class BattleDisplayPanel extends JPanel implements ActionListener {

		private final static boolean ACTIVATE = true;
		private final static boolean DEACTIVATE = false;
		private final static boolean PLAYER = true;
		private final static boolean ENEMY = false;

		private final static int DESTRUCTION_PENALTY = 10;
		private final static int UNRESOLVED = 0;
		private final static int P_WIN = 1;
		private final static int E_WIN = -1;
		private final static int STALEMATE = 10;

		//normally black with disabled buttons
		//six panels, two labels, one textfield

		/*
		 * procedure for specifying battles
		 * 
		 * Player selects a destination in the destination select
		 * Player selects Battle!
		 * Encounter party, Encounter collective, and Encounter spheres
		 * are taken from the location (collective and sphere data are within the party object)
		 * Location objects specify collective traits and avail spheres
		 * 
		 * Battle is initialized with the player and the synthesized party
		 * Upon resolution, text displays the player's winnings
		 * The buttons are disabled
		 * 
		 * The battlefield is cleared
		 * Player status is updated
		 * Player returned to lobby
		 */

		JTextArea battleLog; //tracks battle actions
		JScrollPane jsp;
		JLabel p1Int, p2Int; //integrity for each player
		JButton confirmAction, fleeBattle;
		//radio buttons for action selection
		JRadioButton [] atkArray;
		JRadioButton [] abyArray;
		//list of BattlePanels where spheres are displayed
		BattlePanel [] bpArray;
		//index of bpArray where enemy forces begin
		int eindex;
		//indices of the player's selected actions
		int attackIndex;
		int abilityIndex;
		//participating parties
		Dparty p1, p2;
		//integrity of each collective
		int p1cInt, p2cInt;
		//state variables
		boolean battleResolving, p1nullified, p2nullified;

		public BattleDisplayPanel () {
			setBackground(Color.black);
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		}
		/*
		 * initialize with two parties
		 * when this happens, reenable buttons
		 * upon battle victory, the center shows loot
		 */
		public void initializeBattle (Dparty one, Dparty two){
			blf.clearText();
			battleRunning = true;
			battleResolving = false;
			p1nullified = false;
			p2nullified = false;
			p1 = one;
			p2 = two;
			p1cInt = p1.calculateIntegrity();
			p2cInt = p2.calculateIntegrity();
			attackIndex=-1;
			abilityIndex=-1;
			p1.resetPartyInstancedStats();
			p2.resetPartyInstancedStats();
			/*
			 * Initializing superstructure jpanels
			 */

			//superstructure panels to display player and enemy units respectively
			JPanel ppanel, epanel;
			//			JPanel dp0, dp1, dp2, dp3, dp4, dp5, dp6, dp7, dp8, dp9,dp10,dp11;
			JPanel [] dpArray;

			ppanel = new JPanel();
			epanel = new JPanel();

			ppanel.setLayout (new GridLayout (0,3));
			epanel.setLayout (new GridLayout (0,3));

			/*
			 * Initializing subcomponents
			 */

			battleLog = new JTextArea("");
			battleLog.setEditable(false);
			battleLog.setLineWrap(true);
			jsp = new JScrollPane(battleLog,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			p1Int = new JLabel ("PlayerIntegrity: ");
			p2Int = new JLabel ("EnemyIntegrity: ");
			confirmAction = new JButton ("Resolve");
			confirmAction.addActionListener(this);
			fleeBattle = new JButton ("Retreat");
			fleeBattle.addActionListener(this);
			/*
			 * Initializing sphere panels
			 */

			//pcnt is count of player units, ecnt count of enemy units
			int pcnt =0,ecnt=0;

			//determine the amount of spheres in battle
			for (int i=0;i<one.party.length;i++){
				if (one.party[i]!=null){
					pcnt++;
				}
			}
			for (int i=0;i<two.party.length;i++){
				if (two.party[i]!=null){
					ecnt++;
				}
			}
			//initialize the battlepanel and the larger displaypanel array
			bpArray = new BattlePanel [ecnt+pcnt];
			dpArray = new JPanel [ecnt+pcnt];
			eindex = 0; //counts the amount of allied units to determine where the first enemy is
			int added = 0;//counts added panels
			//add allied battlepanels
			for (int i=0;i<one.party.length && eindex <= pcnt;i++){
				if (one.party[i]!=null){
					bpArray [eindex] = new BattlePanel(one.party[i]);
					eindex++;
					//					System.out.println(i+" added "+eindex+" "+pcnt);
				}
			}
			//add enemy battlepanels
			for (int i=0;i<two.party.length && added < ecnt;i++){
				if (two.party[i]!=null){
					bpArray [added+eindex] = new BattlePanel(two.party[i]);
					added++;
					//					System.out.println(i+" added "+eindex+" "+ecnt+" "+added);
				}
			}
			//initialize displaypanels
			for (int i=0;i<dpArray.length;i++){
				dpArray[i]=new JPanel();
			}
			//initialize player command radio buttons
			atkArray = new JRadioButton [eindex];
			abyArray = new JRadioButton [eindex];
			for (int i=0;i<eindex;i++){
				//				System.out.println(bpArray[i]);
				atkArray[i]=new JRadioButton(Dsphere.ATKNAMES[bpArray[i].myDsphere.atkID]);
				abyArray[i]=new JRadioButton(bpArray[i].myDsphere.ability.toString());
				atkArray[i].addActionListener(this);
				abyArray[i].addActionListener(this);
			}
			ButtonGroup atkg = new ButtonGroup ();
			ButtonGroup abyg = new ButtonGroup ();
			for (int i=0;i<atkArray.length;i++){
				atkg.add(atkArray[i]);
			}
			for (int i=0;i<abyArray.length;i++){
				abyg.add(abyArray[i]);
			}
			/*
			 * Initializing putting components together
			 */
			added = 0;
			for (int i=0;i<one.party.length;i++){
				if (one.party[i]!=null){
					dpArray[added].add (bpArray[added]);
					JPanel jp = new JPanel();
					jp.add (atkArray[added]);
					jp.add (abyArray[added]);
					dpArray[added].add (jp);
					dpArray[added].setLayout(new BoxLayout(dpArray[added], BoxLayout.PAGE_AXIS));
					ppanel.add (dpArray[added]);
					added++;
				}
			}
			added = 0;
			for (int i=0;i<two.party.length;i++){
				if (two.party[i]!=null){
					dpArray[added+eindex].add (bpArray[added+eindex]);
					dpArray[added+eindex].setLayout(new BoxLayout(dpArray[added+eindex], BoxLayout.PAGE_AXIS));
					epanel.add (dpArray[added+eindex]);	
					added++;
				}
			}
			add (epanel);
			JPanel midpanel = new JPanel();
			midpanel.add (p1Int);
			midpanel.add (confirmAction);
			midpanel.add (fleeBattle);
			midpanel.add (p2Int);			
			add (midpanel);
			add (jsp);
			add (ppanel);

			ppanel.setMaximumSize (new Dimension (480,270));
			epanel.setMaximumSize (new Dimension (480,190));
			midpanel.setMaximumSize (new Dimension (480,30));
			jsp.setMaximumSize (new Dimension (480,50));

			setMaximumSize (new Dimension (482,555));

			updateDisplay();
			battleLog.append("Battle begins!\n");
		}
		public void updateDisplay () {
			for (int i=0;i<bpArray.length;i++){
				bpArray[i].updateDisplay();
			}
			p1Int.setText("PlayerIntegrity: "+p1cInt);
			p2Int.setText("EnemyIntegrity: "+p2cInt);
		}
		public void processTurn () {
			battleResolving = true;
			//enemy actions are completely random
			Dsphere [] fhm = p2.findHasMagic();
			//default enemy sphere
			Dsphere eauser=bpArray[bpArray.length-1].myDsphere;
			Dsphere eatker=p2.getRandomPartyMember();
			Dsphere patker=bpArray[attackIndex].myDsphere;
			if (fhm.length>0){
				eauser = fhm[rand.nextInt(fhm.length)];				
			}
			Dsphere pauser = bpArray[abilityIndex].myDsphere;
			if (checkPriority(pauser, eauser)){
				resolveAbility (pauser,PLAYER);
				resolveAbility (eauser,ENEMY);
			} else {
				resolveAbility (eauser,ENEMY);
				resolveAbility (pauser,PLAYER);
			}
			if (checkPriority(patker, eatker)){
				resolveAttack (patker,PLAYER);
				resolveAttack (eatker,ENEMY);
			} else {
				resolveAttack (eatker,ENEMY);
				resolveAttack (patker,PLAYER);
			}
			processTurnEnd();
			updateDisplay();
			//insert a timer
			if (checkBattleState() != UNRESOLVED){
				resolveBattle (checkBattleState(),false);
			}
			battleResolving = false;
		}
		private void processTurnEnd (){
			p1.gainTurnEXP();
			p2.gainTurnEXP();
			printDivider();
			blf.updateText();
		}
		private boolean checkPriority (Dsphere one, Dsphere two){
			if (one.getStat(Dsphere.PRIORITY)>two.getStat(Dsphere.PRIORITY)){
				return true;
			} else if (one.getStat(Dsphere.PRIORITY)<two.getStat(Dsphere.PRIORITY)){
				return false;
			} else {
				return rand.nextBoolean();
			}
		}
		//use the boolean to determine whether the ability is activated or deactivated
		//THIS IS DEAD CODE
		//output to the log from this method
		public void resolveAbility (Dsphere user, boolean activate,boolean ally) {
			user.attrition++;
			//
			if (ally && user.curr_int>0){

			} else if (user.curr_int>0) {
				//within the first 6 abilities
				if (user.ability.powerID>=Ability.BOOST_AURA_INDEX && user.ability.powerID<Ability.BASIC_AURA_INDEX){
					if (activate && user.payAbilityCost()){


					} else {
						//temporary boost
						if (user.ability.powerID<Ability.BOOST_AURA_INDEX && user.payAbilityCost()){
							if (user.ability.powerID!=2){

							} else {

							}
						}
					}
				}
			}
		}
		//the boolean refers to whether the user is the player or not
		public void resolveAbility (Dsphere user, boolean player){
			if (user.curr_int>0 && p1.checkActive() && p2.checkActive()){
				user.attrition++;
				user.gainAttackExperience();
				printAbilityAuraLog (user,user.ability.powerID,PLAYER);
				//might
				if (user.ability.powerID==0){
					if (player && user.payAbilityCost()){
						p1.changePartyStatBoosts(Dsphere.OFFENSE, user.getAbilityBoostVal());
					} else if (user.payAbilityCost()) {
						p2.changePartyStatBoosts(Dsphere.OFFENSE, user.getAbilityBoostVal());
					}
				}
				//barrier
				if (user.ability.powerID==1){
					if (player && user.payAbilityCost()){
						p1.changePartyStatBoosts(Dsphere.DEFENSE, user.getAbilityBoostVal());
					} else if (user.payAbilityCost()) {
						p2.changePartyStatBoosts(Dsphere.DEFENSE, user.getAbilityBoostVal());
					}
				}
				//burst
				if (user.ability.powerID==2){
					if (player && user.payAbilityCost()){
						resolveAbilityAttack(user,PLAYER,0,Dsphere.IGNOREDEF,Dsphere.ISMAGIC);
					} else if (user.payAbilityCost()) {
						resolveAbilityAttack(user,ENEMY,0,Dsphere.IGNOREDEF,Dsphere.ISMAGIC);
					}	
				}
				//blitz
				if (user.ability.powerID==3){
					if (player && user.payAbilityCost()){
						p1.changePartyStatBoosts(Dsphere.PRIORITY, user.getAbilityBoostVal());
						resolveAbilityAttack(user,PLAYER,1,!Dsphere.IGNOREDEF,!Dsphere.ISMAGIC);
					} else if (user.payAbilityCost()) {
						p2.changePartyStatBoosts(Dsphere.PRIORITY, user.getAbilityBoostVal());
						resolveAbilityAttack(user,ENEMY,1,!Dsphere.IGNOREDEF,!Dsphere.ISMAGIC);
					}	
				}
				//regen
				if (user.ability.powerID==4){
					if (player && user.payAbilityCost()){
						p1.healPartyInt(user.getAbilityBoostVal());
					} else if (user.payAbilityCost()) {
						p2.healPartyInt(user.getAbilityBoostVal());
					}	
				}
				//forge
				if (user.ability.powerID==5){
					if (player && user.payAbilityCost()){
						p1.healPartyMag(user.getAbilityBoostVal(),user);
					} else if (user.payAbilityCost()) {
						p2.healPartyMag(user.getAbilityBoostVal(),user);
					}	
				}
			}
		}
		/**
		 * Used to resolve abilities that act as attacks
		 * aid tracks for additional complications in the method such as bonus damage or base attack
		 */
		public void resolveAbilityAttack (Dsphere attacker, boolean player, int aid,boolean ignoreDef, boolean isMagic){
			Dsphere victim =null;
			//default is a ignoredef magic attack all (BURST)
			if (aid ==0){
				if (player){
					for (int i=0;i<p2.party.length;i++){
						if (p2.party[i]!=null && p2.party[i].isNotDisabled()){
							victim = p2.party[i]; 
							int dmg = victim.takeDamage(attacker, p1, p2, ignoreDef, isMagic);
							printAbilityDamageLog (dmg, victim, attacker);
							checkForKill (attacker, victim);
						}
					}
				} else {
					for (int i=0;i<p1.party.length;i++){
						if (p1.party[i]!=null && p1.party[i].isNotDisabled()){
							victim = p1.party[i]; 
							int dmg = victim.takeDamage(attacker, p2, p1, ignoreDef, isMagic);
							printAbilityDamageLog (dmg, victim, attacker);
							checkForKill (attacker, victim);
						}
					}
				}			
			}
			if (aid ==1){
				if (player){
					for (int i=0;i<p2.party.length;i++){
						if (p2.party[i]!=null && p2.party[i].isNotDisabled() && rand.nextBoolean()){
							victim = p2.party[i]; 
							int dmg = victim.takeDamage(attacker, p1, p2, ignoreDef, isMagic);
							printAbilityDamageLog (dmg, victim, attacker);
							checkForKill (attacker, victim);
						}
					}
				} else {
					for (int i=0;i<p1.party.length;i++){
						if (p1.party[i]!=null && p1.party[i].isNotDisabled() && rand.nextBoolean()){
							victim = p1.party[i]; 
							int dmg = victim.takeDamage(attacker, p2, p1, ignoreDef, isMagic);
							printAbilityDamageLog (dmg, victim, attacker);
							checkForKill (attacker, victim);
						}
					}
				}			
			}
		}
		//output to the log from this method
		public void resolveAttack (Dsphere attacker, boolean player) {
			if (attacker.curr_int>0 && p1.checkActive() && p2.checkActive()){
				boolean randAttack = false;
				attacker.gainTurnAttrition();
				attacker.gainAttackExperience();
				Dsphere victim =null;
				//random attack randomly selects another attack
				if (attacker.atkID==DAttack.RANDOM_A){
					attacker.atkID=rand.nextInt(DAttack.ATKNAMES.length);
					randAttack = true;
				}
				if (attacker.atkID==DAttack.NULLIFY_A){
					if (player){
						p2.resetPartyStatChanges();
					} else {
						p1.resetPartyStatChanges();
					}
				}
				if (attacker.atkID==DAttack.BARRAGE_A){
					int rounds = rand.nextInt(4)+1;
					while (rounds>0 && p1.checkActive() && p2.checkActive()){
						if (player){
							victim = p2.getRandomPartyMember(); 
							int dmg = victim.takeDamage(attacker, p1, p2,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
							printDamageLog (dmg, victim, attacker);
						} else {
							victim = p1.getRandomPartyMember(); 
							int dmg = victim.takeDamage(attacker, p2, p1,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
							printDamageLog (dmg, victim, attacker);
						}
						checkForKill (attacker, victim);
						rounds--;
					}
				}
				if (attacker.atkID==DAttack.BASIC_A){
					if (player){
						victim = p2.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p1, p2,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					} else {
						victim = p1.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p2, p1,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					}
					checkForKill (attacker, victim);
				}
				if (attacker.atkID==DAttack.ASSAULT_A ||
						attacker.atkID==DAttack.NULLIFY_A ||
						attacker.atkID==DAttack.SUBDUE_A){
					if (player){
						victim = p2.getHighestDamage(attacker, p1, !Dsphere.IGNOREDEF, !Dsphere.ISMAGIC); 
						int dmg = victim.takeDamage(attacker, p1, p2,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					} else {
						victim = p1.getHighestDamage(attacker, p2, !Dsphere.IGNOREDEF, !Dsphere.ISMAGIC); 
						int dmg = victim.takeDamage(attacker, p2, p1,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					}
					checkForKill (attacker, victim);
				}
				if (attacker.atkID==DAttack.SPIRIT_A){
					if (player){
						victim = p2.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p1, p2,Dsphere.IGNOREDEF, Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					} else {
						victim = p1.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p2, p1,Dsphere.IGNOREDEF, Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					}
					checkForKill (attacker, victim);
				}
				if (attacker.atkID==DAttack.CRITICAL_A){
					if (player){
						victim = p2.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p1, p2,Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					} else {
						victim = p1.getRandomPartyMember(); 
						int dmg = victim.takeDamage(attacker, p2, p1,Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
						printDamageLog (dmg, victim, attacker);
					}
					checkForKill (attacker, victim);
				}
				if (attacker.atkID==DAttack.WAVE_A ||
						attacker.atkID==DAttack.RANDOM_A){
					if (player){
						for (int i=0;i<p2.party.length;i++){
							if (p2.party[i]!=null && p2.party[i].isNotDisabled()){
								victim = p2.party[i]; 
								int dmg = victim.takeDamage(attacker, p1, p2, !Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
								printDamageLog (dmg, victim, attacker);
								checkForKill (attacker, victim);
							}
						}
					} else {
						for (int i=0;i<p1.party.length;i++){
							if (p1.party[i]!=null && p1.party[i].isNotDisabled()){
								victim = p1.party[i]; 
								int dmg = victim.takeDamage(attacker, p2, p1,!Dsphere.IGNOREDEF, !Dsphere.ISMAGIC);
								printDamageLog (dmg, victim, attacker);
								checkForKill (attacker, victim);
							}
						}
					}
				}
				if (randAttack){
					attacker.atkID=DAttack.RANDOM_A;
				}
			}
		}
		/*
		 * battle resolution 
		 */
		public void printDamageLog (int dmg, Dsphere victim, Dsphere attacker){
			if (dmg>0){
				battleLog.append(victim.toString()+" takes "+dmg+" damage from "+attacker.toString()+"'s "+Dsphere.ATKNAMES[attacker.atkID]+" attack!"+"\n");						
			} else {
				battleLog.append(attacker.toString()+" misses "+victim.toString()+"!"+"\n");
			}
		}
		public void printAbilityDamageLog (int dmg, Dsphere victim, Dsphere attacker){
			if (dmg>0){
				battleLog.append(victim.toString()+" takes "+dmg+" damage from "+attacker.toString()+"'s "+Ability.POWERS[attacker.ability.powerID]+" ability!"+"\n");						
			} else {
				battleLog.append(attacker.toString()+" misses "+victim.toString()+"!"+"\n");
			}
		}
		public void printAbilityAuraLog (Dsphere user, int aid, boolean player){
			//specific auras go here
			if (user.ability.powerID==-99){

			} else { //default auras just display user and name
				if (player){
					battleLog.append(user.toString()+" activates "+Ability.POWERS[aid]+"!"+"\n");
				} else {
					battleLog.append(user.toString()+" activates "+Ability.POWERS[aid]+"!"+"\n");
				}
			}
		}
		public void printDivider (){
			battleLog.append("* * * * *"+"\n");
		}
		private void checkForKill (Dsphere attacker, Dsphere victim){
			if (victim != null && victim.curr_int <=0){
				attacker.gainKillExperience();
				battleLog.append(attacker.toString()+" destroys "+victim.toString()+"!"+"\n");
			}
		}
		public void resolveBattle (int battleState, boolean fled) {
			if (fled){
				battleLog.append("You flee the battle!"+"\n");
				resolveEXPGain ();
				resolveAttritionDmg ();
			} else {
				if (battleState == E_WIN){
					battleLog.append("You are defeated!"+"\n");
				}
				//
				if (battleState == P_WIN || battleState == STALEMATE){
					resolveEXPGain ();
					resolveAttritionDmg ();
					int spgain = (int) ((1+p1.cl.scavMastery[0]*0.1)* (p1.calculateSPLoot()+p2.calculateSPLoot())/DESTRUCTION_PENALTY);
					//check to see if attack cores are gained
					int [] acLoot = p2.calculateAtkCLoot(p1);
					boolean acGained = false;
					for (int i=0;i<acLoot.length;i++){
						if (acLoot[i]>0){
							acGained = true;
						}
					}
					ArrayList <Dsphere> winnings = p2.calculateDSLoot();
					Dsphere.addLootDsphere(winnings, p2, dgs.myPlayer);
					//add won spheres
					if (winnings.size()>0){
						String winlist="";
						for (int i=0;i<winnings.size();i++){
							winlist = winlist+winnings.get(i).toString();
						}
						battleLog.append ("You acquire "+winlist+"\n");
						JOptionPane.showMessageDialog(null, "You acquire "+winlist+"\n");
						dgs.myDspheres.addAll(winnings);
					}
					//add won cores
					if (acGained){
						battleLog.append ("You acquire attack cores!"+"\n");
						JOptionPane.showMessageDialog(null, "You acquire attack cores!"+"\n");
						for (int i=0;i<acLoot.length;i++){
							if (acLoot[i]>0){
								dgs.addAttackCore(i, acLoot[i]);
							}
						}
					}
					battleLog.append("You gain "+spgain+"!"+"\n");
					JOptionPane.showMessageDialog(null, "You gain "+spgain+"!"+"\n");
					dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints+spgain;
				}
			}
			blf.updateText();
			p1.pruneParty();
			wipePanel ();
			updateAllLabels();
		}
		public void resolveEXPGain (){
			ArrayList <Dsphere> lvled = p1.resolveBattleEXP();
			if (lvled.size()>0){
				for (int i=0;i<lvled.size();i++){
					battleLog.append(lvled.get(i).toString()+" has leveled!"+"\n");
					JOptionPane.showMessageDialog(null, lvled.get(i).toString()+" has leveled!");
				}
			}
		}
		public void resolveAttritionDmg () {
			p1.resolveAttrition();
			p2.resolveAttrition();
		}
		public void wipePanel (){
			removeAll();
			battleRunning = false;
			revalidate();
			repaint();
		}
		public int checkBattleState () {
			int result = UNRESOLVED;
			if (!p1.checkActive() && p2.checkActive()){
				result = E_WIN;
			}
			if (!p1.checkActive() && !p2.checkActive()){
				result = STALEMATE;
			}
			if (p1.checkActive() && !p2.checkActive()){
				result = P_WIN;
			}
			return result;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==fleeBattle){
				resolveBattle (E_WIN,true);
			}
			if (e.getSource()==confirmAction){
				if (attackIndex>-1 && abilityIndex >-1)
					processTurn();
			}
			//radiobutton listeners
			if (abyArray.length>=1 && e.getSource()==atkArray[0]){
				attackIndex = 0;
			}
			if (abyArray.length>=2 && e.getSource()==atkArray[1]){
				attackIndex = 1;
			}
			if (abyArray.length>=3 && e.getSource()==atkArray[2]){
				attackIndex = 2;
			}
			if (abyArray.length>=4 && e.getSource()==atkArray[3]){
				attackIndex = 3;
			}
			if (abyArray.length>=5 && e.getSource()==atkArray[4]){
				attackIndex = 4;
			}
			if (abyArray.length>=6 && e.getSource()==atkArray[5]){
				attackIndex = 5;
			}

			if (abyArray.length>=1 && e.getSource()==abyArray[0]){
				abilityIndex = 0;
			}
			if (abyArray.length>=2 && e.getSource()==abyArray[1]){
				abilityIndex = 1;
			}
			if (abyArray.length>=3 && e.getSource()==abyArray[2]){
				abilityIndex = 2;
			}
			if (abyArray.length>=4 && e.getSource()==abyArray[3]){
				abilityIndex = 3;
			}
			if (abyArray.length>=5 && e.getSource()==abyArray[4]){
				abilityIndex = 4;
			}
			if (abyArray.length>=6 && e.getSource()==abyArray[5]){
				abilityIndex = 5;
			}
			if (abyArray.length>=7 && e.getSource()==abyArray[6]){
				abilityIndex = 6;
			}
		}
	}
	private class ShopDisplayPanel extends JPanel implements ActionListener {

		public static final int SPHERE_TAX_LEVEL = 1000;
		public static final int SPHERE_CATALYST_COST = 50;
		public static final double SALE_TAX = 0.50;
		
		JButton buyRandomSphere, refreshLevel, showBattleLog,
		purchaseLevel, purchasePlayerEXP, purchaseDice, purchaseAbility,
		changeAttack, customizeSphere, buyCustomSphere;
		SGDPanel sgd;

		public ShopDisplayPanel (SGDPanel s) {
			sgd = s;
			setMaximumSize (new Dimension (100,400));
			setLayout (new BoxLayout(this, BoxLayout.PAGE_AXIS));
			JPanel midpanel = new JPanel ();
			midpanel.setLayout (new BoxLayout(midpanel, BoxLayout.PAGE_AXIS));
			JScrollPane jsp = new JScrollPane(midpanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			buyRandomSphere = new JButton ("Buy Random Sphere");
			buyRandomSphere.addActionListener (this);
			refreshLevel = new JButton ("Refresh Levels");
			refreshLevel.addActionListener (this);
			showBattleLog = new JButton ("Show Battle Log");
			showBattleLog.addActionListener (this);
			purchaseLevel = new JButton ("Purchase Sphere Levels");
			purchaseLevel.addActionListener (this);
			purchasePlayerEXP = new JButton ("Purchase Player EXP");
			purchasePlayerEXP.addActionListener (this);
			purchaseDice = new JButton ("Purchase Dice");
			purchaseDice.addActionListener (this);
			purchaseAbility = new JButton ("Purchase Ability");
			purchaseAbility.addActionListener (this);
			changeAttack = new JButton ("Purchase Attack");
			changeAttack.addActionListener (this);
			customizeSphere = new JButton ("Customize Sphere");
			customizeSphere.addActionListener (this);
			buyCustomSphere = new JButton ("Buy Custom Sphere");
			buyCustomSphere.addActionListener (this);

			midpanel.add (showBattleLog);
			midpanel.add (buyRandomSphere);
//			midpanel.add (buyCustomSphere);
			midpanel.add (customizeSphere);
			midpanel.add (refreshLevel);
			midpanel.add (purchaseLevel);
			midpanel.add (purchasePlayerEXP);
			midpanel.add (purchaseDice);
			midpanel.add (purchaseAbility);
//			midpanel.add (changeAttack);
			add (jsp);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==buyRandomSphere){
				if (gameRunning && !battleRunning){
					String refs = JOptionPane.showInputDialog("How many scorepoints will you spend? Note: " +
							"an additional "+SPHERE_CATALYST_COST+" scorepoints are required to catalyze the creation of a sphere.");
					if (refs != null){
						try {
							int ref = Integer.parseInt(refs);
							if (ref > 0){
								if (dgs.myPlayer.scorepoints>ref+SPHERE_CATALYST_COST){
									dgs.myPlayer.changeScorePoints(-ref-SPHERE_CATALYST_COST);
									dgs.myDspheres.add(new Dsphere (ref));
								} 
//								else {
//									ref = dgs.myPlayer.scorepoints;
//									dgs.myPlayer.scorepoints=0;
//									dgs.myDspheres.add(new Dsphere (ref));
//								}
							} 
						} catch (Exception ex){
							System.out.println("Bad input");
						}
					}
					updateAllLabels();
				}
			}
			if (e.getSource()==refreshLevel){
				if (gameRunning && !battleRunning &&  rlf.inferSelectedSphere()){
					rlf.showMe();
				}
			}
			if (e.getSource()==showBattleLog){
				if (gameRunning){
					blf.showMe();
				}
			}
			if (e.getSource()==purchaseLevel && !battleRunning &&  pslf.inferSelectedSphere()){
				if (gameRunning){
					pslf.showMe();
				}
			}
			//rest not yet working
			if (e.getSource()==purchasePlayerEXP && gameRunning && !battleRunning){
				if (gameRunning){
					pplf.showMe();
				}
			}
			if (e.getSource()==purchaseDice){
				if (gameRunning){
					upf.showMe();
				}
			}
			if (e.getSource()==purchaseAbility){
				if (gameRunning){
					upf.showMe();
				}
			}
			if (e.getSource()==customizeSphere && gameRunning && !battleRunning){
				if (gameRunning){
					csf.showMe();
				}
			}
			//other actions go here
		}
	}
	private class SphereStatFrame extends JFrame implements ActionListener {
		JTextArea sphereStats;
		JComboBox invy;
		JRadioButton [] rArray;
		JScrollPane jsp;
		boolean initialized;

		public SphereStatFrame (){
			initialized = false;
		}
		public void initializeFrame (){
			if (!initialized){
				initialized = true;
				sphereStats = new JTextArea ("");
				sphereStats.setEditable(false);
				jsp = new JScrollPane (sphereStats,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				invy = new JComboBox();
				invy.addActionListener(this);
				
				JPanel outpanel = new JPanel();
				JPanel sp0 = new JPanel ();
				JPanel sp1 = new JPanel ();
				JPanel sp2 = new JPanel ();
				outpanel.setLayout (new BoxLayout(outpanel, BoxLayout.PAGE_AXIS));

				rArray = new JRadioButton[dgs.myParty.party.length];
				ButtonGroup bg = new ButtonGroup();
				for (int i=0;i<rArray.length;i++){
					rArray [i] = new JRadioButton();
					bg.add (rArray[i]);
					rArray[i].addActionListener(this);
				}
				sp1.setLayout (new GridLayout (2,3));
				for (int i=0;i<rArray.length;i++){
					sp1.add (rArray[i]);
				}


				sp0.add(jsp);
				sp2.add(invy);

				outpanel.add(sp0);
				outpanel.add(sp1);
				outpanel.add(sp2);

				jsp.setPreferredSize (new Dimension (300,330));
				sp1.setPreferredSize (new Dimension (300,75));
				invy.setPreferredSize (new Dimension (300,25));

				add (outpanel);

				setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				setTitle("Sphere Viewer");
				outpanel.setPreferredSize (new Dimension (400,450));

				getContentPane();
				pack();
				setVisible(true);
			} else {
				jsp = new JScrollPane (sphereStats,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				rArray = new JRadioButton[dgs.myParty.party.length];
				ButtonGroup bg = new ButtonGroup();
				for (int i=0;i<rArray.length;i++){
					rArray [i] = new JRadioButton();
					bg.add (rArray[i]);
					rArray[i].addActionListener(this);
				}
				jsp.setPreferredSize (new Dimension (300,330));
				
				getContentPane();
				pack();
				setVisible(true);
			}
			updateDisplay();
		}
		public void updateText (){
			if (selectedSphere!=null){
				sphereStats.setText(selectedSphere.dumpAttributes());				
			}
		}
		public void updateDisplay(){
			if (!initialized)
				initializeFrame();
			updateText();
			JComboBox temp = new JComboBox (dgs.myDspheres.toArray());
			invy.setModel(temp.getModel());
			for (int i=0;i<rArray.length && i<dgs.myParty.party.length;i++){
				if (dgs.myParty.party[i]!=null){
					if (rArray [i]==null){
						rArray [i]=new JRadioButton(dgs.myParty.party[i].toString());
					} else {
						rArray [i].setText (dgs.myParty.party[i].toString());
					}
				} else {
					rArray [i].setText ("");
				}
			}
			validate();
			repaint();
		}
		//to not reset the combobox, use this updatedisplay instead
		public void updateDisplayLocal(){
			if (!initialized)
				initializeFrame();
			updateText();
			for (int i=0;i<rArray.length && i<dgs.myParty.party.length;i++){
				if (dgs.myParty.party[i]!=null){
					if (rArray [i]==null){
						rArray [i]=new JRadioButton(dgs.myParty.party[i].toString());
					} else {
						rArray [i].setText (dgs.myParty.party[i].toString());
					}
				} else {
					rArray [i].setText ("");
				}
			}
			validate();
			repaint();
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==invy){
				selectedSphere = dgs.myDspheres.get(invy.getSelectedIndex());
				updateDisplayLocal();
			}
			if (e.getSource()==rArray[0]){
				if (dgs.myParty.party[0]!=null){
					selectedSphere = dgs.myParty.party[0];
					updateDisplayLocal();
				}
			}
			if (e.getSource()==rArray[1]){
				if (dgs.myParty.party[1]!=null){
					selectedSphere = dgs.myParty.party[1];
					updateDisplayLocal();
				}
			}
			if (e.getSource()==rArray[2]){
				if (dgs.myParty.party[2]!=null){
					selectedSphere = dgs.myParty.party[2];
					updateDisplayLocal();
				}
			}
			if (e.getSource()==rArray[3]){
				if (dgs.myParty.party[3]!=null){
					selectedSphere = dgs.myParty.party[3];
					updateDisplayLocal();
				}
			}
			if (e.getSource()==rArray[4]){
				if (dgs.myParty.party[4]!=null){
					selectedSphere = dgs.myParty.party[4];
					updateDisplayLocal();
				}
			}
			if (e.getSource()==rArray[5]){
				if (dgs.myParty.party[5]!=null){
					selectedSphere = dgs.myParty.party[5];
					updateDisplayLocal();
				}
			}
		}
	}
	private class BattleLogFrame extends JFrame {
		JTextArea frameLog;
		JScrollPane jsp;
		JPanel jp;

		public BattleLogFrame (){	
			jp = new JPanel ();
			frameLog = new JTextArea ("");
			frameLog.setLineWrap(true);
			frameLog.setWrapStyleWord(true);
			frameLog.setEditable(false);
			jsp = new JScrollPane (frameLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp.setPreferredSize (new Dimension (300,400));
			jp.add (jsp);
			add (jp);
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Battle Log");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe (){
			//			getContentPane();
			//			pack();
			setVisible(true);
		}
//		public void initializeFrame (){
//
//		}
		public void clearText (){
			frameLog.setText("");
		}
		public void updateText () {
			frameLog.setText(bdp.battleLog.getText());
		}
	}
	/**
	 * Current restrictions on leveling: levels can only be refreshed to 20, at (dicetotal) sp per level
	 */
	private class RefreshLevelFrame extends JFrame  implements ActionListener{
		public final static int REFRESH_LEVEL_LIMIT = 999;
		JTextArea instructions;
		JLabel costlabel, ssName;
		JTextField enterValue;
		JButton confirm;
		JButton cancel;
		SGDPanel sgd;
		boolean isActive; //can use this as a hard way to disable actionlisteners if needed
		JScrollPane jsp;
		int convertValue, rlevels; //cost of refreshing, number of levels to refresh
		Dsphere mSelectedSphere;

		public RefreshLevelFrame(SGDPanel s) {
			convertValue = 0;
			rlevels = 0;
			isActive = false;
			sgd =s ;
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Refresh Sphere Level");

			getContentPane();
			pack();
			setVisible(false);

			String ins = "" +
			"Spheres normally decrease in power as they are used. This function replenishes lost stats. \n" +
			"Enter the amount of levels you wish to refresh on the selected sphere. \n" +
			"The cost for refreshing those levels is displayed next to the input box.\n" +
			"You may only refresh up to the level of the selected sphere, \n" +
			"or the maximum refresh level: "+REFRESH_LEVEL_LIMIT+"\n"+
			"Note: Select a sphere by choosing it in your inventory or on the Sphere Viewer";
			instructions = new JTextArea(ins);
			instructions.setEditable(false);
			instructions.setLineWrap(true);
			jsp = new JScrollPane (instructions,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			costlabel = new JLabel("0");
			ssName = new JLabel ("SelectedSphere: ");
			confirm = new JButton ("Confirm");
			cancel = new JButton ("Cancel");
			enterValue = new JTextField("enter value");

			JPanel outpanel = new JPanel();
			JPanel instpanel = new JPanel();
			instpanel.add (jsp);
			JPanel enterpanel = new JPanel();
			enterpanel.add (enterValue);
			enterpanel.add (costlabel);
			JPanel buttonpanel = new JPanel();
			buttonpanel.add(confirm);
			buttonpanel.add(cancel);
			jsp.setPreferredSize (new Dimension (300,300));
			buttonpanel.setPreferredSize (new Dimension (300,200));
			instpanel.setPreferredSize (new Dimension (300,400));
			cancel.addActionListener(this);
			confirm.addActionListener(this);
			enterValue.addActionListener(this);
			outpanel.add (ssName);
			outpanel.add (instpanel);
			outpanel.add (enterpanel);
			outpanel.add (buttonpanel);
			outpanel.setPreferredSize (new Dimension (300,400));
			add (outpanel);
			outpanel.setLayout (new BoxLayout(outpanel, BoxLayout.PAGE_AXIS));
			setMinimumSize (new Dimension (300,400));
		}
		public void showMe () {
			setVisible(true);
			sgd.setEnabled(false);
			sgd.disableAll();
			convertValue= 0;
			rlevels = 0;
			enterValue.setColumns(10);
			enterValue.setText ("0");
			costlabel.setText ("0");
		}
		public boolean inferSelectedSphere () {
			boolean inferred = false;
			if (selectedSphere != null){
				mSelectedSphere = selectedSphere;
				inferred = true;
				ssName.setText ("Selected Sphere: "+mSelectedSphere.toString());
			}
			return inferred;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==cancel){
				setVisible(false);
				sgd.setEnabled(true); //doesn't work, get comprehensive method for this
				sgd.enableAll();
				isActive = false;
				mSelectedSphere = null;
				updateAllLabels();
			}
			if (e.getSource()==confirm){
				if (rlevels>0 && convertValue < sgd.dgs.myPlayer.scorepoints){
					mSelectedSphere.refreshLevels(rlevels);
					sgd.dgs.myPlayer.changeScorePoints(-convertValue);
				}
				setVisible(false);
				sgd.setEnabled(true);//doesn't work, get comprehensive method for this
				isActive = false;
				mSelectedSphere = null;	
				sgd.enableAll();
				updateAllLabels();
			}
			if (e.getSource()==enterValue && mSelectedSphere != null){
				String curr = enterValue.getText();
				int lvCost = mSelectedSphere.getLevelRollCost();
//				boolean converted= false;
				try {
					String res = "0";
					for (int i=0;i<curr.length();i++){
						String ch = ""+curr.charAt(i);
						if (Character.isDigit(curr.charAt(i))){
							res=res+ch;
						}
					}
					int ref = Integer.parseInt(res);
					if (ref > 0){
						if (ref>REFRESH_LEVEL_LIMIT)
							ref = REFRESH_LEVEL_LIMIT;
						if (ref>mSelectedSphere.level)
							ref = mSelectedSphere.level;
						convertValue = ref*lvCost;
						rlevels = ref;
						costlabel.setText (Integer.toString(ref*lvCost));
					}else{
						convertValue= 0;
						rlevels = 0;
						costlabel.setText ("0");
					}
//					converted = true;
					
				} catch (Exception ex){
					System.out.println("Bad input");
				}
			}
		}
	}
	private class PurchaseSphereLevelFrame extends JFrame  implements ActionListener{
		JTextArea instructions;
		JLabel costlabel, ssName;
		JTextField enterValue;
		JButton confirm;
		JButton cancel;
		SGDPanel sgd;
		boolean isActive; //can use this as a hard way to disable actionlisteners if needed
		JScrollPane jsp;
		int convertValue, rlevels; //cost of refreshing, number of levels to refresh
		Dsphere mSelectedSphere;

		public PurchaseSphereLevelFrame(SGDPanel s) {
			convertValue = 0;
			rlevels = 0;
			isActive = false;
			sgd =s ;
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Purchase Sphere Experience");

			getContentPane();
			pack();
			setVisible(false);

			String ins = "" +
			"Normally temperance/experience is gained by battle. However, you may also exchange SP for it. \n" +
			"Enter the amount of levels you wish to purchase on the selected sphere. \n" +
			"The cost for these levels is displayed next to the input box.\n" +
			"Cost is equal to the sum of all previous levels the selected sphere has gained.\n";
			instructions = new JTextArea(ins);
			instructions.setEditable(false);
			instructions.setLineWrap(true);
			jsp = new JScrollPane (instructions,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp.setPreferredSize (new Dimension (300,500));
			costlabel = new JLabel("0");
			ssName = new JLabel ("SelectedSphere: ");
			confirm = new JButton ("Confirm");
			cancel = new JButton ("Cancel");
			enterValue = new JTextField("enter value");

			JPanel outpanel = new JPanel();
			JPanel instpanel = new JPanel();
			instpanel.add (jsp);
			JPanel enterpanel = new JPanel();
			enterpanel.add (enterValue);
			enterpanel.add (costlabel);
			JPanel buttonpanel = new JPanel();
			buttonpanel.add(confirm);
			buttonpanel.add(cancel);
			buttonpanel.setPreferredSize (new Dimension (400,200));
			cancel.addActionListener(this);
			confirm.addActionListener(this);
			enterValue.addActionListener(this);
			outpanel.add (ssName);
			outpanel.add (instpanel);
			outpanel.add (enterpanel);
			outpanel.add (buttonpanel);
			outpanel.setPreferredSize (new Dimension (300,300));
			add (outpanel);
			outpanel.setLayout (new BoxLayout(outpanel, BoxLayout.PAGE_AXIS));
			setMinimumSize (new Dimension (400,300));
		}
		public void showMe () {
			setVisible(true);
			sgd.setEnabled(false);
			sgd.disableAll();
			convertValue= 0;
			rlevels = 0;
			enterValue.setText ("           0");
			costlabel.setText ("0");
		}
		public boolean inferSelectedSphere () {
			boolean inferred = false;
			if (selectedSphere != null){
				mSelectedSphere = selectedSphere;
				inferred = true;
				ssName.setText ("Selected Sphere: "+mSelectedSphere.toString());
			}
			return inferred;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==cancel){
				setVisible(false);
				sgd.setEnabled(true); //doesn't work, get comprehensive method for this
				sgd.enableAll();
				isActive = false;
				mSelectedSphere = null;
				updateAllLabels();
			}
			if (e.getSource()==confirm){
				if (rlevels>0 && convertValue < sgd.dgs.myPlayer.scorepoints && gameRunning && !battleRunning){
					for (int i=0;i<rlevels;i++){
						mSelectedSphere.gainEXP(Dsphere.EXP_MAX);
					}
					sgd.dgs.myPlayer.changeScorePoints(-convertValue);
				}
				setVisible(false);
				sgd.setEnabled(true);//doesn't work, get comprehensive method for this
				isActive = false;
				mSelectedSphere = null;	
				sgd.enableAll();
				updateAllLabels();
			}
			if (e.getSource()==enterValue && mSelectedSphere != null){
				String curr = enterValue.getText();
//				boolean converted= false;
				try {
					String res = "0";
					for (int i=0;i<curr.length();i++){
						String ch = ""+curr.charAt(i);
						if (Character.isDigit(curr.charAt(i))){
							res=res+ch;
						}
					}
					int ref = Integer.parseInt(res);
					if (ref > 0){
						convertValue = mSelectedSphere.getLevelBoostCost(ref);
						rlevels = ref;
						costlabel.setText (Integer.toString(convertValue));
					}else{
						convertValue= 0;
						rlevels = 0;
						costlabel.setText ("0");
					}
//					converted = true;
					
				} catch (Exception ex){
					System.out.println("Bad input");
				}
			}
		}
	}
	private class PurchasePlayerLevelFrame extends JFrame  implements ActionListener{
		public final static int CONVERT_RATIO = 1;
		JTextArea instructions;
		JLabel costlabel, currSP;
		JButton confirm;
		JButton cancel;
		SGDPanel sgd;
		boolean isActive; //can use this as a hard way to disable actionlisteners if needed
		JScrollPane jsp;
		int convertValue; //cost of exp
		int edex, adex,sdex,fdex; //indices of selected 

		public int [] elemMasteryx;
		public int [] elemEXPx;
		public int [] attMasteryx;
		public int [] attEXPx;
		public int [] skilMasteryx;
		public int [] skilEXPx;
		public int [] scavMasteryx;
		public int [] scavEXPx;
		
		public int scorepointsx;
		
		JComboBox ebox;
		JComboBox abox;
		JComboBox skbox;
		JComboBox scbox;
		
		JLabel evdyn;
		JLabel avdyn;
		JLabel svdyn;
		JLabel fvdyn;
		
		JLabel exvdyn;
		JLabel axvdyn;
		JLabel sxvdyn;
		JLabel fxvdyn;
		
		JLabel envdyn;
		JLabel anvdyn;
		JLabel snvdyn;
		JLabel fnvdyn;
		
		JButton pebut;
		JButton pabut;
		JButton psbut;
		JButton pfbut;

		public PurchasePlayerLevelFrame(SGDPanel s) {
			isActive = false;
			convertValue = 0;
			edex=0;
			adex=0;
			sdex=0;
			fdex=0;
			isActive = false;
			sgd =s ;

			String ins = "" +
			"Increase the level of your masteries to strengthen your spheres in battle and give yourself other bonuses. \n" +
			"1 EXP costs "+CONVERT_RATIO+" scorepoints. \n" +
			"The default amount of EXP needed to gain the first level in a mastery is "+Collective.EXP_MAX+", " +
			"but it can be decreased by increasing the level of the Temperance Attribute. " +
			"The amount of EXP needed to gain successive levels increases by 50% per level." 
			;
			instructions = new JTextArea(ins);
			instructions.setEditable(false);
			instructions.setLineWrap(true);
			jsp = new JScrollPane (instructions,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp.setPreferredSize (new Dimension (700,100));
			
			currSP = new JLabel ("SP: 0");
			costlabel = new JLabel("Cost: 0");
			confirm = new JButton ("Confirm");
			cancel = new JButton ("Cancel");

			JLabel emlabel = new JLabel ("Elem Mastery: ");
			JLabel amlabel = new JLabel ("Attrib Mastery: ");
			JLabel smlabel = new JLabel ("Skill Mastery: ");
			JLabel fmlabel = new JLabel ("Finder Mastery: ");
			
			JLabel mncol = new JLabel ("Masteries");
			JLabel mtcol = new JLabel ("Type");
			JLabel lvcol = new JLabel ("Current Level");
			JLabel excol = new JLabel ("Current EXP");
			JLabel nlcol = new JLabel ("EXP to Next Lvl");
			JLabel adcol = new JLabel ("Add EXP");
			JLabel blcol = new JLabel ("  ");
			
			evdyn = new JLabel ("*"); //changes depending on the value of mastery selected
			avdyn = new JLabel ("*");
			svdyn = new JLabel ("*");
			fvdyn = new JLabel ("*");
			
			exvdyn = new JLabel ("*"); //changes depending on the value of mastery selected
			axvdyn = new JLabel ("*");
			sxvdyn = new JLabel ("*");
			fxvdyn = new JLabel ("*");
			
			envdyn = new JLabel ("*"); //changes depending on the value of mastery selected
			anvdyn = new JLabel ("*");
			snvdyn = new JLabel ("*");
			fnvdyn = new JLabel ("*");
			
			ebox = new JComboBox (Collective.elemNames);
			abox = new JComboBox (Collective.attNames);
			skbox = new JComboBox (Collective.skilNames);
			scbox = new JComboBox (Collective.scavNames);
			
			pebut = new JButton ("Add Elem EXP");
			pabut = new JButton ("Add Attrib EXP");
			psbut = new JButton ("Add Skill EXP");
			pfbut = new JButton ("Add Scav EXP");
			
			ebox.addActionListener(this);
			abox.addActionListener(this);
			skbox.addActionListener(this);
			scbox.addActionListener(this);
			
			pebut.addActionListener(this);
			pabut.addActionListener(this);
			psbut.addActionListener(this);
			pfbut.addActionListener(this);
			
			cancel.addActionListener(this);
			confirm.addActionListener(this);
			
			JPanel outpanel = new JPanel(); //superpanel
			JPanel instpanel = new JPanel(); //instruction panel
			instpanel.add (jsp);
			JPanel enterpanel = new JPanel();  //playerinfo mechanics panel
			
			enterpanel.add(mncol);
			enterpanel.add(mtcol);
			enterpanel.add(lvcol);
			enterpanel.add(excol);
			enterpanel.add(nlcol);
			enterpanel.add(adcol);
			enterpanel.add(blcol);
			
			enterpanel.add(emlabel);
			enterpanel.add(ebox);
			enterpanel.add(evdyn);
			enterpanel.add(exvdyn);
			enterpanel.add(envdyn);
			enterpanel.add(pebut);
			enterpanel.add (currSP);
			
			enterpanel.add(amlabel);
			enterpanel.add(abox);
			enterpanel.add(avdyn);
			enterpanel.add(axvdyn);
			enterpanel.add(anvdyn);
			enterpanel.add(pabut);
			enterpanel.add (costlabel);
			
			enterpanel.add(smlabel);
			enterpanel.add(skbox);
			enterpanel.add(svdyn);
			enterpanel.add(sxvdyn);
			enterpanel.add(snvdyn);
			enterpanel.add(psbut);
			enterpanel.add(confirm);

			enterpanel.add(fmlabel);
			enterpanel.add(scbox);
			enterpanel.add(fvdyn);
			enterpanel.add(fxvdyn);
			enterpanel.add(fnvdyn);
			enterpanel.add(pfbut);
			enterpanel.add(cancel);
			
			outpanel.add (instpanel);
			outpanel.add (enterpanel);
			outpanel.setPreferredSize (new Dimension (800,600));
			add (outpanel);
			enterpanel.setLayout(new GridLayout(5,7,20,20)); //row,col,space b/w
			outpanel.setLayout (new BoxLayout(outpanel, BoxLayout.PAGE_AXIS));
			setMinimumSize (new Dimension (800,600));
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Purchase Player Experience");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe () {
			isActive = true;
			elemMasteryx = dgs.myPlayer.elemMastery.clone();
			elemEXPx = dgs.myPlayer.elemEXP.clone();
			attMasteryx = dgs.myPlayer.attMastery.clone();
			attEXPx = dgs.myPlayer.attEXP.clone();
			skilMasteryx = dgs.myPlayer.skilMastery.clone();
			skilEXPx = dgs.myPlayer.skilEXP.clone();
			scavMasteryx = dgs.myPlayer.scavMastery.clone();
			scavEXPx = dgs.myPlayer.scavEXP.clone();
			
			scorepointsx = dgs.myPlayer.scorepoints+0;
			
			setVisible(true);
			sgd.setEnabled(false);
			sgd.disableAll();
			convertValue= 0;
			updatePPLabels();
		}
		//updates all labels in this frame
		public void updatePPLabels (){
			currSP.setText ("SP: "+Integer.toString(dgs.myPlayer.scorepoints));
			costlabel.setText ("Cost: "+Integer.toString(convertValue));
			updateLabels();
		}
		//for updating display
		public void updateLabels () {
			updateEMastery();
			updateAMastery();
			updateSkMastery();
			updateScMastery();
//			evdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemMastery[ebox.getSelectedIndex()]));
//			avdyn.setText (Integer.toString(sgd.dgs.myPlayer.attMastery[abox.getSelectedIndex()]));
//			svdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilMastery[skbox.getSelectedIndex()]));
//			fvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavMastery[scbox.getSelectedIndex()]));
//			
//			exvdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemEXP[ebox.getSelectedIndex()]));
//			axvdyn.setText (Integer.toString(sgd.dgs.myPlayer.attEXP[abox.getSelectedIndex()]));
//			sxvdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilEXP[skbox.getSelectedIndex()]));
//			fxvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavEXP[scbox.getSelectedIndex()]));
//			
//			envdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.elemMastery[ebox.getSelectedIndex()])));
//			anvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.attMastery[abox.getSelectedIndex()])));
//			snvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.skilMastery[skbox.getSelectedIndex()])));
//			fnvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.scavMastery[scbox.getSelectedIndex()])));
		}
		public void updateEMastery(){
			evdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemMastery[ebox.getSelectedIndex()]));
			exvdyn.setText (Integer.toString(sgd.dgs.myPlayer.elemEXP[ebox.getSelectedIndex()]));
			envdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.elemMastery[ebox.getSelectedIndex()])));
		}
		public void updateAMastery(){
			avdyn.setText (Integer.toString(sgd.dgs.myPlayer.attMastery[abox.getSelectedIndex()]));
			axvdyn.setText (Integer.toString(sgd.dgs.myPlayer.attEXP[abox.getSelectedIndex()]));
			anvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.attMastery[abox.getSelectedIndex()])));
		}
		public void updateSkMastery(){
			svdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilMastery[skbox.getSelectedIndex()]));
			sxvdyn.setText (Integer.toString(sgd.dgs.myPlayer.skilEXP[skbox.getSelectedIndex()]));
			snvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.skilMastery[skbox.getSelectedIndex()])));
		}
		public void updateScMastery(){
			fvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavMastery[scbox.getSelectedIndex()]));
			fxvdyn.setText (Integer.toString(sgd.dgs.myPlayer.scavEXP[scbox.getSelectedIndex()]));
			fnvdyn.setText (Integer.toString(sgd.dgs.myPlayer.calcEXPtoLevel(dgs.myPlayer.scavMastery[scbox.getSelectedIndex()])));
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==cancel){
				dgs.myPlayer.elemMastery=elemMasteryx;
				dgs.myPlayer.elemEXP=elemEXPx;
				dgs.myPlayer.attMastery=attMasteryx;
				dgs.myPlayer.attEXP=attEXPx;
				dgs.myPlayer.skilMastery=skilMasteryx;
				dgs.myPlayer.skilEXP=skilEXPx;
				dgs.myPlayer.scavMastery=scavMasteryx;
				dgs.myPlayer.scavEXP=scavEXPx;
				
				dgs.myPlayer.scorepoints=scorepointsx;
				setVisible(false);
				sgd.setEnabled(true); //doesn't work, get comprehensive method for this
				sgd.enableAll();
				isActive = false;
				updateAllLabels();
			}
			if (e.getSource()==confirm){
				convertValue =0;
				setVisible(false);
				sgd.setEnabled(true);//doesn't work, get comprehensive method for this
				isActive = false;

				sgd.enableAll();
				updateAllLabels();
			}
			if (e.getSource()==ebox){
				edex = ebox.getSelectedIndex();
				updateEMastery();
			}
			if (e.getSource()==abox){
				adex = abox.getSelectedIndex();
				updateAMastery();
			}
			if (e.getSource()==skbox){
				sdex = skbox.getSelectedIndex();
				updateSkMastery();
			}
			if (e.getSource()==scbox){
				fdex = scbox.getSelectedIndex();
				updateScMastery();
			}
			if (e.getSource()==pebut){
				if (gameRunning && !battleRunning){
					String refs = JOptionPane.showInputDialog("How many scorepoints will you spend? Note: " +CONVERT_RATIO+
							" scorepoints are required to purchase 1 experience.");
					if (refs != null){
						try {
							int ref = Integer.parseInt(refs);
							if (ref > 0 && ref <= dgs.myPlayer.scorepoints){
								dgs.myPlayer.gainStatEXP(Collective.ELEM_M, edex, ref/CONVERT_RATIO);
								convertValue = convertValue + ref;
								dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-ref;
							} 
						} catch (Exception ex){
							System.out.println("Bad input");
						}
					}
					updateAllLabels();
				}
			}
			if (e.getSource()==pabut){
				if (gameRunning && !battleRunning){
					String refs = JOptionPane.showInputDialog("How many scorepoints will you spend? Note: " +CONVERT_RATIO+
							" scorepoints are required to purchase 1 experience.");
					if (refs != null){
						try {
							int ref = Integer.parseInt(refs);
							if (ref > 0 && ref <= dgs.myPlayer.scorepoints){
								dgs.myPlayer.gainStatEXP(Collective.ATTR_M, adex, ref/CONVERT_RATIO);
								convertValue = convertValue + ref;
								dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-ref;
							} 
						} catch (Exception ex){
							System.out.println("Bad input");
						}
					}
					updateAllLabels();
				}
			}
			if (e.getSource()==psbut){
				if (gameRunning && !battleRunning){
					String refs = JOptionPane.showInputDialog("How many scorepoints will you spend? Note: " +CONVERT_RATIO+
							" scorepoints are required to purchase 1 experience.");
					if (refs != null){
						try {
							int ref = Integer.parseInt(refs);
							if (ref > 0 && ref <= dgs.myPlayer.scorepoints){
								dgs.myPlayer.gainStatEXP(Collective.SKIL_M, sdex, ref/CONVERT_RATIO);
								convertValue = convertValue + ref;
								dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-ref;
							} 
						} catch (Exception ex){
							System.out.println("Bad input");
						}
					}
					updateAllLabels();
				}
			}
			if (e.getSource()==pfbut){
				if (gameRunning && !battleRunning){
					String refs = JOptionPane.showInputDialog("How many scorepoints will you spend? Note: " +CONVERT_RATIO+
							" scorepoints are required to purchase 1 experience.");
					if (refs != null){
						try {
							int ref = Integer.parseInt(refs);
							if (ref > 0 && ref <= dgs.myPlayer.scorepoints){
								dgs.myPlayer.gainStatEXP(Collective.SCAV_M, fdex, ref/CONVERT_RATIO);
								convertValue = convertValue + ref;
								dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-ref;
							} 
						} catch (Exception ex){
							System.out.println("Bad input");
						}
					}
					updateAllLabels();
				}
			}
		}
	}
	private class UnitPurchaseFrame extends JFrame implements ActionListener {
		public final static int DICE_NUM_LIM = 5;
		public final static int DICE_VAL_LIM = 30;
		public final static int DICE_BON_LIM = 12;
		public final static int DICE_PREMIUM = 20;
		public final static int ABI_PREMIUM = 15;
		int initSP, totalCost;
		int sdNum, sdVal, sdBon;
		int powID, staID;
		JTextField dNum, dVal, dBon;
		JComboBox aTypeBox, aLevelBox;
		JLabel abiOutput, diceOutput, abiCost, diceCost, currSP, costLabel;
		JButton buyDice, buyAbility, cancel;
		SGDPanel sgd;
		
		public UnitPurchaseFrame (SGDPanel s){
			sgd = s;
			initSP = 0;
			totalCost = 0;
			sdNum = 1;
			sdVal = 1;
			sdBon = 0;
			powID = 0;
			staID = 0;
			JLabel purchaseDiceT, purchaseAbilityT;
			JLabel dNumber, dValue, dBonus;
			JLabel abilityType,abilityLevel;
			
			purchaseDiceT = new JLabel ("Purchase Dice");
			purchaseAbilityT = new JLabel ("Purchase Ability");
			
			dNumber = new JLabel ("Number");
			dValue = new JLabel ("Value");
			dBonus = new JLabel ("Bonus");
			
			abilityType = new JLabel ("Power");
			abilityLevel = new JLabel ("Level");
			
			dNum = new JTextField(5);
			dVal = new JTextField(5);
			dBon = new JTextField(5);
			aTypeBox = new JComboBox(Ability.POWERS);
			aLevelBox = new JComboBox(Ability.STARLIST);
			abiOutput = new JLabel("   ");
			diceOutput = new JLabel("   ");
			diceCost = new JLabel("   ");
			abiCost = new JLabel("   ");
			currSP = new JLabel("   ");
			costLabel = new JLabel("   ");
			buyDice = new JButton ("Purchase Dice");
			buyAbility = new JButton ("Purchase Ability");
			cancel = new JButton ("Done");
			
			dNum.addActionListener(this);
			dVal.addActionListener(this);
			dBon.addActionListener(this);
			aTypeBox.addActionListener(this);
			aLevelBox.addActionListener(this);
			buyDice.addActionListener(this);
			buyAbility.addActionListener(this);
			cancel.addActionListener(this);
			
			JPanel opanel = new JPanel(); //biggest panel
			JPanel topPanel = new JPanel(); //top panel holding all but confirmp components
			JPanel dicePanel = new JPanel(); //all dice panels
			JPanel abiPanel = new JPanel(); //all ability panels
			JPanel diceConfigp = new JPanel(); //dice configuration related
			JPanel abiConfigp = new JPanel(); //ability configuration related
			JPanel sppane = new JPanel(); //display scorepoint related labels
			JPanel confirmp = new JPanel(); //display stuff at the bottom of the panels
			
			opanel.setLayout (new BoxLayout(opanel, BoxLayout.PAGE_AXIS));
			topPanel.setLayout (new BoxLayout(topPanel, BoxLayout.X_AXIS));
			dicePanel.setLayout (new BoxLayout(dicePanel, BoxLayout.PAGE_AXIS));
			abiPanel.setLayout (new BoxLayout(abiPanel, BoxLayout.PAGE_AXIS));
			diceConfigp.setLayout(new GridLayout(2,3,0,0));
			abiConfigp.setLayout(new GridLayout(2,2,0,0));
			sppane.setLayout(new GridLayout(1,2));
			confirmp.setLayout(new BoxLayout(confirmp, BoxLayout.PAGE_AXIS));
			
			sppane.add(currSP);
			sppane.add(costLabel);
			confirmp.add(sppane);
			confirmp.add(cancel);
			
			diceConfigp.add(dNumber);
			diceConfigp.add(dValue);
			diceConfigp.add(dBonus);
			diceConfigp.add(dNum);
			diceConfigp.add(dVal);
			diceConfigp.add(dBon);
			
			abiConfigp.add(abilityType);
			abiConfigp.add(abilityLevel);
			abiConfigp.add(aTypeBox);
			abiConfigp.add(aLevelBox);
			
			dicePanel.add(purchaseDiceT);
			dicePanel.add(diceConfigp);
			dicePanel.add(diceOutput);
			dicePanel.add(diceCost);
			dicePanel.add(buyDice);
			
			abiPanel.add(purchaseAbilityT);
			abiPanel.add(abiConfigp);
			abiPanel.add(abiOutput);
			abiPanel.add(abiCost);
			abiPanel.add(buyAbility);
			
			topPanel.add(dicePanel);
			topPanel.add(abiPanel);
			
			opanel.add(topPanel);
			opanel.add(confirmp);
			add (opanel);			
			opanel.setPreferredSize(new Dimension(400,300));
			
			//generic border information
			Border rbb = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
			TitledBorder ebd;

			//adding and bordering the panels
			ebd = BorderFactory.createTitledBorder(rbb, "Dice");
			dicePanel.setBorder(ebd);
			ebd = BorderFactory.createTitledBorder(rbb, "Ability");
			abiPanel.setBorder(ebd);
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Purchase Dice/Abilities");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe (){
			if (!isVisible()){
				initSP = 0;
				totalCost = 0;
				sdNum = 1;
				sdVal = 1;
				sdBon = 0;
				powID = 0;
				staID = 0;
				initSP = dgs.myPlayer.scorepoints;
				totalCost = 0;
				updateLabels();
				setVisible(true);
			}
		}
		public void updateLabels(){
			if (isVisible()){
				diceCost.setText("Cost: "+Integer.toString(new DDice(sdNum,sdVal,sdBon).calculateSPValue()*DICE_PREMIUM));
				abiCost.setText("Cost: "+Integer.toString(new Ability(powID,staID).calculateSPValue()*ABI_PREMIUM));
				diceOutput.setText(sdNum+"d "+sdVal+" + "+sdBon);
				abiOutput.setText(Ability.POWERS[powID]+" "+Ability.STARLIST[staID]);
				currSP.setText("SP: "+dgs.myPlayer.scorepoints);
				costLabel.setText("Cost: "+totalCost);	
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==dNum){
				int ref = SGDPanel.processIntInput(dNum.getText());
				if (ref>0 && ref<=Math.max(1, 3+dgs.myPlayer.scavMastery[2]/3)){
					sdNum = ref;
				}
				updateLabels();
			}
			if (e.getSource()==dVal){
				int ref = SGDPanel.processIntInput(dVal.getText());
				if (ref>0 && ref<=Math.max(1, 10+dgs.myPlayer.scavMastery[2]*2)){
					sdVal = ref;
				}
				updateLabels();
			}
			if (e.getSource()==dBon){
				int ref = SGDPanel.processIntInput(dBon.getText());
				if (ref>=0 && ref<=DICE_BON_LIM){
					sdBon = ref;
				}
				updateLabels();
			}
			if (e.getSource()==aTypeBox){
				powID = aTypeBox.getSelectedIndex();
				updateLabels();
			}
			if (e.getSource()==aLevelBox){
				staID = aLevelBox.getSelectedIndex();
				updateLabels();
			}
			if (e.getSource()==buyDice){
				if (sdNum>0 && sdVal>0){
					DDice di = new DDice(sdNum, sdVal, sdBon);
					if (di.calculateSPValue()*10<=dgs.myPlayer.scorepoints){
						dgs.myUpgradeDice.add(di);
						dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-di.calculateSPValue()*DICE_PREMIUM;
					}
				}
				sgd.updateAllLabels();
			}
			if (e.getSource()==buyAbility){
				Ability ab = new Ability (powID, staID);
				if (ab.calculateSPValue()*10<=dgs.myPlayer.scorepoints){
					dgs.myAbilityCores.add(ab);
					dgs.myPlayer.scorepoints=dgs.myPlayer.scorepoints-ab.calculateSPValue()*ABI_PREMIUM;
				}
				sgd.updateAllLabels();
			}
			if (e.getSource()==cancel){
				initSP = 0;
				totalCost = 0;
				sdNum = 1;
				sdVal = 1;
				sdBon = 0;
				powID = 0;
				staID = 0;
				setVisible(false);
				sgd.setEnabled(true);//doesn't work, get comprehensive method for this

				sgd.enableAll();
				updateAllLabels();
			}
		}
	}
	private class CustomizeSphereFrame extends JFrame implements ActionListener,ListSelectionListener {
		JLabel ssLevel,ssElement,ssChassis;
		JButton  ssName,changeDice, changeAbi, changeAtk;
		JButton confirm,cancel; //remove confirm
		Dsphere msSphere;
		JTextField dice0,dice1,dice2,dice3,dice4,abiTA,atkTA;
		JList sp;
		JScrollPane lsp;
		JRadioButton d0select, d1select,d2select,d3select,d4select;
		JComboBox sphereBox,abiBox,atkBox;
		int diceDex,selectedDice,abiDex,atkDex;
		
		public CustomizeSphereFrame () {
			selectedDice = -1;
			diceDex = -1;
			abiDex = -1;
			atkDex = 0;
			JLabel statLabel0 = new JLabel ("Integrity: ");
			JLabel statLabel1 = new JLabel ("Force: ");
			JLabel statLabel2 = new JLabel ("Barrier: ");
			JLabel statLabel3 = new JLabel ("Gravitas: ");
			JLabel statLabel4 = new JLabel ("Priority: ");
			
			ssLevel = new JLabel();
			ssElement = new JLabel();
			ssChassis = new JLabel();
			dice0 = new JTextField();
			dice1 = new JTextField();
			dice2 = new JTextField();
			dice3 = new JTextField();
			dice4 = new JTextField();
			abiTA = new JTextField();
			atkTA = new JTextField();
			
			dice0.setEditable(false);
			dice1.setEditable(false);
			dice2.setEditable(false);
			dice3.setEditable(false);
			dice4.setEditable(false);
			abiTA.setEditable(false);
			atkTA.setEditable(false);
			
			dice0.setHorizontalAlignment(JTextField.CENTER);
			dice1.setHorizontalAlignment(JTextField.CENTER);
			dice2.setHorizontalAlignment(JTextField.CENTER);
			dice3.setHorizontalAlignment(JTextField.CENTER);
			dice4.setHorizontalAlignment(JTextField.CENTER);
			abiTA.setHorizontalAlignment(JTextField.CENTER);
			atkTA.setHorizontalAlignment(JTextField.CENTER);
			
			dice0.setFont(new Font("Arial", Font.BOLD, 14));
			dice1.setFont(new Font("Arial", Font.BOLD, 14));
			dice2.setFont(new Font("Arial", Font.BOLD, 14));
			dice3.setFont(new Font("Arial", Font.BOLD, 14));
			dice4.setFont(new Font("Arial", Font.BOLD, 14));
			abiTA.setFont(new Font("Arial", Font.BOLD, 14));
			atkTA.setFont(new Font("Arial", Font.BOLD, 14));
			
			d0select = new JRadioButton();
			d1select = new JRadioButton();
			d2select = new JRadioButton();
			d3select = new JRadioButton();
			d4select = new JRadioButton();
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(d0select);
			bg.add(d1select);
			bg.add(d2select);
			bg.add(d3select);
			bg.add(d4select);
			
			sphereBox = new JComboBox();
			abiBox = new JComboBox();
			atkBox = new JComboBox(DAttack.ATKNAMES);
			
			ssName = new JButton();
			changeDice = new JButton("Switch Dice");
			changeAbi = new JButton("Switch Ability");
			changeAtk = new JButton("Switch Attack");
			confirm = new JButton("Confirm");
			cancel = new JButton("Close");
			
			sp = new JList ();
			sp.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			sp.setLayoutOrientation(JList.VERTICAL);
			sp.getSelectionModel().addListSelectionListener(
					this);
			lsp = new JScrollPane(sp);
			lsp.setPreferredSize (new Dimension (200,300));
			
			JPanel overpanel = new JPanel();
			
			JPanel dicePanel = new JPanel();
			JPanel diceLeft = new JPanel();
			JPanel diceRight = new JPanel();
			
			JPanel titlePanel = new JPanel();
			
			JPanel abiPanel = new JPanel();
			JPanel atkPanel = new JPanel();
			JPanel abiTop = new JPanel();
			JPanel atkTop = new JPanel();
			
			JPanel rightPanel = new JPanel();
			JPanel bottomPanel = new JPanel();
						
			abiTop.add(abiTA);
			abiTop.add(changeAbi);
			atkTop.add(atkTA);
			atkTop.add(changeAtk);
			
			abiPanel.add(abiTop);
			abiPanel.add(abiBox);
			atkPanel.add(atkTop);
			atkPanel.add(atkBox);
			
			titlePanel.add(sphereBox);
			titlePanel.add(ssName);
			titlePanel.add(ssElement);
			titlePanel.add(ssChassis);
			titlePanel.add(ssLevel);
			titlePanel.add(confirm);
			diceLeft.add(statLabel0);
			diceLeft.add(dice0);
			diceLeft.add(d0select);
			
			diceLeft.add(statLabel1);
			diceLeft.add(dice1);
			diceLeft.add(d1select);
			
			diceLeft.add(statLabel2);
			diceLeft.add(dice2);
			diceLeft.add(d2select);
			
			diceLeft.add(statLabel3);
			diceLeft.add(dice3);
			diceLeft.add(d3select);
			
			diceLeft.add(statLabel4);
			diceLeft.add(dice4);
			diceLeft.add(d4select);
			
			diceRight.add(lsp);
			diceRight.add(changeDice);
			
			dicePanel.add(diceLeft);
			dicePanel.add(diceRight);
			
			rightPanel.add(abiPanel);
			rightPanel.add(atkPanel);
			
			bottomPanel.add(dicePanel);
			bottomPanel.add(rightPanel);
			
			overpanel.add(titlePanel);
			overpanel.add(bottomPanel);
			overpanel.setPreferredSize(new Dimension(750,430));
			add(overpanel);
			
			//generic border information
			Border rbb = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
			TitledBorder ebd;

			//adding and bordering the panels
			ebd = BorderFactory.createTitledBorder(rbb, "Sphere");
			titlePanel.setBorder(ebd);
			ebd = BorderFactory.createTitledBorder(rbb, "Dice");
			dicePanel.setBorder(ebd);
			ebd = BorderFactory.createTitledBorder(rbb, "Ability");
			abiPanel.setBorder(ebd);
			ebd = BorderFactory.createTitledBorder(rbb, "Attack");
			atkPanel.setBorder(ebd);
			
			bottomPanel.setLayout (new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
			rightPanel.setLayout (new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
			dicePanel.setLayout (new BoxLayout(dicePanel, BoxLayout.X_AXIS));
			abiPanel.setLayout (new BoxLayout(abiPanel, BoxLayout.PAGE_AXIS));
			atkPanel.setLayout (new BoxLayout(atkPanel, BoxLayout.PAGE_AXIS));
			diceLeft.setLayout(new GridLayout(5,3,0,0));
			
			d0select.addActionListener(this);
			d1select.addActionListener(this);
			d2select.addActionListener(this);
			d3select.addActionListener(this);
			d4select.addActionListener(this);
			sphereBox.addActionListener(this);
			abiBox.addActionListener(this);
			atkBox.addActionListener(this);
			
			ssName.addActionListener(this);
			changeDice.addActionListener(this);
			changeAbi.addActionListener(this);
			changeAtk.addActionListener(this);
			confirm.addActionListener(this);
			cancel.addActionListener(this);
			sp.addListSelectionListener(this);
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Customize Sphere");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe () {
			if (!isVisible() && inferSelectedSphere()){
				setVisible(true);
//need disabling method
			}
		}
		public boolean inferSelectedSphere () {
			boolean inferred = false;
			if (selectedSphere != null){
				msSphere = selectedSphere;
				inferred = true;
				updateLabels ();
			}
			return inferred;
		}
		/**
		 * For all labels in CSF
		 */
		public void updateLabels (){
			if (msSphere!=null){
				JList tl = new JList (dgs.myUpgradeDice.toArray());
				sp.setModel(tl.getModel());
				ArrayList <Dsphere> alsphere = (ArrayList<Dsphere>) dgs.myDspheres.clone();
				for (int i=0;i<dgs.myParty.party.length;i++){
					if (dgs.myParty.party[i]!=null){
						alsphere.add(dgs.myParty.party[i]);
					}
				}
				JComboBox temp = new JComboBox (alsphere.toArray());
				sphereBox.setModel(temp.getModel());
				temp = new JComboBox (dgs.myAbilityCores.toArray());
				abiBox.setModel(temp.getModel());
				//no method for attack cores
				temp = new JComboBox (dgs.toStringAttackCore());
				atkBox.setModel(temp.getModel());
				updateDisplay();
			}
		}
		/**
		 * For all but the comboboxes in CSF
		 */
		public void updateDisplay(){
			if (msSphere!=null){
				ssName.setText (msSphere.toString());
				ssLevel.setText ("Level: "+Integer.toString(msSphere.level));
				ssElement.setText("Element: "+ElementalAffinity.ELEMNAMES[msSphere.eleID]);
				ssChassis.setText("Chassis: "+Dchassis.chnames[msSphere.chaID]);
				dice0.setText(msSphere.dice[0].toString());
				dice1.setText(msSphere.dice[1].toString());
				dice2.setText(msSphere.dice[2].toString());
				dice3.setText(msSphere.dice[3].toString());
				dice4.setText(msSphere.dice[4].toString());
				abiTA.setText(msSphere.ability.toString());
				atkTA.setText(DAttack.ATKNAMES[msSphere.atkID]);
				validate();
				repaint();	
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==ssName){
				String nname = JOptionPane.showInputDialog(null, "Enter a name for the Sphere: ");
				if (nname !=null){
					msSphere.myName=nname;
					updateAllLabels();
				}
			}
			if (e.getSource()==confirm){
				setVisible(false);
//				sgd.setEnabled(true);//doesn't work, get comprehensive method for this
//				isActive = false;
//				sgd.enableAll();
				msSphere = null;	
				updateAllLabels();
			}
			if (e.getSource()==sphereBox){
				msSphere = (Dsphere) sphereBox.getSelectedItem();
//				dgs.myDspheres.get(sphereBox.getSelectedIndex());
				updateDisplay();
			}
			if (e.getSource()==abiBox){
				abiDex = abiBox.getSelectedIndex();
			}
			if (e.getSource()==atkBox){
				atkDex = atkBox.getSelectedIndex();
			}
			if (e.getSource()==d0select){
				diceDex=0;
			}
			if (e.getSource()==d1select){
				diceDex=1;
			}
			if (e.getSource()==d2select){
				diceDex=2;
			}
			if (e.getSource()==d3select){
				diceDex=3;
			}
			if (e.getSource()==d4select){
				diceDex=4;
			}
			if (e.getSource()==changeDice && gameRunning && !battleRunning){
				if (selectedDice>-1 && dgs.myUpgradeDice.size()>selectedDice){
					DDice temp = msSphere.dice[diceDex];
					msSphere.dice[diceDex]=dgs.myUpgradeDice.get(selectedDice);
					dgs.myUpgradeDice.remove(selectedDice);
					dgs.myUpgradeDice.add(0, temp);
					updateAllLabels();
				}
			}
			if (e.getSource()==changeAbi && gameRunning && !battleRunning){
				if (abiDex>-1){
					Ability temp = msSphere.ability;
					msSphere.ability=dgs.myAbilityCores.get(abiDex);
					dgs.myAbilityCores.remove(abiDex);
					dgs.myAbilityCores.add(0,temp);
					updateAllLabels();				
				}
			}
			if (e.getSource()==changeAtk && gameRunning && !battleRunning){
				if (dgs.myAttackCores[atkDex]>0){
					dgs.myAttackCores[msSphere.atkID]++;
					msSphere.atkID=atkDex;
					dgs.myAttackCores[atkDex]--;
					updateAllLabels();
				}
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource()==sp.getSelectionModel()){
				try{
					selectedDice = sp.getSelectionModel().getMinSelectionIndex();				
				} catch (Exception ex){
					System.out.println("Null List Error: Customize Sphere Panel");
				}
			}
		}
	}
	private class ShowHelpFrame extends JFrame {
		JTextArea frameLog;
		JScrollPane jsp;
		JPanel jp;

		public ShowHelpFrame (){	
			jp = new JPanel ();
			frameLog = new JTextArea (
					"Magistra: The Game of Ascension \n" +
					"\n" +
					"The goal of Magistra is to amass Scorepoints and gain power by conquering higher levels " +
					"of battle. \n\n" +
					"One does so by winning battles against randomly generated enemies. " +
					"One can also process the loot gained from battles for more Scorepoints. " +
					"The content included in this file is limited. " +
					"I advise consulting any documentation included in the game. " +
					"However, it is likely that the in-game documentation will improve over time." +
					"\n\n" +
					"Spheres \n" +
					"\n" +
					"The body of the souls that fight for you are represented as Spheres. " +
					"Spheres have five statistics: Integrity, Force, Barrier, Gravitas, and Priority. " +
					"They also have a Die that corresponds to each statistic. " +
					"In addition, they gain experience; reaching "+Dsphere.EXP_MAX+" results in a level gain. " +
					"Spheres also possess an Ability and an Attack, which represent its actions in battle. " +
					"Finally, a Sphere has an Element and a Chassis. " +
					"A Sphere's Chassis is merely to help distinguish one Sphere from another and has no other effect. " +
					"A Sphere's Element can subtly influence its performance in battle." +
					"\n\n" +
					"Statistics\n\n" +
					"Integrity represents the amount of damage a Sphere can take before it is destroyed.\n" +
					"Force represents the amount of damage a Sphere does with non-magical attacks.\n" +
					"Barrier represents the degree to which a Sphere can reduce Force-damage. \n" +
					"Gravitas represents the pool of power used to execute Abilities, and is " +
					"reduced depending on the cost of the used Ability. It also affects the " +
					"damage of some Abilities. \n" +
					"Priority is compared to determine which Sphere moves first during a battle." +
					"\n\n" +
					"When a Sphere gains a level, its statistics are increased depending on the current " +
					"Die equipped corresponding to that statistic. One can imagine a Die as a bunch of real Dice " +
					"of varying sides. The first number on a Die corresponds to the number of such dice that are rolled. " +
					"The second number following the D represents the value of each Die. The last number " +
					"is a bonus amount that is added to whatever result the Dice rolled. For example, a " +
					"1D6+0 is a standard six-sided dice representing a gain of 1-6 points per level, and a 4D20+12 represents four twenty sided dice, " +
					"to whose result is added a bonus 12 points, representing a gain of 16-92 points per level.\n\n" +
					"Parties \n" +
					"\n" +
					"A party is a grouping of Spheres into a battle unit. A party may have six members at most. " +
					"Party composition should be changed using the Party Select box on the top left. " +
					"Clear removes a party member, while Set sets the sphere currently selected in the drop down box " +
					"as a member of the party in the slot corresponding to the current tab. Rename simply renames the " +
					"selected party member." +
					"\n\n" +
					"Battle\n\n" +
					"Battle is the primary way by which one earns Scorepoints and other loot. " +
					"One can start a battle by ensuring that one has at least one Sphere in the party, " +
					"then selecting a destination from the drop-down box at the Destination box. " +
					"The game will display the currently selected destination above the Battle! button. " +
					"Battles increase in difficulty from left to right and top down." +
					"Click the Battle! button to begin. " +
					"\n\n" +
					"Battle Resolution\n\n" +
					"The enemy party is displayed on top, and your party is displayed on the bottom. " +
					"Each party member of either part is represented by a screen that displays the " +
					"name, current Integrity, and current Gravitas of each sphere. Below those of your party " +
					"are buttons; the left corresponds to the Sphere's Ability, and the left to the Sphere's attack. " +
					"During a turn, a party overall may only execute one of each. \n\n" +
					"In between both parties are the Resolve and Retreat buttons. Retreat immediately ends " +
					"the current battle. Experience but no loot will be awarded. " +
					"On either side are the total integrities of both parties. This may aid " +
					"in comparing relative strength at a glance." +
					" \n\n" +
					"A battle ends when one party is destroyed or when the player retreats. In the case of victory, " +
					"a player gains loot and experience. Note that ANY DESTROYED SPHERES ARE LOST FOREVER. \n\n" +
					"The statistics of spheres tend to decline after they take action in a battle. Any " +
					"lost statistics can be replenished by Refreshing Levels at the Shop. " +
					"\n\n" +
					"Player Masteries \n\n" +
					"You should use Scorepoints to increase your Masteries using the Purchase Player EXP button in the Shop. " +
					"This will make winning battles and gaining loot much easier. \n" +
					"Elemental Masteries: Increasing an elemental mastery increases the power of Spheres aligned with that element. \n" +
					"Attribute Masteries: Increasing the level of an Attribute Mastery means that your Spheres have higher corresponding Statistics. " +
					"Note: Temperance is extremely important, as higher Temperance means that all experience gain, including player experience, is increased. \n" +
					"Skill Masteries: Increasing these increases the ability of your Spheres to avoid or not miss enemy Spheres in battle. \n" +
					"Finder Masteries: Increasing these increases loot gained in battle as well as allows you to create better Dice" +
					"\n\nHints:\n\n" +
					"Abilities and Attacks are only preliminary, which is why I hesitate to " +
					"provide any concrete information on them; they may only be a moment away from obsolescence." +
					"\n\n" +
					"Abilities: Blitz does damage and increase priority, " +
					"Forge replenishes Gravitas and increases experienced earned " +
					"(but does not stack), " +
					"Burst does Gravitas based damage, and Regen replenishes Integrity. " +
					"Might and Shield increase Force and Barrier respectively. " +
					"The effectiveness of an ability depends on its corresponding statistic; " +
					"Might depends on Force and Blitz on Priority, for example. " +
					"The symbol next to an ability represents its relative power.\n\n" +
					"Attacks: Some attacks like Barrage and Wave attack multiple times, some ignore defense like Critical and Spirit, and " +
					"Nullify even resets enemy stat changes. Do some experimentation to figure out which are most effective " +
					"for your strategy." +
					"");
			frameLog.setLineWrap(true);
			frameLog.setWrapStyleWord(true);
			frameLog.setEditable(false);
			jsp = new JScrollPane (frameLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp.setPreferredSize (new Dimension (300,400));
			jp.add (jsp);
			add (jp);
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Help");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe (){
			setVisible(true);
		}
//		public void initializeFrame (){
//
//		}
		public void clearText (){
			frameLog.setText("");
		}
		public void updateText (String show) {
			frameLog.setText(show);
		}
	}
	private class ShowAboutFrame extends JFrame {
		JTextArea frameLog;
		JScrollPane jsp;
		JPanel jp;

		public ShowAboutFrame (){	
			jp = new JPanel ();
			frameLog = new JTextArea (
					"Magistra: The Game of Ascension \n" +
					"\n" +
					"Alpha version 0.02 \n" +
					"\n" +
					"An essay in programming by Zachary Lim. Please do not distribute this application without the consent of its creator.\n\n" +
					"You wouldn't do that...would you? ;P" +
					"");
			frameLog.setLineWrap(true);
			frameLog.setWrapStyleWord(true);
			frameLog.setEditable(false);
			jsp = new JScrollPane (frameLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp.setPreferredSize (new Dimension (300,400));
			jp.add (jsp);
			add (jp);
			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("About Magistra...");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe (){
			setVisible(true);
		}
//		public void initializeFrame (){
//
//		}
		public void clearText (){
			frameLog.setText("");
		}
		public void updateText (String show) {
			frameLog.setText(show);
		}
	}
	private class HeavenTierFrame extends JFrame implements ActionListener {
		/*
		 * Two buttons:
		 *  One is ascend tower
		 *  Other is Enter Dreamworld
		 * 
		 */
		final static int TIER_COUNT = 15;
		final static int LOCK_COUNT = 5;
		final String [] TIER_NAMES = {
			"Gatekeeper","1st Tier","2nd Tier","3rd Tier","4th Tier",
			"5th Tier","6th Tier","7th Tier","8th Tier","9th Tier",
			"10th Tier","11th Tier","12th Tier","13th Tier","Ascension"
		};
		int [] locks;
		JButton [] tiers;
		JTextArea frameLog;
		JScrollPane jsp;
		JPanel jp,hp,dp;

		public HeavenTierFrame (){
			JPanel op = new JPanel(); 
			op.setLayout (new BorderLayout());
			
			jp = new JPanel ();
			hp = new JPanel ();
			dp = new JPanel ();
			frameLog = new JTextArea (
					"Ascend the tiers, if you can. \n" +
					"Defeating one's guardian shatters a lock. \n" +
					"Shatter all five to access the next tier." +
					"");
			frameLog.setLineWrap(true);
			frameLog.setWrapStyleWord(true);
			frameLog.setEditable(false);
			jsp = new JScrollPane (frameLog,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			locks = new int [TIER_COUNT];
			tiers = new JButton[TIER_COUNT];
			
			hp.setLayout(new BoxLayout(hp,BoxLayout.PAGE_AXIS));
			for (int i=0;i<TIER_COUNT;i++){
				locks[i]=LOCK_COUNT;
				tiers[i]=new JButton (TIER_NAMES[i]+", Locks: "+locks[i]);
			}
			
			jp.setPreferredSize (new Dimension (300,400));
			jp.add (jsp,BorderLayout.NORTH);
			
			op.add(jp);
			op.add(hp);
			op.add(dp);
			
			add (jp);
			

			
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setTitle("Tiers of Ascension");

			getContentPane();
			pack();
			setVisible(false);
		}
		public void showMe (){
			setVisible(true);
		}
		public void updateTiers (){
			for (int i=0;i<tiers.length;i++){
				tiers[i].setText(TIER_NAMES[i]+", Locks: "+locks[i]);
			}
		}
//		public void initializeFrame (){
//
//		}
		public void clearText (){
			frameLog.setText("");
		}
		public void updateText (String show) {
			frameLog.setText(show);
		}
		public void actionPerformed(ActionEvent e) {
			//if locks >0, allow a battle when a tier is reached
			if (gameRunning && !battleRunning ){
				for (int i=0;i<tiers.length;i++){
					if (e.getSource()==tiers[i] && locks [i]>0 && i>0 && locks[i-1]==0){
						
					}
				}
				updateTiers();
			}
		}
	}
}