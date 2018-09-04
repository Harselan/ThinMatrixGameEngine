package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	 public static void main(String[] args) {
		 
	        DisplayManager.createDisplay();
	        Loader loader = new Loader();
	        
	        //*********** TERRAIN TEXTURE STUFF **********
	        
	        TerrainTexture backgroundTexture = new TerrainTexture( loader.loadTexture( "grass" ) );
	        TerrainTexture rTexture = new TerrainTexture( loader.loadTexture( "dirt" ) );
	        TerrainTexture gTexture = new TerrainTexture( loader.loadTexture( "pinkFlowers" ) );
	        TerrainTexture bTexture = new TerrainTexture( loader.loadTexture( "path" ) );
	        
	        TerrainTexturePack texturePack = new TerrainTexturePack( backgroundTexture, rTexture, gTexture, bTexture );
	        TerrainTexture blendMap = new TerrainTexture( loader.loadTexture( "blendMap" ) );
	        
	        //********************************************
	        
	        ModelData data = OBJFileLoader.loadOBJ("tree");
	         
	         
	        RawModel treeModel = loader.loadToVAO( data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices() );
	         
	        TexturedModel staticModel = new TexturedModel( treeModel,new ModelTexture(loader.loadTexture("tree")));
	        TexturedModel grass = new TexturedModel( OBJLoader.loadObjModel( "grassModel", loader ), new ModelTexture( loader.loadTexture( "grassTexture" ) ) );
	        grass.getTexture().setHasTrasparency( true );
	        grass.getTexture().setUseFakeLightning( true );
	        
	        ModelTexture fernTextureAtlas = new ModelTexture( loader.loadTexture( "fern" ) );
	        fernTextureAtlas.setNumberOfRows( 2 );
	        
	        
	        TexturedModel fern = new TexturedModel( OBJLoader.loadObjModel( "fern", loader ), fernTextureAtlas );
	        fern.getTexture().setHasTrasparency( true );
	        
	        List<Entity> entities = new ArrayList<Entity>();
	        Random random = new Random(676452);
	        
	        Terrain terrain = new Terrain( 0,-1,loader, texturePack, blendMap, "heightmap" );
	        
	        for(int i=0;i<500;i++){
	        	
	        	float x = random.nextFloat() * 800 - 400;
	        	float z = random.nextFloat() * -600;
	        	float y = terrain.getHeightOfTerrain( x, z );
	        	
	            entities.add(new Entity(staticModel, new Vector3f( x, y, z ),0,0,0,3));
	            
	            x = random.nextFloat() * 800 - 400;
	        	z = random.nextFloat() * -600;
	        	y = terrain.getHeightOfTerrain( x, z );
	            
	            entities.add(new Entity(grass, new Vector3f( x, y, z ),0,0,0,1));
	            
	            x = random.nextFloat() * 800 - 400;
	        	z = random.nextFloat() * -600;
	        	y = terrain.getHeightOfTerrain( x, z );
	        	
	            entities.add(new Entity(fern, random.nextInt( 4 ), new Vector3f( x, y, z ),0,random.nextFloat() * 360,0,0.9f));
	        }
	         
	        Light light = new Light(new Vector3f(0,10000,-7000),new Vector3f(1,1,1));
	        Light light2 = new Light(new Vector3f(-200,10,-200),new Vector3f(10,0,0));
	        Light light3 = new Light(new Vector3f(200,10,200),new Vector3f(0,0,10));
	        
	        List<Light> lights = new ArrayList<Light>();
	         
	        lights.add( light );
	        lights.add( light2 );
	        lights.add( light3 );
	        
	        MasterRenderer renderer = new MasterRenderer();
	        
	        RawModel playerModel = OBJLoader.loadObjModel( "person", loader );
	        TexturedModel playerTexture = new TexturedModel( playerModel, new ModelTexture( loader.loadTexture( "playerTexture" ) ) );
	        
	        Player player = new Player( playerTexture, new Vector3f( 100, 0, -50 ), 0, 180, 0, 0.6f );
	        
	        Camera camera = new Camera( player );
	         
	        List<GuiTexture> guis = new ArrayList<GuiTexture>();
	        GuiTexture gui = new GuiTexture( loader.loadTexture( "socuwan" ), new Vector2f( 0.5f, 0.5f ), new Vector2f( 0.25f, 0.25f ) );
	        GuiTexture gui2 = new GuiTexture( loader.loadTexture( "thinmatrix" ), new Vector2f( 0.3f, 0.6f ), new Vector2f( 0.25f, 0.25f ) );
	        guis.add( gui );
	        guis.add( gui2 );
	        
	        GuiRenderer guiRenderer = new GuiRenderer( loader );
	        
	        while(!Display.isCloseRequested()){
	        	player.move( terrain );
	        	camera.move();
	        	
	            renderer.processEntity(player);
	            renderer.processTerrain(terrain);
	            
	            for(Entity entity:entities){
	                renderer.processEntity(entity);
	            }
	            
	            renderer.render(lights, camera);
	            guiRenderer.render( guis );
	            DisplayManager.updateDisplay();
	        }
	 
	        guiRenderer.cleanUp();
	        renderer.cleanUp();
	        loader.cleanUp();
	        DisplayManager.closeDisplay();
	 
	    }
}
