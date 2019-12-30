package neon;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


public class Model {
	//private int drawCount;
	private int vertexArrayId;
	
	private int vertexBufferId;
	private int indexBufferId;
	//private int tId;
	//private int texturesLength;
	
	private int indiciesLength;
	public Model(float[] verticies,float[] textureCoordinates,int[] indicies) {
		//drawCount = indicies.length;
		indiciesLength = indicies.length;
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indicies.length);
		for (int i=0; i<indicies.length; i++) {
			indexBuffer.put(indicies[i]);
		}
		indexBuffer.flip();
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer((verticies.length/3) * (3+3+2));
		for (int i=0; i<(verticies.length/3); i++) {
			vertexBuffer.put(verticies[(i*3)+0]);
			vertexBuffer.put(verticies[(i*3)+1]);
			vertexBuffer.put(verticies[(i*3)+2]);
			vertexBuffer.put((float) Math.random());
			vertexBuffer.put((float) Math.random());
			vertexBuffer.put((float) Math.random());
			vertexBuffer.put(textureCoordinates[(i*2)+0]);
			vertexBuffer.put(textureCoordinates[(i*2)+1]);
			//vertexBuffer.put(((float) i) / (verticies.length/3));
		}
		vertexBuffer.flip();
		
		vertexArrayId = glGenVertexArrays();  
		glBindVertexArray(vertexArrayId);
		
		vertexBufferId = glGenBuffers();
		indexBufferId = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(glGetAttribLocation(Neon.shaderProgram, "inPosition"),3,GL_FLOAT,false, 12+12+8, 0); // 4 bytes per float, 3 used per vector, 2 vectors;
		glEnableVertexAttribArray(glGetAttribLocation(Neon.shaderProgram, "inPosition"));
		 
		glVertexAttribPointer(glGetAttribLocation(Neon.shaderProgram, "inColor"),3,GL_FLOAT,false, 12+12+8, 12); 
		glEnableVertexAttribArray(glGetAttribLocation(Neon.shaderProgram, "inColor")); 
		
		glVertexAttribPointer(glGetAttribLocation(Neon.shaderProgram, "inTexCoord"),2,GL_FLOAT,false, 12+12+8, 24); // 4 bytes per float, 2 per vector2;
		glEnableVertexAttribArray(glGetAttribLocation(Neon.shaderProgram, "inTexCoord")); 
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		glBindVertexArray(vertexArrayId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		
		//glDrawArrays(GL_TRIANGLES, 0, indiciesLength/3);
		glDrawElements(GL_TRIANGLES, indiciesLength, GL_UNSIGNED_INT, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
