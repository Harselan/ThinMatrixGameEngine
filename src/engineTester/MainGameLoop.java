package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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
	        TexturedModel fern = new TexturedModel( OBJLoader.loadObjModel( "fern", loader ), new ModelTexture( loader.loadTexture( "fern" ) ) );
	        fern.getTexture().setHasTrasparency( true );
	        
	        List<Entity> entities = new ArrayList<Entity>();
	        Random random = new Random();
	        for(int i=0;i<500;i++){
	            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
	            entities.add(new Entity(grass, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,1));
	            entities.add(new Entity(fern, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,0.6f));
	        }
	         
	        Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
	         
	        Terrain terrain = new Terrain( -1,-1,loader, texturePack, blendMap );
	        Terrain terrain2 = new Terrain( 0,-1,loader, texturePack, blendMap );
	         
	        Camera camera = new Camera();   
	        MasterRenderer renderer = new MasterRenderer();
	        
	        RawModel bunnyModel = OBJLoader.loadObjModel( "stanfordBunny", loader );
	        TexturedModel stanfordBunny = new TexturedModel( bunnyModel, new ModelTexture( loader.loadTexture( "white" ) ) );
	        
	        Player player = new Player( stanfordBunny, new Vector3f( 10, 0, -50 ), 0, 0, 0, 1 );
	         
	        while(!Display.isCloseRequested()){
	            camera.move();
	            player.move();
	            renderer.processEntity(player);
	            
	            renderer.processTerrain(terrain);
	            renderer.processTerrain(terrain2);
	            for(Entity entity:entities){
	                renderer.processEntity(entity);
	            }
	            renderer.render(light, camera);
	            DisplayManager.updateDisplay();
	        }
	 
	        renderer.cleanUp();
	        loader.cleanUp();
	        DisplayManager.closeDisplay();
	 
	    }
}
