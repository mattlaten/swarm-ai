package util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {
	
	String className = null;
	PrintWriter info = null;
	PrintWriter err = null;
	
	public Logger(Class<?> classRepr, OutputStream info, OutputStream err)
	{
		this.className = classRepr.getName();
		this.info = new PrintWriter(info);
		this.err = new PrintWriter(err);
	}
	
	public void info(String message)
	{
		info.printf("%s : [INFO: %s] %s\n", (new Date()).toString(), className, message);
	}
	
	public void err(String message)
	{
		err.printf("%s : [ERR: %s] %s\n", (new Date()).toString(), className, message);
	}
	
	public void warn(String message)
	{
		info.printf("%s : [WARN: %s] %s\n", (new Date()).toString(), className, message);
	}
	
	public void debug(String message)
	{
		info.printf("%s : [DEBUG: %s] %s\n", (new Date()).toString(), className, message);
	}
}
