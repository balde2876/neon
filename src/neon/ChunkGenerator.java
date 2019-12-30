package neon;

public class ChunkGenerator extends Thread {
	public int x;
	public int y;
	public int z;
	public short[] data = new short[32*32*32];
	public int seed;
	public long[] lastBlockChange = new long[] {System.currentTimeMillis()};
	public Chunk[] chunksAvaliable;
	
	public void run() { 
        try { 
            // Displaying the thread that is running 
        	//Neon.logger.logInfo("Thread " + Thread.currentThread().getId() + " ( Chunk Generator ) is running"); 
    		PerlinNoise noiseGenerator = new PerlinNoise(seed);
    		PerlinNoise noiseGeneratorArid = new PerlinNoise(seed+32);
    		PerlinNoise noiseGeneratorMountain = new PerlinNoise(seed+64);
    		PerlinNoise noiseGeneratorOcean = new PerlinNoise(seed+96);
    		PerlinNoise noiseGeneratorForest = new PerlinNoise(seed+128);
    		PerlinNoise noiseGeneratorIndividual = new PerlinNoise(seed-32);
    	
    		//its generation time
    		double[] heightmap = new double[32*32];
    		
    		double[] heightmapexpanded = new double[64*64];
    		int[] heightmapfinalexpanded = new int[64*64];
    		double[] forestmapexpanded = new double[64*64];
    		double[] mountainmapexpanded = new double[64*64];
    		double[] individualmapexpanded = new double[64*64];
    		double[] oceanmapexpanded = new double[64*64];
    		
    		double[] aridmap = new double[32*32];
    		double[] mountainmap = new double[32*32];
    		double[] oceanmap = new double[32*32];
    		double[] forestmap = new double[64*64];
    		double[] individualmap = new double[64*64];
    		
    		float max = 0;
    		float min = 0;
    		
    		//float usfmodifier = ((float) noiseGeneratorShitpost.noise2d((x)/30f, (-y)/30f) + 0.01f) * 30f;
    		float usfmodifier = 0.5f;
    		
    		float scaleFactor = 1f*usfmodifier; //1
    		float scaleFactorArid = 3f*usfmodifier; //3
    		float scaleFactorMountain = 16f*usfmodifier; //3
    		float scaleFactorOcean = 16f*usfmodifier; //3
    		float scaleFactorForest = 3f*usfmodifier; //3
    		float scaleFactorIndividual = 0.1f*usfmodifier; //3
    		
    		for (int i=0; i<64; i++) {
    			for (int j=0; j<64; j++) {
    				heightmapexpanded[(i*64)+j] = noiseGenerator.noise2d((x+((i-16)/32f))/scaleFactor, (-y-((j-16)/32f))/scaleFactor);
    				forestmapexpanded[(i*64)+j] = noiseGeneratorForest.noise2d((x+((i-16)/32f))/scaleFactorForest, (-y-((j-16)/32f))/scaleFactorForest);
    				mountainmapexpanded[(i*64)+j] = noiseGeneratorMountain.noise2d((x+((i-16)/32f))/scaleFactorMountain, (-y-((j-16)/32f))/scaleFactorMountain);
    				individualmapexpanded[(i*64)+j] = noiseGeneratorIndividual.noise2d((x+((i-16)/32f))/scaleFactorIndividual, (-y-((j-16)/32f))/scaleFactorIndividual);
    				oceanmapexpanded[(i*64)+j] = noiseGeneratorOcean.noise2d((x+((i-16)/32f))/scaleFactorOcean, (-y-((j-16)/32f))/scaleFactorOcean);
    				
    				double mountainous = ((Math.tanh((mountainmapexpanded[(i*64)+j] - 0.5f) * 6f) + 1f) / 2f);
    				double oceanlevel = ((Math.tanh((oceanmapexpanded[(i*64)+j] - 0.5f) * 6f) + 1f) / 2f);
    				double heightAtPosition = (((heightmapexpanded[(i*64)+j]*32f)) * ((mountainous * 2f) + 0.05f)) + (mountainous * 8f * 16f) - (oceanlevel * 8f * 16f);
    				heightmapfinalexpanded[(i*64)+j] = (int) Math.floor(heightAtPosition) + 16 - (z*32);
    			}
    		}
    		
    		for (int i=0; i<32; i++) {
    			for (int j=0; j<32; j++) {
    				heightmap[(i*32)+j] = noiseGenerator.noise2d((x+(i/32f))/scaleFactor, (-y-(j/32f))/scaleFactor);
    				aridmap[(i*32)+j] = noiseGeneratorArid.noise2d((x+(i/32f))/scaleFactorArid, (-y-(j/32f))/scaleFactorArid);
    				mountainmap[(i*32)+j] = noiseGeneratorMountain.noise2d((x+(i/32f))/scaleFactorMountain, (-y-(j/32f))/scaleFactorMountain);
    				oceanmap[(i*32)+j] = noiseGeneratorOcean.noise2d((x+(i/32f))/scaleFactorOcean, (-y-(j/32f))/scaleFactorOcean);
    				
    				//System.out.println(genX+(i/32));
    				
    				
    				//double mountainous = 1 - Math.cos(mountainmap[(i*32)+j] * Math.PI * 0.4);
    				double mountainous = ((Math.tanh((mountainmap[(i*32)+j] - 0.5f) * 6f) + 1f) / 2f);
    				//double aridity = (aridmap[(i*32)+j] * 4) - (mountainous * 6);
    				//double aridity = (aridmap[(i*32)+j] * 4);
    				double aridity = ((Math.tanh((aridmap[(i*32)+j] - 0.5f) * 3f) + 1f) / 1f);
    				
    				double oceanlevel = ((Math.tanh((oceanmap[(i*32)+j] - 0.5f) * 6f) + 1f) / 2f);
    				//double heightAtPosition = ((heightmap[(i*32)+j]*32) * (mountainous * 16)) + (mountainous * 16 * 16);
    				double heightAtPosition = (((heightmap[(i*32)+j]*32f)) * ((mountainous * 2f) + 0.05f)) + (mountainous * 8f * 16f) - (oceanlevel * 8f * 16f);
    				//heightmapfinal[(i*32)+j] = (int) Math.floor(heightAtPosition) + 16 - (z*32);
    				
    				
    				
    				// 0 - air
    				// 1 - water
    				// 2 - dirt
    				// 3 - grass
    				// 4 - stone
    				// 5 - snow grass
    				// 6 - sand
    				// 7 - water placeholder
    			
    				for (int k=0; k<32; k++) {
    					if ((k+(z*32) - 16) > Math.floor(heightAtPosition)) {
    						if (Math.floor(heightAtPosition) > 8) {
    							data[(((i*32)+j)*32)+k] = 0; // air
    						} else {
    							if ((k+(z*32) - 8) > 0) {
    								data[(((i*32)+j)*32)+k] = 0; // air
    							} else {
    								data[(((i*32)+j)*32)+k] = 1; // water
    							}
    						}
    					} else {
    						if ((k+(z*32) - 16) == Math.floor(heightAtPosition)) {
    							if (aridity > (0.3f + (heightAtPosition / 32f))) {
    								data[(((i*32)+j)*32)+k] = 6; // sand
    							} else {
    								if (heightAtPosition > (32+aridity)) {
    									if ((k+(z*32) - 8) >= 0) {
    										if (aridity > (0.2f + (heightAtPosition / 32f))) {
    											data[(((i*32)+j)*32)+k] = 3; // grass
    										} else {
    											data[(((i*32)+j)*32)+k] = 5; // snowy grass
    										}
    									} else {
    										data[(((i*32)+j)*32)+k] = 2; // dirt
    									}
    								} else {
    									if ((k+(z*32) - 8) >= 0) {
    										data[(((i*32)+j)*32)+k] = 3; // grass
    									} else {
    										data[(((i*32)+j)*32)+k] = 2; // dirt
    									}
    								}
    							}
    						} else {
    							if ((k+(z*32) - 12) > Math.floor(heightAtPosition)) {
    								if (aridity > (0.3f + (heightAtPosition / 32f))) {
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
    		
    		
    		//TREE GENERATOR
			for (int i=0; i<64; i++) {
    			for (int j=0; j<64; j++) {
    				int sx = i;
					int sy = j;
					int sz = 0;
    				if (forestmapexpanded[(i*64)+j] > 0.25f) {
						if (individualmapexpanded[(i*64)+j] > 0.4f) {
							//TreeGenerator treeGenerator = new TreeGenerator();
							//treeGenerator.chunksAvaliable = chunksAvaliable;
							//treeGenerator.generate(i+(x*32),j+(y*32),k+(z*32));
							
							//k == Math.floor(heightAtPosition) + 16 - (z*32)
							sx = i;
							sy = j;
							sz = 0;
							int treeHeight = 7;
							int treeSize = 2;
							for (int itr = 0;itr < treeHeight; itr++) {
								
								if (sx >= 16 && sx <= 47) {
									if (sy >= 16 && sy <= 47) {
										sz = itr + heightmapfinalexpanded[(sx * 64) + sy] + 16;
										//System.out.print(sz);
										if (sz >= 16 && sz <= 47) {
											if (data[((((sx-16)*32)+(sy-16))*32)+(sz-16)] == 0) {
												data[((((sx-16)*32)+(sy-16))*32)+(sz-16)] = 7;
											}
										}
									}
								}
							}
							if (sx >= 16 && sx <= 47) {
								if (sy >= 16 && sy <= 47) {
									sz = heightmapfinalexpanded[(sx * 64) + sy] + 16;
									if (sz >= 16 && sz <= 47) {
										data[((((sx-16)*32)+(sy-16))*32)+(sz-16)] = 2;
									}
								}
							}
							for (int tlx = -treeSize;tlx <= treeSize; tlx++) {
								for (int tly = -treeSize;tly <= treeSize; tly++) {
									for (int tlz = -treeSize;tlz <= treeSize; tlz++) {
										sx = tlx + i;
										sy = tly + j;
										if (!((tlx * tlx) + (tly * tly) > (3-(tlz * 2)))) {
											if (sx >= 16 && sx <= 47) {
												if (sy >= 16 && sy <= 47) {
													sz = tlz + treeHeight + heightmapfinalexpanded[(i * 64) + j] + 16; // + (z * 32)
													if (sz >= 16 && sz <= 47) {
														if (data[((((sx-16)*32)+(sy-16))*32)+(sz-16)] == 0) {
															data[((((sx-16)*32)+(sy-16))*32)+(sz-16)] = 8;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
    			}
			}
			
			//Neon.logger.logInfo ("Thread " + Thread.currentThread().getId() + " ( Chunk Generator ) has completed"); 
    		//lastBlockChange[0] = System.currentTimeMillis();
    		lastBlockChange[0] = System.currentTimeMillis();
    		for (int chkid=0; chkid<chunksAvaliable.length; chkid++) {
    			if ((chunksAvaliable[chkid].x == (x + 1)) || (chunksAvaliable[chkid].x == (x - 1)) 
    			|| (chunksAvaliable[chkid].y == (y + 1)) || (chunksAvaliable[chkid].y == (y - 1))
    			|| (chunksAvaliable[chkid].z == (z + 1)) || (chunksAvaliable[chkid].z == (z - 1))) {
    				//chunksAvaliable[chkid].lastBlockChange[0] = System.currentTimeMillis();
    				//chunksAvaliable[chkid].lastUrgentBlockChange[0] = System.currentTimeMillis();
    			}
    		}
            
        } catch (Exception e) { 
        	// Throwing an exception 
        	Neon.logger.logError("Exception is caught"); 
            //System.err.println(e.getStackTrace());
            e.printStackTrace(System.err);
        } 
    } 
}
