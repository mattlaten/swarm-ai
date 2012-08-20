package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import util.Logger;

public class HeightMap {

	double terrain[][] = null;
	int length = 512;
	int width = 512;
	
	BufferedReader reader = null;
	
	Logger log = new Logger(HeightMap.class, System.out, System.err);
	
	public HeightMap(String filename)
	{
		terrain = new double [length][width];
		init(filename);
	}
	
	public HeightMap()
	{
		terrain = new double [length][width];
		generateRandomHeights();
	}
	
	public void init(String filename)
	{
		try
		{	
			reader = new BufferedReader(new FileReader(filename));
			String line = null;
			for (int i = 0; i < length; i++)
			{
				line = reader.readLine();
				String tokens [] = line.split(" ");
				for (int j = 0; j < length; j++)
				{
					terrain[i][j] = Double.parseDouble(tokens[j]);
				}
					
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
	
	public void generateRandomHeights()
	{
		for (int i = 0; i < width; i++)
			for (int j = 0; j < length; j++)
				if (i == 0)
					if (j == 0)
						terrain[i][j] = Math.random()*30;
					else
						terrain[i][j] = terrain[i][j-1] + (Math.random()*5)-2.5;
				else 
					if (j == 0)
						terrain[i][j] = terrain[i-1][j] + (Math.random()*5)-2.5;
					else
						terrain[i][j] = (terrain[i-1][j-1] + (Math.random()*5)-2.5
									  + terrain[i-1][j] + (Math.random()*5)-2.5
									  + terrain[i][j-1] + (Math.random()*5)-2.5)/3;
	}
}
