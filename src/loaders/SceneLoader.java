package loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Player;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import scene.Scene;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.MyFile;

public class SceneLoader 
{
	private Loader loader;
	
	//*********** TERRAIN TEXTURE STUFF **********
    
    TerrainTexture backgroundTexture;
    TerrainTexture rTexture;
    TerrainTexture gTexture;
    TerrainTexture bTexture;
    TerrainTexture blendMap;
    
    TerrainTexturePack texturePack;
    
    //********************************************
	
    private int x = 0, y = 0, z = 0;
	
    public SceneLoader( Loader loader )
    {
    	this.loader = loader;
    	
    	backgroundTexture 	= new TerrainTexture( loader.loadTexture( "grass" ) );
        rTexture 			= new TerrainTexture( loader.loadTexture( "dirt" ) );
        gTexture 			= new TerrainTexture( loader.loadTexture( "pinkFlowers" ) );
        bTexture 			= new TerrainTexture( loader.loadTexture( "path" ) );
        blendMap 			= new TerrainTexture( loader.loadTexture( "blendMap" ) );

        texturePack 		= new TerrainTexturePack( backgroundTexture, rTexture, gTexture, bTexture );
    }
    
    public SceneLoader()
    {
    	this( new Loader() );
    }
    
	public Scene loadScene( MyFile sceneFile ) 
	{
		MyFile sceneList = new MyFile( sceneFile, LoaderSettings.ENTITY_LIST_FILE );
		BufferedReader reader = getReader( sceneList );
		
		MyFile player 			= readEntityFiles( reader, sceneFile )[0];
		MyFile[] terrainFiles 	= readEntityFiles( reader, sceneFile );
		MyFile[] entityFiles 	= readEntityFiles( reader, sceneFile );
		
		closeReader( reader );
		
		return createScene( terrainFiles, entityFiles, sceneFile );
	}
	
	private Scene createScene( MyFile[] terrainFiles, MyFile[] entityFiles, MyFile sceneFile )
	{
		String sceneName = sceneFile.getName();
		
		TexturedModel playerTexture = new TexturedModel( OBJLoader.loadObjModel( sceneName + "/player/model", loader ), new ModelTexture( loader.loadTexture( "playerTexture" ) ) );
		Player player = new Player( playerTexture, new Vector3f( 100, 0, -50 ), 0, 180, 0, 0.6f );
        Camera camera = new Camera( player );
		
		Scene scene = new Scene(camera, sceneName);
		
		scene.setPlayer( player );
		
		scene = EntityList.load( sceneFile, loader, scene );
		
		//addEntities(scene, entityFiles);
		addTerrains(scene, terrainFiles);
		
		return scene;
	}
	
	private void addEntities( Scene scene, MyFile[] entityFiles )
	{
		Random rand = new Random();
		
		for( MyFile file : entityFiles )
		{
			String filePath = scene.getName() + "/" + file.getName() + "/";
			TexturedModel model = new TexturedModel( OBJLoader.loadObjModel( filePath + "model", loader ), new ModelTexture( loader.loadTexture( filePath + "material" ) ) );
			Entity entity = new Entity( model, new Vector3f( x, y, z ),0,0,0,1 );
			
			x += rand.nextInt( 15 );
			z += rand.nextInt( 15 );
			
			scene.addEntity( entity );
		}
	}
	
	private void setEntityConfigs( Entity entity, Configs configs )
	{
		//entity.setCastsShadow( configs.castsShadow() );
		//entity.setHasReflection( configs.hasReflection() );
		//entity.setSeenUnderWater( configs.hasRefraction() );
		//entity.setImportant( configs.isImportant() );
	}
	
	private void addTerrains( Scene scene, MyFile[] terrainFiles )
	{
		for( MyFile file : terrainFiles )
		{
			Terrain terrain = new Terrain( 0,-1,loader, texturePack, blendMap, "heightmap" );
			scene.addTerrain(terrain);
		}
	}
	
	private BufferedReader getReader( MyFile file ) 
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
	
	private void closeReader( BufferedReader reader )
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
	
	private MyFile[] readEntityFiles( BufferedReader reader, MyFile sceneFile ) 
	{
		try 
		{
			String line = reader.readLine();
			String[] names = line.split(LoaderSettings.SEPARATOR);
			MyFile[] files = new MyFile[names.length];
			
			for( int i=0;i<files.length;i++ )
			{
				files[i] = new MyFile(sceneFile, names[i]);
			}
			
			return files;
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
			System.err.println( "Couldn't read scene file: "+sceneFile );
			System.exit(-1);
			return null;
		}
	}
}
