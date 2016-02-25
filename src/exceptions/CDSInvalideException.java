package exceptions;

public class CDSInvalideException extends Exception
{
	public CDSInvalideException()
	{
		System.out.println("CDS incorrecte");
	}
	
	public CDSInvalideException(String str)
	{
		System.out.println("CDS incorrecte : "+str);
	}
}
