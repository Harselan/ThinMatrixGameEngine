package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolBox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	 public static void main(String[] args) {
		 
	        DisplayManager.createDisplay();
	        Loader loader = new Loader();
	        TextMaster.init( loader );
	        
	        //*********** TERRAIN TEXTURE STUFF **********
	        
	        TerrainTexture backgroundTexture 	= new TerrainTexture( loader.loadTexture( "grass" ) );
	        TerrainTexture rTexture 			= new TerrainTexture( loader.loadTexture( "dirt" ) );
	        TerrainTexture gTexture 			= new TerrainTexture( loader.loadTexture( "pinkFlowers" ) );
	        TerrainTexture bTexture 			= new TerrainTexture( loader.loadTexture( "path" ) );
	        TerrainTexture blendMap 			= new TerrainTexture( loader.loadTexture( "blendMap" ) );
	        
	        TerrainTexturePack texturePack 		= new TerrainTexturePack( backgroundTexture, rTexture, gTexture, bTexture );
	        
	        //********************************************
	        
	        //*********** RAW MODELS **********
	        
	        ModelData data 			= OBJFileLoader.loadOBJ("tree");   
	        RawModel treeModel 		= loader.loadToVAO( data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices() );
	        RawModel playerModel 	= OBJLoader.loadObjModel( "person", loader );
	        
	        //********************************************
	        
	        //*********** MODELS WITH NORMAL MAP **********
	        
	        TexturedModel barrelModel 	= new TexturedModel( NormalMappedObjLoader.loadOBJ( "barrel", loader ), new ModelTexture( loader.loadTexture( "barrel" ) ) );
	        TexturedModel stoneModel 	= new TexturedModel( NormalMappedObjLoader.loadOBJ( "boulder", loader ), new ModelTexture( loader.loadTexture( "boulder" ) ) );
	        
	        barrelModel.getTexture().setNormalMap( loader.loadTexture( "barrelNormal" ) );
	        barrelModel.getTexture().setShineDamper( 10 );
	        barrelModel.getTexture().setReflectivity( 0.5f );
	        
	        stoneModel.getTexture().setNormalMap( loader.loadTexture( "boulderNormal" ) );
	        stoneModel.getTexture().setShineDamper( 10 );
	        stoneModel.getTexture().setReflectivity( 0.5f );
	        
	        //********************************************
	        
	        //*********** MODEL TEXTURES **********
	        
	        ModelTexture fernTextureAtlas = new ModelTexture( loader.loadTexture( "fern" ) );
	        
	        //********************************************
	        
	        fernTextureAtlas.setNumberOfRows( 2 );
	        
	        //*********** TEXTURED MODELS **********
	        
	        TexturedModel staticModel 	= new TexturedModel( treeModel,new ModelTexture(loader.loadTexture("tree")));
	        TexturedModel grass 		= new TexturedModel( OBJLoader.loadObjModel( "grassModel", loader ), new ModelTexture( loader.loadTexture( "grassTexture" ) ) );
	        TexturedModel fern 			= new TexturedModel( OBJLoader.loadObjModel( "fern", loader ), fernTextureAtlas );
	        TexturedModel lampModel 	= new TexturedModel( OBJLoader.loadObjModel( "lamp", loader ), new ModelTexture( loader.loadTexture("lamp") ) );
	        TexturedModel playerTexture = new TexturedModel( playerModel, new ModelTexture( loader.loadTexture( "playerTexture" ) ) );
	        
	        //********************************************
	        
	        //*********** Lists of game objects **********
	        
	        List<Entity> entities 			= new ArrayList<Entity>();
	        List<Entity> normalMapEntities 	= new ArrayList<Entity>();
	        List<Light> lights 				= new ArrayList<Light>();
	        List<Terrain> terrains 			= new ArrayList<Terrain>();
	        
	      	//********************************************
	        
	        Player player = new Player( playerTexture, new Vector3f( 100, 0, -50 ), 0, 180, 0, 0.6f );
	        entities.add( player );
	        
	        Camera camera = new Camera( player );
	        
	        MasterRenderer renderer = new MasterRenderer( loader, camera );
	        ParticleMaster.init( loader, renderer.getProjectionMatrix() );
	        
	        FontType font 	= new FontType( loader.loadFontTexture( "candara" ), new File( "res/candara.fnt" ) );
	        GUIText text 	= new GUIText( "PARTY HARD", 6, font, new Vector2f( -0.25f, 0.02f ), 1f, true );
	        text.setColour( 0, 0, 0 );
	        
	        grass.getTexture().setHasTrasparency( true );
	        grass.getTexture().setUseFakeLightning( true );
	        fern.getTexture().setHasTrasparency( true );
	        
	        Light sun = new Light( new Vector3f( 10000000, 15000000, -10000000 ), new Vector3f( 1.3f, 1.3f, 1.3f ) );
	        lights.add( sun );
	        
	        normalMapEntities.add( new Entity( barrelModel, new Vector3f( 75, 10, -75 ), 0, 0, 0, 1f ) );
	        
	        Terrain terrain1 	= new Terrain( 0,-1,loader, texturePack, blendMap, "heightmap" );
	        Terrain terrain2 	= new Terrain( -1,-1,loader, texturePack, blendMap, "heightmap" );
	        terrains.add( terrain1 );
	        terrains.add( terrain2 );
	        
	        int length 			= 800;
	        Random random 		= new Random(676452);
	        
	        for( int i = 0; i < length; i++ )
	        {
	        	float x = random.nextFloat() * length - 400;
	        	float z = random.nextFloat() * -600;
	        	float terrainHeight = 0;
	        	
	        	for( Terrain terrain : terrains )
	    		{
	    			terrainHeight = terrain.getHeightOfTerrain( x, z );
	    			
	    			if( terrainHeight != 0 )
	    			{
	    				break;
	    			}
	    		}
	        	
	        	float y = terrainHeight;
	        	
	        	if( y > 1 )
	        	{
	        		entities.add(new Entity(staticModel, new Vector3f( x, y, z ),0,0,0,3));
		            entities.add(new Entity(grass, new Vector3f( x, y, z ),0,0,0,1));
		            entities.add(new Entity(fern, new Vector3f( x, y, z ),0,random.nextFloat() * 360,0,0.9f));
		            
		            if( i % 49 == 0 )
		            {
		            	normalMapEntities.add( new Entity( stoneModel, new Vector3f( x, y, z ), 0, 0, 0, 1f ) );
		            }
		            
	        	}
	            
	            if( i < 2 )
	            {
	            	x = random.nextFloat() * length - 400;
		        	z = random.nextFloat() * -600;
		        	
		        	for( Terrain terrain : terrains )
		    		{
		    			terrainHeight = terrain.getHeightOfTerrain( x, z );
		    			
		    			if( terrainHeight != 0 )
		    			{
		    				break;
		    			}
		    		}
		        	
		        	y = terrainHeight;
		        	
		        	lights.add( new Light( new Vector3f( x, y + 13.8f, z ), new Vector3f( random.nextInt( 4 ), random.nextInt( 4 ), random.nextInt( 4 ) ), new Vector3f( 1, 0.01f, 0.002f ) ) );
		        	entities.add( new Entity( lampModel, new Vector3f( x, y, z ), 0, 0, 0, 1 ) );
	            }

	        }
	         
	        List<GuiTexture> guis = new ArrayList<GuiTexture>();
	        //GuiTexture gui = new GuiTexture( loader.loadTexture( "socuwan" ), new Vector2f( 0.5f, 0.5f ), new Vector2f( 0.25f, 0.25f ) );
	        //GuiTexture gui2 = new GuiTexture( loader.loadTexture( "thinmatrix" ), new Vector2f( 0.3f, 0.6f ), new Vector2f( 0.25f, 0.25f ) );
	        //guis.add( gui );
	        //guis.add( gui2 );
	        
	        GuiRenderer guiRenderer = new GuiRenderer( loader );
	        
	        GuiTexture shadowMap = new GuiTexture( renderer.getShadowMapTexture(), new Vector2f( 0.5f, 0.5f ), new Vector2f( 0.5f, 0.5f ) );
	        guis.add( shadowMap );
	        MousePicker picker = new MousePicker( camera, renderer.getProjectionMatrix(), terrains );
	        
	        Light light = new Light( new Vector3f( 293, 7, -305 ), new Vector3f( 0, 2, 2 ), new Vector3f( 1, 0.01f, 0.002f ) );
	        lights.add( light );
	        Entity lampEntity = ( new Entity( lampModel, new Vector3f( 293, -6.8f, -305 ), 0, 0, 0, 1 ) );
	        entities.add( lampEntity );
	        
	        WaterFrameBuffers buffers = new WaterFrameBuffers();
	        WaterShader waterShader = new WaterShader();
	        WaterRenderer waterRenderer = new WaterRenderer( loader, waterShader, renderer.getProjectionMatrix(), buffers );
	        List<WaterTile> waters = new ArrayList<WaterTile>();
	        
	        for( int i = 0; i < 2; i++ )
	        {
	        	for( int j = 0; j < 2; j++ )
	        	{
	        		waters.add( new WaterTile( i * WaterTile.TILE_SIZE, -j * WaterTile.TILE_SIZE - 100, -5 ) );
	        	}
	        }

	        //*********** Particle System Example **********
	        
	        ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( "particleAtlas" ), 4, false ); 
	        
	        ParticleSystem system = new ParticleSystem( particleTexture, 40, 10, 0.1f, 1, 1.6f );
	        
	        system.randomizeRotation();
	        system.setDirection( new Vector3f( 0, 1, 0 ), 0.1f );
	        system.setLifeError( 0.1f );
	        system.setSpeedError( 0.25f );
	        system.setScaleError( 0.5f );
	        
	        //********************************************
	        
	        //*********** Game Loop Below **********
	        
	        while(!Display.isCloseRequested()){
	        	player.move( terrains );
	        	camera.move();
	        	picker.update();
	        	
	        	system.generateParticles( player.getPosition() );
	        	
	        	ParticleMaster.update( camera );
	        	renderer.renderShadowMap( entities, sun );
	        	
	        	GL11.glEnable( GL30.GL_CLIP_DISTANCE0 );
	        	
	        	for( WaterTile water : waters )
	        	{
	        		//Render reflection texture
		        	buffers.bindReflectionFrameBuffer();
		        	float distance = 2 * ( camera.getPosition().y - water.getHeight() );
		        	camera.getPosition().y -= distance;
		        	camera.invertPitch();
		        	renderer.renderScene( entities, normalMapEntities, terrains, lights, camera, new Vector4f( 0, 1, 0, -water.getHeight() + 1 ) );
		        	camera.getPosition().y += distance;
		        	camera.invertPitch();
		        	
		        	//Render refraction texture
		        	buffers.bindRefractionFrameBuffer();
		        	renderer.renderScene( entities, normalMapEntities, terrains, lights, camera, new Vector4f( 0, -1, 0, water.getHeight() ) );
	        	}	
	        	
	        	//Render to screen
	        	GL11.glDisable( GL30.GL_CLIP_DISTANCE0 );
	        	buffers.unbindCurrentFrameBuffer();
	        	renderer.renderScene( entities, normalMapEntities, terrains, lights, camera, new Vector4f( 0, -1, 0, 100000000 ) );
	        	waterRenderer.render( waters, camera, light );
	        	
	        	Vector3f terrainPoint = picker.getCurrentTerrainPoint();
	        	
	        	if( terrainPoint != null )
	        	{
	        		lampEntity.setPosition( terrainPoint );
	        		light.setPosition( new Vector3f( terrainPoint.x, terrainPoint.y + 15, terrainPoint.z ) );
	        	}

	        	ParticleMaster.renderParticles( camera );
	        	
	            guiRenderer.render( guis );
	            TextMaster.render();
	            
	            DisplayManager.updateDisplay();
	        }
	        
	        //********************************************
	 
	        //*********** Clean Up **********
	        ParticleMaster.cleanUp();
	        TextMaster.cleanUp();
	        buffers.cleanUp();
	        waterShader.cleanUp();
	        guiRenderer.cleanUp();
	        renderer.cleanUp();
	        loader.cleanUp();
	        DisplayManager.closeDisplay();
	        
	        //********************************************
	 
	    }
}
