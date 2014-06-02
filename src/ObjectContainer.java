public class ObjectContainer {
	/*-------------------------------------------------------------------------------------------*/
	/*-----------------------------------------Variables-----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public static GameControl gameControl;
	public static Hero[] hero;
	public static ZombieGenerator zombieGenerator;
	
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*----------------------------------------Constructor----------------------------------------*/
	/*-------------------------------------------------------------------------------------------*/
	public ObjectContainer() {
		//generate the four heros
		hero = new Hero[4];
		hero[0] = new Hero(Character.RIGHT, 338, 216, 120, 5, 6, "hero_0");
		hero[1] = new Hero(Character.RIGHT, 338, 216, 200, 3, 5, "hero_1");
		hero[2] = new Hero(Character.RIGHT, 338, 216, 140, 4, 7, "hero_2");
		hero[3] = new Hero(Character.RIGHT, 338, 216, 80, 4, 10, "hero_3");
		
		//generate the zombies
		zombieGenerator = new ZombieGenerator();
		
		gameControl = new GameControl();
	}
}
