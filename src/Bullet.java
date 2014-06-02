import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Bullet	implements Runnable {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	private int initXcoord, initYcoord;
	private int x, y;
	private int direction;
	private final int UP = Character.UP, DOWN = Character.DOWN, LEFT = Character.LEFT, RIGHT = Character.RIGHT;
	private final JLabel[][] bullet;
	private final JLabel explosion;
	private final int[] bullet_verWidth, bullet_verHeight, bullet_horWidth, bullet_horHeight;
	private final int S = 0, L = 1, Z = 0;
	private int bulletType;
	
	private final int[] weaponRange, weaponSpeed;
	
	private final Character shooter;
	private int currentWeapon;
	
	boolean isActive;
	boolean outOfRange, hitObstacle, hitZombie, hitHero;
	
	private boolean isTwoPlayer;
	private Hero[] player;
	private Zombie[] zombies, bosses;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public Bullet(Character shooter) {
		//refer to a shooter
		this.shooter = shooter;
		
		//default the weapon & bullet type
		this.currentWeapon = 0;
		this.bulletType = this.S;
		
		/*--- Start: set default range for each weapon ---*/
		this.weaponRange = new int[4];
		this.weaponSpeed = new int[4];
		
		//Pistol
		this.weaponRange[0] = 200;
		this.weaponSpeed[0] = 5;
		
		//Machine gun
		this.weaponRange[1] = 270;
		this.weaponSpeed[1] = 6;
		
		//Cannon
		this.weaponRange[2] = 999;
		this.weaponSpeed[2] = 8;
		
		//Zombie
		this.weaponRange[3] = 999;
		this.weaponSpeed[3] = 8;
		/*--- End: set default range for each weapon ---*/
		
		/*--- Start: instantiate the JLabel for the 4 directions of each type of bullet ---*/
		if (shooter instanceof Zombie) {
			bullet = new JLabel[1][4];
			bullet_verWidth = new int[1];
			bullet_verHeight = new int[1];
			bullet_horWidth = new int[1];
			bullet_horHeight = new int[1];
			
			//zombie bullet
			bullet[Z][0] = createLabel("zombie/up.png");
			bullet[Z][1] = createLabel("zombie/down.png");
			bullet[Z][2] = createLabel("zombie/left.png");
			bullet[Z][3] = createLabel("zombie/right.png");
			for (int i = 0; i < bullet[Z].length; i++)
				Core.mainPane.add(bullet[Z][i], Core.LAYER[16]);
			bullet_verWidth[Z] = getLabelDimension("zombie/up.png", 'w');
			bullet_verHeight[Z] = getLabelDimension("zombie/up.png", 'h');
			bullet_horWidth[Z] = getLabelDimension("zombie/left.png", 'w');
			bullet_horHeight[Z] = getLabelDimension("zombie/left.png", 'h');
		}
		else if (shooter instanceof Hero) {
			bullet = new JLabel[2][4];
			bullet_verWidth = new int[2];
			bullet_verHeight = new int[2];
			bullet_horWidth = new int[2];
			bullet_horHeight = new int[2];
			
			//small bullet
			bullet[S][0] = createLabel("small/up.gif");
			bullet[S][1] = createLabel("small/down.gif");
			bullet[S][2] = createLabel("small/left.gif");
			bullet[S][3] = createLabel("small/right.gif");
			for (int i = 0; i < bullet[S].length; i++)
				Core.mainPane.add(bullet[S][i], Core.LAYER[16]);
			bullet_verWidth[S] = getLabelDimension("small/up.gif", 'w');
			bullet_verHeight[S] = getLabelDimension("small/up.gif", 'h');
			bullet_horWidth[S] = getLabelDimension("small/left.gif", 'w');
			bullet_horHeight[S] = getLabelDimension("small/left.gif", 'h');
			
			//large bullet
			bullet[L][0] = createLabel("large/up.gif");
			bullet[L][1] = createLabel("large/down.gif");
			bullet[L][2] = createLabel("large/left.gif");
			bullet[L][3] = createLabel("large/right.gif");
			for (int i = 0; i < bullet[L].length; i++)
				Core.mainPane.add(bullet[L][i], Core.LAYER[16]);
			bullet_verWidth[L] = getLabelDimension("large/up.gif", 'w');
			bullet_verHeight[L] = getLabelDimension("large/up.gif", 'h');
			bullet_horWidth[L] = getLabelDimension("large/left.gif", 'w');
			bullet_horHeight[L] = getLabelDimension("large/left.gif", 'h');
		}
		else {
			bullet = null;
			bullet_verWidth = null;
			bullet_verHeight = null;
			bullet_horWidth = null;
			bullet_horHeight = null;
		}
		/*--- End: instantiate the JLabel for the 4 directions of each type of bullet ---*/
		
		/*--- Start: instantiate the JLabel for explosion of bullet ---*/
		explosion = createLabel("explosion.gif");
		Core.mainPane.add(explosion, Core.LAYER[16]);
		/*--- End: instantiate the JLabel for explosion of bullet ---*/
		
		player = new Hero[2];
	}
	
		
		
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public void run()
	{
		isActive = true;
		
		//initialize the bullet type
		bulletType();
		
		//initialize the x- and y-coordinate of the bullet
		initBulletPosition();
		
		//notify the bullet about the existing characters
		isTwoPlayer = ObjectContainer.gameControl.isTwoPlayer();
		player[0] = ObjectContainer.gameControl.getHero(0);
		if (isTwoPlayer)
			player[1] = ObjectContainer.gameControl.getHero(1);
		zombies = ObjectContainer.zombieGenerator.zombies;
		bosses = ObjectContainer.zombieGenerator.bosses;
		
		//determine whether the bullet should fly onward
		while (isActive && ObjectContainer.gameControl.isContinueGame()) {
			try
            {
                Thread.sleep(30);
            }
            catch(InterruptedException interruptedexception) {}
			
			outOfRange = checkOutOfRange();
			//hitObstacle = checkHitObstacle();
			hitZombie = checkHitZombie();
			hitHero = checkHitHero();
			System.out.println("--------------------------------------");
			System.out.println("outOfRange: " + outOfRange);
			System.out.println("hitObstacle: " + hitObstacle);
			System.out.println("hitZombie: " + hitZombie);
			System.out.println("hitHero: " + hitHero);
			
			if (outOfRange || hitObstacle || hitZombie || hitHero)
				isActive = false;
			else
				flyOnward();
		}
		
		//action after the bullet is deactivated
		setBulletInvisible();
		try
        {
            Thread.sleep(250);
        }
        catch(InterruptedException interruptedexception) {}
		explosion.setVisible(false);
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	private JLabel createLabel(String path) {
		URL imageURL = this.getClass().getResource("images/bullet/" + path);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		JLabel imageLabel = new JLabel(imageIcon);
		
		imageLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		imageLabel.setVisible(false);
		
		return imageLabel;
	}
	
	private int getLabelDimension(String path, char dimension) {
		URL imageURL = this.getClass().getResource("images/bullet/" + path);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		switch (dimension) {
		case 'w':
			return imageIcon.getIconWidth();
		case 'h':
			return imageIcon.getIconHeight();
		default:
			return -1;	//image does not exist
		}
	}
	
	private void setBulletInvisible() {
		for (int i = 0; i < bullet.length; i++)
			bullet[i][direction].setVisible(false);
	}
	
	private void bulletType() {
		currentWeapon = shooter.currentWeapon();
		switch (currentWeapon) {
		case 0:
		case 1:
			bulletType = S;
			break;
		case 2:
			bulletType = L;
			break;
		case 3:
			bulletType = Z;
		}
	}
	
	private void initBulletPosition() {
		//initialize the position of the bullet
		switch (shooter.lastOrientation) {
		case UP:
			direction = UP;
			if (currentWeapon == 0 || currentWeapon == 1) {
				initXcoord = x = shooter.x + 33;
				initYcoord = y = shooter.y - 10;
			}
			else if (currentWeapon == 2) {
				initXcoord = x = shooter.x + 13;
				initYcoord = y = shooter.y - 50;
			}
			else if (currentWeapon == 3) {
				initXcoord = x = shooter.x;
				initYcoord = y = shooter.y;
			}
			break;
		case DOWN:
			direction = DOWN;
			if (currentWeapon == 0 || currentWeapon == 1) {
				initXcoord = x = shooter.x + 12;
				initYcoord = y = shooter.y + 42;
			}
			else if (currentWeapon == 2) {
				initXcoord = x = shooter.x - 10;
				initYcoord = y = shooter.y + 30;
			}
			else if (currentWeapon == 3) {
				initXcoord = x = shooter.x;
				initYcoord = y = shooter.y;
			}
			break;
		case LEFT:
			direction = LEFT;
			if (currentWeapon == 0 || currentWeapon == 1) {
				initXcoord = x = shooter.x - 9;
				initYcoord = y = shooter.y + 10;
			}
			else if (currentWeapon == 2) {
				initXcoord = x = shooter.x - 30;
				initYcoord = y = shooter.y - 12;
			}
			else if (currentWeapon == 3) {
				initXcoord = x = shooter.x;
				initYcoord = y = shooter.y;
			}
			break;
		case RIGHT:
			direction = RIGHT;
			if (currentWeapon == 0 || currentWeapon == 1) {
				initXcoord = x = shooter.x + 45;
				initYcoord = y = shooter.y + 23;
			}
			else if (currentWeapon == 2) {
				initXcoord = x = shooter.x + 30;
				initYcoord = y = shooter.y + 2;
			}
			else if (currentWeapon == 3) {
				initXcoord = x = shooter.x;
				initYcoord = y = shooter.y;
			}
			break;
		}
		bullet[bulletType][direction].setLocation(x, y);
		bullet[bulletType][direction].setVisible(true);
	}
	
	private void flyOnward() {		
		switch (direction) {
		case UP:
			y -= weaponSpeed[currentWeapon];
			break;
		case DOWN:
			y += weaponSpeed[currentWeapon];
			break;
		case LEFT:
			x -= weaponSpeed[currentWeapon];
			break;
		case RIGHT:
			x += weaponSpeed[currentWeapon];
			break;
		}
		bullet[bulletType][direction].setLocation(x, y);
	}
	
	private boolean checkOutOfRange() {
		if ((Math.abs(x - initXcoord) > weaponRange[currentWeapon])
				|| (Math.abs(y - initYcoord) > weaponRange[currentWeapon])) {
			if (currentWeapon == 2)
				explode();
			return true;
		}
		else
			return false;
	}
	
	/*
	private boolean checkHitObstacle() {
		int[][] grid = background.grid;
		int i = 0, j = 0;
		
		switch (direction) {
		case 'u':
			if (currentWeapon == 0 || currentWeapon == 1) {
				i = y/48;
				j = (x + sbullet.verticalWidth/2)/48;
			}
			else if (currentWeapon == 2) {
				i = y/48;
				j = (x + lbullet.verticalWidth/2)/48;
			}
			break;
		case 'd':
			if (currentWeapon == 0 || currentWeapon == 1) {
				i = (y + sbullet.verticalHeight)/48;
				j = (x + sbullet.verticalWidth/2)/48;
			}
			else if (currentWeapon == 2) {
				i = (y + lbullet.verticalHeight)/48;
				j = (x + lbullet.verticalWidth/2)/48;
			}
			break;
		case 'l':
			if (currentWeapon == 0 || currentWeapon == 1) {
				i = (y + sbullet.horizontalHeight/2)/48;
				j = x/48;
			}
			else if (currentWeapon == 2) {
				i = (y + lbullet.horizontalHeight/2)/48;
				j = x/48;
			}
			break;
		case 'r':
			if (currentWeapon == 0 || currentWeapon == 1) {
				i = (y + sbullet.horizontalHeight/2)/48;
				j = (x + sbullet.horizontalWidth)/48;
			}
			else if (currentWeapon == 2) {
				i = (y + lbullet.horizontalHeight/2)/48;
				j = (x + lbullet.horizontalWidth)/48;
			}
			break;
		}
		if ((x < 0 || y < 0) || (i >= 10 || j >= 14)) {
			if (currentWeapon == 2)
				explode();
			return true;
		}
		else if (grid[i][j] == 1 || grid[i][j] == 2 || grid[i][j] == 3
				|| grid[i][j] == 4 || grid[i][j] == 5 || grid[i][j] == 6) {
			if (currentWeapon == 2)
				explode();
			return true;
		}
		else
			return false;
	}
	*/
	
	private boolean checkHitZombie() {
		if (shooter instanceof Hero) {
			switch (direction) {
			case UP:
				for (int i = 0; i < zombies.length; i++)
					if (zombies[i].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > zombies[i].x
								&& x + bullet_verWidth[bulletType]/2 < zombies[i].x + zombies[i].width
								&& y > zombies[i].y
								&& y < zombies[i].y + zombies[i].height) {
							hurtCharacter(zombies[i]);
							return true;
						}
				for (int i = 0; i < bosses.length; i++)
					if (bosses[i].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > bosses[i].x
								&& x + bullet_verWidth[bulletType]/2 < bosses[i].x + bosses[i].width
								&& y > bosses[i].y
								&& y < bosses[i].y + bosses[i].height) {
							hurtCharacter(bosses[i]);
							return true;
						}
				return false;
				
			case DOWN:
				for (int i = 0; i < zombies.length; i++)
					if (zombies[i].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > zombies[i].x
								&& x + bullet_verWidth[bulletType]/2 < zombies[i].x + zombies[i].width
								&& y + bullet_verHeight[bulletType] > zombies[i].y
								&& y + bullet_verHeight[bulletType] < zombies[i].y + zombies[i].height) {
							hurtCharacter(zombies[i]);
							return true;
						}
				for (int i = 0; i < bosses.length; i++)
					if (bosses[i].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > bosses[i].x
								&& x + bullet_verWidth[bulletType]/2 < bosses[i].x + bosses[i].width
								&& y + bullet_verHeight[bulletType] > bosses[i].y
								&& y + bullet_verHeight[bulletType] < bosses[i].y + bosses[i].height) {
							hurtCharacter(bosses[i]);
							return true;
						}
				return false;

			case LEFT:
				for (int i = 0; i < zombies.length; i++)
					if (zombies[i].isAlive())
						if (x < zombies[i].x + zombies[i].width
								&& x > zombies[i].x
								&& y + bullet_horHeight[bulletType]/2 > zombies[i].y
								&& y + bullet_horHeight[bulletType]/2 < zombies[i].y + zombies[i].height) {
							hurtCharacter(zombies[i]);
							return true;
						}
				for (int i = 0; i < bosses.length; i++)
					if (bosses[i].isAlive())
						if (x < bosses[i].x + bosses[i].width
								&& x > bosses[i].x
								&& y + bullet_horHeight[bulletType]/2 > bosses[i].y
								&& y + bullet_horHeight[bulletType]/2 < bosses[i].y + bosses[i].height) {
							hurtCharacter(bosses[i]);
							return true;
						}
				return false;

			case RIGHT:
				for (int i = 0; i < zombies.length; i++)
					if (zombies[i].isAlive())
						if (x + bullet_horWidth[bulletType] > zombies[i].x
								&& x + bullet_horWidth[bulletType] < zombies[i].x + zombies[i].width
								&& y + bullet_horHeight[bulletType]/2 > zombies[i].y
								&& y + bullet_horHeight[bulletType]/2 < zombies[i].y + zombies[i].height) {
							hurtCharacter(zombies[i]);
							return true;
						}
				for (int i = 0; i < bosses.length; i++)
					if (bosses[i].isAlive())
						if (x + bullet_horWidth[bulletType] > bosses[i].x
								&& x + bullet_horWidth[bulletType] < bosses[i].x + bosses[i].width
								&& y + bullet_horHeight[bulletType]/2 > bosses[i].y
								&& y + bullet_horHeight[bulletType]/2 < bosses[i].y + bosses[i].height) {
							hurtCharacter(bosses[i]);
							return true;
						}
				return false;
			}
		}
		return false;
	}
	
	private boolean checkHitHero() {
		if (shooter instanceof Hero) {			
			switch (direction) {
			case UP:
				if (player[0].isAlive())
					if (x + bullet_verWidth[bulletType]/2 > player[0].x
							&& x + bullet_verWidth[bulletType]/2 < player[0].x + player[0].width
							&& y > player[0].y
							&& y < player[0].y + player[0].height) {
						hurtCharacter(player[0]);
						return true;
					}
				if (isTwoPlayer) {
					if (player[1].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > player[1].x
								&& x + bullet_verWidth[bulletType]/2 < player[1].x + player[1].width
								&& y > player[1].y
								&& y < player[1].y + player[1].height) {
							hurtCharacter(player[1]);
							return true;
						}
				}
				return false;
				
			case DOWN:
				if (player[0].isAlive())
					if (x + bullet_verWidth[bulletType]/2 > player[0].x
							&& x + bullet_verWidth[bulletType]/2 < player[0].x + player[0].width
							&& y + bullet_verHeight[bulletType] > player[0].y
							&& y + bullet_verHeight[bulletType] < player[0].y + player[0].height) {
						hurtCharacter(player[0]);
						return true;
					}
				if (isTwoPlayer) {
					if (player[1].isAlive())
						if (x + bullet_verWidth[bulletType]/2 > player[1].x
								&& x + bullet_verWidth[bulletType]/2 < player[1].x + player[1].width
								&& y + bullet_verHeight[bulletType] > player[1].y
								&& y + bullet_verHeight[bulletType] < player[1].y + player[1].height) {
							hurtCharacter(player[1]);
							return true;
						}
				}
				return false;

			case LEFT:
				if (player[0].isAlive())
					if (x < player[0].x + player[0].width
							&& x > player[0].x
							&& y + bullet_horHeight[bulletType]/2 > player[0].y
							&& y + bullet_horHeight[bulletType]/2 < player[0].y + player[0].height) {
						hurtCharacter(player[0]);
						return true;
					}
				if (isTwoPlayer) {
					if (player[1].isAlive())
						if (x < player[1].x + player[1].width
								&& x > player[1].x
								&& y + bullet_horHeight[bulletType]/2 > player[1].y
								&& y + bullet_horHeight[bulletType]/2 < player[1].y + player[1].height) {
							hurtCharacter(player[1]);
							return true;
						}
				}
				return false;

			case RIGHT:
				if (player[0].isAlive())
					if (x + bullet_horWidth[bulletType] > player[0].x
							&& x + bullet_horWidth[bulletType] < player[0].x + player[0].width
							&& y + bullet_horHeight[bulletType]/2 > player[0].y
							&& y + bullet_horHeight[bulletType]/2 < player[0].y + player[0].height) {
						hurtCharacter(player[0]);
						return true;
					}
				if (isTwoPlayer) {
					if (player[1].isAlive())
						if (x + bullet_horWidth[bulletType] > player[1].x
								&& x + bullet_horWidth[bulletType] < player[1].x + player[1].width
								&& y + bullet_horHeight[bulletType]/2 > player[1].y
								&& y + bullet_horHeight[bulletType]/2 < player[1].y + player[1].height) {
							hurtCharacter(player[1]);
							return true;
						}
				}
				return false;
			}
		}
		return false;
	}
	
	private void hurtCharacter(Character victim) {
		switch (currentWeapon) {
		case 0:
			victim.healthPoint -= 25;
			break;
		case 1:
			victim.healthPoint -= 25;
			break;
		case 2:
			victim.healthPoint -= 75;
			explode();
			break;
		case 3:
			victim.healthPoint -= 50;
			break;
		}
		
		//reward points for shooting zombie
		if (victim instanceof Zombie && victim.healthPoint < 0) {
			if (((Zombie) victim).getType() == Zombie.SOLDIER)
				ObjectContainer.gameControl.updateScore(10);
			else
				ObjectContainer.gameControl.updateScore(30);
			
			victim.deactivate();
			
			victim.x = ((Zombie) victim).initX;
			victim.y = ((Zombie) victim).initY;
			
			Hero.updateKills();
		}
		
		//deactivate died player
		else if (victim instanceof Hero) {			
			if (victim.healthPoint <= 0) {
				victim.healthPoint = 0;
				victim.deactivate();
			}
			ObjectContainer.gameControl.updateHpLabel(((Hero) victim).getPlayerID());
		}
		
		//display blood
		victim.displayBlood();
	}
	
	private void explode() {
		switch (direction) {
		case UP:
			explosion.setLocation(x - 8, y - 52);
			break;
		case DOWN:
			explosion.setLocation(x - 8, y);
			break;
		case LEFT:
			explosion.setLocation(x - 35, y - 30);
			break;
		case RIGHT:
			explosion.setLocation(x + 15, y - 30);
			break;
		}
		explosion.setVisible(true);
	}

	public void activate()
	{
		new Thread(this).start();
	}
}
