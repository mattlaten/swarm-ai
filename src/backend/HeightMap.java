package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import util.Logger;

public class HeightMap {

	double terrain[][] = null;
	int y = 512;
	int x = 512;
	
	double max = 0;
	double min = Double.MAX_VALUE;
	double range = 0;
	
	BufferedReader reader = null;
	
	Logger log = new Logger(HeightMap.class, System.out, System.err);
	
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
		printGrid();
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

	}
}
