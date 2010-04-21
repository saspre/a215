package cubedrawer;




import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Port;
import javax.swing.JPanel;
import javax.swing.Timer;

import algorithms.*;

import static cubedrawer.MoveButtons.*;
import cube.CornerCubie;
import cube.Cube;
import cube.EdgeCubie;
import cube.Face;

public class DrawPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cube cube;
	private int rectHW = 30; 
	private int dispHW = 10; //the distance moved after drawing each polygon
	private int startDelay = 500;
	private Console console;
	private boolean moving;
	private boolean specialMove;
	private boolean doNotSaveNextMove;
	private EnumSet<MoveButtons> moves = EnumSet.of(U, UP ,U2, D, DP, D2, F, FP,F2,  B, BP, B2, L, LP, L2, R, RP , R2);
	private Timer scrambleDanceTimer;
	private Timer playDanceTimer;
	private MP3 mp3;
	private Beginners beginners;
	private Kociemba kociemba;
	//private ArrayList<MoveButtons> previousMoves;
	private LinkedList<MoveButtons> previousMoves;

	public DrawPanel(Console console) {
		this.console = console;
		cube = new Cube();
		this.setBackground(Color.white);
		//this.setPreferredSize(new Dimension(400,300));
		console.addTextln("Behold the Cube ");
		//previousMoves = new ArrayList<MoveButtons>();
		previousMoves = new LinkedList<MoveButtons>();

		beginners = new Beginners(cube);
		
		this.setPreferredSize(new Dimension(20 + rectHW*12 , 20 + rectHW*9));
		scrambleDanceTimer = new Timer(startDelay, new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 	
				scramble(1); 
				if(scrambleDanceTimer.getDelay() > 100){ scrambleDanceTimer.setDelay(scrambleDanceTimer.getDelay() - 23); }
				repaint(); 
			}
		});
		playDanceTimer = new Timer(2*60*1000+28*1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mp3.play();
			}
		});


		String filename = "Khachaturian-Sabre_Dance.mp3";
		mp3 = new MP3(filename);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		int startX = 10;
		int startY = 10;
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING ,    RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(this.getBackground());
		g2.fillRect(0, 0, this.getWidth() + 1, this.getHeight());


		draw3x3(startX  + 3*rectHW , startY + 3*rectHW * 2,g2, cube.getPrimary()[1]);
		draw3x3(startX , startY + 3*rectHW,g2, cube.getTertiary()[0]);
		draw3x3(startX  + 3*rectHW , startY + 3*rectHW,g2 ,cube.getSecondary()[0]);
		draw3x3(startX  + 3*rectHW*3 , startY + 3*rectHW,g2, cube.getSecondary()[1]);
		draw3x3(startX + 3*rectHW, startY,g2,cube.getPrimary()[0]);
		draw3x3(startX  + 3*rectHW*2 , startY + 3*rectHW,g2, cube.getTertiary()[1]);

		//draw3x3(startX  + 8*rectHW , startY + 2*rectHW,g2, cube.getSecondary()[1]);
		//draw3x3poly(startX + 3*rectHW + 4*dispHW, startY + 7*dispHW, g2,cube.getPrimary()[0]);

	}


	public void draw3x3(int x, int y, Graphics2D g, Face face){
		byte cornerCount = 0;
		byte edgeCount = 0;
		int[] newCornerOrder = {0 , 1, 3 ,2 };
		int[] newEdgeOrder = {0 , 3, 1 ,2 };
		int faceOrder = (int) Math.floor(face.getFacelet().ordinal()/2);
		for(int i = 0; i < 9; i++){
			g.setColor(Color.black);
			g.drawRect(i%3*rectHW + x, (int)Math.ceil(i/3)*rectHW + y, rectHW, rectHW);
			if(i == 4){
				g.setColor(face.getFacelet().toColor());
				g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
			} else if(i%2 == 0){
				CornerCubie ccubie = face.getCornerCubicle()[newCornerOrder[cornerCount]].getCubie();

				if(ccubie.getPrimaryOrientation() == faceOrder){
					g.setColor(ccubie.getFacelet(0).toColor());
				} else {
					if (ccubie.getSecondaryOrientation() == faceOrder){
						g.setColor(ccubie.getFacelet(1).toColor());
					} else {
						g.setColor(ccubie.getFacelet(2).toColor());
					}
				}

				g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
				g.setColor(Color.black);
				cornerCount++;

			} else if (i%2 != 0){
				EdgeCubie ecubie = face.getEdgeCubicle()[newEdgeOrder[edgeCount]].getCubie();
				if(faceOrder == 0){
					g.setColor(ecubie.getFacelet(ecubie.getPrimaryOrientation()).toColor());
				} else 	if(faceOrder == 1){
					if(i == 7 || i == 1){
						if(ecubie.getPrimaryOrientation() == 0){
							g.setColor(ecubie.getFacelet(1).toColor());
						} else {
							g.setColor(ecubie.getFacelet(0).toColor());
						}
					} else {
						if(ecubie.getPrimaryOrientation() == 0){
							g.setColor(ecubie.getFacelet(0).toColor());
						} else {
							g.setColor(ecubie.getFacelet(1).toColor());
						}
					}
				} else 	if(faceOrder == 2){
					if(ecubie.getPrimaryOrientation() == 0){
						g.setColor(ecubie.getFacelet(1).toColor());
					} else {
						g.setColor(ecubie.getFacelet(0).toColor());
					}
				}

				edgeCount++;
				g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
			}

			g.setColor(Color.black);
			g.drawRect(i%3*rectHW + x, (int)Math.ceil(i/3)*rectHW + y, rectHW, rectHW);

		}
		g.drawRect(x - 1, y - 1, 3*rectHW, 3*rectHW);
	}

	public void draw3x3poly(int x, int y, Graphics2D g, Face face){

		int[] listX = {x, x + 2*dispHW, x + rectHW + 2*dispHW, x + rectHW};
		int[] tempX = new int[4];
		int[] listY = {y, y - dispHW, y - dispHW, y};
		int[] tempY = new int[4];
		byte cornerCount = 0;
		byte edgeCount = 0;
		int[] newCornerOrder = {0 , 1, 3 ,2 };
		int[] newEdgeOrder = {0 , 3, 1 ,2 };
		int faceOrder = (int) Math.floor(face.getFacelet().ordinal()/2);

		g.drawPolygon(new int[]{x + 2*dispHW, x - 4*dispHW, x - dispHW + 2*rectHW, x + 2*dispHW + 3*rectHW}, 
				new int[]{y - dispHW - 1, y + 2*dispHW - 1, y + 2*dispHW - 1, y - dispHW - 1}, 4);
		for(int i = 0; i < 9; i++){
			g.setColor(Color.black);
			//g.drawRect(i%3*rectHW + x, (int)Math.ceil(i/3)*rectHW + y, rectHW, rectHW);
			//g.drawPolygon(,, 4);

			for (int j = 0; j < 4; j++) {
				tempX[j] = listX[j] + i%3*rectHW - ((int)Math.ceil(i/3)*2*dispHW);
				tempY[j] = listY[j] + (int)Math.ceil(i/3)*dispHW;
			}
			g.drawPolygon(tempX, tempY, 4);

			if(i == 4){
				g.setColor(face.getFacelet().toColor());
				//g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
			} else if(i%2 == 0){
				CornerCubie ccubie = face.getCornerCubicle()[newCornerOrder[cornerCount]].getCubie();

				if(ccubie.getPrimaryOrientation() == faceOrder){
					g.setColor(ccubie.getFacelet(0).toColor());
				} else {
					if (ccubie.getSecondaryOrientation() == faceOrder){
						g.setColor(ccubie.getFacelet(1).toColor());
					} else {
						g.setColor(ccubie.getFacelet(2).toColor());
					}
				}

				//g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
				g.setColor(Color.black);
				cornerCount++;

			} else if (i%2 != 0){
				EdgeCubie ecubie = face.getEdgeCubicle()[newEdgeOrder[edgeCount]].getCubie();
				if(faceOrder == 0){
					g.setColor(ecubie.getFacelet(ecubie.getPrimaryOrientation()).toColor());
				} else 	if(faceOrder == 1){
					if(i == 7 || i == 1){
						if(ecubie.getPrimaryOrientation() == 0){
							g.setColor(ecubie.getFacelet(1).toColor());
						} else {
							g.setColor(ecubie.getFacelet(0).toColor());
						}
					} else {
						if(ecubie.getPrimaryOrientation() == 0){
							g.setColor(ecubie.getFacelet(0).toColor());
						} else {
							g.setColor(ecubie.getFacelet(1).toColor());
						}
					}
				} else 	if(faceOrder == 2){
					if(ecubie.getPrimaryOrientation() == 0){
						g.setColor(ecubie.getFacelet(1).toColor());
					} else {
						g.setColor(ecubie.getFacelet(0).toColor());
					}
				}

				edgeCount++;
				//g.fillRect(i%3*rectHW + x + 1, (int)Math.ceil(i/3)*rectHW + y + 1, rectHW - 1, rectHW -1);
			}

			g.setColor(Color.black);
			//g.drawRect(i%3*rectHW + x, (int)Math.ceil(i/3)*rectHW + y, rectHW, rectHW);

		}
		//g.drawRect(x - 1, y - 1, 3*rectHW, 3*rectHW);





		//g.setColor(Color.black);
		//g.drawPolygon(listX, listY, 4);
		/*
		for(int i = 0; i < 9; i++){

			listX[j] = listX[j] + dispHW * i;

		g.setColor(Color.black);
		g.drawPolygon(listX, listY, 4);
		 */
	}

	public void buttonHandler(MoveButtons t){
		if(!specialMove && moves.contains(t) && !doNotSaveNextMove){
			startMoving();
			console.addText(t + " ");

			previousMoves.add(t);
		} else if(doNotSaveNextMove) {

			doNotSaveNextMove = false;
		}

		switch(t){
		case U:
			cube.getPrimary()[0].cwTwist();
			break;
		case UP:
			cube.getPrimary()[0].ccwTwist();
			break;
		case U2:
			cube.getPrimary()[0].cwTwist();
			cube.getPrimary()[0].cwTwist();
			break;
		case D:
			cube.getPrimary()[1].cwTwist();
			break;
		case DP:
			cube.getPrimary()[1].ccwTwist();
			break;
		case D2:
			cube.getPrimary()[1].cwTwist();
			cube.getPrimary()[1].cwTwist();
			break;
		case F:
			cube.getSecondary()[0].cwTwist();
			break;
		case FP:
			cube.getSecondary()[0].ccwTwist();
			break;
		case F2:
			cube.getSecondary()[0].cwTwist();
			cube.getSecondary()[0].cwTwist();
			break;
		case B:
			cube.getSecondary()[1].cwTwist();
			break;
		case BP:
			cube.getSecondary()[1].ccwTwist();
			break;
		case B2:
			cube.getSecondary()[1].cwTwist();
			cube.getSecondary()[1].cwTwist();
			break;
		case L:
			cube.getTertiary()[0].cwTwist();
			break;
		case LP:
			cube.getTertiary()[0].ccwTwist();
			break;
		case L2:
			cube.getTertiary()[0].cwTwist();
			cube.getTertiary()[0].cwTwist();
			break;
		case R:
			cube.getTertiary()[1].cwTwist();
			break;
		case RP:
			cube.getTertiary()[1].ccwTwist();
			break;
		case R2:
			cube.getTertiary()[1].cwTwist();
			cube.getTertiary()[1].cwTwist();
			break;
		case SCREWDRIVER:
			reset();
			break;
		case SCRAMBLE:
			scramble(50);
			break;
		case YOU_KNOW:
			setSystemVolume(30000);
			if(scrambleDanceTimer.isRunning()){
				//System.out.println("aha");
				mp3.close();
				scrambleDanceTimer.stop();
				playDanceTimer.stop();
				scrambleDanceTimer.setDelay(startDelay);

			} else {
				mp3.play();
				playDanceTimer.start();
				scrambleDanceTimer.start();
			}
			break;
		case UNDO:
			undo();
			break;
		case KOCIEMBA:
			kociemba();
			break;
			
		case BEGINNERS:
			stopMoving();
			//moving = true;
			//specialMove = true;
			console.addTextln("Beginners Algortihm: ");
			beginners.solve();
			//specialMove = false;
			break;
		default:
			console.addTextln("Something is wrong");

		}	


	}

	private void startMoving(){
		if(moving == false){
			previousMoves.clear();
			moving = true;
			console.addText("Applying Moves: ");
		}
	}

	private void stopMoving(){
		if(moving){

			moving = false;
			console.addTextln("");
		}
	}

	public void youKnowMove(){

		stopMoving();
		moving = true;
		console.addText("Pons asinurum:");
		twistSequence(U2, D2,F2, B2,  L2, R2);
		//console.addTextln("");
		//moving = false;
		//moving = false;
	}

	/**
	 * 
	 * @param n the number of moves to be performed on the cube
	 */
	private void scramble(int n){
		stopMoving();
		specialMove = true;
		// TODO Auto-generated method stub
		String moveSequence = "";
		console.addText("Scrambling:");
		for(int i = 0; i < n; i++){
			int moveNum = (int)(Math.random()*18);
			twistSequence((MoveButtons)moves.toArray()[moveNum]);
			moveSequence = moveSequence + " " + ((MoveButtons)moves.toArray()[moveNum]).toString();
		}
		console.addTextln(moveSequence);
		specialMove = false;
		/*
		if(scrambles > 500){
			timer.stop();
			mp3.close();
		}
		scrambles++;
		 */
	}

	public void twistSequence(MoveButtons... t){
		//stopMoving();
		for(MoveButtons key: t){
			buttonHandler(key);
			previousMoves.add(key);
		}
	}
	
	public void twistSequence(ArrayList<MoveButtons> e){
		//stopMoving();
		for(MoveButtons key: e){
			buttonHandler(key);
			previousMoves.add(key);
		}
	}

	public void reset(){
		stopMoving();
		previousMoves.clear();
		console.addTextln("Pick up screwdriver, disassemble cube, assemble cube correctly \n");
		cube = new Cube();
		beginners = new Beginners(cube);
		kociemba = new Kociemba(cube);
		repaint();
	}



	public Cube getCube(){
		return cube;
	}

	public void setSystemVolume(final int vol)
	{
		final Port lineOut;
		try
		{
			if(AudioSystem.isLineSupported(Port.Info.LINE_OUT))
			{
				lineOut = (Port)AudioSystem.getLine(Port.Info.LINE_OUT);
				lineOut.open();
			}
			else if(AudioSystem.isLineSupported(Port.Info.HEADPHONE))
			{
				lineOut = (Port)AudioSystem.getLine(Port.Info.HEADPHONE);
				lineOut.open();
			}
			else if(AudioSystem.isLineSupported(Port.Info.SPEAKER))
			{
				lineOut = (Port)AudioSystem.getLine(Port.Info.SPEAKER);
				lineOut.open();
			}
			else
			{
				System.out.println("Unable to get Output Port");
				return;
			}

			final FloatControl controlIn = (FloatControl)lineOut.getControl(FloatControl.Type.VOLUME);
			final float volume = 100 * (controlIn.getValue() / controlIn.getMaximum());
			controlIn.setValue((float)vol / 100);
			System.out.println("SetSystemVolume : volume = " + volume);
		}
		catch(final Exception e)
		{
			System.out.println(e + " LINE_OUT");
		}
	}

	private void undo() {
		stopMoving();
		doNotSaveNextMove = true;
		try {
		buttonHandler(previousMoves.removeLast().invert());
		}
		catch (NoSuchElementException e) {
			// TODO: handle exception
		}
	}

	private void kociemba() {
		stopMoving();
		//console.addTextln("Solving with Kociemba's algorithm, please wait.");
		if (cube.isInH()){
			if (cube.isSolved()) {
				console.addTextln("The cube is solved!");
			} else {
				console.addTextln("The cube is in the subgroup H!");
			}
		} else {
			console.addTextln("The cube is not in the subgroup H!");
		}
		
		
		/*
		kociemba.solve(12);
		*/
	}
}
