package backend;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import util.Logger;

import math.Vec;

public class HeightMap {

	double terrain[][] = null;
	int y = 513;
	int x = 513;
	
	double max = 0;
	double min = Double.MAX_VALUE;
	double range = 0;
	
	BufferedReader reader = null;
	
	File heightMapFile = null;
	
	Logger log = null;
	
	public Vec topLeft, botRight;
	
	public HeightMap(File file)
	{
		log = new Logger(HeightMap.class, System.out, System.err);
		readInFile(file);
		normalizeTerrain(1);
	}
	
	/*
	public HeightMap()
	{
		log = new Logger(HeightMap.class, System.out, System.err);
		terrain = new double [y][x];
		generateRandomHeights();
		normalizeTerrain(1);
	}
	*/
	
	public HeightMap()
	{
		log = new Logger(HeightMap.class, System.out, System.err);
		terrain = new double [y][x];
		normalizeTerrain(1);
	}
	
	
	public void readInFile(File file)
	{
		try
		{	
			heightMapFile = file;
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			String tokens [] = line.split(" ");			
			x = Integer.parseInt(tokens[0]);
			y = Integer.parseInt(tokens[0]);
			terrain = new double[y][x];
			
			int xcoord = 0; //xcoord
			int ycoord = 0; //ycoord
			int k = 2; //pos in list
			while (true)
			{
				terrain[xcoord][ycoord] = Double.parseDouble(tokens[k]);
				//System.out.println(terrain[i][j]);
				max = Math.max(max, terrain[xcoord][ycoord]);
				min = Math.min(min, terrain[xcoord][ycoord]);
				xcoord++;
				k++;
				if (xcoord == x)
				{
					ycoord++;
					xcoord = 0;
				}
				if (ycoord == y)
					break;
			}
		}
		catch(Exception ex)
		{
			log.err(ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.err(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes the HeightMap into Wavefront OBJ file
	 * @param file Output File
	 * @throws IOException
	 */
	public void writeOBJ(File file) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		for (int j = 0; j < y; j++)	
			for (int i = 0; i < x; i++)
				pw.println(String.format("v %f %f %f", i + 0.0, j + 0.0, terrain[j][i]));
	}
	
	/**
	 * Takes heights in HeightMap and normalizes them (transpose to range (0,1))
	 * @param factor Scaling factor used by zoom
	 */
	public void normalizeTerrain(double factor)
	{
		log.info("Normalizing Terrain");
		range = max - min; //calc range
		
		//normalize range
		for (int j = 0; j < y; j++)
			for (int i = 0; i < x; i++)	
				terrain[j][i] = (terrain[j][i]-min)/range;
		
		topLeft = new Vec(-x*factor, y*factor);
		botRight= new Vec(x*factor, -y*factor);
	}
	
	/**
	 * Debugging method
	 */
	public void printGrid()
	{
		String line = null;
		for (int i = 0; i < y; i++)
		{
			line = "";
			for (int j = 0; j < x; j++)
				line += (String.format("%.2f ",terrain[i][j]));
			System.out.println(line);
		}
	}
	
	/**
	 * Generate a HeightMap composed of random heights at each point
	 */
	public void generateRandomHeights()
	{	
		for (int j = 0; j < y; j++) 
			for (int i = 0; i < x; i++)
			{
				terrain[j][i] = Math.random();
				max = Math.max(max, terrain[i][j]);
				min = Math.min(min, terrain[i][j]);
			}
	}
	
	public double getHeightAt(int x, int y)	{
		if(x < this.x && x >= 0 && y < this.y && y >= 0)
			return terrain[this.y-y-1][this.x-x-1];
		return 0;
	}
	
	//http://en.wikipedia.org/wiki/Bilinear_interpolation
	public double getInterpolatedHeightAt(Vec pp)	{
		double width = botRight.x - topLeft.x,
			   height = topLeft.y - botRight.y,
			   xStep = width/(this.x),
			   yStep = height/(this.y);
		
		Vec p = pp.plus(new Vec(-xStep/2, yStep/2));
		
		Vec tp = p.minus(topLeft).invertY();
		
		int tx1 = (int)(Math.floor(tp.x/xStep)),
			tx2 = (int)(Math.ceil(tp.x/xStep)),
			ty1 = (int)(Math.floor(tp.y/yStep)),
			ty2 = (int)(Math.ceil(tp.y/yStep));
		
		double x1 = tx1*xStep,
			   x2 = tx2*xStep,
			   y1 = ty1*yStep,
			   y2 = ty2*yStep;
		
		double a = 0;
		if((x2-x1)*(y2-y1) != 0)
			 a = (getHeightAt(tx1, ty1)*(x2-tp.x)*(y2-tp.y)
				  + getHeightAt(tx2, ty1)*(tp.x-x1)*(y2-tp.y)
				  + getHeightAt(tx1, ty2)*(x2-tp.x)*(tp.y-y1)
				  + getHeightAt(tx2, ty2)*(tp.x-x1)*(tp.y-y1))
				 /((x2-x1)*(y2-y1));
		else if(x2 != x1)
			a = (getHeightAt(tx1, ty1)*(x2-tp.x) + getHeightAt(tx2, ty1)*(tp.x-x1))/(x2-x1);
		else if(y2 != y1)
			a = (getHeightAt(tx1, ty1)*(y2-tp.y) + getHeightAt(tx1, ty2)*(tp.y-y1))/(y2-y1);
		else
			a = getHeightAt(tx1, ty1);
		return a;
	}
	
	public Rectangle2D.Double getRenderBounds()	{
		double width = botRight.x - topLeft.x,
			   height = topLeft.y - botRight.y,
			   xStep = width/(this.x),
			   yStep = height/(this.y);
		return new Rectangle2D.Double(topLeft.x - xStep, topLeft.y + yStep, width + xStep*2, height + yStep);
	}
}

























