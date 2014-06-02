public class ZombieGenerator implements Runnable {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public Zombie[] zombies, bosses;
	private int[] index;
	private int[] numOf;
	
	private boolean isTwoPlayer;
	private Hero[] player;
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public ZombieGenerator() {
		this.index = new int[2];
		this.index[Zombie.SOLDIER] = this.index[Zombie.BOSS] = 0;
		this.numOf = new int[2];
		this.numOf[Zombie.SOLDIER] = 40;
		this.numOf[Zombie.BOSS] = 25;
		
		this.zombies = new Zombie[this.numOf[Zombie.SOLDIER]];
		for (int i = 0; i < zombies.length; i++) {
			zombies[i] = createZombie(Zombie.SOLDIER);
		}
		
		this.bosses = new Zombie[this.numOf[Zombie.BOSS]];
		for (int i = 0; i < bosses.length; i++) {
			bosses[i] = createZombie(Zombie.BOSS);
		}

		player = new Hero[2];
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*--------------------------------------------Run--------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public void run() {
		isTwoPlayer = ObjectContainer.gameControl.isTwoPlayer();
		player[0] = ObjectContainer.gameControl.getHero(0);
		if (isTwoPlayer)
			player[1] = ObjectContainer.gameControl.getHero(1);
		
		int level = 0;
		while (ObjectContainer.gameControl.isContinueGame()) {
			if (Zombie.getNumOf(Zombie.SOLDIER) + Zombie.getNumOf(Zombie.BOSS) <= level/2) {
				level++;
				
				//generate zombies
				if (Zombie.getNumOf(Zombie.SOLDIER) <= level/2) {
					int reinforcement = 0;
					while (reinforcement < level*13/10) {
						if (!zombies[index[Zombie.SOLDIER]].isAlive()) {
							zombies[index[Zombie.SOLDIER]].activate();
							reinforcement++;
						}
						if (++index[Zombie.SOLDIER] == zombies.length)
							index[Zombie.SOLDIER] = 0;
					}
				}				
				if (Zombie.getNumOf(Zombie.BOSS) <= level/3) {
					int reinforcement = 0;
					while (reinforcement < level/2) {
						if (!bosses[index[Zombie.BOSS]].isAlive()) {
							bosses[index[Zombie.BOSS]].activate();
							reinforcement++;
						}
						if (++index[Zombie.BOSS] == bosses.length)
							index[Zombie.BOSS] = 0;
					}
				}
				
				//expand the zombie list if more zombies will be needed
				if (level/2 + level*13/10 > zombies.length - 5) {
					zombies = expandArray(zombies, Zombie.SOLDIER);
				}				
				if (level/2 + level/2 > bosses.length - 5) {
					bosses = expandArray(bosses, Zombie.BOSS);
				}
			}
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException ex) {}
		}
		
		//deactivate all zombies
		for (int i = 0; i < zombies.length; i++) {
			if (zombies[i].isAlive())
				zombies[i].deactivate();
		}
		for (int i = 0; i < bosses.length; i++) {
			if (bosses[i].isAlive())
				bosses[i].deactivate();
		}
	}
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*------------------------------------------Methods------------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	private Zombie createZombie(int type) {
		int orientation = (int)(Math.random()*4);
		int x = 0, y = 0;
		int healthPoint = 0, speed = 0;
		String characterFolder = null;
		
		switch (orientation) {
		case Character.UP:
			x = (int)(Math.random()*12 + 1)*48;
			y = Core.frameHeight - 48;
			break;
		case Character.DOWN:
			x = (int)(Math.random()*12 + 1)*48;
			y = 0;
			break;
		case Character.LEFT:
			x = Core.frameWidth - 48;
			y = (int)(Math.random()*8 + 1)*48;
			break;
		case Character.RIGHT:
			x = 0;
			y = (int)(Math.random()*8 + 1)*48;
			break;
		}
		
		switch (type) {
		case Zombie.SOLDIER:
			healthPoint = 50;
			speed = 1;
			characterFolder = "zombie";
			break;
		case Zombie.BOSS:
			healthPoint = 150;
			speed = 2;
			characterFolder = "boss";
			break;
		}
		
		return new Zombie(orientation, x, y, healthPoint, speed, 0, characterFolder, type);
	}
	
	private Zombie[] expandArray(Zombie[] zombieList, int type) {
		Zombie[] container = new Zombie[zombieList.length*2];
		System.arraycopy(zombieList, 0, container, 0, zombieList.length);
		for (int i = zombieList.length; i < container.length; i++) {
			container[i] = createZombie(type);
		}
		
		return container;
	}
	
	public void activate() {
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
}
