package neon;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Draw2D {
	int screenWidth = 1;
	int screenHeight = 1;
	private Texture texture;
	private float[] ply = new float[3];
	private float[] plyrot = new float[3];
	private float[] texcoords = new float[] {0f,0f,1f,1f};
	private float[] drawcoords = new float[] {0f,0f,1f,1f};

	
	public Draw2D () {

	}
	
	public void update(int screenWidth, int screenHeight, float fov, float[] playerPosition, float[] cameraRotation, Texture textureObject, float[] textureCoords, float[] objectCoords) {
		ply = playerPosition;
		plyrot = cameraRotation;
		texture = textureObject;
		texcoords = textureCoords;
		drawcoords = objectCoords;
	}
	
	public void drawBox() {
		float[] verticesBox = {			
				(drawcoords[0]*2f)-1f,(drawcoords[3]*(-2f))+1f,1f,//top	
				(drawcoords[0]*2f)-1f,(drawcoords[1]*(-2f))+1f,1f,	
				(drawcoords[2]*2f)-1f,(drawcoords[1]*(-2f))+1f,1f,	
				(drawcoords[2]*2f)-1f,(drawcoords[3]*(-2f))+1f,1f,
		};
		int[] indicesBox = {
				0,1,3,	
				3,1,2,
		};
		float[] textureCoordsBox = {
			texcoords[0],texcoords[3],
			texcoords[0],texcoords[1],
			texcoords[2],texcoords[1],
			texcoords[2],texcoords[3],
		};
		glPushMatrix();
		Model boxModel = new Model(verticesBox, textureCoordsBox, indicesBox);
		
		float distance2D = 3.41f;
		
		float cbx = (float) (ply[0]+((Math.sin(plyrot[2]) * (-Math.sin(plyrot[0]))) * distance2D ));
		float cby = (float) (ply[1]+((Math.cos(plyrot[2]) * (-Math.sin(plyrot[0]))) * distance2D ));
		float cbz = (float) (ply[2]+(((-Math.cos(plyrot[0]))) * distance2D ));
		
		glTranslatef(cbx, cby, cbz);
        
        glRotatef((float) -(plyrot[2]/Math.PI*180f), 0f, 0f, 1f);
        
        glRotatef((float) -(plyrot[0]/Math.PI*180f), 1f, 0f, 0f);
		
		//Matrix4f perspectiveMatrix = new Matrix4f();
		//perspectiveMatrix.perspective((float) Math.toRadians(45), (float)width/height, 0.1f, 10000.0f); // last point is render distance

		//glMatrixMode(GL_PROJECTION);
		//glLoadIdentity();
		//FloatBuffer fb1 = BufferUtils.createFloatBuffer(16);
		//glLoadMatrixf(perspectiveMatrix.get(fb1));
		
		//FloatBuffer fb2 = BufferUtils.createFloatBuffer(16);
		//glLoadMatrixf(viewMatrix.get(fb2));
        texture.bind();
		boxModel.render();
		glPopMatrix();
		
	}
	
	public void drawTextAuto(String text, int fontSize) {
		drawcoords[2] = drawcoords[0] + ((((float) fontSize)/256f) * ((float) text.length()));
		drawcoords[3] = drawcoords[1] + (((float) fontSize)/256f);
		drawText(text);
	}
	
	public void drawText(String text) {
		float scharw = ((drawcoords[2] - drawcoords[0])/(float)text.length());
		for (int i = 0; i < text.length(); i++){
		    char c = text.charAt(i);
		    float fontWidth = 8f;
		    float offsetX = 4f;
		    float offsetY = 4f;
		    switch (c) {
			    case 48: offsetX = 0f; offsetY = 0f; break;
		    	case 49: offsetX = 1f; offsetY = 0f; break;
		    	case 50: offsetX = 2f; offsetY = 0f; break;
		    	case 51: offsetX = 3f; offsetY = 0f; break;
		    	case 52: offsetX = 4f; offsetY = 0f; break;
		    	case 53: offsetX = 5f; offsetY = 0f; break;
		    	case 54: offsetX = 6f; offsetY = 0f; break;
		    	case 55: offsetX = 7f; offsetY = 0f; break;
		    	case 56: offsetX = 0f; offsetY = 1f; break;
		    	case 57: offsetX = 1f; offsetY = 1f; break;
	    	
		    	case 65: case 97: offsetX = 2f; offsetY = 1f; break;
		    	case 66: case 98: offsetX = 3f; offsetY = 1f; break;
		    	case 67: case 99: offsetX = 4f; offsetY = 1f; break;
		    	case 68: case 100: offsetX = 5f; offsetY = 1f; break;
		    	case 69: case 101: offsetX = 6f; offsetY = 1f; break;
		    	case 70: case 102: offsetX = 7f; offsetY = 1f; break;
		    	case 71: case 103: offsetX = 0f; offsetY = 2f; break;
		    	case 72: case 104: offsetX = 1f; offsetY = 2f; break;
		    	case 73: case 105: offsetX = 2f; offsetY = 2f; break;
		    	case 74: case 106: offsetX = 3f; offsetY = 2f; break;
		    	case 75: case 107: offsetX = 4f; offsetY = 2f; break;
		    	case 76: case 108: offsetX = 5f; offsetY = 2f; break;
		    	case 77: case 109: offsetX = 6f; offsetY = 2f; break;
		    	case 78: case 110: offsetX = 7f; offsetY = 2f; break;
		    	case 79: case 111: offsetX = 0f; offsetY = 3f; break;
		    	case 80: case 112: offsetX = 1f; offsetY = 3f; break;
		    	case 81: case 113: offsetX = 2f; offsetY = 3f; break;
		    	case 82: case 114: offsetX = 3f; offsetY = 3f; break;
		    	case 83: case 115: offsetX = 4f; offsetY = 3f; break;
		    	case 84: case 116: offsetX = 5f; offsetY = 3f; break;
		    	case 85: case 117: offsetX = 6f; offsetY = 3f; break;
		    	case 86: case 118: offsetX = 7f; offsetY = 3f; break;
		    	case 87: case 119: offsetX = 0f; offsetY = 4f; break;
		    	case 88: case 120: offsetX = 1f; offsetY = 4f; break;
		    	case 89: case 121: offsetX = 2f; offsetY = 4f; break;
		    	case 90: case 122: offsetX = 3f; offsetY = 4f; break;
		    	
		    	case 46: offsetX = 5f; offsetY = 4f; break;
		    	case 47: offsetX = 6f; offsetY = 4f; break;
		    	case 92: offsetX = 7f; offsetY = 4f; break;
		    	case 33: offsetX = 0f; offsetY = 5f; break;
		    	case 63: offsetX = 1f; offsetY = 5f; break;
		    	case 39: offsetX = 2f; offsetY = 5f; break;
		    	case 45: offsetX = 3f; offsetY = 5f; break;
		    	case 43: offsetX = 4f; offsetY = 5f; break;
		    	case 61: offsetX = 5f; offsetY = 5f; break;
		    	case 95: offsetX = 6f; offsetY = 5f; break;
		    	case 40: offsetX = 7f; offsetY = 5f; break;
		    	case 41: offsetX = 0f; offsetY = 6f; break;
		    }
		    
			float[] texcoordschar = new float[] {(offsetX/fontWidth),(offsetY/fontWidth),((1f + offsetX)/fontWidth),((1f + offsetY)/fontWidth)};
			float[] drawcoordschar = new float[] {drawcoords[0] + (scharw * i),drawcoords[1],drawcoords[0] + (scharw * (i + 1)),drawcoords[3]};
		    
			float[] verticesBox = {			
					(drawcoordschar[0]*2f)-1f,(drawcoordschar[3]*(-2f))+1f,1f,//top	
					(drawcoordschar[0]*2f)-1f,(drawcoordschar[1]*(-2f))+1f,1f,	
					(drawcoordschar[2]*2f)-1f,(drawcoordschar[1]*(-2f))+1f,1f,	
					(drawcoordschar[2]*2f)-1f,(drawcoordschar[3]*(-2f))+1f,1f,
			};
			int[] indicesBox = {
					0,1,3,	
					3,1,2,
			};
			float[] textureCoordsBox = {
				texcoordschar[0],texcoordschar[3],
				texcoordschar[0],texcoordschar[1],
				texcoordschar[2],texcoordschar[1],
				texcoordschar[2],texcoordschar[3],
			};
			glPushMatrix();
			Model boxModel = new Model(verticesBox, textureCoordsBox, indicesBox);
			
			float distance2D = 3.41f;
			
			float cbx = (float) (ply[0]+((Math.sin(plyrot[2]) * (-Math.sin(plyrot[0]))) * distance2D ));
			float cby = (float) (ply[1]+((Math.cos(plyrot[2]) * (-Math.sin(plyrot[0]))) * distance2D ));
			float cbz = (float) (ply[2]+(((-Math.cos(plyrot[0]))) * distance2D ));
			
			glTranslatef(cbx, cby, cbz);
	        
	        glRotatef((float) -(plyrot[2]/Math.PI*180f), 0f, 0f, 1f);
	        
	        glRotatef((float) -(plyrot[0]/Math.PI*180f), 1f, 0f, 0f);
			
			//Matrix4f perspectiveMatrix = new Matrix4f();
			//perspectiveMatrix.perspective((float) Math.toRadians(45), (float)width/height, 0.1f, 10000.0f); // last point is render distance

			//glMatrixMode(GL_PROJECTION);
			//glLoadIdentity();
			//FloatBuffer fb1 = BufferUtils.createFloatBuffer(16);
			//glLoadMatrixf(perspectiveMatrix.get(fb1));
			
			//FloatBuffer fb2 = BufferUtils.createFloatBuffer(16);
			//glLoadMatrixf(viewMatrix.get(fb2));
	        texture.bind();
			boxModel.render();
			glPopMatrix();
		    
		}
	}
}
