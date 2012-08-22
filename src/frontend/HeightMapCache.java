package frontend;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import math.Vec;
import backend.HeightMap;

class HeightMapCache implements Runnable	{
	HeightMap hm;
	public double width, height;
	BufferedImage img;
	volatile double completion = 0;
	Canvas master;
	
	Thread renderThread = new Thread(this);
	
	public HeightMapCache(Canvas master, HeightMap hm)	{
		this.hm = hm;
		this.master = master;
		Rectangle2D.Double bounds = hm.getRenderBounds();
		System.out.println(bounds + " " + hm.topLeft + " " + hm.botRight);
		width = bounds.width;
		height = bounds.height;
		render();
		System.out.println("done");
	}
	
	void setHeightMap(HeightMap hm)
	{
		this.hm = hm;
	}
	
	void render()	{
		if(renderThread.isAlive())
			renderThread.interrupt();
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public BufferedImage getImage()	{
		Rectangle2D.Double bounds = hm.getRenderBounds();
		if(bounds.width != width || bounds.height != height)
			render();
		return img;
	}
	
	public void run()	{
		try	{
			//render this guy
			img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
			double offX = (width - (hm.botRight.x - hm.topLeft.x))/2,
				   offY = (height -(hm.topLeft.y - hm.botRight.y))/2;
			int yStep = Math.max(img.getHeight()/25, 1);
			for(int y = 0; y < img.getHeight(); y++)	{
				for(int x = 0; x < img.getWidth(); x++)	{
					double height = hm.getInterpolatedHeightAt(hm.topLeft.plus(new Vec(x-offX,-y+offY)));
					int h = (int)(height*255);
					img.setRGB(x, y, new Color(h,h,h).getRGB());//(((((255 << 8) | h) << 8) | h) << 8) | h);
				}
				if(y%yStep == 0)	{
					completion = (double)y/img.getHeight();
					master.repaint();
					//Thread.sleep(10);
				}
			}
			Thread.sleep(10);
		}
		catch(InterruptedException ie)	{
			
		}
		finally	{
			completion = 1;
			master.repaint();
		}
	}
}