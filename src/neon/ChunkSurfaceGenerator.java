package neon;

public class ChunkSurfaceGenerator extends Thread {
	public int x;
	public int y;
	public int z;
	public short[] data = new short[32*32*32];
	
	public long[] lastUpdated = new long[] {0};
	
	public boolean[] chunkModelStatus = new boolean[] {false,false,false};
	
	private short[] chunkSurfaces = new short[32*32*32*6];
	private byte[] lightingModel = new byte[32*32*32*6];
	private int surfaceBlocksFaces = 0;
	private short[] chunkTransparentSurfaces = new short[32*32*32*6];
	private int surfaceBlocksFacesTransparent = 0;
	private byte[] lightingModelTransparent = new byte[32*32*32*6];
	
	public ChunkSurfaceData chunkSurfaceData = new ChunkSurfaceData();
	
	public Chunk[] chunksAvaliable;
	
	static private float[] verticesBlock = {			
			-0.5f,0.5f,-0.5f,//bottom
			-0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,0.5f,-0.5f,		
			
			-0.5f,0.5f,0.5f,//top	
			-0.5f,-0.5f,0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			0.5f,0.5f,0.5f,
			0.5f,0.5f,-0.5f,//right
			0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,0.5f,	
			
			-0.5f,0.5f,0.5f,
			-0.5f,0.5f,-0.5f,//left	
			-0.5f,-0.5f,-0.5f,	
			-0.5f,-0.5f,0.5f,	
			
			
			-0.5f,0.5f,0.5f,//front
			-0.5f,0.5f,-0.5f,
			0.5f,0.5f,-0.5f,
			0.5f,0.5f,0.5f,
			
			-0.5f,-0.5f,0.5f,//back
			-0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,0.5f
			
	};
	static private int[] indicesBlock = {
			0,1,3,	
			3,1,2,
			4,7,5,
			7,6,5,
			8,9,11,
			11,9,10,
			12,15,13,
			15,14,13,
			16,17,19,
			19,17,18,
			20,23,21,
			23,22,21,
	};
	static private float[] textureCoordsStandard = {
		0f,0f,
		0f,1f,
		1f,1f,
		1f,0f,
	};
	
	public boolean updateSurfaceBlocks() {
		boolean neededUpdate = false;
			//Neon.logger.logInfo("Updating surface blocks for chunk");
			chunkSurfaces = new short[32*32*32*6];
			lightingModel = new byte[32*32*32*6];
			lightingModelTransparent = new byte[32*32*32*6];
			chunkTransparentSurfaces = new short[32*32*32*6];
			surfaceBlocksFaces = 0;
			for (int blkx=0; blkx<32; blkx++) {
				for (int blky=0; blky<32; blky++) {
					for (int blkz=0; blkz<32; blkz++) {
						int myBlockId = getBlock(blkx,blky,blkz);
						if ((myBlockId != 0)) {
							if (!isTransparent(myBlockId)) {
								if (isTransparent(getBlock(blkx-1,blky,blkz))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+3] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+3] = 90; 
								}
								if (isTransparent(getBlock(blkx+1,blky,blkz))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+2] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+2] = 110; 
								}
								if (isTransparent(getBlock(blkx,blky-1,blkz))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+5] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+5] = 80; 
								}
								if (isTransparent(getBlock(blkx,blky+1,blkz))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+4] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+4] = 100; 
								}
								if (isTransparent(getBlock(blkx,blky,blkz-1))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+0] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+0] = 40; 
								}
								if (isTransparent(getBlock(blkx,blky,blkz+1))) {
									surfaceBlocksFaces++; 
									chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+1] = (short) myBlockId; 
									lightingModel[(((((blkx*32)+blky)*32)+blkz)*6)+1] = 127; 
								}
							} else {
								if (getBlock(blkx-1,blky,blkz) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+3] = (short) myBlockId; 
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+3] = 90; 
								}
								if (getBlock(blkx+1,blky,blkz) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+2] = (short) myBlockId;
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+2] = 110; 
								}
								if (getBlock(blkx,blky-1,blkz) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+5] = (short) myBlockId; 
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+5] = 80; 
								}
								if (getBlock(blkx,blky+1,blkz) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+4] = (short) myBlockId; 
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+4] = 100; 
								}
								if (getBlock(blkx,blky,blkz-1) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+0] = (short) myBlockId; 
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+0] = 40; 
								}
								if (getBlock(blkx,blky,blkz+1) != myBlockId) { 
									surfaceBlocksFacesTransparent++; 
									chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+1] = (short) myBlockId; 
									lightingModelTransparent[(((((blkx*32)+blky)*32)+blkz)*6)+1] = 127; 
								}
							}
						}
					}
				}
			}
			int culledFaces = (32*32*32*6) +1 - surfaceBlocksFaces;
			//Neon.logger.logInfo("Culled ".concat(Integer.toString(culledFaces)).concat(" faces"));
			if (culledFaces > 0) {
				neededUpdate = true;
			}
			//Neon.logger.logInfo("Chunk contains ".concat(Integer.toString(surfaceBlocksFaces)).concat(" faces"));

		return neededUpdate;
	}
	
	public void generateSurface() {
		
		boolean needsUpdate = updateSurfaceBlocks();
		if (needsUpdate) {
			//Neon.logger.logInfo("Generating surface for chunk");
			
			float[] outputVertices = new float[surfaceBlocksFaces*12];
			float[] outputLighting = new float[surfaceBlocksFaces*12];
			float[] outputTextureCoords = new float[surfaceBlocksFaces*8];
			int[] outputIndices = new int[surfaceBlocksFaces*6];
			
			//Neon.logger.logInfo("Generating chunk with ".concat(Integer.toString(surfaceBlocksFaces)).concat(" faces"));
			
			int offsetSkippedFaces = 0;
			for (int i=0; i<chunkSurfaces.length; i++) {
				if (chunkSurfaces[i] == 0) {
					offsetSkippedFaces = offsetSkippedFaces + 1;
				} else {	
					int fn = i % 6;
					int blockaddr = (int) Math.floor(((float) i)/6f);
					int blkx = (int) Math.floor((float) blockaddr / (32f * 32f));
					int blky = (int) Math.floor((float) blockaddr / 32f) - (blkx * 32);
					int blkz = (int) Math.floor((float) blockaddr) - (blkx * 32 * 32) - (blky * 32);
					
					float offsetX = 0f;
					float offsetY = 0f;
					float texturePackBlocksWidth = 16f;
					
					switch (chunkSurfaces[i]) {
					
						case 2: offsetX = 2f; offsetY = 0f; break; // dirt
						case 3: //grass
							switch(fn) {
							case 0: offsetX = 2f; offsetY = 0f; break;
							case 1: offsetX = 0f; offsetY = 0f; break;
							case 2: offsetX = 3f; offsetY = 0f; break;
							case 3: offsetX = 3f; offsetY = 0f; break;
							case 4: offsetX = 3f; offsetY = 0f; break;
							case 5: offsetX = 3f; offsetY = 0f; break;
							}
							break;
						case 4: offsetX = 1f; offsetY = 0f; break; //stone
						case 5: //snowy grass
							switch(fn) {
							case 0: offsetX = 2f; offsetY = 0f; break;
							case 1: offsetX = 2f; offsetY = 4f; break;
							case 2: offsetX = 4f; offsetY = 4f; break;
							case 3: offsetX = 4f; offsetY = 4f; break;
							case 4: offsetX = 4f; offsetY = 4f; break;
							case 5: offsetX = 4f; offsetY = 4f; break;
							}
							break;
						case 6: offsetX = 2f; offsetY = 1f; break; //sand
						case 7: //oak log
							switch(fn) {
							case 0: offsetX = 5f; offsetY = 1f; break;
							case 1: offsetX = 5f; offsetY = 1f; break;
							case 2: offsetX = 4f; offsetY = 1f; break;
							case 3: offsetX = 4f; offsetY = 1f; break;
							case 4: offsetX = 4f; offsetY = 1f; break;
							case 5: offsetX = 4f; offsetY = 1f; break;
							}
							break;
						case 8: offsetX = 5f; offsetY = 3f; break;
					
					}
					for (int j=0; j<8; j++) {
						if ((j % 2) == 0) {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
						} else {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
						}
					}
					for (int j=0; j<12; j++) {
						if ((j % 3) == 0) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkx);
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = ((float) lightingModel[i]) / 128f;
						}
						if ((j % 3) == 1) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blky);			
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = ((float) lightingModel[i]) / 128f;
						}
						if ((j % 3) == 2) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkz);
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = ((float) lightingModel[i]) / 128f;
						}
					}
					for (int j=0; j<6; j++) {
						outputIndices[(i*6) + j - (offsetSkippedFaces * 6)] = (indicesBlock[j + (fn * 6)] % 4) + (i * 4) - (offsetSkippedFaces * 4); // 4 because there are (4) vector3s
					}
				}
			}
			
			chunkSurfaceData.indicesSurface = outputIndices;
			chunkSurfaceData.vertexSurface = outputVertices;
			chunkSurfaceData.vertexSurfaceColors = outputLighting;
			chunkSurfaceData.textureCoordsSurface = outputTextureCoords;
			
			outputVertices = new float[surfaceBlocksFacesTransparent * 12];
			outputLighting = new float[surfaceBlocksFacesTransparent * 12];
			outputTextureCoords = new float[surfaceBlocksFacesTransparent * 8];
			outputIndices = new int[surfaceBlocksFacesTransparent * 6];
			
			offsetSkippedFaces = 0;
			
			for (int i=0; i<chunkTransparentSurfaces.length; i++) {
				if (chunkTransparentSurfaces[i] == 0) {
					offsetSkippedFaces = offsetSkippedFaces + 1;
				} else {	
					int fn = i % 6;
					int blockaddr = (int) Math.floor(((float) i)/6f);
					int blkx = (int) Math.floor((float) blockaddr / (32f * 32f));
					int blky = (int) Math.floor((float) blockaddr / 32f) - (blkx * 32);
					int blkz = (int) Math.floor((float) blockaddr) - (blkx * 32 * 32) - (blky * 32);
					
					float offsetX = 0f;
					float offsetY = 0f;
					float texturePackBlocksWidth = 16f;
					
					switch (chunkTransparentSurfaces[i]) {
					
						case 1: offsetX = 15f; offsetY = 13f; break;
						case 8: offsetX = 5f; offsetY = 3f; break;
					
					}
					for (int j=0; j<8; j++) {
						if ((j % 2) == 0) {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
						} else {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
						}
					}
					for (int j=0; j<12; j++) {
						if ((j % 3) == 0) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkx);
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = (float) lightingModelTransparent[i] / 128f;
						}
						if ((j % 3) == 1) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blky);	
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = (float) lightingModelTransparent[i] / 128f;
						}
						if ((j % 3) == 2) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkz);
							outputLighting[(i*12) + j - (offsetSkippedFaces * 12)] = (float) lightingModelTransparent[i] / 128f;
						}
					}
					for (int j=0; j<6; j++) {
						outputIndices[(i*6) + j - (offsetSkippedFaces * 6)] = (indicesBlock[j + (fn * 6)] % 4) + (i * 4) - (offsetSkippedFaces * 4); // 4 because there are (4) vector3s
					}
				}
			}

			chunkSurfaceData.indicesSurfaceTransparent = outputIndices;
			chunkSurfaceData.vertexSurfaceColorsTransparent = outputLighting;
			chunkSurfaceData.vertexSurfaceTransparent = outputVertices;
			chunkSurfaceData.textureCoordsSurfaceTransparent = outputTextureCoords;
			chunkModelStatus[0] = true; // surfaces generated
			chunkModelStatus[1] = false; // not in process of generating
			lastUpdated[0] = System.currentTimeMillis();
		} else {
			Neon.logger.logInfo("No need to update, already generated latest");
			chunkModelStatus[0] = true; // surfaces generated
			chunkModelStatus[1] = false; // not in process of generating
			lastUpdated[0] = System.currentTimeMillis();
		}
	}

	
	private int getBlock(int px, int py, int pz) {
		if ((px >= 0) && (px <= 31) && (py >= 0) && (py <= 31) && (pz >= 0) && (pz <= 31)) {
			return data[(px*32*32) + (py*32) + pz];	
		} else {
			int[] targetChunkRel = new int[] {(int) Math.floor(((float) px)/32f),(int) Math.floor(((float) py)/32f),(int) Math.floor(((float) pz)/32f)};
			int blkid = 0;
			for (int chkid=0; chkid<chunksAvaliable.length; chkid++) {
				if (chunksAvaliable[chkid].x == (x + targetChunkRel[0])) {
					if (chunksAvaliable[chkid].y == (y + targetChunkRel[1])) {
						if (chunksAvaliable[chkid].z == (z + targetChunkRel[2])) {
							blkid = chunksAvaliable[chkid].getBlock(px - (targetChunkRel[0] * 32), py - (targetChunkRel[1] * 32), pz - (targetChunkRel[2] * 32));
						}
					}
				}
			}
			return blkid;
		}
	}
	
	
	private boolean isTransparent(int blockId) {
		boolean tmp1 = false;
		if (blockId == 0) {
			tmp1 = true;
		}
		if (blockId == 1) {
			tmp1 = true;
		}
		if (blockId == 8) {
			//tmp1 = true;
		}
		return tmp1;
	}
	
	public void run() { 
        try { 
            // Displaying the thread that is running 
        	//Neon.logger.logInfo("Surface generator " + Thread.currentThread().getId() + " is running"); 
            generateSurface();
            
            //Neon.logger.logInfo("Surface generator " + Thread.currentThread().getId() + " has completed");
    		//lastBlockChange[0] = System.currentTimeMillis();
            
        } catch (Exception e) { 
            // Throwing an exception 
        	Neon.logger.logError("Exception is caught"); 
            //System.err.println(e.getStackTrace());
            e.printStackTrace(System.err);
        } 
    } 
}
