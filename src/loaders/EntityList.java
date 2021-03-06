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
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.MyFile;

public class EntityList 
{
	public static String[] headers;

	private static Loader loader;
	
	private static Scene loadRow( String line, String sceneName, Scene scene )
	{
		String[] names = line.split(LoaderSettings.SEPARATOR);
		
		String objectType 	= names[0];
		String objectName 	= names[1];
		Vector3f pos 		= new Vector3f( Float.parseFloat( names[2] ), Float.parseFloat( names[3] ), Float.parseFloat( names[4] ) );
		float scale 		= Float.parseFloat( names[5] );
		String filePath 	= sceneName + "/" + objectName + "/";
		
		switch( objectType )
		{
			case "entity":
				filePath = sceneName + "/entities/" + objectName + "/";
				scene.addEntity( createEntity( filePath, pos, scale ) );
			break;
			
			case "terrain":
				scene.addTerrain( createTerrain( filePath, pos ) );
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
	
	private static Terrain createTerrain( String path, Vector3f grid )
	{
		TerrainTexture backgroundTexture 	= new TerrainTexture( loader.loadTexture( path + "background" ) );
		TerrainTexture rTexture 			= new TerrainTexture( loader.loadTexture( path + "r" ) );
		TerrainTexture gTexture 			= new TerrainTexture( loader.loadTexture( path + "g" ) );
		TerrainTexture bTexture 			= new TerrainTexture( loader.loadTexture( path + "b" ) );
		TerrainTexture blendMap 			= new TerrainTexture( loader.loadTexture( path + "blendMap" ) );

		TerrainTexturePack texturePack 		= new TerrainTexturePack( backgroundTexture, rTexture, gTexture, bTexture );
		
		return new Terrain( (int)grid.x, (int)grid.y, loader, texturePack, blendMap, "heightmap" );
	}
	
	private static ParticleSystem createParticleSystem( String path, Vector3f pos, float scale )
	{	
		ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( path + "material" ), 1, false ); 
        ParticleSystem system 			= new ParticleSystem( particleTexture, 50, 25, 0.3f, 4, 1, pos );
        
        system.randomizeRotation();
        system.setDirection( new Vector3f( 0, 1, 0 ), 0.1f );
        system.setLifeError( 1f );
        system.setSpeedError( 1f );
        system.setScaleError( 1f );
        
		return system;
	}
	
	private static ParticleSystem createParticleAtlasSystem( String path, Vector3f pos, float scale )
	{	
		ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( path + "material" ), 4, false ); 
        ParticleSystem system 			= new ParticleSystem( particleTexture, 50, 10, 0.1f, 1, 1.6f, pos );
        
        system.randomizeRotation();
        system.setDirection( new Vector3f( 0.31f, 0.31f, 0 ), 0.1f );
        system.setLifeError( 0.1f );
        system.setSpeedError( 0.25f );
        system.setScaleError( 0.5f );
        
		return system;
	}
	
	private static GuiTexture createGuiTexture( String path, Vector3f pos, float scale )
	{
		//Kom ig�g. x och y koordinaterna ska vara mellan 0 och 1. 0 �r h�gst upp till v�nster och 1 �r l�ngst ner till h�ger
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
