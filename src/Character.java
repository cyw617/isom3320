import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class Character implements Runnable {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/	
	//characteristics
	private boolean lifeStatus;	//true - alive; false - dead
	public final int healthPoint_Max, speed, defense;
	public int healthPoint;
	public int x, y;
	public final int width, height;
	public int lastOrientation;	//0 - Up; 1 - Down; 2 - Left; 3 - Right
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	protected int lastMotion, motionCount;
	protected final int DEAD = 3;
	protected final int motionDelay = 4;
	
	//weapon
	private int weapon;	//-1: No weapon; 0: Pistol; 1: Machine gun; 2: Cannon; 3: Zombie's ball
	protected BulletGenerator bulletGenerator;
	public int[] ammo;
	public boolean isShoot, pauseShoot;
	
	//labels for the actions
	protected final JLabel[][] walk;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public Character(int lastOrientation, int x, int y, int healthPoint, int speed, int defense, String characterFolder) {
		//define basic parameters
		this.lastOrientation = lastOrientation;
		this.lastMotion = 0;
		this.motionCount = 0;
		this.x = x;
		this.y = y;		
		this.healthPoint_Max = healthPoint;
		this.healthPoint = 0;
		this.speed = speed;
		this.defense = defense;
		
		//default to have no weapon
		this.weapon = -1;
		this.isShoot = false;
		this.pauseShoot = false;
		
		//create labels
		walk = new JLabel[4][4];
		for (int id = 0; id < this.walk[UP].length; id++)
			this.walk[UP][id] = createLabel(characterFolder, "up", id);
		for (int id = 0; id < this.walk[DOWN].length; id++)
			this.walk[DOWN][id] = createLabel(characterFolder, "down", id);
		for (int id = 0; id < this.walk[LEFT].length; id++)
			this.walk[LEFT][id] = createLabel(characterFolder, "left", id);
		for (int id = 0; id < this.walk[RIGHT].length; id++)
			this.walk[RIGHT][id] = createLabel(characterFolder, "right", id);
		
		//get the dimension of the labels
		this.width = getLabelDimension(characterFolder, "up", 0, 'w');
		this.height = getLabelDimension(characterFolder, "up", 0, 'h');
		
		//add the labels to the main pane
		for (int i = 0; i < walk.length; i++) {
			for (int j = 0; j < walk[i].length; j++) {
				Core.mainPane.add(walk[i][j], Core.LAYER[addToLayer()]);
			}
		}
		
		//default to be inactive
		lifeStatus = false;
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public abstract void run();
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/	
	protected JLabel createLabel(String characterFolder, String orientation, int id) {
		URL imageURL = this.getClass().getResource("images/character/" + characterFolder + "/" + orientation + "_" + id + ".gif");
		ImageIcon imageIcon = new ImageIcon(imageURL);
		JLabel imageLabel = new JLabel(imageIcon);
		
		imageLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		imageLabel.setVisible(false);
		
		return imageLabel;
	}
	
	private int getLabelDimension(String characterFolder, String orientation, int id, char dimension) {
		URL imageURL = this.getClass().getResource("images/character/" + characterFolder + "/" + orientation + "_" + id + ".gif");
		ImageIcon imageIcon = new ImageIcon(imageURL);
		switch (dimension) {
		case 'w':
			return imageIcon.getIconWidth();
		case 'h':
			return imageIcon.getIconHeight();
		}
		return -1;	//image does not exist
	}
	
	protected int addToLayer() {
		if (y < 0)
			return 2;
		if (y >= Core.frameHeight)
			return 16;
		return (y + 48)/32 + 1;
	}
	
	protected void walking(int currentOrientation) {
		if ((currentOrientation != this.lastOrientation) || (this.motionCount == this.motionDelay)) {
			this.walk[this.lastOrientation][this.lastMotion].setVisible(false);
			this.lastOrientation = currentOrientation;
			this.lastMotion = (this.lastMotion + 1 == this.walk[currentOrientation].length - 1) ? 1 : (this.lastMotion + 1);
			this.motionCount = 0;
		}
		System.out.println("-------------------------- walking ---------------------------");
		System.out.println("LastOreientation: " + this.lastOrientation);
		System.out.println("      LastMotion: " + this.lastMotion);
		System.out.println("    Add to layer: " + this.addToLayer());
		Core.mainPane.setLayer(this.walk[this.lastOrientation][this.lastMotion], this.addToLayer(), (lastOrientation == UP) ? 0 : -1);
		this.walk[this.lastOrientation][this.lastMotion].setLocation(this.x, this.y);
		this.walk[this.lastOrientation][this.lastMotion].setVisible(true);
		this.motionCount++;
	}
	
	public synchronized void displayBlood() {
		int random = (int)(Math.random()*3);	//generate a random number between 0 and 2 (inclusive)
		
		URL bloodURL = this.getClass().getResource("images/blood/" + random + ".png");
		ImageIcon bloodIcon = new ImageIcon(bloodURL);
		JLabel blood = new JLabel(bloodIcon);
		blood.setBounds(x - 20 + (int)(Math.random()*9)*5, y - 3 + (int)(Math.random()*9)*5, bloodIcon.getIconWidth(), bloodIcon.getIconHeight());
		
		Core.mainPane.add(blood, Core.LAYER[1]);
	}

	/*============================ Start: Weapon-related ============================*/
	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}
	
	public int currentWeapon() {
		return this.weapon;
	}
	/*============================= End: Weapon-related =============================*/
	
	public boolean isAlive() {
		return this.lifeStatus;
	}
	
	public synchronized void activate() {
		this.healthPoint = this.healthPoint_Max;
		this.lifeStatus = true;
		this.walk[this.lastOrientation][0].setLocation(this.x, this.y);
		this.walk[this.lastOrientation][0].setVisible(true);
		
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	public synchronized void deactivate() {
		this.lifeStatus = false;
		
		//show died picture
		if (ObjectContainer.gameControl.isContinueGame()) {
			this.walk[this.lastOrientation][this.lastMotion].setVisible(false);
			Core.mainPane.setLayer(this.walk[this.lastOrientation][DEAD], 2);
			this.walk[this.lastOrientation][DEAD].setLocation(x, y);
			this.walk[this.lastOrientation][DEAD].setVisible(true);
		}
	}
}
