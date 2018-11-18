package engineTester;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import guis.GuiRenderer;
import loaders.LoaderSettings;
import loaders.SceneLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import scene.Scene;
import utils.MyFile;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainApp 
{
	public static void main( String[] args ) 
	{
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		SceneLoader sceneLoader = new SceneLoader( loader );
		Scene scene = sceneLoader.loadScene(new MyFile(LoaderSettings.RES_FOLDER, "scene"));
		
		MasterRenderer renderer = new MasterRenderer( loader, scene.getCamera() );
        //MousePicker picker = new MousePicker( camera, renderer.getProjectionMatrix(), scene.getTerrains() );
		
		ParticleMaster.init( loader, renderer.getProjectionMatrix() );
		
		Light light = new Light( new Vector3f( 293, 7, -305 ), new Vector3f( 0, 2, 2 ), new Vector3f( 1, 0.01f, 0.002f ) );
        scene.addLight( light );
        
        //********************************************
        
        WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix()); 
		GuiRenderer guiRenderer = new GuiRenderer( loader );
		
        for( int i = 0; i < 2; i++ )
        {
        	for( int j = 0; j < 1; j++ )
        	{
        		scene.addWater( new WaterTile( i * WaterTile.TILE_SIZE + WaterTile.TILE_SIZE + 50, -j * WaterTile.TILE_SIZE - WaterTile.TILE_SIZE, -2 ) );
        	}
        }
        
        Fbo multisampleFbo 	= new Fbo( Display.getWidth(), Display.getHeight() );
        Fbo outputFbo 		= new Fbo( Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE );
        Fbo outputFbo2 		= new Fbo( Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE );
        PostProcessing.init( loader );
        
        Camera camera = scene.getCamera();
        
		while( !Display.isCloseRequested() )
		{
			scene.getPlayer().move( scene.getTerrains() );
        	camera.move();
        	//picker.update();
        	
        	for( ParticleSystem pSystem : scene.getParticleSystems() )
        	{
        		pSystem.generateParticles();
        		//pSystem.generateParticles( scene.getPlayer().getPosition() );
        	}
        	
        	ParticleMaster.update( scene.getCamera() );
        	
        	Light sun = new Light( new Vector3f( 100000000, 150000000, -100000000 ), new Vector3f( 1.3f, 1.3f, 1.3f ) );
            scene.addLight( sun );
        	
        	/*system.generateParticles( new Vector3f( 100, 50, 50 ) );*/
        	
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

        	ParticleMaster.renderParticles( scene.getCamera() );
        	
        	multisampleFbo.unbindFrameBuffer();
        	multisampleFbo.resolveToFbo( GL30.GL_COLOR_ATTACHMENT0, outputFbo );
        	multisampleFbo.resolveToFbo( GL30.GL_COLOR_ATTACHMENT1, outputFbo2 );
        	PostProcessing.doPostProcessing( outputFbo.getColourTexture(), outputFbo2.getColourTexture() );
        	
        	/*Vector3f terrainPoint = picker.getCurrentTerrainPoint();
        	
        	if( terrainPoint != null )
        	{
        		lampEntity.setPosition( terrainPoint );
        		light.setPosition( new Vector3f( terrainPoint.x, terrainPoint.y + 15, terrainPoint.z ) );
        	}*/
        	
            guiRenderer.render( scene.getGuis() );
            //TextMaster.render();
            
            DisplayManager.updateDisplay();
		}

		//*********** Clean Up **********
        PostProcessing.cleanUp();
        outputFbo.cleanUp();
        outputFbo2.cleanUp();
        multisampleFbo.cleanUp();
        ParticleMaster.cleanUp();
        //TextMaster.cleanUp();
        
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