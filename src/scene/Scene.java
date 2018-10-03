package scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GuiTexture;
import particles.ParticleSystem;
import terrains.Terrain;
import water.WaterTile;

public class Scene 
{
	private List<Entity> entities 					= new ArrayList<Entity>();
	private List<Entity> normalMapEntities 			= new ArrayList<Entity>();
	private List<Light> lights 						= new ArrayList<Light>();
	private List<Terrain> terrains 					= new ArrayList<Terrain>();
	private List<WaterTile> waters 					= new ArrayList<WaterTile>();
	private List<GuiTexture> guis 					= new ArrayList<GuiTexture>();
	private List<ParticleSystem> particleSystems 	= new ArrayList<ParticleSystem>();

	private Vector3f lightDirection;
	private Vector4f clipPlane;
	private Camera camera;
    
	public Scene( Camera camera )
	{
		this.camera 	= camera;
		this.clipPlane 	= new Vector4f( 0, -1, 0, 100000000 );
	}
	
    public Scene( Camera camera, Vector4f clipPlane )
    {
    	this.camera 	= camera;
    	this.clipPlane 	= clipPlane;
    }
    
    public void setLightDirection( Vector3f direction ) 
    {
		direction.normalise();
		this.lightDirection.set( direction );
	}
    
    public void addEntity( Entity entity )
    {
    	entities.add( entity );
    }
    
    public void addNormalEntity( Entity normalEntity )
    {
    	normalMapEntities.add( normalEntity );
    }
    
    public void addLight( Light light )
    {
    	lights.add( light );
    }
    
    public void addParticleSystem( ParticleSystem system )
    {
    	particleSystems.add( system );
    }
    
    public void addTerrain( Terrain terrain )
    {
    	terrains.add( terrain );
    }
    
    public void addWater( WaterTile water )
    {
    	waters.add( water );
    }

    public void addGui( GuiTexture texture )
    {
    	guis.add(texture);
    }
    
	public List<Entity> getEntities() 
	{
		return entities;
	}

	public List<Entity> getNormalMapEntities() 
	{
		return normalMapEntities;
	}

	public List<Light> getLights() 
	{
		return lights;
	}

	public List<ParticleSystem> getParticleSystems()
	{
		return particleSystems;
	}
	
	public List<Terrain> getTerrains() 
	{
		return terrains;
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public List<GuiTexture> getGuis()
	{
		return guis;
	}
	
	public List<WaterTile> getWaters()
	{
		return waters;
	}
	
	public Vector4f getClipPlane()
	{
		return clipPlane;
	}
	
	public void setClipPlane( Vector4f clipPlane )
	{
		this.clipPlane = clipPlane;
	}
}
