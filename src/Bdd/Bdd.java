package Bdd;
import exceptions.CharInvalideException;

public class Bdd 
{
/*
 * on incremente des valeurs tampon
 * en cas d'erreur (CDS_invalide) il faut faire un clear de la base
 * en cas de succès il faut faire un push de la base
 * fait remonter une exception char_invalide qu'il faut matcher
 * 
 * s'utilise en faisant open_tampon, remplissant la base puis close_tampon si tout c'est bien passé
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
	
	private int nb_trinucleotides; //nb trinucleotides toutes phases confondues (triple de la valeur de chaque phase)
	private int nbTrinucleotidesParPhase[]; //nb_trinucleotides_par_phase[phase]
	
	private int nb_dinucleotides; //nb dinucleotides toutes phases confondues (triple de la valeur de chaque phase)
	private int nbDinucleotidesParPhase[]; //nb_dinucleotides_par_phase[phase]
	
	private int nb_CDS_non_traites;
	
	private int tableautrinucleotides[][]; //tableautrinucleotides[phase][trinucleotide]
	
	private int tableaudinucleotides[][]; //tableautrinucleotides[phase][dinucleotide]
	
	//tampon
	private int tampon_nb_trinucleotides;
	private int tamponNbTrinucleotidesParPhase[]; //nb_trinucleotides_par_phase[phase]
	
	private int tampon_nb_dinucleotides;
	private int tamponNbDinucleotidesParPhase[]; //nb_dinucleotides_par_phase[phase]
	
	private int tampon_tableautrinucleotides[][]; //tableautrinucleotides[phase][trinucleotide]
	private int tampon_tableaudinucleotides[][]; //tableautrinucleotides[phase][trinucleotide]
	
	boolean empty_tamp;
	
//-----------------------------------------------------------------------------	
//fonctions publiques
	
//constructeur
	public Bdd (String chem)
	{
		chemin = chem;
		
		nb_CDS = 0;
		nb_trinucleotides = 0;
		nbTrinucleotidesParPhase = new int[3];
		nb_dinucleotides = 0;
		nbDinucleotidesParPhase = new int[2];
		nb_CDS_non_traites = 0;
		
		tampon_nb_trinucleotides = 0;
		tamponNbTrinucleotidesParPhase = new int[3];
		tampon_nb_dinucleotides = 0;
		tamponNbDinucleotidesParPhase = new int[2];
		tableautrinucleotides = new int[3][64];
		tampon_tableautrinucleotides = new int[3][64];
		tableaudinucleotides = new int[2][16];
		tampon_tableaudinucleotides = new int[2][16];
		
		empty_tamp=true;
	}
	
//incrementeurs
	public void incr_nb_CDS ()
	{
		nb_CDS++;
	}
	
	public void incr_nb_CDS_non_traites ()
	{
		nb_CDS_non_traites++;
	}
	
	//tampon
	//ajoute un dinucleotide et un tri nucleotides au phases indiquées
	public void ajoute_nucleotides (int phase2, int phase3, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		tampon_tableautrinucleotides[phase3][position_of_nucleotides(nucleotide1,nucleotide2,nucleotide3)]++;
		tamponNbTrinucleotidesParPhase[phase3]++;
		tampon_nb_trinucleotides++;
		
		tampon_tableaudinucleotides[phase2][position_of_nucleotides(nucleotide1,nucleotide2)]++;
		tamponNbDinucleotidesParPhase[phase2]++;
		tampon_nb_dinucleotides++;
	}
	
	//ajoute un tri nucleotides a la phase indiquée
	public void ajoute_nucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		tampon_tableautrinucleotides[phase][position_of_nucleotides(nucleotide1,nucleotide2,nucleotide3)]++;
		tamponNbTrinucleotidesParPhase[phase]++;
		tampon_nb_trinucleotides++;
	}
	
	//ajoute un dinucleotide à la phase indiquée
	public void ajoute_nucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		tampon_tableaudinucleotides[phase][position_of_nucleotides(nucleotide1,nucleotide2)]++;
		tamponNbDinucleotidesParPhase[phase]++;
		tampon_nb_dinucleotides++;
	}
	
	//retire un dinucleotide à la phase indiquée
	public void retire_nucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		tampon_tableaudinucleotides[phase][position_of_nucleotides(nucleotide1,nucleotide2)]--;
		tamponNbDinucleotidesParPhase[phase]--;
		tampon_nb_dinucleotides--;
	}
	
//getters (resultat final)
	public int get_nb_CDS ()
	{
		return nb_CDS;
	}
	
	public int get_nb_trinucleotides ()
	{
		return nb_trinucleotides;
	}
	
	public int get_nb_dinucleotides ()
	{
		return nb_dinucleotides;
	}
	
	public int get_nb_CDS_non_traites ()
	{
		return nb_CDS_non_traites;
	}
	
	public int get_tableautrinucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		return tableautrinucleotides[phase][position_of_nucleotides(nucleotide1,nucleotide2,nucleotide3)];
	}
	
	public int get_tableaudinucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		return tableaudinucleotides[phase][position_of_nucleotides(nucleotide1,nucleotide2)];
	}
	
	public int[][] get_tableautrinucleotides ()
	{
		return tableautrinucleotides;
	}
	
	public int[][] get_tableaudinucleotides ()
	{
		return tableaudinucleotides;
	}
	
//tampon
	
	public int get_nb_trinucleotides_tampon ()
	{
		return tampon_nb_trinucleotides;
	}
	
	public int get_nb_dinucleotides_tampon ()
	{
		return tampon_nb_dinucleotides;
	}
	
	//déplace le contenus du tampon dans la mémoire
		public void close_tampon()
		{
			nb_trinucleotides += tampon_nb_trinucleotides;
			
			for(int i = 0 ; i<3 ; i++)
			{
				nbTrinucleotidesParPhase[i]+=tamponNbTrinucleotidesParPhase[i];
				
				for(int j = 0 ; j<64 ; j++)
				{
					tableautrinucleotides[i][j]+=tampon_tableautrinucleotides[i][j];
				}
			}
			
			nb_dinucleotides += tampon_nb_dinucleotides;

			for(int i = 0 ; i<2 ; i++)
			{
				nbDinucleotidesParPhase[i]+=tamponNbDinucleotidesParPhase[i];
				
				for(int j = 0 ; j<16 ; j++)
				{
					tableaudinucleotides[i][j]+=tampon_tableaudinucleotides[i][j];
				}
			}
			
			clear_tampon();
			empty_tamp=true;
		}
		
		//s'assure que le tampon est vide pour avancer
		public void open_tampon()
		{
			if (empty_tamp)
			{
				empty_tamp=false;
			}
			else
			{
				clear_tampon();
			}
		}
	
	//remet un tampon à 0
	public void clear_tampon()
	{
		tampon_nb_trinucleotides = 0;
		
		for(int i = 0 ; i<3 ; i++)
		{
			nbTrinucleotidesParPhase[i]=0;
			
			for(int j = 0 ; j<64 ; j++)
			{
				tampon_tableautrinucleotides[i][j]=0;
			}
		}
		
		tampon_nb_dinucleotides = 0;
		
		for(int i = 0 ; i<2 ; i++)
		{
			nbDinucleotidesParPhase[i]=0;

			for(int j = 0 ; j<16 ; j++)
			{
				tampon_tableaudinucleotides[i][j]=0;
			}
		}
	}
	
//acces tableau
	//renvois la position du tableau associee a un triplet de caractere (sous forme d'entier)
	//renvois une erreur si un des caracteres est invalide (codé par un -1)
	private int position_of_nucleotides (int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		if ( (nucleotide1<0) || (nucleotide2<0) || (nucleotide3<0) )
		{	
			throw new CharInvalideException();
		}
		
		return nucleotide1*16 + nucleotide2*4 + nucleotide3;
	}
	
	private int position_of_nucleotides (int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		if ( (nucleotide1<0) || (nucleotide2<0) )
		{	
			throw new CharInvalideException();
		}
		
		return nucleotide1*4 + nucleotide2;
	}
	
	//renvois une chaine de caracteres (trinucleotide en majuscule) correspondant a une position dans le tableau
	//il est beaucoup plus efficasse de mettre les case du tableau à coté de valeur notées en dur à l'avance étant donné qu'on sais d'offfice à quoi correspond chaque case
	public String int_to_trinucleotide (int num) throws CharInvalideException
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
	
	private String int_to_dinucleotide (int num) throws CharInvalideException
	{
		if ((num<0)||(num>15)) { throw new CharInvalideException(); }
		
		String res = "";
		int val = num;

		//Test 1
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

		// Test 2
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
	
	//sort un string qui représente le profil du tableau de trinucleotides
	public String get_tableauxnucleotides_string ()
	{
		String str = "";
				
		try 
		{
			str += "trinucleotide	phase1	phase2	phase3\n";
			for (int i=0 ; i<64 ; i++)
			{
				str=str+"	"+int_to_trinucleotide(i)+" :	"+tableautrinucleotides[0][i]+"	"+tableautrinucleotides[1][i]+"	"+tableautrinucleotides[2][i]+"\n";
			}
			
			str += "dinucleotide	phase1	phase2\n";
			for (int i=0 ; i<16 ; i++)
			{
				str=str+"	"+int_to_dinucleotide(i)+" :	"+tableaudinucleotides[0][i]+"	"+tableaudinucleotides[1][i]+"\n";
			}
		} 
		catch (CharInvalideException e) {/*cette exception ne peux pas se produire, on ne rentre que des entiers valides*/}
		
		return str;
	}

}
