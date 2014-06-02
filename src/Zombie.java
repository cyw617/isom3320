import javax.swing.JLabel;

public class Zombie extends Character {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	//number of each type of zombie
	private static int numOfSoldier = 0, numOfBoss = 0;
	
	//type
	private final int type;
	public static final int SOLDIER = 0, BOSS = 1;
	
	public final int initX, initY;
	
	//labels for the action
	private final JLabel[][] attack;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public Zombie(int lastOrientation, int x, int y, int healthPoint, int speed, int defense, String characterFolder, int type) {
		super(lastOrientation, x, y, healthPoint, speed, defense, characterFolder);
		
		this.type = type;
		
		this.initX = x;
		this.initY = y;
		
		//create labels and ammo for attacking
		switch (type) {
		case SOLDIER:
			this.attack = new JLabel[4][4];
			break;
		case BOSS:
			this.attack = new JLabel[4][2];
			this.setWeapon(3);
			this.ammo = new int[4];
			this.ammo[0] = this.ammo[1] = this.ammo[2] = this.ammo[3] = 0;
			this.bulletGenerator = new BulletGenerator(this);
			break;
		default:
			attack = null;
		}
		for (int id = 0; id < this.attack[UP].length; id++)
			this.attack[UP][id] = createLabel(characterFolder, "upAtk", id);
		for (int id = 0; id < this.attack[DOWN].length; id++)
			this.attack[DOWN][id] = createLabel(characterFolder, "downAtk", id);
		for (int id = 0; id < this.attack[LEFT].length; id++)
			this.attack[LEFT][id] = createLabel(characterFolder, "leftAtk", id);
		for (int id = 0; id < this.attack[RIGHT].length; id++)
			this.attack[RIGHT][id] = createLabel(characterFolder, "rightAtk", id);
		for (int i = 0; i < attack.length; i++)
			for (int j = 0; j < attack[i].length; j++)
				Core.mainPane.add(attack[i][j], Core.LAYER[0]);
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	@Override
	public void run() {
		//initialize the zombies
		if (type == BOSS) {
			setWeapon(3);
			this.ammo[0] = this.ammo[1] = this.ammo[2] = 0;
			this.ammo[3] = 999;
			bulletGenerator.activate();
		}
		
		while (isAlive()) {
			try {
				Thread.sleep(40);
			}
			catch (InterruptedException ex) {}
		}
		
		//hide the dead zombie
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException ex) {}
		this.walk[this.lastOrientation][DEAD].setVisible(false);
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public int getType() {
		return this.type;
	}
	
	public static int getNumOf(int type) {
		switch (type) {
		case 0:
			return numOfSoldier;
		case 1:
			return numOfBoss;
		}
		
		return -1;	//the type does not exist
	}
	
	@Override
	public synchronized void activate() {
		super.activate();
		
		switch (this.type) {
		case 0:
			numOfSoldier++;
			break;
		case 1:
			numOfBoss++;
			break;
		}
		
		if (this.type == BOSS)
			this.bulletGenerator.activate();
	}
	
	@Override
	public synchronized void deactivate() {
		super.deactivate();
		
		switch (this.type) {
		case SOLDIER:
			numOfSoldier--;
			break;
		case BOSS:
			numOfBoss--;
			break;
		}
	}
}
