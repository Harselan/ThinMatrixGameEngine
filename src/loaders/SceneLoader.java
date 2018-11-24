package loaders;

import java.io.BufferedReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import scene.Scene;
import scene.Settings;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.MyFile;
import water.WaterTile;

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
	
	public Scene createScene( MyFile sceneFile )
	{
		String sceneName = sceneFile.getName();
		
		String path = sceneName + "/" + Settings.playerFolder + "/";

		TexturedModel playerTexture = new TexturedModel( 
			OBJLoader.loadObjModel( path + "model", loader ), 
			new ModelTexture( loader.loadTexture( path + "material" ) ) 
		);
		
		Player player = new Player( playerTexture, Settings.startPosition, 0, 180, 0, 0.6f );
        Camera camera = new Camera( player );
		
		Scene scene = new Scene(camera, sceneName);
		
		scene.setPlayer( player );
		
		scene = EntityList.load( sceneFile, loader, scene );
		
		addWater( scene );
		
		return scene;
	}
	
	private void addWater( Scene scene )
	{
		for( int i = 0; i < 1; i++ )
        {
        	for( int j = 0; j < 1; j++ )
        	{
        		scene.addWater( new WaterTile( i * WaterTile.TILE_SIZE + WaterTile.TILE_SIZE + 50, -j * WaterTile.TILE_SIZE - WaterTile.TILE_SIZE, Settings.waterHeight ) );
        	}
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
