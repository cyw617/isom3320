public class Hero extends Character {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	//game-related
	private int playerID;
	private static int numOfKills;
	
	//control
	private boolean[] key;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public Hero(int lastOrientation, int x, int y, int healthPoint, int speed, int defense, String characterFolder) {
		super(lastOrientation, x, y, healthPoint, speed, defense, characterFolder);
		
		this.setWeapon(0);	//default to install pistol
		this.ammo = new int[3];
		this.ammo[0] = this.ammo[1] = ammo[2] = 0;
		this.bulletGenerator = new BulletGenerator(this);
		
		this.setPlayer(0);	//default to be Player 1
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	@Override
	public void run() {
		//initialize the character
		numOfKills = 0;
		setWeapon(0);
		ammo[0] = 999;
		ammo[1] = ammo[2] = 999;
		bulletGenerator.activate();
		ObjectContainer.gameControl.updateHpLabel(playerID);
		ObjectContainer.gameControl.updateWeaponLabel(playerID, currentWeapon());
		ObjectContainer.gameControl.updateAmmoLabel(playerID, currentWeapon());
		
		while (isAlive()) {
			if (key[Core.ATK]) {
				isShoot = true;
			}
			else {
				isShoot = false;
				pauseShoot = false;
			}
			
			if (key[Core.WP_L] && !Core.isKeyFrozen[playerID]) {
				Core.isKeyFrozen[playerID] = true;
				if (currentWeapon() - 1 >= 0)
					setWeapon(currentWeapon() - 1);
				ObjectContainer.gameControl.updateWeaponLabel(playerID, currentWeapon());
				ObjectContainer.gameControl.updateAmmoLabel(playerID, currentWeapon());
			}
			else if (key[Core.WP_R] && !Core.isKeyFrozen[playerID]) {
				Core.isKeyFrozen[playerID] = true;
				if (currentWeapon() + 1 < ammo.length)
					setWeapon(currentWeapon() + 1);
				ObjectContainer.gameControl.updateWeaponLabel(playerID, currentWeapon());
				ObjectContainer.gameControl.updateAmmoLabel(playerID, currentWeapon());
			}
			else if (!(key[Core.WP_L] || key[Core.WP_R])) {
				Core.isKeyFrozen[playerID] = false;
			}
			
			if (key[Core.UP]) {
				walking(UP);
				if (y - speed < 0)
					y = 0;
				else
					y -= speed;
			}
			else if (key[Core.DOWN]) {
				walking(DOWN);
				if ((y + height) + speed > Core.frameHeight)
					y = Core.frameHeight - height;
				else
					y += speed;
			}
			else if (key[Core.LEFT]) {
				walking(LEFT);
				if (x - speed < 0)
					x = 0;
				else
					x -= speed;
			}
			else if (key[Core.RIGHT]) {
				walking(RIGHT);
				if ((x + width) + speed > Core.frameWidth)
					x = Core.frameWidth - width;
				else
					x += speed;
			}
			else {				
				if (lastMotion != 0) {
					walk[lastOrientation][lastMotion].setVisible(false);
					Core.mainPane.setLayer(walk[lastOrientation][0], addToLayer());
				}
				walk[lastOrientation][0].setLocation(x, y);
				walk[lastOrientation][0].setVisible(true);
				lastMotion = 0;
			}
			
			try {
				Thread.sleep(40);
			}
			catch (InterruptedException ex) {}
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	//playerID: 0 - Player1, 1 - Player2
	public void setPlayer (int playerID) {
		this.playerID = playerID;
		this.key = Core.key[playerID];
		
		switch (playerID) {
		case 0:
			this.lastOrientation = RIGHT;
			this.x = 338;
			this.y = 216;
			break;
		case 1:
			this.lastOrientation = LEFT;
			this.x = 286;
			this.y = 216;
			break;
		}
	}
	
	public int getPlayerID () {
		return playerID;
	}
	
	public synchronized static void updateKills() {
		numOfKills++;
	}
	
	public static int getNumOfKills() {
		return numOfKills;
	}
}
