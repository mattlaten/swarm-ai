package backend;

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
	
	Logger log = new Logger(HeightMap.class, System.out, System.err);
	
	Vec topLeft, botRight;
	
	public HeightMap(String filename)
	{
		terrain = new double [y][x];
		init(filename);
		//printGrid();
	}
	
	public HeightMap()
	{
		terrain = new double [y][x];
		generateRandomHeights();
		//printGrid();
		
		
	}
	
	public void init(String filename)
	{
		try
		{	
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			String tokens [] = line.split(" ");			
			y = Integer.parseInt(tokens[0]);
			x = Integer.parseInt(tokens[0]);
			
			int i = 0;
			int j = 0;
			int k = 2;
			while (true)
			{
				terrain[i][j] = Double.parseDouble(tokens[k]);
				//System.out.println(terrain[i][j]);
				max = Math.max(max, terrain[i][j]);
				min = Math.min(min, terrain[i][j]);
				j++;
				k++;
				if (j == x)
				{
					i++;
					j = 0;
				}
				if (i == y)
					break;
			}
			range = max - min;
			System.out.println("Max: " + max + " Min: " + min);
			
			for (i = 0; i < y; i++)
				for (j = 0; j < x; j++)
					terrain[i][j] = (terrain[i][j]-min)/range;
			topLeft = new Vec(-x*10/2, y*10);
			botRight= new Vec(x*10/2, -y*10);
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
		for (int i = 0; i < x; i++) 
			for (int j = 0; j < y; j++)
			{
				if (i == 0)
					if (j == 0)
						terrain[i][j] = Math.random()*30;
					else
						terrain[i][j] = Math.max(0, terrain[i][j-1] + (Math.random()*5)-2.5);
				else 
					if (j == 0)
						terrain[i][j] = Math.max(0, terrain[i-1][j] + (Math.random()*5)-2.5);
					else
						terrain[i][j] = Math.max(0, (terrain[i-1][j-1] + (Math.random()*5)-2.5
									  + terrain[i-1][j] + (Math.random()*5)-2.5
									  + terrain[i][j-1] + (Math.random()*5)-2.5)/3);
				max = Math.max(max, terrain[i][j]);
				min = Math.min(min, terrain[i][j]);
			}
		
		range = max - min;
		System.out.println("Max: " + max + " Min: " + min);
		
		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++)
				terrain[i][j] = (terrain[i][j]-min)/range;
		
		topLeft = new Vec(-x*10/2, y*10);
		botRight= new Vec(x*10/2, -y*10);
	}
	
	public double getHeightAt(int x, int y)	{
		if(x < terrain.length && x >= 0 && y < terrain[0].length && y >= 0)
			return terrain[y][x];
		return 0;
	}
	
	//http://en.wikipedia.org/wiki/Bilinear_interpolation
	public double getInterpolatedHeightAt(Vec p)	{
		double width = botRight.x - topLeft.x,
			   height = botRight.y - topLeft.y,
			   xStep = (double)(terrain.length)/width,
			   yStep = (double)(terrain[0].length)/height;
		
		int tx1 = (int)(Math.floor((p.x - topLeft.x)/xStep)),
			tx2 = (int)(Math.ceil((p.x - topLeft.x)/xStep)),
			ty1 = (int)(Math.floor((topLeft.y - p.y)/yStep)),
			ty2 = (int)(Math.ceil((topLeft.y - p.y)/yStep));
		
		double x1 = tx1*xStep,
			   x2 = tx2*xStep,
			   y1 = ty1*yStep,
			   y2 = ty2*yStep;
		
		System.out.println(x1 + " " + x2 + " " + y1 + " " + y2);
		
		double a = 0;
		if((x2-x1)*(y2-y1) != 0)
			 a = (getHeightAt(tx1, ty1)*(x2-p.x)*(y2-p.y)
				  + getHeightAt(tx2, ty1)*(p.x-x1)*(y2-p.y)
				  + getHeightAt(tx1, ty2)*(x2-p.x)*(p.y-y1)
				  + getHeightAt(tx2, ty2)*(p.x-x1)*(p.y-y1))
				 /((x2-x1)*(y2-y1));
		else if(x2 != x1)
			a= (getHeightAt(tx1, ty1)*(x2-p.x) + getHeightAt(tx2, ty1)*(p.x-x1))/(x2-x1);
		else
			a= (getHeightAt(tx1, ty1)*(y2-p.y) + getHeightAt(tx1, ty2)*(p.y-y1))/(y2-y1);
		System.out.println(a);
		return a;
	}
}
