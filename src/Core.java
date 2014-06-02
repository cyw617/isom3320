import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class Core extends JApplet implements Runnable, KeyListener {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	//frame
	public static final int frameWidth = 672, frameHeight = 480;
	
	//key control
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, ATK = 4, WP_L = 5, WP_R = 6;
	public static boolean[][] key;
	public static boolean[] isKeyFrozen;
	public static boolean keyEsc;
	
	//pane for displaying the graphics
	public static JLayeredPane mainPane;
	public static final int LAYER_MAX = 21;
	public static final Integer[] LAYER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	/*
	 * 0-17: Game
	 * 18: Game Over
	 * 19: Ranking
	 * 20: Menu
	 */
	
	//game menu
	private JLabel bg;
	private JLabel onePlayer, onePlayer_onFocus, twoPlayer, twoPlayer_onFocus, ranking, ranking_onFocus;
	private int choice;
	private final int choice_Max = 3;
	
	

	/*------------------------------------------------------------------------------------------*/
	/*-----------------------------------Initialization & Run-----------------------------------*/
	/*------------------------------------------------------------------------------------------*/
	@Override
	public void init() {
		setSize(frameWidth, frameHeight);
		
		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(this);
		
		//initialize the key control
		key = new boolean[2][7];
		for (int i = 0; i < key.length; i++) {
			for (int j = 0; j < key[i].length; j++)
				key[i][j] = false;
		}
		isKeyFrozen = new boolean[2];
		isKeyFrozen[0] = isKeyFrozen[1] = false;
		
		//instantiate a pane for displaying the graphics
		mainPane = new JLayeredPane();
		
		//instantiate all game-related objects
		new ObjectContainer();
		
		//instantiate menu's buttons
		bg = createLabel("bg.gif");
		onePlayer = createLabel("1p.gif");
		onePlayer.setLocation(240, 180);
		onePlayer_onFocus = createLabel("1p_onFocus.gif");
		onePlayer_onFocus.setLocation(240, 180);
		twoPlayer = createLabel("2p.gif");
		twoPlayer.setLocation(240, 240);
		twoPlayer_onFocus = createLabel("2p_onFocus.gif");
		twoPlayer_onFocus.setLocation(240, 240);
		ranking = createLabel("ranking.gif");
		ranking.setLocation(240, 300);
		ranking_onFocus = createLabel("ranking_onFocus.gif");
		ranking_onFocus.setLocation(240, 300);
		mainPane.add(onePlayer, LAYER[20]);
		mainPane.add(onePlayer_onFocus, LAYER[20]);
		mainPane.add(twoPlayer, LAYER[20]);
		mainPane.add(twoPlayer_onFocus, LAYER[20]);
		mainPane.add(ranking, LAYER[20]);
		mainPane.add(ranking_onFocus, LAYER[20]);
		mainPane.add(bg, LAYER[20]);
		
		//start the thread
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		//add the layered pane onto the panel
		add(mainPane);
		
		while (true) {
			System.out.println("<--------------------------------- Start --------------------------------->");
			
			hideAllLayers();
			
			//configure the display of menu
			choice = 0;
			bg.setVisible(true);
			while (true) {
				if (key[0][UP] && !isKeyFrozen[0]) {
					isKeyFrozen[0] = true;
					if (--choice < 0)
						choice = choice_Max - 1;
				}
				else if (key[0][DOWN] && !isKeyFrozen[0]) {
					isKeyFrozen[0] = true;
					if (++choice == choice_Max)
						choice = 0;
				}
				else if (key[0][ATK] && !isKeyFrozen[0]) {
					isKeyFrozen[0] = true;
					break;
				}
				
				switch (choice) {
				case 0:
					onePlayer.setVisible(false);
					onePlayer_onFocus.setVisible(true);
					twoPlayer.setVisible(true);
					twoPlayer_onFocus.setVisible(false);
					ranking.setVisible(true);
					ranking_onFocus.setVisible(false);
					break;
				case 1:
					onePlayer.setVisible(true);
					onePlayer_onFocus.setVisible(false);
					twoPlayer.setVisible(false);
					twoPlayer_onFocus.setVisible(true);
					ranking.setVisible(true);
					ranking_onFocus.setVisible(false);
					break;
				case 2:
					onePlayer.setVisible(true);
					onePlayer_onFocus.setVisible(false);
					twoPlayer.setVisible(true);
					twoPlayer_onFocus.setVisible(false);
					ranking.setVisible(false);
					ranking_onFocus.setVisible(true);
					break;
				}
				
				try {
					Thread.sleep(65);
				}
				catch (InterruptedException ex) {}
			}
			
			hideLayer(20);
			
			switch (choice) {
			case 0:
				System.out.println("One-player Mode");
				(ObjectContainer.gameControl).setTwoPlayer(false);
				(ObjectContainer.gameControl).activate();
				break;
			case 1:
				System.out.println("Two-player Mode");
				(ObjectContainer.gameControl).setTwoPlayer(true);
				(ObjectContainer.gameControl).activate();
				break;
			case 2:
				System.out.println("The rank is under construction.");
				break;
			}
			
			System.out.println("<---------------------------------- End ---------------------------------->");
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	private JLabel createLabel(String path) {
		URL imageURL = this.getClass().getResource("images/" + path);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		JLabel imageLabel = new JLabel(imageIcon);
		
		imageLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		imageLabel.setVisible(false);
		
		return imageLabel;
	}
	
	public static JLabel createLabel(String text, String name, int style, int size, Color fontColor, int width, int height) {
		JLabel textLabel = new JLabel(text);
		textLabel.setFont(new Font(name, style, size));
		textLabel.setForeground(fontColor);
		textLabel.setBounds(0, 0, width, height);
		textLabel.setVisible(false);
		
		return textLabel;
	}
	
	public static JLabel createLabel(String text, String name, int style, int size, Color fontColor, Color bgColor, int width, int height) {
		JLabel textLabel = createLabel(text, name, style, size, fontColor, width, height);
		textLabel.setBackground(bgColor);
		textLabel.setOpaque(true);
		
		return textLabel;
	}
	
	private void showLayer(int n) {
		Component[] components = mainPane.getComponentsInLayer(n);
		
		for (int i = 0; i < components.length; i++)
			components[i].setVisible(true);
	}
	
	public static void hideLayer(int n) {
		Component[] components = mainPane.getComponentsInLayer(n);
		
		for (int i = 0; i < components.length; i++)
			components[i].setVisible(false);
	}
	
	public static void hideAllLayers() {
		Component[] components = null;
		for (int n = 0; n < LAYER_MAX; n++) {
			components = mainPane.getComponentsInLayer(n);
			
			for (int i = 0; i < components.length; i++)
				components[i].setVisible(false);
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Key Control----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		//---Exit
		case KeyEvent.VK_ESCAPE:
			keyEsc = true;
			break;
		
		//---Player 1
		case KeyEvent.VK_UP:
			key[0][UP] = true;
			System.out.println("Pressed: [0] UP");
			break;
		case KeyEvent.VK_DOWN:
			key[0][DOWN] = true;
			System.out.println("Pressed: [0] Down");
			break;
		case KeyEvent.VK_LEFT:
			key[0][LEFT] = true;
			System.out.println("Pressed: [0] Left");
			break;
		case KeyEvent.VK_RIGHT:
			key[0][RIGHT] = true;
			System.out.println("Pressed: [0] Right");
			break;
		case KeyEvent.VK_ENTER:
			key[0][ATK] = true;
			System.out.println("Pressed: [0] Attack");
			break;
		case KeyEvent.VK_O:
			key[0][WP_L] = true;
			System.out.println("Pressed: [0] WeaponL");
			break;
		case KeyEvent.VK_P:
			key[0][WP_R] = true;
			System.out.println("Pressed: [0] WeaponR");
			break;

		//---Player 2
		case KeyEvent.VK_R:
			key[1][UP] = true;
			System.out.println("Pressed: [1] UP");
			break;
		case KeyEvent.VK_F:
			key[1][DOWN] = true;
			System.out.println("Pressed: [1] Down");
			break;
		case KeyEvent.VK_D:
			key[1][LEFT] = true;
			System.out.println("Pressed: [1] Left");
			break;
		case KeyEvent.VK_G:
			key[1][RIGHT] = true;
			System.out.println("Pressed: [1] Right");
			break;
		case KeyEvent.VK_A:
			key[1][ATK] = true;
			System.out.println("Pressed: [1] Attack");
			break;
		case KeyEvent.VK_Q:
			key[1][WP_L] = true;
			System.out.println("Pressed: [1] WeaponL");
			break;
		case KeyEvent.VK_W:
			key[1][WP_R] = true;
			System.out.println("Pressed: [1] WeaponR");
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		//---Exit
		case KeyEvent.VK_ESCAPE:
			keyEsc = false;
			break;
		
		//---Player 1
		case KeyEvent.VK_UP:
			key[0][UP] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] UP");
			break;
		case KeyEvent.VK_DOWN:
			key[0][DOWN] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] Down");
			break;
		case KeyEvent.VK_LEFT:
			key[0][LEFT] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] Left");
			break;
		case KeyEvent.VK_RIGHT:
			key[0][RIGHT] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] Right");
			break;
		case KeyEvent.VK_ENTER:
			key[0][ATK] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] Attack");
			break;
		case KeyEvent.VK_O:
			key[0][WP_L] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] WeaponL");
			break;
		case KeyEvent.VK_P:
			key[0][WP_R] = false;
			isKeyFrozen[0] = false;
			System.out.println("Released: [0] WeaponR");
			break;
		
		//---Player 2
		case KeyEvent.VK_R:
			key[1][UP] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] UP");
			break;
		case KeyEvent.VK_F:
			key[1][DOWN] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] Down");
			break;
		case KeyEvent.VK_D:
			key[1][LEFT] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] Left");
			break;
		case KeyEvent.VK_G:
			key[1][RIGHT] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] Right");
			break;
		case KeyEvent.VK_A:
			key[1][ATK] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] Attack");
			break;
		case KeyEvent.VK_Q:
			key[1][WP_L] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] WeaponL");
			break;
		case KeyEvent.VK_W:
			key[1][WP_R] = false;
			isKeyFrozen[1] = false;
			System.out.println("Released: [1] WeaponR");
			break;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
}