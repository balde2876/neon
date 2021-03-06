package neon;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;

//import static org.lwjgl.bgfx.BGFX.*;

import org.lwjgl.opengl.GL;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.JSONStringer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
//import org.lwjgl.bgfx.BGFXVertexDecl;
//import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
//import org.lwjgl.system.MemoryUtil;

public class Neon {
	static Logger logger = new Logger();
	static int shaderProgram;
	
	public Neon() {
		int seed = 177013; // Seed for terrain generation
		int worldRenderRadius = 4; // How far the player can see
		int worldRenderHeight = 3; // How far the player can see up or down
		int worldRadius = worldRenderRadius+1; // How far the world will be loaded
		int worldHeight = worldRenderHeight+1; // How far the world will be loaded up or down
		int worldRamRecoverRadius = worldRadius + 4; // How aggressive the ram reclaimer is
		int concurrentChunkRenderingLevel = 128; // Threads used to create chunk surfaces
		int width = 1280;//1280
		int height = 720;//720
		
		float renderDistance = worldRamRecoverRadius * 128;
		
		if (glfwInit() != true) {
			logger.logFatalError("GLFW INIT FAILED");
			System.exit(1);
		}
		
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		
		long win = glfwCreateWindow(width,height,"Neon",0,0);
		glfwShowWindow(win);
		glfwMakeContextCurrent(win);
		
		GL.createCapabilities();
		
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glFrontFace(GL_CW);
		
		//glDepthFunc(GL_LESS);
		glDepthFunc(GL_LEQUAL);
		
		glEnable(GL_BLEND);
	    //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	   // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
	    //glBlendFunc(GL_ONE, GL_ZERO);
		
		//glEnable(GL_LIGHTING);
		
		//glEnable(GL_COLOR_MATERIAL);
		//glColorMaterial(GL_FRONT, GL_DIFFUSE);
		
		//float light_ambient[] = { 0.5f, 0.5f, 0.5f, 0.5f };
		//float light_diffuse[] = { 1.0f, 0.5f, 1.0f, 0.5f };
		//float light_specular[] = { 1.0f, 1.0f, 0.5f, 0.5f };
	
		//float x = 0;
	
		
		Chunk[] loadedChunks = new Chunk[] {};
		
		logger.logInfo("Set world render distance to ".concat(Integer.toString(worldRadius * 32)).concat("m "));
		for (int blkx=-worldRadius; blkx<worldRadius; blkx++) {
			for (int blky=-worldRadius; blky<worldRadius; blky++) {
				for (int blkz=-1; blkz<2; blkz++) {
					Chunk newChunk = new Chunk(blkx, blky, blkz, seed, loadedChunks);
					loadedChunks = ArrayHelper.push(loadedChunks,newChunk);
					//newChunk.generateSurface();
				}
			}
		}
		
//		System.out.println("Ignoring render distance, in debug mode");
//		Chunk newChunk = new Chunk(0, 0, 0, seed, loadedChunks);
//		loadedChunks = ArrayHelper.push(loadedChunks,newChunk);
		
		shaderProgram = glCreateProgram();
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
			logger.logInfo("Vertex shader loaded");
		} catch (IOException e) {
			logger.logFatalError("Vertex shader couldn't be loaded");
			System.exit(1);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./shaders/fragmentShader.frag"));
			String line;
			while ((line = reader.readLine()) != null) {
				fragmentShaderSource.append(line).append("\n");
			}
			reader.close();
			logger.logInfo("Fragment shader loaded");
		} catch (IOException e) {
			logger.logFatalError("Fragment shader couldn't be loaded");
			System.exit(1);
		}
		
		glShaderSource(vertexShader, vertexShaderSource);
	    glCompileShader(vertexShader);
	    if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
	    	logger.logFatalError("Vertex shader wasn't able to be compiled correctly.");
	    } else {
	    	logger.logInfo("Vertex shader compiled");
	    }
	    logger.logInfo(glGetShaderInfoLog(vertexShader));
	    glShaderSource(fragmentShader, fragmentShaderSource);
	    glCompileShader(fragmentShader);
	    if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
	    	logger.logFatalError("Fragment shader wasn't able to be compiled correctly.");
	    } else {
	    	logger.logInfo("Fragment shader compiled");
	    }
	    logger.logInfo(glGetShaderInfoLog(fragmentShader));
	    
	    glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        //glValidateProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
	    	logger.logFatalError("Shader program wasn't able to be compiled correctly.");
	    } else {
	    	logger.logInfo("Shader program compiled");
	    }
        logger.logInfo(glGetProgramInfoLog(shaderProgram));
        
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader); 
		
		glClearColor(0.6f,0.9f,1.0f,0.0f);

		Texture terrainTexture = new Texture("./assets/terrain.png");
		Texture UIFont = new Texture("./assets/font.png");
		Texture UITextures[] = new Texture[] {};
		
		UITextures = ArrayHelper.push(UITextures,new Texture("./assets/logo.png"));
		
		glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		Vector3f cameraRotation = new Vector3f((float) (-Math.PI/2),0.0f,0.0f);
		Vector3f playerPosition = new Vector3f(16.0f,16.0f,32.0f);
		

		//DrawSkybox SkyboxRenderer = new DrawSkybox(32*(worldRadius+1));
		DrawSkybox SkyboxRenderer = new DrawSkybox(renderDistance * (float) Math.pow((float) Math.sin(Math.PI / 4),3f) );
		
		Draw2D Renderer2D = new Draw2D();
		Draw2D TextRenderer2D = new Draw2D();
		
		float currentFPS = 0;
		float lastFPSDisplay = 0;

		while(glfwWindowShouldClose(win) != true) {
			
			long frameStartTime = System.nanoTime();
			glfwPollEvents();
			
			if(glfwGetKey(win, GLFW_KEY_ESCAPE) == GL_TRUE) {
				logger.logInfo("Exiting by user input");
				//glfwDestroyWindow(win);
		        glDeleteProgram(shaderProgram);
		        glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				glfwSetWindowShouldClose(win, true);
				//break;
			}
			
			double walkspeed = 0.5f;
			
			if(glfwGetKey(win, GLFW_KEY_W) == GL_TRUE) {
				//System.out.println("w");
				//cameraRotation.z
				playerPosition.y = (float) (playerPosition.y + (Math.cos(cameraRotation.z) * walkspeed));
				playerPosition.x = (float) (playerPosition.x + (Math.sin(cameraRotation.z) * walkspeed));
			}
			if(glfwGetKey(win, GLFW_KEY_A) == GL_TRUE) {
				//System.out.println("w");
				playerPosition.y = (float) (playerPosition.y + (Math.sin(cameraRotation.z) * walkspeed));
				playerPosition.x = (float) (playerPosition.x - (Math.cos(cameraRotation.z) * walkspeed));
			}
			if(glfwGetKey(win, GLFW_KEY_S) == GL_TRUE) {
				//System.out.println("w");
				playerPosition.y = (float) (playerPosition.y - (Math.cos(cameraRotation.z) * walkspeed));
				playerPosition.x = (float) (playerPosition.x - (Math.sin(cameraRotation.z) * walkspeed));
			}
			if(glfwGetKey(win, GLFW_KEY_D) == GL_TRUE) {
				//System.out.println("w");
				playerPosition.y = (float) (playerPosition.y - (Math.sin(cameraRotation.z) * walkspeed));
				playerPosition.x = (float) (playerPosition.x + (Math.cos(cameraRotation.z) * walkspeed));
			}
			if(glfwGetKey(win, GLFW.GLFW_KEY_LEFT_SHIFT) == GL_TRUE) {
				//System.out.println("w");
				playerPosition.z = (float) (playerPosition.z - walkspeed);
			}
			if(glfwGetKey(win, GLFW.GLFW_KEY_SPACE) == GL_TRUE) {
				//System.out.println("w");
				playerPosition.z = (float) (playerPosition.z + walkspeed);
			}
			double[] xpos = new double[] {0};
			double[] ypos = new double[] {0};
			glfwGetCursorPos(win, xpos, ypos);
			//JSONStringer json = new JSONStringer();
			//System.out.println(JSONStringer.valueToString(xpos));
			if(glfwGetMouseButton(win,0) == GL_TRUE) {
				logger.logInfo("Mouse info: click");
			}
			
			//shader.start();
			
			SkyboxRenderer.updatePosition(new float[] {playerPosition.x,playerPosition.y,playerPosition.z});
			
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1);
				IntBuffer pHeight = stack.mallocInt(1);
	
				glfwGetFramebufferSize(win, pWidth, pHeight);
				width = pWidth.get(0);
				height = pHeight.get(0);
				//glViewport(0, 0, pWidth.get(0), pHeight.get(0));
			}
			
			cameraRotation.z = (float) ((xpos[0] * 1 * Math.PI) / Math.hypot(width,height));
			cameraRotation.x = (float) ((ypos[0] * 1 * Math.PI) / Math.hypot(width,height));
			
			//cameraRotation.x = (float) (cameraRotation.x + ((Math.PI * 0.5 * 16)/1000f));
			//cameraRotation.y = (float) (cameraRotation.y + ((Math.PI * 0.3 * 16)/1000f));
			//cameraRotation.z = (float) (cameraRotation.z + ((Math.PI * 0.05 * 16)/1000f));
		
			//Matrix4f perspective = Matrix4f.CreatePerspectiveFieldOfView(1.04, 4 / 4, 1, 10000) //Setup Perspective
			Matrix4f viewMatrix = new Matrix4f();
//			viewMatrix.
//			        rotateX((float)cameraRotation.x).
//			        rotateY((float)cameraRotation.y).
//			        rotateZ((float)cameraRotation.z).
//			        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
			
			
			viewMatrix.
	        rotateX((float)cameraRotation.x).
	        rotateY((float)cameraRotation.y).
	        rotateZ((float)cameraRotation.z).
	        translate(-playerPosition.x, -playerPosition.y, -playerPosition.z);
			
			Matrix4f perspectiveMatrix = new Matrix4f();
			perspectiveMatrix.perspective((float) Math.toRadians(45), (float)width/height, 0.1f, renderDistance); // last point is render distance

			
			glClear(GL_DEPTH_BUFFER_BIT);
			glClear(GL_COLOR_BUFFER_BIT);
			
			glUseProgram(shaderProgram);
			
			//glMatrixMode(GL_PROJECTION);
			//glLoadIdentity();
			FloatBuffer fb1 = BufferUtils.createFloatBuffer(16);
			perspectiveMatrix.get(fb1);
			//glLoadMatrixf(perspectiveMatrix.get(fb1));
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "projectionMatrix"),false,fb1);
			
			//glMatrixMode(GL_MODELVIEW);
			//glLoadIdentity();
			FloatBuffer fb2 = BufferUtils.createFloatBuffer(16);
			viewMatrix.get(fb2);
			//glLoadMatrixf();
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "modelViewMatrix"),false,fb2);
			
			glViewport(0,0,width,height);
			
			//float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			//float mat_shininess[] = { 50.0f };
			//float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
			//glShadeModel(GL_SMOOTH);

			//glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
			//glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);
			//glLightfv(GL_LIGHT0, GL_POSITION, light_position);
			
			
			//Chunk chunkvar = new Chunk(0, 0, 0, "nice");
			
			//chunkvar
			
			//SkyboxRenderer.drawBox();  // TEMP TEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMPTEMP
			
			int[] currentChunkLocation = new int[] {(int) Math.floor(playerPosition.x/32f),(int) Math.floor(playerPosition.y/32f),(int) Math.floor(playerPosition.z/32f)};
			
			int currentlyRendering = 0;
			int currentlyRendered = 0;
			
			
			//Code for dynamically loading chunks
			for (int blkx=-worldRadius+currentChunkLocation[0]; blkx<worldRadius+currentChunkLocation[0]; blkx++) {
				for (int blky=-worldRadius+currentChunkLocation[1]; blky<worldRadius+currentChunkLocation[1]; blky++) {
					for (int blkz=-worldHeight+currentChunkLocation[2]; blkz<worldHeight+currentChunkLocation[2]; blkz++) {
						boolean chunkNeedsLoading = true;
						for (int chkid=0; chkid<loadedChunks.length; chkid++) {
							if (loadedChunks[chkid].x == blkx) { if (loadedChunks[chkid].y == blky) { if (loadedChunks[chkid].z == blkz) {
								//System.out.println("Chunks Already Loaded : ".concat(Integer.toString(blkx)).concat(",").concat(Integer.toString(blky)).concat(",").concat(Integer.toString(blkz)));
								chunkNeedsLoading = false;
							}}}
						}
						if (chunkNeedsLoading) {
							Chunk newChunk2 = new Chunk(blkx, blky, blkz, seed, loadedChunks);
							loadedChunks = ArrayHelper.push(loadedChunks,newChunk2);
							
							//System.out.println("Chunks Loaded: ".concat(Integer.toString(loadedChunks.length)));
						}
						//Chunk newChunk = new Chunk(blkx, blky, blkz, seed);
						//loadedChunks = ArrayHelper.push(loadedChunks,newChunk);
						//newChunk.generateSurface();
					}
				}
			}
			
			for (int chkid=0; chkid<loadedChunks.length; chkid++) {
				if (loadedChunks[chkid].chunkModelStatus[1]) { // if the chunk is currently rendering
					currentlyRendering = currentlyRendering + 1;
				}
				if (loadedChunks[chkid].chunkModelStatus[2]) { // if the chunk is ready to be drawn
					currentlyRendered = currentlyRendered + 1;
				}
			}
			//Code for dynamically loading chunks
			
			//System.out.println("OH = ".concat(Integer.toString(currentlyRendering)).concat(" CR = ").concat(Integer.toString(currentlyRendered)));
			
			terrainTexture.bind();
			
			for (int chkid=0; chkid<loadedChunks.length; chkid++) {
				int[] chunkPosition = loadedChunks[chkid].getPosition();
				
				viewMatrix.translate((float) chunkPosition[0] * 32,(float) chunkPosition[1] * 32,(float) chunkPosition[2] * 32);
				viewMatrix.get(fb2);
				viewMatrix.translate((float) chunkPosition[0] * -32,(float) chunkPosition[1] * -32,(float) chunkPosition[2] * -32);
				glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "modelViewMatrix"),false,fb2);
				loadedChunks[chkid].updateAvaliableChunks(loadedChunks);
				
				if ((loadedChunks[chkid].x > -worldRenderRadius+currentChunkLocation[0]) && (loadedChunks[chkid].x < worldRenderRadius+currentChunkLocation[0])) {
				if ((loadedChunks[chkid].y > -worldRenderRadius+currentChunkLocation[1]) && (loadedChunks[chkid].y < worldRenderRadius+currentChunkLocation[1])) {
				if ((loadedChunks[chkid].z > -worldRenderHeight+currentChunkLocation[2]) && (loadedChunks[chkid].z < worldRenderHeight+currentChunkLocation[2])) {
					if (currentlyRendering < concurrentChunkRenderingLevel) {
						if (loadedChunks[chkid].generateSurfaces()) {
							currentlyRendering = currentlyRendering + 1;
						}
					}
				}}}
				
				if (loadedChunks[chkid].chunkModelStatus[2]) {
					loadedChunks[chkid].chunkModel.render();
					
				}
			}
			
			
			for (int chkid=0; chkid<loadedChunks.length; chkid++) {
				int[] chunkPosition = loadedChunks[chkid].getPosition();
				
				viewMatrix.translate((float) chunkPosition[0] * 32,(float) chunkPosition[1] * 32,(float) chunkPosition[2] * 32);
				viewMatrix.get(fb2);
				viewMatrix.translate((float) chunkPosition[0] * -32,(float) chunkPosition[1] * -32,(float) chunkPosition[2] * -32);
				glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "modelViewMatrix"),false,fb2);
				
				if (loadedChunks[chkid].chunkModelStatus[2]) {
					loadedChunks[chkid].chunkModelTransparent.render();
				}
				if ( (chunkPosition[0]>worldRamRecoverRadius+1+currentChunkLocation[0] || chunkPosition[0]<-worldRamRecoverRadius-1+currentChunkLocation[0]) || 
					 (chunkPosition[1]>worldRamRecoverRadius+1+currentChunkLocation[1] || chunkPosition[1]<-worldRamRecoverRadius-1+currentChunkLocation[1]) ||
					 (chunkPosition[2]>worldRamRecoverRadius+1+currentChunkLocation[2] || chunkPosition[2]<-worldRamRecoverRadius-1+currentChunkLocation[2]) ) {
					loadedChunks = ArrayHelper.remove(loadedChunks,chkid);
				}
			}
			
			//Draw2D Renderer2D = new Draw2D(width,height,(float) Math.PI/2f, new float[] {playerPosition.x,playerPosition.y,playerPosition.z}, new float[] {cameraRotation.x,cameraRotation.y,cameraRotation.z}, UITextures[0],new float[] {0f,0.4f,1f,0.6f},new float[] {0f,0f,1f,0.2f});
			///////Renderer2D.update(width,height,(float) Math.PI/2f, new float[] {playerPosition.x,playerPosition.y,playerPosition.z}, new float[] {cameraRotation.x,cameraRotation.y,cameraRotation.z}, UITextures[0],new float[] {0f,0.4f,1f,0.6f},new float[] {0.3f,0f,0.7f,0.08f});
			//////Renderer2D.drawBox();
			
			//Draw2D TextRenderer2D = new Draw2D(width,height,(float) Math.PI/2f, new float[] {playerPosition.x,playerPosition.y,playerPosition.z}, new float[] {cameraRotation.x,cameraRotation.y,cameraRotation.z}, UIFont,new float[] {0f,0.4f,1f,0.6f},new float[] {0f,0.9f,1f,0.93f});
			
			
			//////TextRenderer2D.update(width,height,(float) Math.PI/2f, new float[] {playerPosition.x,playerPosition.y,playerPosition.z}, new float[] {cameraRotation.x,cameraRotation.y,cameraRotation.z}, UIFont,new float[] {0f,0.4f,1f,0.6f},new float[] {0f,0.03f,1f,0.93f});
			
			long frameTime = System.nanoTime() - frameStartTime;
			lastFPSDisplay = lastFPSDisplay + frameTime;
			if (lastFPSDisplay > (1000000000f * 0.1f)) {
				lastFPSDisplay = 0f;
				//System.out.println(Float.toString(frameTime/1000000).concat("ms"));
				float secondsPerFrame = (((float) (frameTime+1))/1000000000f);
				currentFPS = 1/secondsPerFrame;
			}
			String fpscount = new DecimalFormat("#.00").format(currentFPS);

			//////TextRenderer2D.drawTextAuto(fpscount.concat(" fps"),8);
			
			glfwSwapBuffers(win);
			
			
			//System.out.println(Float.toString(1/secondsPerFrame).concat("fps"));
		}
		
		glfwTerminate();
	}
	
	

	public static void main(String[] args) {
		new Neon();
	}

}
