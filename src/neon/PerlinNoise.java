package neon;

import java.util.Random;

public class PerlinNoise {
	
	String seed = "";
	Random rand = new Random();
	
//	static final int p[] = new int[512], permutation[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194,
//			233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
//			197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168,
//			68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220,
//			105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
//			89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
//			124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
//			223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39,
//			253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238,
//			210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
//			184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243,
//			141, 128, 195, 78, 66, 215, 61, 156, 180 };
//	static {
//		for (int i = 0; i < 256; i++)
//			p[256 + i] = p[i] = permutation[i];
//	}
	
	private int[] p = new int[512];
	
	public PerlinNoise(int seedInput) {
		rand = new Random(seedInput);
		for (int i = 0; i < 512; i++) {
			rand.nextInt(256); //[0-255]
		}
		initPerlin1();
	}

	private double grad(int hash, double x, double y) {
		switch (hash & 3) {
		case 0:
			return x + y;
		case 1:
			return -x + y;
		case 2:
			return x - y;
		case 3:
			return -x - y;
		default:
			return 0;
		}
	}
	
	private double grad(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
				v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	public double noise2dTile(double x, double y) {
		int xi = (int) Math.floor(x) & 255;
		int yi = (int) Math.floor(y) & 255;
		int g1 = p[p[xi] + yi];
		int g2 = p[p[xi + 1] + yi];
		int g3 = p[p[xi] + yi + 1];
		int g4 = p[p[xi + 1] + yi + 1];

		double xf = x - Math.floor(x);
		double yf = y - Math.floor(y);

		double d1 = grad(g1, xf, yf);
		double d2 = grad(g2, xf - 1, yf);
		double d3 = grad(g3, xf, yf - 1);
		double d4 = grad(g4, xf - 1, yf - 1);

		double u = fade(xf);
		double v = fade(yf);

		double x1Inter = lerp(u, d1, d2);
		double x2Inter = lerp(u, d3, d4);
		double yInter = lerp(v, x1Inter, x2Inter);

		return yInter;

	}
	
    private static final int B = 0x1000;
    private static final int BM = 0xff;

    private static final int N = 0x1000;
    private static final int NP = 12;   /* 2^N */
    private static final int NM = 0xfff;
    
    private double[][] g3;
    private double[][] g2;
    private double[] g1;
    
    private double sCurve(double t)
    {
        return (t * t * (3 - 2 * t));
    }
    
    private void normalize2(double[] v)
    {
        double s = (double)(1 / Math.sqrt(v[0] * v[0] + v[1] * v[1]));
        v[0] *= s;
        v[1] *= s;
    }

    /**
     * 3D-vector normalisation function.
     */
    private void normalize3(double[] v)
    {
        double s = (double)(1 / Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
        v[0] *= s;
        v[1] *= s;
        v[2] *= s;
    }

    
    private void initPerlin1()
    {
        p = new int[B + B + 2];
        g3 = new double[B + B + 2][3];
        g2 = new double[B + B + 2][2];
        g1 = new double[B + B + 2];
        int i, j, k;

        for(i = 0; i < B; i++)
        {
            p[i] = i;

            g1[i] = (double)(((rand.nextInt(Integer.MAX_VALUE)) % (B + B)) - B) / B;

            for(j = 0; j < 2; j++)
                g2[i][j] = (double)(((rand.nextInt(Integer.MAX_VALUE)) % (B + B)) - B) / B;
            normalize2(g2[i]);

            for(j = 0; j < 3; j++)
                g3[i][j] = (double)(((rand.nextInt(Integer.MAX_VALUE)) % (B + B)) - B) / B;
            normalize3(g3[i]);
        }

        while(--i > 0)
        {
            k = p[i];
            j = (int)((rand.nextInt(Integer.MAX_VALUE)) % B);
            p[i] = p[j];
            p[j] = k;
        }

        for(i = 0; i < B + 2; i++)
        {
            p[B + i] = p[i];
            g1[B + i] = g1[i];
            for(j = 0; j < 2; j++)
                g2[B + i][j] = g2[i][j];
            for(j = 0; j < 3; j++)
                g3[B + i][j] = g3[i][j];
        }
    }
	
	public double noise2d(double x, double y)
    {
        double t = x + N;
        int bx0 = ((int)t) & BM;
        int bx1 = (bx0 + 1) & BM;
        double rx0 = t - (int)t;
        double rx1 = rx0 - 1;

        t = y + N;
        int by0 = ((int)t) & BM;
        int by1 = (by0 + 1) & BM;
        double ry0 = t - (int)t;
        double ry1 = ry0 - 1;

        int i = p[bx0];
        int j = p[bx1];

        int b00 = p[i + by0];
        int b10 = p[j + by0];
        int b01 = p[i + by1];
        int b11 = p[j + by1];

        double sx = sCurve(rx0);
        double sy = sCurve(ry0);

        double[] q = g2[b00];
        double u = rx0 * q[0] + ry0 * q[1];
        q = g2[b10];
        double v = rx1 * q[0] + ry0 * q[1];
        double a = lerp(sx, u, v);

        q = g2[b01];
        u = rx0 * q[0] + ry1 * q[1];
        q = g2[b11];
        v = rx1 * q[0] + ry1 * q[1];
        double b = lerp(sx, u, v);

        return lerp(sy, a, b);
    }
	
	
}
