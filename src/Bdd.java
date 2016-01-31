import exceptions.CharInvalideException;

public class Bdd 
{
//on incremente des valeurs tampon
//en cas d'erreur (CDS_invalide) il faut faire un clear de la base
//en cas de succès il faut faire un push de la base
//fait remonter une exception char_invalide qu'il faut matcher
	
//-----------------------------------------------------------------------------
//variable d'instance
	
//sortie
	
	//TODO : placeholder en attendant de connaitre le format d'un chemin
	private String chemin;
	
	private int nb_CDS = 0;
	
	private int nb_trinucleotides = 0;
	
	private int nb_CDS_non_traites = 0;
	
	//TODO : initialise par defaud a 0 ?
	private int tableautrinucleotides[][] = new int[3][64]; //tableautrinucleotides[phase][trinucleotide]
	
//tampon
		
	private int tampon_nb_trinucleotides = 0;
		
	//TODO : initialise par defaud a 0 ?
	private int tampon_tableautrinucleotides[][] = new int[3][64]; //tableautrinucleotides[phase][trinucleotide]

	
//-----------------------------------------------------------------------------	
//fonctions publiques
	
//constructeur
	Bdd (String chem)
	{
		chemin = chem;
	}
	
//incrementeurs
	void incr_nb_CDS ()
	{
		nb_CDS++;
	}
	
	void incr_nb_CDS_non_traites ()
	{
		nb_CDS_non_traites++;
	}
	
	//tampon
	void incr_nb_trinucleotides ()
	{
		tampon_nb_trinucleotides++;
	}
	
	//tampon
	void incr_tableautrinucleotides (int phase, char nucleotide1, char nucleotide2, char nucleotide3) throws CharInvalideException
	{
		tampon_tableautrinucleotides[phase][position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)]++;
	}
	
//setters (resultat final)
	void set_nb_CDS (int val)
	{
		nb_CDS=val;
	}
	
	void set_nb_trinucleotides (int val)
	{
		nb_trinucleotides=val;
	}
	
	void set_nb_CDS_non_traites (int val)
	{
		nb_CDS_non_traites=val;
	}
	
	void set_tableautrinucleotides (int phase, char nucleotide1, char nucleotide2, char nucleotide3, int val) throws CharInvalideException
	{
		tableautrinucleotides[phase][position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)]=val;
	}
	
//getters (resultat final)
	int get_nb_CDS ()
	{
		return nb_CDS;
	}
	
	int get_nb_trinucleotides ()
	{
		return nb_trinucleotides;
	}
	
	int get_nb_CDS_non_traites ()
	{
		return nb_CDS_non_traites;
	}
	
	int get_tableautrinucleotides (int phase, char nucleotide1, char nucleotide2, char nucleotide3) throws CharInvalideException
	{
		return tableautrinucleotides[phase][position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)];
	}

//-----------------------------------------------------------------------------	
//fonctions privees
	
//tampon
	
	//déplace le contenus du tampon dans la mémoire
		void push_tampon()
		{
			nb_trinucleotides += tampon_nb_trinucleotides;
			
			for(int i = 0 ; i<3 ; i++)
			{
				for(int j = 0 ; j<64 ; j++)
				{
					tableautrinucleotides[i][j]+=tampon_tableautrinucleotides[i][j];
				}
			}
			
			clear_tampon();
		}
	
	//remet un tampon à 0
	void clear_tampon()
	{
		tampon_nb_trinucleotides = 0;
		
		for(int i = 0 ; i<3 ; i++)
		{
			for(int j = 0 ; j<64 ; j++)
			{
				tampon_tableautrinucleotides[i][j]=0;
			}
		}
	}
	
//acces tableau
	
	//renvois la position du tableau associee a un triplet de caractere
	//retourne une exception et vide le tampon si un des caracteres est invalise
	int position_of_trinucleotide (char nucleotide1, char nucleotide2, char nucleotide3) throws CharInvalideException
	{
		int res= 0;
		
		switch(nucleotide1)
		{
			case 'a' :
				break;
			case 'c' :
				res=res+16;
				break;
			case 'g' :
				res=res+32;
				break;
			case 't' :
				res=res+48;
				break;
			default:
				throw new CharInvalideException();
		}
		
		switch(nucleotide2)
		{
			case 'a' :
				break;
			case 'c' :
				res=res+4;
				break;
			case 'g' :
				res=res+8;
				break;
			case 't' :
				res=res+12;
				break;
			default:
				throw new CharInvalideException();
		}
		
		switch(nucleotide3)
		{
			case 'a' :
				break;
			case 'c' :
				res=res+1;
				break;
			case 'g' :
				res=res+2;
				break;
			case 't' :
				res=res+3;
				break;
			default:
				throw new CharInvalideException();
		}
		
		return res;
	}
	
	//renvois une chaine de caracteres (trinucleotide en majuscule) correspondant a une position dans le tableau
	String int_to_trinucleotide (int num) throws CharInvalideException
	{
		if ((num<0)||(num>63)) { throw new CharInvalideException(); }
		
		String res = "";
		int val = num;
		
		//Test 1
		if (val >= 48) 
		{
			res = res + "T";
			val = val - 48;
		} 
		else if (val >= 32) 
		{
			res = res + "G";
			val = val - 32;
		} 
		else if (val >= 16) 
		{
			res = res + "C";
			val = val - 16;
		}
		else 
		{
			res = res + "A";
		}

		//Test 2
		if (val >= 12) 
		{
			res = res + "T";
			val = val - 12;
		}
		else if (val >= 8) 
		{
			res = res + "G";
			val = val - 8;
		}
		else if (val >= 4) 
		{
			res = res + "C";
			val = val - 4;
		}
		else 
		{
			res = res + "A";
		}

		// Test 3
		if (val == 3) 
		{
			res = res + "T";
		}
		else if (val == 2) 
		{
			res = res + "G";
		}
		else if (val == 1)
		{
			res = res + "C";
		}
		else 
		{
			res = res + "A";
		}

		return res;
	}

}
