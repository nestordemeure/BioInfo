package exceptions;

public class CharInvalideException extends Exception
{
	public CharInvalideException()
	{
		System.out.println("caractere inconnu detecte dans la Bdd");
	}
}
