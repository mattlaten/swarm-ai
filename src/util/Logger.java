package util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {
	
	String className = null;
	PrintWriter infostream = null;
	PrintWriter errstream = null;
	
	public Logger(Class<?> classRepr, OutputStream infostream, OutputStream errstream)
	{
		this.className = classRepr.getName();
		this.infostream = new PrintWriter(infostream);
		this.errstream = new PrintWriter(errstream);
	}
	
	public void info(String message)
	{
		infostream.printf("%s : [INFO: %s] %s\n", (new Date()).toString(), className, message);
		infostream.flush();
	}
	
	public void err(String message)
	{
		errstream.printf("%s : [ERR: %s] %s\n", (new Date()).toString(), className, message);
		errstream.flush();
	}
	
	public void warn(String message)
	{
		infostream.printf("%s : [WARN: %s] %s\n", (new Date()).toString(), className, message);
		infostream.flush();
	}
	
	public void debug(String message)
	{
		infostream.printf("%s : [DEBUG: %s] %s\n", (new Date()).toString(), className, message);
		infostream.flush();
	}
}
