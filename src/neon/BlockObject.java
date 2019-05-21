package neon;

public class BlockObject {
	int x;
	int y;
	int z;
	int id;
	boolean[] sidesVisible = new boolean[] {true,true,true,true,true,true};
	
	public BlockObject(int genX, int genY, int genZ, int genType) {
		x = genX;
		y = genY;
		z = genZ;
		id = genType;
	}
	
	public int[] returnPosition() {
		return new int[] {x,y,z};
	}
	
	public int returnType() {
		return id;
	}
}
