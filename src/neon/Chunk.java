package neon;

import org.json.JSONStringer;

public class Chunk {
	private int x;
	private int y;
	private int z;
	private int[] data = new int[32*32*32];
	private long lastUpdated = 0;
	private long lastBlockChange = System.currentTimeMillis();
	private BlockObject[] surfaceBlocks = new BlockObject[] {};
	private int surfaceBlocksFaces = 0;
	public float[] vertexSurface = new float[] {};
	public float[] textureCoordsSurface = new float[] {};
	public int[] indicesSurface = new int[] {};
	public Model chunkModel;
	//float[] vertexSurface = new float[] {};
	
	public Chunk(int genX, int genY, int genZ, int seed) {
		
		PerlinNoise noiseGenerator = new PerlinNoise(seed);
		PerlinNoise noiseGeneratorArid = new PerlinNoise(seed+32);
		PerlinNoise noiseGeneratorMountain = new PerlinNoise(seed+64);
		
		x = genX;
		y = genY;
		z = genZ;
		//its generation time
		double[] heightmap = new double[32*32];
		double[] aridmap = new double[32*32];
		double[] mountainmap = new double[32*32];
		
		float max = 0;
		float min = 0;
		
		float scaleFactor = 1f;
		float scaleFactorArid = 3f;
		float scaleFactorMountain = 3f;
		
		for (int i=0; i<32; i++) {
			for (int j=0; j<32; j++) {
				heightmap[(i*32)+j] = noiseGenerator.noise2d((x+(i/32f))/scaleFactor, (-y-(j/32f))/scaleFactor);
				aridmap[(i*32)+j] = noiseGeneratorArid.noise2d((x+(i/32f))/scaleFactorArid, (-y-(j/32f))/scaleFactorArid);
				mountainmap[(i*32)+j] = noiseGeneratorMountain.noise2d((x+(i/32f))/scaleFactorMountain, (-y-(j/32f))/scaleFactorMountain);
				//System.out.println(genX+(i/32));
				
				
				double mountainous = 1 - Math.cos(mountainmap[(i*32)+j] * Math.PI * 0.4);
				double aridity = (aridmap[(i*32)+j] * 4) - (mountainous * 6);
				double heightAtPosition = ((heightmap[(i*32)+j]*32) * (mountainous * 16));
				
				// 0 - air
				// 1 - water
				// 2 - dirt
				// 3 - grass
				// 4 - stone
				// 5 - snow grass
				// 6 - sand
			
				for (int k=0; k<32; k++) {
					if ((k+(z*32) - 16) > Math.floor(heightAtPosition)) {
						if (Math.floor(heightAtPosition) > 0) {
							data[(((i*32)+j)*32)+k] = 0; // air
						} else {
							data[(((i*32)+j)*32)+k] = 1; // water
						}
					} else {
						if ((k+(z*32) - 16) == Math.floor(heightAtPosition)) {
							if (aridity > 0.7) {
								data[(((i*32)+j)*32)+k] = 6; // sand
							} else {
								if (heightAtPosition > (12+aridity)) {
									data[(((i*32)+j)*32)+k] = 5; // snowy grass
								} else {
									data[(((i*32)+j)*32)+k] = 3; // grass
								}
							}
						} else {
							if ((k+(z*32) - 12) > Math.floor(heightAtPosition)) {
								if (aridity > 0.7) {
									data[(((i*32)+j)*32)+k] = 6; // sand
								} else {
									data[(((i*32)+j)*32)+k] = 2; // dirt
								}
							} else {
								data[(((i*32)+j)*32)+k] = 4; // stone
							}
						}
					}
				}
			}
		}
		
		lastBlockChange = System.currentTimeMillis();
		//String jsonOut = JSONStringer.valueToString(heightmap);
		
		
		
	}
	
	public Chunk(int genX, int genY, int genZ) {
		//its loading time
	}
	
	public int getBlock(int px, int py, int pz) {
		return data[(px*32*32) + (py*32) + pz];	
	}
	
	public BlockObject[] returnSurfaceBlocks() {
		
		//lastBlockChange = System.currentTimeMillis();
		updateSurfaceBlocks();
		return surfaceBlocks;
	}
	
	public void updateSurfaceBlocks() {
		if (lastUpdated < lastBlockChange) {
			System.out.print("Updating surface blocks for chunk\n");
			surfaceBlocks = new BlockObject[] {};
			surfaceBlocksFaces = 0;
			for (int blkx=0; blkx<32; blkx++) {
				for (int blky=0; blky<32; blky++) {
					for (int blkz=0; blkz<32; blkz++) {
						//surfaceBlocks
						if ((getBlock(blkx,blky,blkz) != 0) && (getBlock(blkx,blky,blkz) != 1)) {
							if ((blkx > 0) && (blkx < 31) && (blky > 0) && (blky < 31) && (blkz > 0) && (blkz < 31)) {
								boolean isSurfaceBlock = false;
								boolean[] sidesVisible = new boolean[] {false,false,false,false,false,false}; // bottom, top, right, left, front, back
								if (getBlock(blkx-1,blky,blkz) == 1 || getBlock(blkx-1,blky,blkz) == 0) { isSurfaceBlock = true; sidesVisible[3] = true; surfaceBlocksFaces++; }
								if (getBlock(blkx+1,blky,blkz) == 1 || getBlock(blkx+1,blky,blkz) == 0) { isSurfaceBlock = true; sidesVisible[2] = true; surfaceBlocksFaces++; }
								if (getBlock(blkx,blky-1,blkz) == 1 || getBlock(blkx,blky-1,blkz) == 0) { isSurfaceBlock = true; sidesVisible[5] = true; surfaceBlocksFaces++; }
								if (getBlock(blkx,blky+1,blkz) == 1 || getBlock(blkx,blky+1,blkz) == 0) { isSurfaceBlock = true; sidesVisible[4] = true; surfaceBlocksFaces++; }
								if (getBlock(blkx,blky,blkz-1) == 1 || getBlock(blkx,blky,blkz-1) == 0) { isSurfaceBlock = true; sidesVisible[0] = true; surfaceBlocksFaces++; }
								if (getBlock(blkx,blky,blkz+1) == 1 || getBlock(blkx,blky,blkz+1) == 0) { isSurfaceBlock = true; sidesVisible[1] = true; surfaceBlocksFaces++; }
								if (isSurfaceBlock) {
									BlockObject blksfc = new BlockObject(blkx,blky,blkz,getBlock(blkx,blky,blkz));
									blksfc.sidesVisible = sidesVisible;
									surfaceBlocks = ArrayHelper.push(surfaceBlocks, blksfc);
								}
							} else {
								surfaceBlocksFaces = surfaceBlocksFaces + 6;
								BlockObject blksfc = new BlockObject(blkx,blky,blkz,getBlock(blkx,blky,blkz));
								surfaceBlocks = ArrayHelper.push(surfaceBlocks, blksfc);
							}
						}
					}
				}
			}
			System.out.print("Culled ".concat(Integer.toString((32*32*32) - surfaceBlocks.length)).concat(" blocks from chunk\n"));
		}
		lastUpdated = System.currentTimeMillis();
	}
	
	public float[] getVerticesSurface() {
		return vertexSurface;
	}
	
	public int[] getIndicesSurface() {
		return indicesSurface;
	}
	
	public float[] getTextureCoordsSurface() {
		return textureCoordsSurface;
	}
	
	
	public void generateSurface() {
		System.out.print("Generating surface for chunk\n");
		updateSurfaceBlocks();
		float[] outputVertices = new float[surfaceBlocksFaces * 12];
		float[] outputTextureCoords = new float[surfaceBlocksFaces * 8];
		int[] outputIndices = new int[surfaceBlocksFaces * 6];
		int offsetSkippedFaces = 0;
		for (int i=0; i<surfaceBlocks.length; i++) {
			float[] verticesBlock = {			
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
			int[] indicesBlock = {
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
			float[] textureCoordsStandard = {
				0f,0f,
				0f,1f,
				1f,1f,
				1f,0f,
			};
			for (int fn=0; fn<6; fn++) {
				if (surfaceBlocks[i].sidesVisible[fn] == false) {
					offsetSkippedFaces = offsetSkippedFaces + 1;
				} else {		
					for (int j=0; j<8; j++) {
						float offsetX = 0f;
						float offsetY = 0f;
						float texturePackBlocksWidth = 16f;
						switch (surfaceBlocks[i].id) {
						case 2: offsetX = 2f; offsetY = 0f; break;
						case 3: 
							switch(fn) {
							case 0: offsetX = 2f; offsetY = 0f; break;
							case 1: offsetX = 0f; offsetY = 0f; break;
							case 2: offsetX = 3f; offsetY = 0f; break;
							case 3: offsetX = 3f; offsetY = 0f; break;
							case 4: offsetX = 3f; offsetY = 0f; break;
							case 5: offsetX = 3f; offsetY = 0f; break;
							}
							break;
						case 4: offsetX = 1f; offsetY = 0f; break;
						case 5: 
							switch(fn) {
							case 0: offsetX = 2f; offsetY = 0f; break;
							case 1: offsetX = 2f; offsetY = 4f; break;
							case 2: offsetX = 4f; offsetY = 4f; break;
							case 3: offsetX = 4f; offsetY = 4f; break;
							case 4: offsetX = 4f; offsetY = 4f; break;
							case 5: offsetX = 4f; offsetY = 4f; break;
							}
							break;
						case 6: offsetX = 2f; offsetY = 1f; break;
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
							outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].x);
						}
						if ((j % 3) == 1) {
							outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].y);			
						}
						if ((j % 3) == 2) {
							outputVertices[(i*72) + j - (offsetSkippedFaces * 12) + (fn * 12)] = verticesBlock[j + (fn * 12)] + ((float) surfaceBlocks[i].z);
						}
					}
				}
			}
			//outputIndices = (int[])ArrayHelper.concatenate(outputIndices, indicesBlock);
			//outputVertices = (float[])ArrayHelper.concatenate(outputVertices, verticesBlock);
		}
		indicesSurface = outputIndices;
		vertexSurface = outputVertices;
		textureCoordsSurface = outputTextureCoords;
		chunkModel = new Model(vertexSurface, textureCoordsSurface, indicesSurface);
		System.out.print("Surface completed for chunk\n");
	}
	
	public int[] getPosition() {
		int[] chkpos = {x,y,z};
		return chkpos;	
	}
	
}
