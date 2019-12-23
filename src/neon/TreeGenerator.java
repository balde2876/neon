package neon;

public class TreeGenerator {
	public Chunk[] chunksAvaliable = new Chunk[] {};
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public int treeHeight = 5;
	
	public TreeGenerator() {
		//setBlock(px, py, pz, 7);
	}
	
	public void generate(int px, int py, int pz) {
		System.out.println("New Tree ".concat(Integer.toString(px)).concat(",").concat(Integer.toString(py)).concat(",").concat(Integer.toString(pz)));
		x = px;
		y = py;
		z = pz;
		for (int i = 0;i < treeHeight; i++) {
			setBlock(x, y, z+i, 7);
		}
	}

	public void setBlock(int px, int py, int pz, int blkId) {
			int[] targetChunkRel = new int[] {(int) Math.floor(((float) px)/32f),(int) Math.floor(((float) py)/32f),(int) Math.floor(((float) pz)/32f)};
			targetChunkRel = new int[] {0,0,0};
			System.out.println("Tree Chunk ".concat(Integer.toString(targetChunkRel[0])).concat(",").concat(Integer.toString(targetChunkRel[1])).concat(",").concat(Integer.toString(targetChunkRel[2])));
			for (int chkid=0; chkid<chunksAvaliable.length; chkid++) {
				if (chunksAvaliable[chkid].x == targetChunkRel[0]) {
					if (chunksAvaliable[chkid].y == targetChunkRel[1]) {
						if (chunksAvaliable[chkid].z == targetChunkRel[2]) {
							System.out.println("Attempting block set");
							chunksAvaliable[chkid].setBlock(px - (targetChunkRel[0] * 32), py - (targetChunkRel[1] * 32), pz - (targetChunkRel[2] * 32), blkId);
						}
					}
				}
			}
	}
}
