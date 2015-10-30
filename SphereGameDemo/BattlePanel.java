package SphereGameDemo;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BattlePanel extends JPanel {

	//normally black with disabled buttons
	//this is redundant
//	boolean isPlayer;
	public Dsphere myDsphere;
	public JTextArea jta;
	
	public BattlePanel (Dsphere oj){
		myDsphere = oj;
		
		setBackground(Color.blue);
		Border rbb = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		TitledBorder ebd;
		ebd = BorderFactory.createTitledBorder(rbb, "");
		setBorder(ebd);
		
		jta = new JTextArea();
		jta.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(jta);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//		scrollPane.setPreferredSize(new Dimension(40, 40));
		
		add (scrollPane);
	}
	/*
	 * initialize with two parties
	 * when this happens, reenable buttons
	 * upon battle victory, the center shows loot
	 */
	public void updateDisplay (){
		String dtext = 
			myDsphere.toString()+"\n"+
			"CI:"+Integer.toString(myDsphere.curr_int)+"\n"+
			"CG:"+Integer.toString(myDsphere.curr_mag);
		jta.setText (dtext);
	}
	
	/*
	 * battle resolution 
	 */
}
