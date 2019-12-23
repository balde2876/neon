package neon;

import org.json.JSONStringer;

public class Chunk {
	public int x;
	public int y;
	public int z;
	private short[] data = new short[32*32*32];
	private long[] lastUpdated = new long[] {0};
	long[] lastBlockChange = new long[] {0,0};
	public Model chunkModel;
	public boolean[] chunkModelStatus = new boolean[] {false,false,false};
	public Model chunkModelTransparent;
	public ChunkSurfaceData chunkSurfaceData = new ChunkSurfaceData();
	public Chunk[] chunksAvaliable;
	
	public void updateAvaliableChunks(Chunk[] chksavail) {
		chunksAvaliable = chksavail;
	}
	
	public Chunk(int genX, int genY, int genZ, int seed, Chunk[] chksavail) {
		x = genX;
		y = genY;
		z = genZ;
		
		Neon.logger.logInfo("New Chunk ".concat(Integer.toString(genX)).concat(",").concat(Integer.toString(genY)).concat(",").concat(Integer.toString(genZ)));
		chunksAvaliable = chksavail;
		ChunkGenerator chunkGeneratorThread = new ChunkGenerator(); 
		
		chunkGeneratorThread.x = x;
		chunkGeneratorThread.y = y;
		chunkGeneratorThread.z = z;
		chunkGeneratorThread.data = data;
		chunkGeneratorThread.seed = seed;
		chunkGeneratorThread.lastBlockChange = lastBlockChange;
		chunkGeneratorThread.chunksAvaliable = chunksAvaliable;
		chunkGeneratorThread.start(); 
	}

	
	public Chunk(int genX, int genY, int genZ) {
		//its loading time
	}
	
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
	
	public void setBlock(int px, int py, int pz, int blkId) {
		if ((px >= 0) && (px <= 31) && (py >= 0) && (py <= 31) && (pz >= 0) && (pz <= 31)) {
			Neon.logger.logInfo("Set Block ".concat(Integer.toString(px)).concat(",").concat(Integer.toString(py)).concat(",").concat(Integer.toString(pz)));
			data[(px*32*32) + (py*32) + pz] = (short) blkId;	
		} else {
			//return 0;
			int[] targetChunkRel = new int[] {(int) Math.floor(((float) px)/32f),(int) Math.floor(((float) py)/32f),(int) Math.floor(((float) pz)/32f)};
			for (int chkid=0; chkid<chunksAvaliable.length; chkid++) {
				if (chunksAvaliable[chkid].x == (x + targetChunkRel[0])) {
					if (chunksAvaliable[chkid].y == (y + targetChunkRel[1])) {
						if (chunksAvaliable[chkid].z == (z + targetChunkRel[2])) {
							chunksAvaliable[chkid].setBlock(px - (targetChunkRel[0] * 32), py - (targetChunkRel[1] * 32), pz - (targetChunkRel[2] * 32), blkId);
						}
					}
				}
			}
			//if (blkid > 0) {
				//System.out.println("EDGEBLOCK");
				//System.out.println(Math.floor(32f/32f));
				//System.out.println(JSONStringer.valueToString(targetChunkRel));
			//}
		}
		lastBlockChange[0] = System.currentTimeMillis();
	}

	public boolean generateSurfaces() {
		if (chunkModelStatus[0]) { // surfaces generated
			if (!chunkModelStatus[1]) { // surfaces not currently generating
				if (chunkModelStatus[2]) { // model not yet ready
					//System.out.println("Model Already Generated");
					//System.out.println("Model already prepared with ".concat(Integer.toString(indicesSurface.length/6)).concat(" faces"));
				} else {
					
					chunkModelTransparent = new Model(chunkSurfaceData.vertexSurfaceTransparent, chunkSurfaceData.textureCoordsSurfaceTransparent, chunkSurfaceData.indicesSurfaceTransparent);
					chunkModel = new Model(chunkSurfaceData.vertexSurface, chunkSurfaceData.textureCoordsSurface, chunkSurfaceData.indicesSurface);
					chunkModelStatus[2] = true;
					Neon.logger.logInfo("Model Generated with ".concat(Integer.toString(chunkSurfaceData.indicesSurface.length/6)).concat(" faces"));
					//System.out.println("from ".concat(Integer.toString(indicesSurface.length/6)).concat(" blocks"));
				}
			} else {
				Neon.logger.logInfo("Surfaces still generating");
			}
		} else {
			//System.out.println("Surfaces Not Generated");
		}
		if (lastUpdated[0] < lastBlockChange[0]) {
			//if (!chunkModelStatus[2]) { // model not generated already
				if (!chunkModelStatus[1]) { // surfaces not generating already
					Neon.logger.logInfo("Spawning Surface Generator");
					ChunkSurfaceGenerator chunkGeneratorThread = new ChunkSurfaceGenerator(); 
					
					chunkGeneratorThread.x = x;
					chunkGeneratorThread.y = y;
					chunkGeneratorThread.z = z;
					chunkGeneratorThread.data = data;
					
					//chunkGeneratorThread.surfaceLengths = surfaceLengths;
					
					chunkGeneratorThread.chunkSurfaceData = chunkSurfaceData;
					chunkGeneratorThread.chunksAvaliable = chunksAvaliable;
					chunkGeneratorThread.chunkModelStatus = chunkModelStatus;
					chunkGeneratorThread.lastUpdated = lastUpdated;
					
					chunkModelStatus[0] = false; //surfaces generated
					chunkModelStatus[1] = true; //surfaces generating
					chunkModelStatus[2] = false; //model ready
					chunkGeneratorThread.start(); 
				}
		}
		return chunkModelStatus[1];
	}
	
	public int[] getPosition() {
		int[] chkpos = {x,y,z};
		return chkpos;	
	}
}
