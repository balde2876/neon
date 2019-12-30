package neon;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


public class Model {
	//private int drawCount;
	private int vertexArrayId;
	
	private int vertexBufferId;
	private int vertexAttribBufferId;
	private int indexBufferId;
	//private int tId;
	//private int texturesLength;
	
	private int indiciesLength;
	public Model(float[] verticies,float[] textureCoordinates,int[] indicies) {
		//drawCount = indicies.length;
		indiciesLength = indicies.length;
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indicies.length);
		indexBuffer.put(indicies);
		indexBuffer.flip();
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(verticies.length);
		vertexBuffer.put(verticies);
		vertexBuffer.flip();
		
		FloatBuffer vertexAttribBuffer = BufferUtils.createFloatBuffer(verticies.length);
		for (int i=0; i<verticies.length; i++) {
			vertexAttribBuffer.put((float) Math.random() / 2);
		}
		vertexAttribBuffer.flip();
		
		vertexArrayId = glGenVertexArrays();  
		glBindVertexArray(vertexArrayId);
		
		vertexBufferId = glGenBuffers();
		vertexAttribBufferId = glGenBuffers(); 
		indexBufferId = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false, 3, 0);
		glEnableVertexAttribArray(0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexAttribBufferId);
		glBufferData(GL_ARRAY_BUFFER, vertexAttribBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1,3,GL_FLOAT,false, 3, 0);
		glEnableVertexAttribArray(1);  
		
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		glBindVertexArray(vertexArrayId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		
		//glDrawArrays(GL_TRIANGLES, 0, indiciesLength/3);
		glDrawElements(GL_TRIANGLES, indiciesLength/3, GL_UNSIGNED_INT, 0);

		glBindVertexArray(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
