package loaders;

import utils.MyFile;

public class LoaderSettings 
{
	public static final MyFile RES_FOLDER = new MyFile("res");
	public static final String SEPARATOR = ";";
	public static final String SCENE_FILE = "sceneFiles.txt";
	
	public static final String SKYBOX_DAY_FOLDER 	= "skyboxDay";
	public static final String SKYBOX_NIGHT_FOLDER 	= "skyboxNight";
	
	public static final String[] SKYBOX_TEX_FILES 	= { "right", "left", "top", "bottom", "back", "front" };
	public static final float SKYBOX_SIZE 			= 100;
}
