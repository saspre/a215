package cubedrawer;


import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import cubedrawer.CubeDraw.MoveButtons;


public class SidePanel extends JPanel {
	private TreeMap<MoveButtons, JButton> twistButtons;

	public SidePanel() {
		this.setLayout(new GridLayout(0,2));
		twistButtons = new TreeMap<MoveButtons, JButton>();
		for(MoveButtons key : MoveButtons.values()){
			twistButtons.put(key, new JButton(key.toString()));
			this.add(twistButtons.get(key));
		}
		
	}
	
	public void addActionListener(ActionListener l){
		for(MoveButtons key : MoveButtons.values()){
			twistButtons.get(key).addActionListener(l);
		}
	}

	public TreeMap<MoveButtons, JButton> getTwistButtons(){
		return twistButtons;
	}
	
}
