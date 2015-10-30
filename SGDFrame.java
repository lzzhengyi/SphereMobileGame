package SphereGameDemo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class SGDFrame extends JFrame implements ActionListener, ItemListener {

	JMenuBar dMenuBar;
	JMenu dMenu0, dMenu1, dMenu2;
	JMenuItem newgame,save,load,exit, sphereViewer, battleLog, showAbout, showHelp;
	public SGDPanel sgd;

	public SGDFrame (){
		dMenuBar = new JMenuBar();
		dMenu0 = new JMenu("File");
		dMenu0.setMnemonic(KeyEvent.VK_F);
		dMenu1 = new JMenu("Windows");
		dMenu1.setMnemonic(KeyEvent.VK_W);
		dMenu2 = new JMenu("Help");
		dMenu2.setMnemonic(KeyEvent.VK_H);
		newgame = new JMenuItem("New Game",KeyEvent.VK_N);
		save = new JMenuItem("Save",KeyEvent.VK_S);
		load = new JMenuItem("Load",KeyEvent.VK_L);
		exit = new JMenuItem("Exit",KeyEvent.VK_X);
		sphereViewer = new JMenuItem("Show Sphere Viewer",KeyEvent.VK_S);
		battleLog = new JMenuItem("Show Battle Log",KeyEvent.VK_B);
		showAbout = new JMenuItem("About Magistra");
		showHelp = new JMenuItem("Playing Magistra",KeyEvent.VK_P);
		dMenu0.add (newgame);
		dMenu0.add (save);
		dMenu0.add (load);
		dMenu0.add (exit);
		dMenu1.add (sphereViewer);
		dMenu1.add (battleLog);
		dMenu2.add (showHelp);
		dMenu2.add (showAbout);
		dMenuBar.add (dMenu0);
		dMenuBar.add (dMenu1);
		dMenuBar.add (dMenu2);
		setJMenuBar (dMenuBar);

		newgame.addActionListener(this);
		save.addActionListener(this);
		load.addActionListener(this);
		exit.addActionListener(this);
		sphereViewer.addActionListener(this);
		battleLog.addActionListener(this);
		showAbout.addActionListener(this);
		showHelp.addActionListener(this);
		sphereViewer.addActionListener(this);
		battleLog.addActionListener(this);
//		sphereViewer.addItemListener(this);
//		battleLog.addItemListener(this);


		sgd = new SGDPanel();
		setLayout(new BorderLayout());
		sgd.setPreferredSize (new Dimension (1050,850));
		JScrollPane sgdjsp=new JScrollPane(sgd);
		add (sgdjsp);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newgame)){
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?")==JOptionPane.OK_OPTION){
				String title = JOptionPane.showInputDialog(null, "What is your player name?");
				if (title.equals("")){
					title = "Player";
				}
				Object [] choices = {"Preset Spheres","Random Spheres"};
				if (JOptionPane.showOptionDialog(null,
						"Use Preselected or Random Spheres?",
						"Select Starting Spheres",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						choices,
						choices[1])==JOptionPane.YES_OPTION){
					sgd.startNewGame(title, true);
				} else {
					sgd.startNewGame(title, false);
				}
			}	
		}
		if (e.getSource().equals(save)){
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to save? You may overwrite a previous save.")==JOptionPane.OK_OPTION){
				sgd.saveState();				
			}
		}
		if (e.getSource().equals(load)){
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to load? You will lose unsaved progress.")==JOptionPane.OK_OPTION){
				try
				{
					FileInputStream fileIn =
						new FileInputStream("savestate.ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					DGameState ngs = (DGameState) in.readObject();
					in.close();
					fileIn.close();
					sgd.loadState(ngs);
				}catch(IOException i)
				{
					i.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error: Stream/Conversion Error");
					return;
				}catch(ClassNotFoundException c)
				{
					System.out.println("Error: savestate not found");
					c.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error: savestate.ser not found");
					return;
				}			
			}
		}
		if (e.getSource().equals(exit)){
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit? You may lose unsaved progress.")==JOptionPane.OK_OPTION){
				this.dispose();
				sgd.disposeSubframes();			
			}
		}
		if (e.getSource() ==sphereViewer){
			if (sgd.gameRunning){
				sgd.showFrame(SGDPanel.SPHEREVIEWER);
			}
		}
		if (e.getSource() ==battleLog){
			if (sgd.gameRunning){
				sgd.showFrame(SGDPanel.BATTLELOG);
			}
		}
		if (e.getSource() ==showAbout){
			sgd.showFrame(SGDPanel.SHOWABOUT);
		}
		if (e.getSource() ==showHelp){
			sgd.showFrame(SGDPanel.SHOWHELP);
		}
	}
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source ==sphereViewer){
			if (e.getStateChange()==ItemEvent.DESELECTED){
				if (sgd.gameRunning){
					sgd.hideFrame(SGDPanel.SPHEREVIEWER);
				}
			}
			if (e.getStateChange()==ItemEvent.SELECTED){
				if (sgd.gameRunning){
					sgd.showFrame(SGDPanel.SPHEREVIEWER);
				}
			}
		}
		if (source ==battleLog){
			if (e.getStateChange()==ItemEvent.DESELECTED){
				if (sgd.gameRunning){
					sgd.hideFrame(SGDPanel.BATTLELOG);
				}
			}
			if (e.getStateChange()==ItemEvent.SELECTED){
				if (sgd.gameRunning){
					sgd.showFrame(SGDPanel.BATTLELOG);
				}
			}
		}
	}
}
