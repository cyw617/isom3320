import java.awt.Color;
import java.net.URL;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GameControl implements Runnable {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	//control key
	private final boolean[][] key = Core.key;
	
	//two-player option
	private boolean isTwoPlayer;
	
	//option panel
	private boolean isStartGame, isReturnMenu;
	private final JLabel optionBg, optionTitle;
	private final JLabel[] rowChoiceBg;
	private final JLabel[] heroStatus, heroStatus_Selected;
	private final JLabel[] bg, bgPreview;
	private final JLabel startButton;
	private final JLabel[] startButton_Selected;
	private int rowChoice, mapChoice, startChoice;
	private int[] heroChoice;
	private final int rowChoice_Max = 3, heroChoice_Max = 4, mapChoice_Max = 2, startChoice_Max = 2;
	
	//display game status
	private int score;
	private JLabel scoreLabel;
	private JLabel[] playerLabel, hpLabel, weaponLabel, ammoLabel;
	
	//id of the top layer
	private final int topLayer = 17;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public GameControl() {		
		/*============================ Start: Option-related ============================*/
		//create labels & add labels to the main pane		
		//heros
		this.heroStatus = new JLabel[4];
		for (int i = 0, x = 65, y = 95; i < 4; ++i, x += 138) {
			this.heroStatus[i] = this.createLabel("gameOption/hero_" + i + ".png");
			this.heroStatus[i].setLocation(x, y);
			Core.mainPane.add(this.heroStatus[i], Core.LAYER[topLayer]);
		}
		this.heroStatus_Selected = new JLabel[2];
		for (int i = 0, x = 65, y = 95; i < 2; ++i, x += 138) {
			this.heroStatus_Selected[i] = this.createLabel("gameOption/heroSelected_" + i + ".png");
			this.heroStatus_Selected[i].setLocation(x, y);
			Core.mainPane.add(this.heroStatus_Selected[i], Core.LAYER[topLayer]);
		}
		
		//game background
		this.bg = new JLabel[2];
		this.bgPreview = new JLabel[2];
		for (int i = 0; i < 2; ++i) {
			this.bg[i] = this.createLabel("court/bg" + i + ".jpg");
			this.bgPreview[i] = this.createLabel("court/bg" + i + "_Preview.jpg");
			this.bgPreview[i].setBorder(new LineBorder(new Color(40,40,40,120), 6));
			this.bgPreview[i].setLocation(258, 260);
			Core.mainPane.add(this.bg[i], Core.LAYER[0]);
			Core.mainPane.add(this.bgPreview[i], Core.LAYER[topLayer]);
		}
		
		//start buttons
		this.startButton = this.createLabel("gameOption/startButton.png");
		this.startButton.setLocation(234, 392);
		Core.mainPane.add(this.startButton, Core.LAYER[topLayer]);
		this.startButton_Selected = new JLabel[2];
		for (int i = 0; i < 2; ++i) {
			this.startButton_Selected[i] = this.createLabel("gameOption/startSelected_" + i + ".png");
			this.startButton_Selected[i].setLocation(234, 392);
			Core.mainPane.add(this.startButton_Selected[i], Core.LAYER[topLayer]);
		}
		
		//selection background
		this.rowChoiceBg = new JLabel[2];
		this.rowChoiceBg[0] = this.createLabel("gameOption/rowChoiceBg_Left.png");
		this.rowChoiceBg[0].setLocation(10, 142);
		this.rowChoiceBg[1] = this.createLabel("gameOption/rowChoiceBg_Right.png");
		this.rowChoiceBg[1].setLocation(614, 142);
		Core.mainPane.add(this.rowChoiceBg[0], Core.LAYER[topLayer]);
		Core.mainPane.add(this.rowChoiceBg[1], Core.LAYER[topLayer]);
		
		//option title
		this.optionTitle = this.createLabel("court/optionTitle.png");
		this.optionTitle.setLocation(266, 30);
		Core.mainPane.add(this.optionTitle, Core.LAYER[topLayer]);
		
		//option background
		this.optionBg = this.createLabel("court/optionBg.png");
		this.optionBg.setLocation(40, 50);
		Core.mainPane.add(this.optionBg, Core.LAYER[topLayer]);
		/*============================= End: Option-related =============================*/
		
		/*============================= Start: Game-related =============================*/
		//create labels and add labels to the main pane
		//score
		this.score = 0;
		this.scoreLabel = Core.createLabel("Score: 0", "Candara", 3, 30, Color.YELLOW, 150, 50);
		this.scoreLabel.setLocation(261, 0);
		Core.mainPane.add(scoreLabel, Core.LAYER[topLayer]);
		
		//players
		//this.playerLabel = new JLabel[2];
		//this.playerLabel[0] = Core.createLabel(text, name, style, size, fontColor, bgColor width, height);
		//this.playerLabel[0].setLocation(x, y);
		//Core.mainPane.add(playerLabel[0], Core.LAYER[topLayer]);
		//Core.mainPane.add(playerLabel[1], Core.LAYER[topLayer]);
		
		//health points
		this.hpLabel = new JLabel[2];
		this.hpLabel[0] = Core.createLabel("HP: 0", "Times New Roman", 1, 20, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.hpLabel[0].setLocation(512, 30);
		this.hpLabel[1] = Core.createLabel("HP: 0", "Times New Roman", 1, 20, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.hpLabel[1].setLocation(10, 30);
		Core.mainPane.add(hpLabel[0], Core.LAYER[topLayer]);
		Core.mainPane.add(hpLabel[1], Core.LAYER[topLayer]);
		
		//weapons
		this.weaponLabel = new JLabel[2];
		this.weaponLabel[0] = Core.createLabel("Weapon: Pistol", "Times New Roman", 1, 14, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.weaponLabel[0].setLocation(512, 55);
		this.weaponLabel[1] = Core.createLabel("Weapon: Pistol", "Times New Roman", 1, 14, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.weaponLabel[1].setLocation(10, 55);
		Core.mainPane.add(weaponLabel[0], Core.LAYER[topLayer]);
		Core.mainPane.add(weaponLabel[1], Core.LAYER[topLayer]);
		
		//ammos
		this.ammoLabel = new JLabel[2];
		this.ammoLabel[0] = Core.createLabel("Ammo: 999", "Times New Roman", 1, 14, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.ammoLabel[0].setLocation(512, 80);
		this.ammoLabel[1] = Core.createLabel("Ammo: 999", "Times New Roman", 1, 14, Color.BLACK, new Color(255,255,255,80), 150, 25);
		this.ammoLabel[1].setLocation(10, 80);
		Core.mainPane.add(ammoLabel[0], Core.LAYER[topLayer]);
		Core.mainPane.add(ammoLabel[1], Core.LAYER[topLayer]);
		/*============================== End: Game-related ==============================*/
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public void run() {
		/*============================ Start: Option-related ============================*/
		//initialize the option panel
		isStartGame = false;
		isReturnMenu = false;
		rowChoice = 0;
		heroChoice = new int[2];
		heroChoice[0] = 0;
		heroChoice[1] = (isTwoPlayer) ? 1 : -1;
		mapChoice = 0;
		startChoice = 0;
		optionTitle.setVisible(true);
		optionBg.setVisible(true);
		rowChoiceBg[0].setVisible(true);
		rowChoiceBg[1].setVisible(true);
		heroStatus[0].setVisible(true);
		heroStatus[1].setVisible(true);
		heroStatus[2].setVisible(true);
		heroStatus[3].setVisible(true);
		heroStatus_Selected[0].setVisible(true);
		heroStatus_Selected[1].setVisible((isTwoPlayer) ? true : false);
		drawHeroStatus();
		bg[0].setVisible(true);
		bg[1].setVisible(false);
		bgPreview[0].setVisible(true);
		bgPreview[1].setVisible(false);
		startButton.setVisible(true);
		
		//loop the option panel
		while (!(isStartGame || isReturnMenu)) {
			//change the row option
			if (key[0][Core.UP] && !Core.isKeyFrozen[0]) {
				Core.isKeyFrozen[0] = true;
				if (--rowChoice < 0)
					rowChoice = rowChoice_Max - 1;
			}
			else if (key[0][Core.DOWN] && !Core.isKeyFrozen[0]) {
				Core.isKeyFrozen[0] = true;
				if (++rowChoice == rowChoice_Max)
					rowChoice = 0;
			}
			rowChoiceBg();
			
			//select hero(s)
			if (rowChoice == 0) {
				selectHero();
				
				nextChoice(heroChoice, 0, heroChoice_Max);
				nextChoice(heroChoice, 1, heroChoice_Max);
				
			}
			//select map
			else if (rowChoice == 1) {
				selectMap();
				
				mapChoice = nextChoice(mapChoice, mapChoice_Max);
			}
			//select to start or leave the game
			else if (rowChoice == 2) {
				selectStart();
				
				startChoice = nextChoice(startChoice, startChoice_Max);
				
				if (key[0][Core.ATK]) {
					Core.hideLayer(topLayer);
					if (startChoice == 0) {
						isStartGame = true;
					}
					else if (startChoice == 1) {
						Core.hideLayer(0);
						isReturnMenu = true;
					}
				}
			}
			
			try {
				Thread.sleep(65);
			}
			catch (InterruptedException ex) {}
		}
		/*============================= End: Option-related =============================*/
		
		
		/*============================= Start: Game-related =============================*/
		if (isStartGame) {
			//display game information in Java Control Panel
			System.out.println("=== Game Information ===");
			System.out.println("Player[0]: Hero[" + heroChoice[0] + "]");
			if (isTwoPlayer)
				System.out.println("Player[1]: Hero[" + heroChoice[1] + "]");
			System.out.println("      Map: " + mapChoice);
			
			//initialize the game
			scoreLabel.setVisible(true);
			ObjectContainer.hero[heroChoice[0]].setPlayer(0);
			//playerLabel[0].setVisible(true);
			hpLabel[0].setVisible(true);
			weaponLabel[0].setVisible(true);
			ammoLabel[0].setVisible(true);
			if (isTwoPlayer) {
				ObjectContainer.hero[heroChoice[1]].setPlayer(1);
				//playerLabel[1].setVisible(true);
				hpLabel[1].setVisible(true);
				weaponLabel[1].setVisible(true);
				ammoLabel[1].setVisible(true);
			}
			
			//activate the game
			ObjectContainer.hero[heroChoice[0]].activate();
			if (isTwoPlayer)
				ObjectContainer.hero[heroChoice[1]].activate();
			ObjectContainer.zombieGenerator.activate();
		}
		
		while (isStartGame) {
			if (isTwoPlayer) {
				if (!(ObjectContainer.hero[heroChoice[0]].isAlive() || ObjectContainer.hero[heroChoice[1]].isAlive()))
					isStartGame = false;
			}
			else {
				if (!(ObjectContainer.hero[heroChoice[0]].isAlive()))
					isStartGame = false;
			}
			
			/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Exit~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
			if (Core.keyEsc) {
				System.out.println("=== Escaped ===");
				ObjectContainer.hero[heroChoice[0]].deactivate();
				if (isTwoPlayer)
					ObjectContainer.hero[heroChoice[1]].deactivate();
			}
			/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Exit~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex) {}
		}
		
		if (!isStartGame && !isReturnMenu) {
			//display Game Over
		}
		/*============================== End: Game-related ==============================*/
		
		if (isReturnMenu) {
			System.out.println("=== Return Menu ===");
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
	
	private void rowChoiceBg() {
		switch (rowChoice) {
		case 0:
			rowChoiceBg[0].setVisible(true);
			rowChoiceBg[0].setLocation(10, 142);
			rowChoiceBg[1].setVisible(true);
			rowChoiceBg[1].setLocation(614, 142);
			startButton.setVisible(true);
			break;
		case 1:
			rowChoiceBg[0].setVisible(true);
			rowChoiceBg[0].setLocation(10, 292);
			rowChoiceBg[1].setVisible(true);
			rowChoiceBg[1].setLocation(614, 292);
			startButton.setVisible(true);
			break;
		case 2:
			rowChoiceBg[0].setVisible(false);
			rowChoiceBg[1].setVisible(false);
			startButton.setVisible(false);
			break;
		}
	}
	
	//specified for two-player incompatible options
	private int nextChoice(int choice, int choice_Max) {
		if (key[0][Core.LEFT] && !Core.isKeyFrozen[0]) {
			Core.isKeyFrozen[0] = true;
			if (--choice < 0)
				choice = choice_Max - 1;
			return choice;
		}
		else if (key[0][Core.RIGHT] && !Core.isKeyFrozen[0]) {
			Core.isKeyFrozen[0] = true;
			if (++choice == choice_Max)
				choice = 0;
			return choice;
		}
		return choice;
	}
	
	//specified for two-player compatible options
	private void nextChoice(int[] choice, int target, int choice_Max) {
		boolean duplicate = false;
		if (key[target][Core.LEFT] && !Core.isKeyFrozen[target]) {
			Core.isKeyFrozen[target] = true;
			if (--choice[target] < 0)
				choice[target] = choice_Max - 1;
			do {
				for (int i = 0; i < choice.length; i++) {
					if (i != target && (choice[i] == choice[target])) {
						duplicate = true;
						if (--choice[target] < 0)
							choice[target] = choice_Max - 1;
						break;	//break the for-loop to implement the check again
					}
					duplicate = false;
				}
			} while (duplicate);
			return;
		}
		else if (key[target][Core.RIGHT] && !Core.isKeyFrozen[target]) {
			Core.isKeyFrozen[target] = true;
			if (++choice[target] == choice_Max)
				choice[target] = 0;
			do {
				for (int i = 0; i < choice.length; i++) {
					if (i != target && (choice[i] == choice[target])) {
						duplicate = true;
						if (++choice[target] == choice_Max)
							choice[target] = 0;
						break;	//break the for-loop to implement the check again
					}
					duplicate = false;
				}
			} while (duplicate);
			return;
		}
	}
	
	private void selectHero() {
		heroStatus_Selected[0].setLocation(65 + heroChoice[0]*138, 95);
		if (isTwoPlayer)
			heroStatus_Selected[1].setLocation(65 + heroChoice[1]*138, 95);
	}
	
	private void selectMap() {
		bg[mapChoice].setVisible(true);
		bgPreview[mapChoice].setVisible(true);
		for (int i = 0; i < bg.length; i++) {
			if (i != mapChoice) {
				bg[i].setVisible(false);
				bgPreview[i].setVisible(false);
			}
		}
	}
	
	private void selectStart() {
		startButton_Selected[startChoice].setVisible(true);
		for (int i = 0; i < startButton_Selected.length; i++) {
			if (i != startChoice)
				startButton_Selected[i].setVisible(false);
		}
	}
	
	private void drawHeroStatus() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < ObjectContainer.hero[i].healthPoint_Max/20; j++) {
				JLabel bar_Red = createLabel("gameOption/bar_Red.png");
				bar_Red.setLocation(78 + i*138 + j*10, 169);
				bar_Red.setVisible(true);
				Core.mainPane.add(bar_Red, Core.LAYER[topLayer], 0);
			}
			for (int j = 0; j < ObjectContainer.hero[i].speed*2; j++) {
				JLabel bar_Green = createLabel("gameOption/bar_Green.png");
				bar_Green.setLocation(78 + i*138 + j*10, 197);
				bar_Green.setVisible(true);
				Core.mainPane.add(bar_Green, Core.LAYER[topLayer], 0);
			}
			for (int j = 0; j < ObjectContainer.hero[i].defense; j++) {
				JLabel bar_Blue = createLabel("gameOption/bar_Blue.png");
				bar_Blue.setLocation(78 + i*138 + j*10, 225);
				bar_Blue.setVisible(true);
				Core.mainPane.add(bar_Blue, Core.LAYER[topLayer], 0);
			}
		}
	}
	
	public void setTwoPlayer(boolean isTwoPlayer) {
		this.isTwoPlayer = isTwoPlayer;
	}
	
	public boolean isTwoPlayer() {
		return this.isTwoPlayer;
	}
	
	public boolean isContinueGame() {
		return this.isStartGame;
	}
	
	public Hero getHero(int playerID) {
		return ObjectContainer.hero[heroChoice[playerID]];
	}
	
	public void updateScore(int variation) {
		score += variation;
		scoreLabel.setText("Score: " + score);
	}
	
	public void updateHpLabel(int playerID) {
		hpLabel[playerID].setText("HP: " + ObjectContainer.hero[heroChoice[playerID]].healthPoint);
	}
	
	public void updateWeaponLabel(int playerID, int weapon) {
		String displayWeapon = null;
		switch (weapon) {
		case 0:
			displayWeapon = "Pistol";
			break;
		case 1:
			displayWeapon = "Machine Gun";
			break;
		case 2:
			displayWeapon = "Cannon";
			break;
		default:
			displayWeapon = "N/A";	
		}
		weaponLabel[playerID].setText("Weapon: " + displayWeapon);
	}
	
	public void updateAmmoLabel(int playerID, int weapon) {
		ammoLabel[playerID].setText("Ammo: " + ObjectContainer.hero[heroChoice[playerID]].ammo[weapon]);
	}
	
	public void activate() {
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		try {
			thread.join();
		}
		catch (InterruptedException ex) {}
	}
}
