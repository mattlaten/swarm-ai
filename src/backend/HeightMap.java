package backend;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import util.Logger;

import math.Vec;

public class HeightMap {

	double terrain[][] = null;
	int y = 512;
	int x = 512;
	
	double max = 0;
	double min = Double.MAX_VALUE;
	double range = 0;
	
	BufferedReader reader = null;
	
	Logger log = null;
	
	public Vec topLeft, botRight;
	
	public HeightMap(String filename)
	{
		log = new Logger(HeightMap.class, System.out, System.err);
		readInFile(filename);
		normalizeTerrain(1);
		//printGrid();
	}
	
	public HeightMap()
	{
		log = new Logger(HeightMap.class, System.out, System.err);
		terrain = new double [y][x];
		generateRandomHeights();
		normalizeTerrain(1);
		//printGrid();
	}
	
	public void readInFile(String filename)
	{
		try
		{	
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			String tokens [] = line.split(" ");			
			x = Integer.parseInt(tokens[0]);
			y = Integer.parseInt(tokens[0]);
			terrain = new double[x][y];
			
			int i = 0;
			int j = 0;
			int k = 2;
			while (true)
			{
				terrain[i][j] = Double.parseDouble(tokens[k]);
				//System.out.println(terrain[i][j]);
				max = Math.max(max, terrain[i][j]);
				min = Math.min(min, terrain[i][j]);
				i++;
				k++;
				if (i == x)
				{
					j++;
					i = 0;
				}
				if (j == y)
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
	
	public void normalizeTerrain(double factor)
	{
		log.info("Normalizing Terrain");
		range = max - min;
		//System.out.println("Max: " + max + " Min: " + min);
		
		for (int j = 0; j < y; j++)
			for (int i = 0; i < x; i++)	{
				terrain[i][j] = (terrain[i][j]-min)/range;
				//System.out.println(terrain[i][j]);
			}
		topLeft = new Vec(-x*factor, y*factor);
		botRight= new Vec(x*factor, -y*factor);
	}
	
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
	
	public void generateRandomHeights()
	{
		//fillGrid(terrain,0,0);
		for (int i = 0; i < x; i++) 
			for (int j = 0; j < y; j++)
			{
				//terrain[i][j] = Math.random();
				//algorithm for generating terain at i,j
				
				
				if (i == 0)
					if (j == 0)
						terrain[i][j] = Math.random()*30;
					else
						terrain[i][j] = Math.max(0, terrain[i][j-1] + (Math.random()*10)-5);
				else 
					if (j == 0)
						terrain[i][j] = Math.max(0, terrain[i-1][j] + (Math.random()*10)-5);
					else
						if (i%10 == 0 && j%10 == 0)
							terrain[i][j] = Math.random()*10 + terrain[i-1][j-1];
						else
							terrain[i][j] = Math.max(0, (terrain[i-1][j-1] + (Math.random()*10)-5
									  + terrain[i-1][j] + (Math.random()*10)-5
									  + terrain[i][j-1] + (Math.random()*10)-5)/3);
				max = Math.max(max, terrain[i][j]);
				min = Math.min(min, terrain[i][j]);
			}
	}
	
	public void fillGrid(double grid[][], int x, int y)
	{
		terrain[y][x] += Math.random();
		if (x > 0 && y > 0) fillGrid(grid,x-1,y-1);			
		if (x > 0 && y < grid.length) fillGrid(grid,x-1,y+1);
		if (x < grid[0].length && y > 0)fillGrid(grid,x+1,y-1);
		if (x < grid[0].length && y < grid.length) fillGrid(grid,x+1,y+1);
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
		//System.out.println(a);
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

























