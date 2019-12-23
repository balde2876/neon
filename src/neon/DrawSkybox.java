package neon;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class DrawSkybox {
	private float rd = 100;
	private float[] ply = new float[3];
	private Texture skyboxTexture = new Texture("./assets/skybox.png");
	
	public DrawSkybox (float renderDistance) {
		rd = renderDistance;
	}
	
	public void updatePosition(float[] playerPosition) {
		ply = playerPosition;
	}
	
	public void drawBox() {
		float scf = (float) rd * 2;
		float[] verticesBox = {			
				-0.5f*scf,0.5f*scf,-0.5f*scf,//bottom
				-0.5f*scf,-0.5f*scf,-0.5f*scf,	
				0.5f*scf,-0.5f*scf,-0.5f*scf,	
				0.5f*scf,0.5f*scf,-0.5f*scf,		
				
				-0.5f*scf,0.5f*scf,0.5f*scf,//top	
				-0.5f*scf,-0.5f*scf,0.5f*scf,	
				0.5f*scf,-0.5f*scf,0.5f*scf,	
				0.5f*scf,0.5f*scf,0.5f*scf,
				
				0.5f*scf,0.5f*scf,0.5f*scf,
				0.5f*scf,0.5f*scf,-0.5f*scf,//right
				0.5f*scf,-0.5f*scf,-0.5f*scf,	
				0.5f*scf,-0.5f*scf,0.5f*scf,	
				
				-0.5f*scf,0.5f*scf,0.5f*scf,
				-0.5f*scf,0.5f*scf,-0.5f*scf,//left	
				-0.5f*scf,-0.5f*scf,-0.5f*scf,	
				-0.5f*scf,-0.5f*scf,0.5f*scf,	
				
				
				-0.5f*scf,0.5f*scf,0.5f*scf,//front
				-0.5f*scf,0.5f*scf,-0.5f*scf,
				0.5f*scf,0.5f*scf,-0.5f*scf,
				0.5f*scf,0.5f*scf,0.5f*scf,
				
				-0.5f*scf,-0.5f*scf,0.5f*scf,//back
				-0.5f*scf,-0.5f*scf,-0.5f*scf,
				0.5f*scf,-0.5f*scf,-0.5f*scf,
				0.5f*scf,-0.5f*scf,0.5f*scf
		};
		int[] indicesBox = {
				1,0,3,	
				1,3,2,
				7,4,5,
				6,7,5,
				9,8,11,
				9,11,10,
				15,12,13,
				14,15,13,
				17,16,19,
				17,19,18,
				23,20,21,
				22,23,21,
		};
		float[] textureCoordsBox = {
						0,0.25f,
						0,0.5f,
						0.25f,0.5f,
						0.25f,0.25f,

						0,0,
						0,0.25f,
						0.25f,0.25f,
						0.25f,0,

						0.75f,0.25f,
						0.75f,0.5f,
						0.5f,0.5f,
						0.5f,0.25f,
						
						0.25f,0.25f,
						0.25f,0.5f,
						0.5f,0.5f,
						0.5f,0.25f,

						0.75f,0,
						0.75f,0.25f,
						0.5f,0.25f,
						0.5f,0,

						0.25f,0,
						0.25f,0.25f,
						0.5f,0.25f,
						0.5f,0,
		};
		glPushMatrix();
		Model boxModel = new Model(verticesBox, textureCoordsBox, indicesBox);

		glTranslatef(ply[0],ply[1],ply[2]);
		
		skyboxTexture.bind();
		boxModel.render();
		glPopMatrix();
		
	}
}
