import exceptions.CharInvalideException;

public class Bdd 
{
/*
 * on incremente des valeurs tampon
 * en cas d'erreur (CDS_invalide) il faut faire un clear de la base
 * en cas de succès il faut faire un push de la base
 * fait remonter une exception char_invalide qu'il faut matcher
 */

/*
 * correspondance nucleotide/entier :
 * a = 0
 * c = 1
 * g = 2
 * t = 3
 */
	
//-----------------------------------------------------------------------------
//variable d'instance
	
//sortie
	
	//TODO : placeholder en attendant de connaitre le format d'un chemin
	private String chemin;
	
	private int nb_CDS;
	
	private int nb_trinucleotides;
	
	private int nb_CDS_non_traites;
	
	private int tableautrinucleotides[][]; //tableautrinucleotides[phase][trinucleotide]
	
	//tampon
	private int tampon_nb_trinucleotides;
		
	private int tampon_tableautrinucleotides[][]; //tableautrinucleotides[phase][trinucleotide]

	
//-----------------------------------------------------------------------------	
//fonctions publiques
	
//constructeur
	Bdd (String chem)
	{
		chemin = chem;
		
		nb_CDS = 0;
		nb_trinucleotides = 0;
		nb_CDS_non_traites = 0;
		
		tampon_nb_trinucleotides = 0;
		tableautrinucleotides = new int[3][64];
		tampon_tableautrinucleotides = new int[3][64];
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
	void ajoute_trinucleotide (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		tampon_tableautrinucleotides[phase][position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)]++;
		tampon_nb_trinucleotides++;
		
		//TODO affichage pour le debugage
		try {
			System.out.println(int_to_trinucleotide(position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)));
		} catch (CharInvalideException e) {
		}
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
	
	int get_tableautrinucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		return tableautrinucleotides[phase][position_of_trinucleotide(nucleotide1,nucleotide2,nucleotide3)];
	}
	
//tampon
	
	int get_nb_trinucleotides_tampon ()
	{
		return tampon_nb_trinucleotides;
	}
	
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
	//renvois la position du tableau associee a un triplet de caractere (sous forme d'entier)
	//renvois une erreur si un des caracteres est invalide (codé par un -1)
	int position_of_trinucleotide (int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		if ( (nucleotide1<0) || (nucleotide2<0) || (nucleotide3<0) )
		{	
			throw new CharInvalideException();
		}
		
		return nucleotide1*16 + nucleotide2*4 + nucleotide3;
	}
	
	//renvois une chaine de caracteres (trinucleotide en majuscule) correspondant a une position dans le tableau
	//il est beaucoup plus efficasse de mettre les case du tableau à coté de valeur notées en dur à l'avance étant donné qu'on sais d'offfice à quoi correspond chaque case
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
