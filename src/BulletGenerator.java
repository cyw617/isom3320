public class BulletGenerator implements Runnable {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	private final int numOfBullets = 20;
	private final Bullet bullets[];
	private int bulletIndex;
	
	private final Character shooter;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/	
	public BulletGenerator(Character shooter) {
		//set a shooter
		this.shooter = shooter;
		
		//instantiate the bullets
		this.bullets = new Bullet[this.numOfBullets];
		for (int i = 0; i < this.numOfBullets; i++) {
			this.bullets[i] = new Bullet(shooter);
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public synchronized void run() {		
		bulletIndex = 0;
		while(shooter.isAlive() && ObjectContainer.gameControl.isContinueGame()) {
			if (shooter.isShoot && !shooter.pauseShoot && shooter.ammo[shooter.currentWeapon()] > 0) {
				if (!(shooter.currentWeapon() == 0 || shooter.currentWeapon() == 3)) {
					shooter.ammo[shooter.currentWeapon()]--;
					if (shooter instanceof Hero)
						ObjectContainer.gameControl.updateAmmoLabel(((Hero) shooter).getPlayerID(), shooter.currentWeapon());
				}
				bullets[bulletIndex].activate();
				
				//return to use the 1st bullet if all bullets have been consumed
				if(++bulletIndex >= numOfBullets)
					bulletIndex = 0;
				
				//set the shooting frequency for each weapon
				switch (shooter.currentWeapon()) {
				case 0:
					shooter.pauseShoot = true;
					try {
						Thread.sleep(300);
					}
					catch(InterruptedException interruptedexception) {}
					break;
				case 1:
					try {
						Thread.sleep(150);
					}
					catch(InterruptedException interruptedexception) {}
					break;
				case 2:
					shooter.pauseShoot = true;
					try {
						Thread.sleep(150);
					}
					catch(InterruptedException interruptedexception) {}
					break;
				}
			}
			try	{
				Thread.sleep(65);
			}
			catch(InterruptedException interruptedexception) {}
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public void activate() {
		Thread thread = new Thread(this);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}
}