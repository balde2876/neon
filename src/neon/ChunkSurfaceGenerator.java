package neon;

public class ChunkSurfaceGenerator extends Thread {
	public int x;
	public int y;
	public int z;
	public short[] data = new short[32*32*32];
	
	public long[] lastUpdated = new long[] {0};
	
	public boolean[] chunkModelStatus = new boolean[] {false,false,false};
	
	//public BlockObject[] surfaceBlocks = new BlockObject[] {};
	public short[] chunkSurfaces = new short[32*32*32*6];
	public int surfaceBlocksFaces = 0;
	
	public BlockObject[] surfaceBlocksTransparent = new BlockObject[] {};
	public short[] chunkTransparentSurfaces = new short[32*32*32*6];
	public int surfaceBlocksFacesTransparent = 0;
	
	public ChunkSurfaceData chunkSurfaceData = new ChunkSurfaceData();
	
	public int[] surfaceLengths = new int[] {0,0,0,0,0,0};
	
	public Chunk[] chunksAvaliable;
	
	private float[] verticesBlock = {			
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
	private int[] indicesBlock = {
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
	private float[] textureCoordsStandard = {
		0f,0f,
		0f,1f,
		1f,1f,
		1f,0f,
	};
	
	public boolean updateSurfaceBlocks() {
		boolean neededUpdate = false;
		
			System.out.print("Updating surface blocks for chunk\n");
			//surfaceBlocks = new BlockObject[] {};
			//surfaceBlocksFaces = 0;
			//surfaceBlocksTransparent = new BlockObject[] {};
			//surfaceBlocksFacesTransparent = 0;
			chunkSurfaces = new short[32*32*32*6];
			chunkTransparentSurfaces = new short[32*32*32*6];
			surfaceBlocksFaces = 0;
			for (int blkx=0; blkx<32; blkx++) {
				for (int blky=0; blky<32; blky++) {
					for (int blkz=0; blkz<32; blkz++) {
						//surfaceBlocks
						//if ((getBlock(blkx,blky,blkz) != 0) && (getBlock(blkx,blky,blkz) != 1)) {
						int myBlockId = getBlock(blkx,blky,blkz);
						if ((myBlockId != 0)) {
							if (!isTransparent(myBlockId)) {
								boolean isSurfaceBlock = false;
//								if (isTransparent(getBlock(blkx-1,blky,blkz))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+3] = (short) myBlockId; }
//								if (isTransparent(getBlock(blkx+1,blky,blkz))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+2] = (short) myBlockId; }
//								if (isTransparent(getBlock(blkx,blky-1,blkz))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+5] = (short) myBlockId; }
//								if (isTransparent(getBlock(blkx,blky+1,blkz))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+4] = (short) myBlockId; }
//								if (isTransparent(getBlock(blkx,blky,blkz-1))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+0] = (short) myBlockId; }
//								if (isTransparent(getBlock(blkx,blky,blkz+1))) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+1] = (short) myBlockId; }
								
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+3] = (short) myBlockId; }
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+2] = (short) myBlockId; }
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+5] = (short) myBlockId; }
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+4] = (short) myBlockId; }
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+0] = (short) myBlockId; }
								if (true) {surfaceBlocksFaces++; chunkSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+1] = (short) myBlockId; }
							} else {
								boolean isSurfaceBlock = false;
								boolean[] sidesVisible = new boolean[] {false,false,false,false,false,false}; // bottom, top, right, left, front, back
								if (getBlock(blkx-1,blky,blkz) != myBlockId) { isSurfaceBlock = true; sidesVisible[3] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+3] = (short) myBlockId; }
								if (getBlock(blkx+1,blky,blkz) != myBlockId) { isSurfaceBlock = true; sidesVisible[2] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+2] = (short) myBlockId; }
								if (getBlock(blkx,blky-1,blkz) != myBlockId) { isSurfaceBlock = true; sidesVisible[5] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+5] = (short) myBlockId; }
								if (getBlock(blkx,blky+1,blkz) != myBlockId) { isSurfaceBlock = true; sidesVisible[4] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+4] = (short) myBlockId; }
								if (getBlock(blkx,blky,blkz-1) != myBlockId) { isSurfaceBlock = true; sidesVisible[0] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+0] = (short) myBlockId; }
								if (getBlock(blkx,blky,blkz+1) != myBlockId) { isSurfaceBlock = true; sidesVisible[1] = true; surfaceBlocksFacesTransparent++; chunkTransparentSurfaces[(((((blkx*32)+blky)*32)+blkz)*6)+1] = (short) myBlockId; }
								if (isSurfaceBlock) {
									BlockObject blksfc = new BlockObject(blkx,blky,blkz,getBlock(blkx,blky,blkz));
									blksfc.sidesVisible = sidesVisible;
									surfaceBlocksTransparent = ArrayHelper.push(surfaceBlocksTransparent, blksfc);
								}
							}
						}
					}
				}
			}
			int culledFaces = (32*32*32*6) +1 - surfaceBlocksFaces;
			System.out.print("Culled ".concat(Integer.toString(culledFaces)).concat(" faces\n"));
			if (culledFaces > 0) {
				neededUpdate = true;
			}
			System.out.print("Chunk contains ".concat(Integer.toString(surfaceBlocksFaces)).concat(" faces\n"));

		return neededUpdate;
	}
	
	public void generateSurface() {
		
		boolean needsUpdate = updateSurfaceBlocks();
		if (needsUpdate) {
			System.out.print("Generating surface for chunk\n");
			
			float[] outputVertices = new float[surfaceBlocksFaces*12];
			float[] outputTextureCoords = new float[surfaceBlocksFaces*8];
			int[] outputIndices = new int[surfaceBlocksFaces*6];
			
			//chunkSurfaceData.vertexSurface = new float[surfaceBlocksFaces * 12];
			//chunkSurfaceData.textureCoordsSurface = new float[surfaceBlocksFaces * 8];
			//chunkSurfaceData.indicesSurface = new int[surfaceBlocksFaces * 6];
			
			//surfaceLengths[0] = surfaceBlocksFaces * 12;
			//surfaceLengths[1] = surfaceBlocksFaces * 8;
			//surfaceLengths[2] = surfaceBlocksFaces * 6;
			
			System.out.println("Generating chunk with ".concat(Integer.toString(surfaceBlocksFaces)).concat(" faces"));
			
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
					
					if (blockaddr < 6) {
						System.out.println(blkx + "-" + blky + "-" + blkz);
					}
					
					//for (int j=0; j<8; j++) {
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

					//offsetX = offsetX2;
					//offsetY = offsetY2;
					for (int j=0; j<8; j++) {
						if ((j % 2) == 0) {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
						} else {
							outputTextureCoords[(i*8) + j - (offsetSkippedFaces * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
						}
					} // fine
					for (int j=0; j<12; j++) {
						if ((j % 3) == 0) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkx);
						}
						if ((j % 3) == 1) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blky);			
						}
						if ((j % 3) == 2) {
							outputVertices[(i*12) + j - (offsetSkippedFaces * 12)] = verticesBlock[j + (fn * 12)] + ((float) blkz);
						}
					}
					for (int j=0; j<6; j++) {
						outputIndices[(i*6) + j - (offsetSkippedFaces * 6)] = indicesBlock[j + (fn * 6)] + (i * 4) - (offsetSkippedFaces * 4);
					}
				}
			}
			
			chunkSurfaceData.indicesSurface = outputIndices;
			chunkSurfaceData.vertexSurface = outputVertices;
			chunkSurfaceData.textureCoordsSurface = outputTextureCoords;
			
			outputVertices = new float[surfaceBlocksFacesTransparent * 12];
			outputTextureCoords = new float[surfaceBlocksFacesTransparent * 8];
			outputIndices = new int[surfaceBlocksFacesTransparent * 6];
			
			//chunkModel = new Model(vertexSurface, textureCoordsSurface, indicesSurface);
			
			//chunkSurfaceData.vertexSurfaceTransparent = new float[surfaceBlocksFacesTransparent * 12];
			//chunkSurfaceData.textureCoordsSurfaceTransparent = new float[surfaceBlocksFacesTransparent * 8];
			//chunkSurfaceData.indicesSurfaceTransparent = new int[surfaceBlocksFacesTransparent * 6];
			
			//surfaceLengths[3] = surfaceBlocksFacesTransparent * 12;
			//surfaceLengths[4] = surfaceBlocksFacesTransparent * 8;
			//surfaceLengths[5] = surfaceBlocksFacesTransparent * 6;
			
			offsetSkippedFaces = 0;
			for (int i=0; i<surfaceBlocksTransparent.length; i++) {
				for (int fn=0; fn<6; fn++) {
					if (surfaceBlocksTransparent[i].sidesVisible[fn] == false) {
						offsetSkippedFaces = offsetSkippedFaces + 1;
					} else {		
						for (int j=0; j<8; j++) {
							float offsetX = 0f;
							float offsetY = 0f;
							float texturePackBlocksWidth = 16f;
							switch (surfaceBlocksTransparent[i].id) {
							case 1: offsetX = 15f; offsetY = 13f; break;
							case 8: offsetX = 4f; offsetY = 3f; break;
							}
							if ((j % 2) == 0) {
								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
							} else {
								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
							}
						}
						for (int j=0; j<6; j++) {
							outputIndices[(i*36) + j - (offsetSkippedFaces * 6) + (fn * 6)] = indicesBlock[j + (fn * 6)] + (i * 24) - (offsetSkippedFaces * 4);
						}
						for (int j=0; j<12; j++) {
							if ((j % 3) == 0) {
								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].x);
							}
							if ((j % 3) == 1) {
								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].y);			
							}
							if ((j % 3) == 2) {
								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].z);
							}
						}
					}
				}
			}
			
			chunkSurfaceData.indicesSurfaceTransparent = outputIndices;
			chunkSurfaceData.vertexSurfaceTransparent = outputVertices;
			chunkSurfaceData.textureCoordsSurfaceTransparent = outputTextureCoords;
			
			//chunkModelTransparent = new Model(vertexSurfaceTransparent, textureCoordsSurfaceTransparent, indicesSurfaceTransparent);
			
			chunkModelStatus[0] = true; // surfaces generated
			chunkModelStatus[1] = false; // not in process of generating
			lastUpdated[0] = System.currentTimeMillis();
		} else {
			System.out.print("No need to update, already generated latest\n");
			chunkModelStatus[0] = true; // surfaces generated
			chunkModelStatus[1] = false; // not in process of generating
			lastUpdated[0] = System.currentTimeMillis();
		}
	}
	
//	public void generateSurfaceOLD() {
//		
//		boolean needsUpdate = updateSurfaceBlocks();
//		if (needsUpdate) {
//			System.out.print("Generating surface for chunk\n");
//			
//			float[] outputVertices = new float[surfaceBlocksFaces * 12];
//			float[] outputTextureCoords = new float[surfaceBlocksFaces * 8];
//			int[] outputIndices = new int[surfaceBlocksFaces * 6];
//			
//			//chunkSurfaceData.vertexSurface = new float[surfaceBlocksFaces * 12];
//			//chunkSurfaceData.textureCoordsSurface = new float[surfaceBlocksFaces * 8];
//			//chunkSurfaceData.indicesSurface = new int[surfaceBlocksFaces * 6];
//			
//			//surfaceLengths[0] = surfaceBlocksFaces * 12;
//			//surfaceLengths[1] = surfaceBlocksFaces * 8;
//			//surfaceLengths[2] = surfaceBlocksFaces * 6;
//			
//			int offsetSkippedFaces = 0;
//			for (int i=0; i<surfaceBlocks.length; i++) {
//				//float offsetX2 = (float) Math.floor(Math.random() * (17f));
//				//float offsetY2 = (float) Math.floor(Math.random() * (17f));
//				//offsetX2 = 0f;
//				//offsetY2 = 0f;
//				for (int fn=0; fn<6; fn++) {
//					if (surfaceBlocks[i].sidesVisible[fn] == false) {
//						offsetSkippedFaces = offsetSkippedFaces + 1;
//					} else {		
//						for (int j=0; j<8; j++) {
//							float offsetX = 0f;
//							float offsetY = 0f;
//							float texturePackBlocksWidth = 16f;
//							switch (surfaceBlocks[i].id) {
//							
//							case 2: offsetX = 2f; offsetY = 0f; break; // dirt
//							case 3: //grass
//								switch(fn) {
//								case 0: offsetX = 2f; offsetY = 0f; break;
//								case 1: offsetX = 0f; offsetY = 0f; break;
//								case 2: offsetX = 3f; offsetY = 0f; break;
//								case 3: offsetX = 3f; offsetY = 0f; break;
//								case 4: offsetX = 3f; offsetY = 0f; break;
//								case 5: offsetX = 3f; offsetY = 0f; break;
//								}
//								break;
//							case 4: offsetX = 1f; offsetY = 0f; break; //stone
//							case 5: //snowy grass
//								switch(fn) {
//								case 0: offsetX = 2f; offsetY = 0f; break;
//								case 1: offsetX = 2f; offsetY = 4f; break;
//								case 2: offsetX = 4f; offsetY = 4f; break;
//								case 3: offsetX = 4f; offsetY = 4f; break;
//								case 4: offsetX = 4f; offsetY = 4f; break;
//								case 5: offsetX = 4f; offsetY = 4f; break;
//								}
//								break;
//							case 6: offsetX = 2f; offsetY = 1f; break; //sand
//							case 7: //oak log
//								switch(fn) {
//								case 0: offsetX = 5f; offsetY = 1f; break;
//								case 1: offsetX = 5f; offsetY = 1f; break;
//								case 2: offsetX = 4f; offsetY = 1f; break;
//								case 3: offsetX = 4f; offsetY = 1f; break;
//								case 4: offsetX = 4f; offsetY = 1f; break;
//								case 5: offsetX = 4f; offsetY = 1f; break;
//								}
//								break;
//							case 8: offsetX = 5f; offsetY = 3f; break;
//							
//							}
//
//							//offsetX = offsetX2;
//							//offsetY = offsetY2;
//							if ((j % 2) == 0) {
//								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
//							} else {
//								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
//							}
//						}
//						for (int j=0; j<6; j++) {
//							outputIndices[(i*36) + j - (offsetSkippedFaces * 6) + (fn * 6)] = indicesBlock[j + (fn * 6)] + (i * 24) - (offsetSkippedFaces * 4);
//						}
//						for (int j=0; j<12; j++) {
//							if ((j % 3) == 0) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].x);
//							}
//							if ((j % 3) == 1) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].y);			
//							}
//							if ((j % 3) == 2) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].z);
//							}
//						}
//					}
//				}
//				//outputIndices = (int[])ArrayHelper.concatenate(outputIndices, indicesBlock);
//				//outputVertices = (float[])ArrayHelper.concatenate(outputVertices, verticesBlock);
//			}
//			
//			chunkSurfaceData.indicesSurface = outputIndices;
//			chunkSurfaceData.vertexSurface = outputVertices;
//			chunkSurfaceData.textureCoordsSurface = outputTextureCoords;
//			
//			outputVertices = new float[surfaceBlocksFacesTransparent * 12];
//			outputTextureCoords = new float[surfaceBlocksFacesTransparent * 8];
//			outputIndices = new int[surfaceBlocksFacesTransparent * 6];
//			
//			//chunkModel = new Model(vertexSurface, textureCoordsSurface, indicesSurface);
//			
//			//chunkSurfaceData.vertexSurfaceTransparent = new float[surfaceBlocksFacesTransparent * 12];
//			//chunkSurfaceData.textureCoordsSurfaceTransparent = new float[surfaceBlocksFacesTransparent * 8];
//			//chunkSurfaceData.indicesSurfaceTransparent = new int[surfaceBlocksFacesTransparent * 6];
//			
//			//surfaceLengths[3] = surfaceBlocksFacesTransparent * 12;
//			//surfaceLengths[4] = surfaceBlocksFacesTransparent * 8;
//			//surfaceLengths[5] = surfaceBlocksFacesTransparent * 6;
//			
//			offsetSkippedFaces = 0;
//			for (int i=0; i<surfaceBlocksTransparent.length; i++) {
//				for (int fn=0; fn<6; fn++) {
//					if (surfaceBlocksTransparent[i].sidesVisible[fn] == false) {
//						offsetSkippedFaces = offsetSkippedFaces + 1;
//					} else {		
//						for (int j=0; j<8; j++) {
//							float offsetX = 0f;
//							float offsetY = 0f;
//							float texturePackBlocksWidth = 16f;
//							switch (surfaceBlocksTransparent[i].id) {
//							case 1: offsetX = 15f; offsetY = 13f; break;
//							case 8: offsetX = 4f; offsetY = 3f; break;
//							}
//							if ((j % 2) == 0) {
//								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetX) / texturePackBlocksWidth;
//							} else {
//								outputTextureCoords[(i*48) + j - (offsetSkippedFaces * 8) + (fn * 8)] = (textureCoordsStandard[j] + offsetY) / texturePackBlocksWidth;
//							}
//						}
//						for (int j=0; j<6; j++) {
//							outputIndices[(i*36) + j - (offsetSkippedFaces * 6) + (fn * 6)] = indicesBlock[j + (fn * 6)] + (i * 24) - (offsetSkippedFaces * 4);
//						}
//						for (int j=0; j<12; j++) {
//							if ((j % 3) == 0) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].x);
//							}
//							if ((j % 3) == 1) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].y);			
//							}
//							if ((j % 3) == 2) {
//								outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocksTransparent[i].z);
//							}
//						}
//					}
//				}
//			}
//			
//			chunkSurfaceData.indicesSurfaceTransparent = outputIndices;
//			chunkSurfaceData.vertexSurfaceTransparent = outputVertices;
//			chunkSurfaceData.textureCoordsSurfaceTransparent = outputTextureCoords;
//			
//			//chunkModelTransparent = new Model(vertexSurfaceTransparent, textureCoordsSurfaceTransparent, indicesSurfaceTransparent);
//			
//			chunkModelStatus[0] = true; // surfaces generated
//			chunkModelStatus[1] = false; // not in process of generating
//			lastUpdated[0] = System.currentTimeMillis();
//		} else {
//			System.out.print("No need to update, already generated latest\n");
//			chunkModelStatus[0] = true; // surfaces generated
//			chunkModelStatus[1] = false; // not in process of generating
//			lastUpdated[0] = System.currentTimeMillis();
//		}
//	}
	
	public int getBlock(int px, int py, int pz) {
		if ((px >= 0) && (px <= 31) && (py >= 0) && (py <= 31) && (pz >= 0) && (pz <= 31)) {
			return data[(px*32*32) + (py*32) + pz];	
		} else {
			//return 0;
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
			//if (blkid > 0) {
				//System.out.println("EDGEBLOCK");
				//System.out.println(Math.floor(32f/32f));
				//System.out.println(JSONStringer.valueToString(targetChunkRel));
			//}
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
	
	public void run() 
    { 
        try
        { 
            // Displaying the thread that is running 
            System.out.println ("Surface generator " + 
                  Thread.currentThread().getId() + 
                  " is running"); 

            generateSurface();
            
    		System.out.println("Surface generator " + 
                    Thread.currentThread().getId() + 
                    " has completed");
    		//lastBlockChange[0] = System.currentTimeMillis();
            
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println("Exception is caught"); 
            //System.err.println(e.getStackTrace());
            e.printStackTrace(System.err);
        } 
    } 
}
