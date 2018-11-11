package loaders;

import java.io.BufferedReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import guis.GuiTexture;
import models.TexturedModel;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import scene.Scene;
import textures.ModelTexture;
import utils.MyFile;

public class EntityList 
{
	public static String[] headers;

	private static Loader loader;
	
	private static Scene loadRow( String line, String sceneName, Scene scene )
	{
		String[] names = line.split(LoaderSettings.SEPARATOR);
		MyFile[] files = new MyFile[names.length];
		
		String objectType 	= names[0];
		String objectName 	= names[1];
		Vector3f pos 		= new Vector3f( Float.parseFloat( names[2] ), Float.parseFloat( names[3] ), Float.parseFloat( names[4] ) );
		float scale 		= Float.parseFloat( names[5] );
		String filePath 	= sceneName + "/" + objectName + "/";
		
		switch( objectType )
		{
			case "entity":
				scene.addEntity( createEntity( filePath, pos, scale ) );
			break;
			
			case "particle":
				scene.addParticleSystem( createParticleSystem( filePath, pos, scale ) );
			break;
			
			case "particleAtlas":
				scene.addParticleSystem( createParticleAtlasSystem( filePath, pos, scale ) );
			break;
			
			case "guiTexture":
				scene.addGui( createGuiTexture( filePath, pos, scale ) );
			break;
		}
		
		return scene;
	}
	
	private static Entity createEntity( String path, Vector3f pos, float scale )
	{
		TexturedModel model = 
			new TexturedModel( OBJLoader.loadObjModel( path + "model", loader ), 
				new ModelTexture( loader.loadTexture( path + "material" ) ) 
			);
		
		return new Entity( model, pos, 0, 0, 0, scale );
	}
	
	private static ParticleSystem createParticleSystem( String path, Vector3f pos, float scale )
	{	
		ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( path + "material" ), 1, false ); 
        ParticleSystem system 			= new ParticleSystem( particleTexture, 40, 1, 0.5f, 100, 1.6f );
        
        system.randomizeRotation();
        system.setDirection( new Vector3f( 0, 1, 0 ), 0.1f );
        system.setLifeError( 1f );
        system.setSpeedError( 1f );
        system.setScaleError( 1f );
		system.setPosition( pos );
        
		return system;
	}
	
	private static ParticleSystem createParticleAtlasSystem( String path, Vector3f pos, float scale )
	{	
		ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( path + "material" ), 4, false ); 
        ParticleSystem system 			= new ParticleSystem( particleTexture, 40, 10, 0.1f, 1, 1.6f );
        
        system.randomizeRotation();
        system.setDirection( new Vector3f( 0.31f, 0.31f, 0 ), 0.1f );
        system.setLifeError( 0.1f );
        system.setSpeedError( 0.25f );
        system.setScaleError( 0.5f );
		system.setPosition( pos );
        
		return system;
	}
	
	private static GuiTexture createGuiTexture( String path, Vector3f pos, float scale )
	{
		//Kom igåg. x och y koordinaterna ska vara mellan 0 och 1. 0 är högst upp till vänster och 1 är längst ner till höger
		return new GuiTexture( loader.loadTexture( path + "texture" ), new Vector2f( pos.x, pos.y ), new Vector2f( scale, scale ) );
	}
	
	public static Scene load( MyFile sceneFile, Loader loader, Scene scene )
	{
		EntityList.loader 		= loader;
		MyFile sceneList 		= new MyFile( sceneFile, LoaderSettings.SCENE_FILE );
		BufferedReader reader 	= getReader( sceneList );
		String sceneName 		= sceneFile.getName();
		
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
		
		return scene;
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
	
	private static void closeReader( BufferedReader reader )
	{
		try 
		{
			reader.close();
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
		}
	}
}
