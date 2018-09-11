package fontRendering;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}
	
	public void render( Map<FontType, List<GUIText>> texts )
	{
		prepare();
		
		float width 			= 0.5f;
		float edge 				= 0.1f;
		float borderWidth 		= 0.5f;
		float borderEdge 		= 0.4f;
		Vector2f offset 		= new Vector2f( 0.006f, 0.006f );
		Vector3f outlineColour 	= new Vector3f( 1f, 0.2f, 0.2f );
		
		Random rand = new Random();
		float minX = 0.0000001f;
		float maxX = 0.9999999f;
		
		for( FontType font : texts.keySet() )
		{
			GL13.glActiveTexture( GL13.GL_TEXTURE0 );
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, font.getTextureAtlas() );
			
			for( GUIText text : texts.get( font ) )
			{
				outlineColour = new Vector3f( rand.nextFloat() * (maxX - minX) + minX, rand.nextFloat() * (maxX - minX) + minX, rand.nextFloat() * (maxX - minX) + minX );
				renderText( text, width, edge, borderWidth, borderEdge, offset, outlineColour );
			}
		}
		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare()
	{
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		GL11.glDisable( GL11.GL_DEPTH_TEST );
		shader.start();
	}
	
	private void renderText( GUIText text, float width, float edge, float borderWidth, float borderEdge, Vector2f offset, Vector3f outlineColour )
	{	
		GL30.glBindVertexArray( text.getMesh() );
		GL20.glEnableVertexAttribArray( 0 );
		GL20.glEnableVertexAttribArray( 1 );
		
		shader.setStats( width, edge, borderWidth, borderEdge, offset, outlineColour );
		shader.loadColour( text.getColour() );
		shader.loadTranslation( text.getPosition() );
		GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, text.getVertexCount() );
		
		GL20.glDisableVertexAttribArray( 0 );
		GL20.glDisableVertexAttribArray( 1 );
		GL30.glBindVertexArray( 0 );
	}
	
	private void endRendering()
	{
		shader.stop();
		GL11.glDisable( GL11.GL_BLEND );
		GL11.glEnable( GL11.GL_DEPTH_TEST );
	}

}
