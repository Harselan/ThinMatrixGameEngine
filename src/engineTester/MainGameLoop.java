package engineTester;

import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import scene.Scene;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolBox.MousePicker;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	 public static void main(String[] args) 
	 {
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
        
        ModelData data 			= OBJFileLoader.loadOBJ("pine");   
        RawModel treeModel 		= loader.loadToVAO( data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices() );
        RawModel playerModel 	= OBJLoader.loadObjModel( "person", loader );
        RawModel stallModel     = OBJLoader.loadObjModel( "stall", loader );
        
        //********************************************
        
        //*********** MODELS WITH NORMAL MAP **********
        
        TexturedModel barrelModel 	= new TexturedModel( NormalMappedObjLoader.loadOBJ( "barrel", loader ), new ModelTexture( loader.loadTexture( "barrel" ) ) );
        TexturedModel stoneModel 	= new TexturedModel( NormalMappedObjLoader.loadOBJ( "boulder", loader ), new ModelTexture( loader.loadTexture( "boulder" ) ) );
        TexturedModel cherryModel 	= new TexturedModel( OBJLoader.loadObjModel( "cherry", loader ), new ModelTexture( loader.loadTexture( "cherry" ) ) );
        TexturedModel lantern 		= new TexturedModel( OBJLoader.loadObjModel( "lantern", loader ), new ModelTexture( loader.loadTexture( "lantern" ) ) );
        //TexturedModel tower 		= new TexturedModel( OBJLoader.loadObjModel( "tower", loader ), new ModelTexture( loader.loadTexture( "tower", "mtl" ) ) );
        
        lantern.getTexture().setExtraInfoMap( loader.loadTexture( "lanternS" ) );
        
        cherryModel.getTexture().setHasTrasparency( true );
        cherryModel.getTexture().setShineDamper( 10 );
        cherryModel.getTexture().setReflectivity( 0.5f );
        cherryModel.getTexture().setExtraInfoMap( loader.loadTexture( "cherryS" ) );
        
        barrelModel.getTexture().setNormalMap( loader.loadTexture( "barrelNormal" ) );
        barrelModel.getTexture().setShineDamper( 10 );
        barrelModel.getTexture().setReflectivity( 0.5f );
        barrelModel.getTexture().setExtraInfoMap( loader.loadTexture( "barrelS" ) );
        
        stoneModel.getTexture().setNormalMap( loader.loadTexture( "boulderNormal" ) );
        stoneModel.getTexture().setShineDamper( 10 );
        stoneModel.getTexture().setReflectivity( 0.5f );
        
        //********************************************
        
        //*********** MODEL TEXTURES **********
        
        ModelTexture fernTextureAtlas = new ModelTexture( loader.loadTexture( "fern" ) );
        
        //********************************************
        
        fernTextureAtlas.setNumberOfRows( 2 );
        
        //*********** TEXTURED MODELS **********
        
        TexturedModel staticModel 	= new TexturedModel( treeModel,new ModelTexture(loader.loadTexture("pine")));
        TexturedModel grass 		= new TexturedModel( OBJLoader.loadObjModel( "grassModel", loader ), new ModelTexture( loader.loadTexture( "grassTexture" ) ) );
        TexturedModel fern 			= new TexturedModel( OBJLoader.loadObjModel( "fern", loader ), fernTextureAtlas );
        TexturedModel lampModel 	= new TexturedModel( OBJLoader.loadObjModel( "lamp", loader ), new ModelTexture( loader.loadTexture("lamp") ) );
        TexturedModel playerTexture = new TexturedModel( playerModel, new ModelTexture( loader.loadTexture( "playerTexture" ) ) );
        TexturedModel stallTexture  = new TexturedModel( stallModel, new ModelTexture( loader.loadTexture( "stallTexture" ) ) );
        
        //********************************************
        Player player = new Player( playerTexture, new Vector3f( 100, 0, -50 ), 0, 180, 0, 0.6f );
        Camera camera = new Camera( player );
        
        Scene scene = new Scene( camera );
        
        scene.addEntity( player );
        
        MasterRenderer renderer = new MasterRenderer( loader, camera );
        ParticleMaster.init( loader, renderer.getProjectionMatrix() );
        
        FontType font 	= new FontType( loader.loadFontTexture( "candara" ), "candara" );
        GUIText text 	= new GUIText( "PARTY HARD", 6, font, new Vector2f( -0.25f, 0.02f ), 1f, true );
        text.setColour( 0, 0, 0 );
        
        grass.getTexture().setHasTrasparency( true );
        grass.getTexture().setUseFakeLightning( true );
        fern.getTexture().setHasTrasparency( true );
        
        Light sun = new Light( new Vector3f( 10000000, 15000000, -10000000 ), new Vector3f( 1.3f, 1.3f, 1.3f ) );
        scene.addLight( sun );
        
        scene.addNormalEntity( new Entity( barrelModel, new Vector3f( 75, 10, -75 ), 0, 0, 0, 1f ) );
        
        Terrain terrain1 	= new Terrain( 0,-1,loader, texturePack, blendMap, "heightmap" );
        Terrain terrain2 	= new Terrain( -1,-1,loader, texturePack, blendMap, "heightmap" );
        scene.addTerrain( terrain1 );
        scene.addTerrain( terrain2 );
        
        int length 			= (int)Terrain.SIZE * scene.getTerrains().size();
        Random random 		= new Random(676452);
        
        for( int i = 0; i < length * 2; i++ )
        {
        	float x = random.nextFloat() * length - Terrain.SIZE;
        	float z = random.nextFloat() * length - Terrain.SIZE;
        	float terrainHeight = 0;
        	
        	for( Terrain terrain : scene.getTerrains() )
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
        		scene.addEntity(new Entity(fern, new Vector3f( x, y, z ),0,random.nextFloat() * 360,0,0.9f));
        		
        		scene.addEntity(new Entity(cherryModel, new Vector3f( x + 20, y, z + 20 ),0,0,0,3));
        		
        		if( i % 4 == 0 )
	            {
        			scene.addEntity(new Entity(grass, new Vector3f( x, y, z ),0,0,0,1));
	            }
	            
        		if( i % 5 == 0 )
	            {
        			scene.addEntity(new Entity(staticModel, new Vector3f( x, y, z ),0,0,0,3));
	            }
        		
	            if( i % 49 == 0 )
	            {
	            	scene.addNormalEntity( new Entity( stoneModel, new Vector3f( x, y, z ), 0, 0, 0, 1f ) );
	            }
	            
	            if( i % 59 == 0 )
	            {
	            	scene.addEntity( new Entity( stallTexture, new Vector3f( x, y, z ), 0, 0, 0, 3 ) );
	            }
	            
	            if( i % 20 == 0 )
	            {
	            	scene.addEntity(new Entity(lantern, new Vector3f( x + 30, y, z + 30 ),0,0,0,3));
	            }
	            
        	}
            
            if( i < 2 )
            {
            	x = random.nextFloat() * length - 400;
	        	z = random.nextFloat() * -600;
	        	
	        	for( Terrain terrain : scene.getTerrains() )
	    		{
	    			terrainHeight = terrain.getHeightOfTerrain( x, z );
	    			
	    			if( terrainHeight != 0 )
	    			{
	    				break;
	    			}
	    		}
	        	
	        	y = terrainHeight;
	        	
	        	scene.addLight( new Light( new Vector3f( x, y + 13.8f, z ), new Vector3f( random.nextInt( 4 ), random.nextInt( 4 ), random.nextInt( 4 ) ), new Vector3f( 1, 0.01f, 0.002f ) ) );
	        	scene.addEntity( new Entity( lampModel, new Vector3f( x, y, z ), 0, 0, 0, 1 ) );
            }

        }
        
        //GuiTexture gui = new GuiTexture( loader.loadTexture( "socuwan" ), new Vector2f( 0.5f, 0.5f ), new Vector2f( 0.25f, 0.25f ) );
        //GuiTexture gui2 = new GuiTexture( loader.loadTexture( "thinmatrix" ), new Vector2f( 0.3f, 0.6f ), new Vector2f( 0.25f, 0.25f ) );
        //guis.add( gui );
        //guis.add( gui2 );
        
        GuiRenderer guiRenderer = new GuiRenderer( loader );
        
        MousePicker picker = new MousePicker( camera, renderer.getProjectionMatrix(), scene.getTerrains() );
        
        Light light = new Light( new Vector3f( 293, 7, -305 ), new Vector3f( 0, 2, 2 ), new Vector3f( 1, 0.01f, 0.002f ) );
        scene.addLight( light );
        Entity lampEntity = ( new Entity( lampModel, new Vector3f( 293, -6.8f, -305 ), 0, 0, 0, 1 ) );
        scene.addEntity( lampEntity );
        
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix()); 
		
		//WaterTile water1 = new WaterTile(400, -400, -5);
		//WaterTile water2 = new WaterTile(-200, -400, 5);
		//waters.add(water1);
		//waters.add(water2);
		System.out.println( "TileSize:" + WaterTile.TILE_SIZE );
        for( int i = 0; i < 2; i++ )
        {
        	for( int j = 0; j < 1; j++ )
        	{
        		System.out.println( "centerx:" + (-i * WaterTile.TILE_SIZE - WaterTile.TILE_SIZE) );
        		System.out.println( "centerz:" + (-j * WaterTile.TILE_SIZE - WaterTile.TILE_SIZE) );
        		scene.addWater( new WaterTile( i * WaterTile.TILE_SIZE + WaterTile.TILE_SIZE + 50, -j * WaterTile.TILE_SIZE - WaterTile.TILE_SIZE, -2 ) );
        	}
        }

        //*********** Particle System Example **********
        
        ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( "particleStar" ), 1 );
        
        ParticleTexture particleTexture = new ParticleTexture( loader.loadTexture( "particleAtlas" ), 4, false ); 
        
        ParticleSystem system = new ParticleSystem( particleTexture, 40, 10, 0.1f, 1, 1.6f );
        
        system.randomizeRotation();
        system.setDirection( new Vector3f( 0, 1, 0 ), 0.1f );
        system.setLifeError( 0.1f );
        system.setSpeedError( 0.25f );
        system.setScaleError( 0.5f );
        
        scene.addParticleSystem( system );
        
        //********************************************
        
        Fbo multisampleFbo 	= new Fbo( Display.getWidth(), Display.getHeight() );
        Fbo outputFbo 		= new Fbo( Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE );
        Fbo outputFbo2 		= new Fbo( Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE );
        PostProcessing.init( loader );
        
        //*********** Game Loop Below **********
        
        while(!Display.isCloseRequested()){
        	player.move( scene.getTerrains() );
        	camera.move();
        	picker.update();
        	
        	for( ParticleSystem pSystem : scene.getParticleSystems() )
        	{
        		pSystem.generateParticles( player.getPosition() );
        	}
        	
        	system.generateParticles( player.getPosition() );
        	
        	ParticleMaster.update( camera );
        	renderer.renderShadowMap( scene.getEntities(), sun );
        	
        	GL11.glEnable( GL30.GL_CLIP_DISTANCE0 );
        	
        	for( WaterTile water : scene.getWaters() )
        	{
        		//Render reflection texture
        		water.fbos.bindReflectionFrameBuffer();
    			float distance = 2 * (camera.getPosition().y - water.getHeight());
    			camera.getPosition().y -= distance;
    			camera.invertPitch(); // if you're using camera roll as well you'll need to invert Z
    			renderer.renderScene( scene );
    			// return the camera back to its original position after rendering the scene
    			camera.getPosition().y += distance;
    			camera.invertPitch();
	        	
	        	//Render refraction texture
    			water.fbos.bindRefractionFrameBuffer();
    			renderer.renderScene( scene );
        	}
        	
        	//Render to screen
        	GL11.glDisable( GL30.GL_CLIP_DISTANCE0 );
        	for( WaterTile water : scene.getWaters() )
        	{
        		water.fbos.unbindCurrentFrameBuffer();
        	}
        	
        	multisampleFbo.bindFrameBuffer();
        	
        	renderer.renderScene( scene );
        	waterRenderer.render( scene.getWaters(), camera, light );
        	
        	multisampleFbo.unbindFrameBuffer();
        	multisampleFbo.resolveToFbo( GL30.GL_COLOR_ATTACHMENT0, outputFbo );
        	multisampleFbo.resolveToFbo( GL30.GL_COLOR_ATTACHMENT1, outputFbo2 );
        	PostProcessing.doPostProcessing( outputFbo.getColourTexture(), outputFbo2.getColourTexture() );
        	
        	Vector3f terrainPoint = picker.getCurrentTerrainPoint();
        	
        	if( terrainPoint != null )
        	{
        		lampEntity.setPosition( terrainPoint );
        		light.setPosition( new Vector3f( terrainPoint.x, terrainPoint.y + 15, terrainPoint.z ) );
        	}

        	ParticleMaster.renderParticles( camera );
        	
            guiRenderer.render( scene.getGuis() );
            TextMaster.render();
            
            DisplayManager.updateDisplay();
        }
        
        //********************************************
 
        //*********** Clean Up **********
        PostProcessing.cleanUp();
        outputFbo.cleanUp();
        outputFbo2.cleanUp();
        multisampleFbo.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        
        for( WaterTile water : scene.getWaters() )
    	{
        	water.fbos.cleanUp();       	
    	}
        
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
        
        //********************************************
 
    }
}
