package shaders;

import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram
{
	public static final String VERTEX_FILE = "src/shaders/vertexShader.vs";
	public static final String FRAGMENT_FILE = "src/shaders/fragmentShader.fs";
	
	private int location_transformationMatrix;

	public StaticShader() 
	{
		super( VERTEX_FILE, FRAGMENT_FILE );
	}

	@Override
	protected void bindAttributes() 
	{		
		super.bindAttribute( 0, "position" );
		super.bindAttribute( 1, "textureCoords" );
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation( "transformatinoMatrix" );
	}
	
	public void loadTransformationMatrix( Matrix4f matrix )
	{
		super.loadMatrix( location_transformationMatrix, matrix );
	}
}
