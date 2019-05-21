package neon;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.bgfx.BGFX.*;

import org.lwjgl.opengl.GL;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.bgfx.BGFXVertexDecl;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Neon {
	public Neon() {
		if (glfwInit() != true) {
			System.err.println("GLFW INIT FAILED");
			System.exit(1);
		}
		
		long win = glfwCreateWindow(1280,720,"Neon",0,0);
		glfwShowWindow(win);
		glfwMakeContextCurrent(win);
		
		GL.createCapabilities();
		
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glFrontFace(GL_CW);
		glDepthFunc(GL_LESS);
		
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[] {0.05f,0.05f,0.05f,1f} );
		glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {1.5f,1.5f,1.5f,1f} );
		
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		
		float light_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		float light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float light_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		

		glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
		glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);
		
		float light_position[] = { 0.0f, 0.0f, 10.0f, 0.0f };
		glLightfv(GL_LIGHT0, GL_POSITION, light_position);
		
		float[] textureCoordsMultiSidedBlock = {
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				
				0,0,
				0,0.25f,
				0.25f,0.25f,
				0.25f,0,
				
				0.75f,0.5f,
				0.5f,0.5f,
				0.5f,0.25f,
				0.75f,0.25f,
				
				0.25f,0.5f,
				0.5f,0.5f,
				0.5f,0.25f,
				0.25f,0.25f,
				
				0.75f,0,
				0.75f,0.25f,
				0.5f,0.25f,
				0.5f,0,
				
				0.25f,0,
				0.25f,0.25f,
				0.5f,0.25f,
				0.5f,0,
		};
		
		float[] textureCoordsUniformBlock = {
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
				0,0.25f,
				0,0.5f,
				0.25f,0.5f,
				0.25f,0.25f,
		};
		
		//Model modelMultiSidedBlock = new Model(verticesBlock, textureCoordsMultiSidedBlock, indicesBlock);
		//Model modelUniformBlock = new Model(verticesBlock, textureCoordsUniformBlock, indicesBlock);
		
		Texture texture = new Texture("./assets/terrain.png");
		
		//StaticShader shader = new StaticShader();
		
		
		
		float x = 0;
		
		Vector3f cameraRotation = new Vector3f(-0.7f,0.0f,0.0f);
		Vector3f cameraPosition = new Vector3f(0.0f,0.0f,256.0f);
		
		//Chunk chunkvar = new Chunk(0, 0, 0, "nice");
		
		Chunk[] loadedChunks = new Chunk[] {};
		
		int seed = 177014;
		
		int worldRadius = 6;
		
		System.out.println("Set world render distance to ".concat(Integer.toString(worldRadius * 32)).concat("m "));
		
		for (int blkx=-worldRadius; blkx<worldRadius; blkx++) {
			for (int blky=-worldRadius; blky<worldRadius; blky++) {
				for (int blkz=-1; blkz<2; blkz++) {
					Chunk newChunk = new Chunk(blkx, blky, blkz, seed);
					loadedChunks = ArrayHelper.push(loadedChunks,newChunk);
					newChunk.generateSurface();
				}
			}
		}
		
//		Chunk newChunk = new Chunk(0, 0, 0, seed);
//		loadedChunks = ArrayHelper.push(loadedChunks,newChunk);
//		newChunk.generateSurface();
//		
//		loadedChunks[4] = new Chunk(0, 1, 0, seed);
//		loadedChunks[5] = new Chunk(0, 2, 0, seed);
//		loadedChunks[6] = new Chunk(0, -2, 0, seed);
//		loadedChunks[7] = new Chunk(0, -3, 0, seed);
		
		
		int shaderProgram = glCreateProgram();
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		StringBuilder vertexShaderSource = new StringBuilder();
		StringBuilder fragmentShaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./shaders/vertexShader.vert"));
			String line;
			while ((line = reader.readLine()) != null) {
				vertexShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Vertex shader couldn't be loaded");
			System.exit(1);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./shaders/fragmentShader.frag"));
			String line;
			while ((line = reader.readLine()) != null) {
				fragmentShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Fragment shader couldn't be loaded");
			System.exit(1);
		}
		
		glShaderSource(vertexShader, vertexShaderSource);
	    glCompileShader(vertexShader);
	    if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
	        System.err.println("Vertex shader wasn't able to be compiled correctly.");
	    }
	    glShaderSource(fragmentShader, fragmentShaderSource);
	    glCompileShader(fragmentShader);
	    if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
	        System.err.println("Fragment shader wasn't able to be compiled correctly.");
	    }
	    
	    glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
		
		glClearColor(0.6f,0.9f,1.0f,0.0f);
		
		Model modelChunk;
		
		while(glfwWindowShouldClose(win) != true) {
			
			long frameStartTime = System.nanoTime();
			glfwPollEvents();
			
			if(glfwGetKey(win, GLFW_KEY_ESCAPE) == GL_TRUE) {
				System.out.println("Exiting");
				//glfwDestroyWindow(win);
		        glDeleteProgram(shaderProgram);
		        glDeleteShader(vertexShader);
		        glDeleteShader(fragmentShader);
				glfwSetWindowShouldClose(win, true);
				break;
			}
			
			if(glfwGetKey(win, GLFW_KEY_W) == GL_TRUE) {
				System.out.println("w");
				x = x + 0.001f;
			}
			
			if(glfwGetMouseButton(win,0) == GL_TRUE) {
				System.out.println("click");
			}
			
			//shader.start();
			
			
			glClear(GL_DEPTH_BUFFER_BIT);
			glClear(GL_COLOR_BUFFER_BIT);
			
			int width = 1280;
			int height = 720;
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1);
				IntBuffer pHeight = stack.mallocInt(1);
	
				glfwGetFramebufferSize(win, pWidth, pHeight);
				width = pWidth.get(0);
				height = pHeight.get(0);
				//glViewport(0, 0, pWidth.get(0), pHeight.get(0));
			}
			
			//cameraRotation.x = (float) (cameraRotation.x + ((Math.PI * 0.5 * 16)/1000f));
			//cameraRotation.y = (float) (cameraRotation.y + ((Math.PI * 0.3 * 16)/1000f));
			cameraRotation.z = (float) (cameraRotation.z + ((Math.PI * 0.05 * 16)/1000f));
		
			//Matrix4f perspective = Matrix4f.CreatePerspectiveFieldOfView(1.04, 4 / 4, 1, 10000) //Setup Perspective
			Matrix4f viewMatrix = new Matrix4f();
//			viewMatrix.
//			        rotateX((float)cameraRotation.x).
//			        rotateY((float)cameraRotation.y).
//			        rotateZ((float)cameraRotation.z).
//			        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
			
			
			viewMatrix.
	        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z).
	        rotateX((float)cameraRotation.x).
	        rotateY((float)cameraRotation.y).
	        rotateZ((float)cameraRotation.z);
			
			Matrix4f perspectiveMatrix = new Matrix4f();
			perspectiveMatrix.perspective((float) Math.toRadians(45), (float)width/height, 0.1f, 10000.0f); // last point is render distance

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			FloatBuffer fb1 = BufferUtils.createFloatBuffer(16);
			glLoadMatrixf(perspectiveMatrix.get(fb1));
			
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			FloatBuffer fb2 = BufferUtils.createFloatBuffer(16);
			glLoadMatrixf(viewMatrix.get(fb2));
			
			glViewport(0,0,width,height);
			
			float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			//float mat_shininess[] = { 50.0f };
			//float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
			glShadeModel(GL_SMOOTH);

			glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
			//glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);
			//glLightfv(GL_LIGHT0, GL_POSITION, light_position);
			
			
			//Chunk chunkvar = new Chunk(0, 0, 0, "nice");
			
			//chunkvar
			for (int chkid=0; chkid<loadedChunks.length; chkid++) {
				glPushMatrix();
				int[] chunkPosition = loadedChunks[chkid].getPosition();
				glTranslatef((float) chunkPosition[0] * 32,(float) chunkPosition[1] * 32,(float) chunkPosition[2] * 32);
				
				//BlockObject[] chunkBlocks = loadedChunks[chkid].returnSurfaceBlocks();
				
				
				//texture.bind();
				
				//modelChunk = new Model(loadedChunks[chkid].vertexSurface, loadedChunks[chkid].textureCoordsSurface, loadedChunks[chkid].indicesSurface);
				loadedChunks[chkid].chunkModel.render();
				
				
				
//				for (int i=0; i<chunkBlocks.length; i++) {
//					glPushMatrix();
//					int[] blockPosition = chunkBlocks[i].returnPosition();
// 					glTranslatef((float) blockPosition[0],(float) blockPosition[1],(float) blockPosition[2]);
// 					switch (chunkBlocks[i].returnType()) {
// 					case 1:
// 						//air
// 						break;
// 					case 2:
// 						texture.bind();
//						modelUniformBlock.render();
// 					case 3:
// 						texture.bind();
// 						modelMultiSidedBlock.render();
// 					}
//					glPopMatrix();
//				}
				
				glPopMatrix();
			}
			
//			glBegin(GL_QUADS);
//				//glColor4f(1.0f,0.0f,0.0f,0.0f);
//				glTexCoord2f(1,0);
//				glVertex2f(-0.5f+x,0.5f);
//				//glColor4f(1.0f,1.0f,0.0f,0.0f);
//				glTexCoord2f(1,1);
//				glVertex2f(-0.5f+x,-0.5f);
//				//glColor4f(0.0f,1.0f,0.0f,0.0f);
//				glTexCoord2f(0,1);
//				glVertex2f(0.5f+x,-0.5f);
//				//glColor4f(0.0f,0.0f,1.0f,0.0f);
//				glTexCoord2f(0,0);
//				glVertex2f(0.5f+x,0.5f);
//			glEnd();
			
			
			
			//shader.stop();
			
			glfwSwapBuffers(win);
			
			long frameTime = System.nanoTime() - frameStartTime;
			//System.out.println(Float.toString(frameTime/1000000).concat("ms"));
			float secondsPerFrame = (((float) (frameTime+1))/1000000000f);
			System.out.println(Float.toString(1/secondsPerFrame).concat("fps"));
		}
		
		glfwTerminate();
	}

	public static void main(String[] args) {
		new Neon();
	}

}
