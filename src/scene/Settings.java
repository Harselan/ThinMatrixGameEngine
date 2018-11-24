package scene;

import java.io.BufferedReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import loaders.LoaderSettings;
import utils.MyFile;

public class Settings 
{
	public static int waterHeight 			= 5;
	public static int terrainHeight 		= 0;
	
	public static String playerFolder 		= "entities/player";
	public static Vector3f startPosition 	= new Vector3f( 100, 0, -50 );
	
	public static void loadSettings( MyFile settingsFile )
	{
		MyFile sceneList 		= new MyFile( settingsFile, "settings.txt" );
		BufferedReader reader 	= getReader( sceneList );
		
		
		try 
		{
			String line = reader.readLine();
			
			headers = line.split(LoaderSettings.SEPARATOR);
			
			while( ( line = reader.readLine() ) != null ) 
			{
				scene = loadRow( line, sceneName, scene );
		    }
			
			//return files;
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
			System.err.println( "Couldn't read scene file: "+sceneFile );
			System.exit(-1);
		}
	}
	
	private static BufferedReader getReader( MyFile file ) 
	{
		try 
		{
			return file.getReader();
		} 
		catch( Exception e ) 
		{
			e.printStackTrace();
			System.err.println( "Couldn't find scene file: " + file );
			System.exit(-1);
			return null;
		}
	}
}
