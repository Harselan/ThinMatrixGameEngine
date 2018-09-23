package textures;

public class ModelTexture 
{
	private int textureID;
	private int normalMap;
	private int specularMap;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTrasparency = false;
	private boolean useFakeLightning = false;
	private boolean hasSpecularMap = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture( int id )
	{
		this.textureID = id;
	}
	
	public void setExtraInfoMap( int specularMap )
	{
		this.specularMap = specularMap;
		this.hasSpecularMap = true;
	}
	
	public boolean hasSpecularMap()
	{
		return hasSpecularMap;
	}
	
	public int getSpecularMap()
	{
		return specularMap;
	}
	
	public int getNumberOfRows() 
	{
		return numberOfRows;
	}

	public int getNormalMap() 
	{
		return normalMap;
	}

	public void setNormalMap( int normalMap ) 
	{
		this.normalMap = normalMap;
	}

	public void setNumberOfRows( int numberOfRows ) 
	{
		this.numberOfRows = numberOfRows;
	}

	public int getID()
	{
		return this.textureID;
	}

	public float getShineDamper() 
	{
		return shineDamper;
	}

	public void setShineDamper( float shineDamper ) 
	{
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() 
	{
		return reflectivity;
	}

	public void setReflectivity( float reflectivity ) 
	{
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() 
	{
		return hasTrasparency;
	}

	public void setHasTrasparency( boolean hasTrasparency ) 
	{
		this.hasTrasparency = hasTrasparency;
	}

	public boolean isUseFakeLightning() 
	{
		return useFakeLightning;
	}

	public void setUseFakeLightning( boolean useFakeLightning ) 
	{
		this.useFakeLightning = useFakeLightning;
	}
}
