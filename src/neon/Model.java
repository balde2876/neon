package neon;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


public class Model {
	private int drawCount;
	private int vId;
	private int tId;
	private int iId;
	public Model(float[] verticies,float[] textureCoordinates,int[] indicies) {
		drawCount = indicies.length;
		
		vId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vId);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(verticies), GL_STATIC_DRAW);
		
		tId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tId);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureCoordinates), GL_STATIC_DRAW);
		
		iId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iId);
		IntBuffer buffer = BufferUtils.createIntBuffer(indicies.length);
		buffer.put(indicies);
		buffer.flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, vId);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, tId);
		glTexCoordPointer(2, GL_FLOAT, 0, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iId);
		
		glDrawElements(GL_TRIANGLES, drawCount, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}
	
	private FloatBuffer createBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
