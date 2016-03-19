package exceptions;

public class NoOriginException extends Exception
{
	public NoOriginException()
	{
	}
	
	public NoOriginException(String str)
	{
		System.out.println(str);
	}
}
