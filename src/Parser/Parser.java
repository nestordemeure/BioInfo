package Parser;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bdd.Bdd;
import Parser.Parser_deprecated.Automate_lecteur_de_genes;
import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;
import exceptions.NoOriginException;

public class Parser 
{
	private Bdd base_de_donnees;
	private Scanner scanner;
	private ArrayList<CDS> CDS_list;
	
	public Parser (Bdd base, Scanner scan)
	{
		base_de_donnees = base;
		scanner = scan;
		CDS_list = new ArrayList<CDS>();
	}
	
	//fonction qui fait tourner le parseur
	public void parse()
	{
		try 
		{ 
		parser_entete();
		
		parser_gene();
		} 
		catch (NoOriginException eorig) 
		{ 
			/*en l'absence d'origine dans un fichier, on n'en fait rien*/
		}
	}
	
//--------------------------------------------------------------------------
//lire l'entete
	
	void parser_entete() throws NoOriginException
	{
		try
		{
			//on se place dans la catégorie features
			trouver_prefix("FEATURES");
			
			String ligne_actuelle;
			boolean recherche_en_cour = true;

			/*on parcours l'entete
			 * si on croise ORIGIN, on s'arrete
			 * si on croise un CDS, on l'ajoute et on repars
			 * sinon, on continue
			 */
			while (recherche_en_cour)
			{
				ligne_actuelle=scanner.next(); //succeptible de renvoyer une exception qu'on va catcher
				
				if (ligne_actuelle.startsWith("ORIGIN")) //on a finit
				{
					recherche_en_cour=false;
				}
				else
				{
					//TODO : vérifier que l'offset est bien de 5
					if (ligne_actuelle.startsWith("CDS",5)) //on a un CDS (5 espaces après le début de la ligne)
					{
						parser_CDS(ligne_actuelle);
					}
				}
			}
		}
		catch (NoSuchElementException e)
		{
			throw new NoOriginException();
		}
	}
		
	//prend une ligne contenant un CDS en entrée et l'ajoute à la liste de CDS en le parsant
	void parser_CDS(String ligne)
	{
		CDS cds = new CDS();
		try 
		{
			//TODO verifier que l'offset est bien de 21
			automate_sequence(ligne, 21, true, cds); //la description du CDS commence 21char après le début de la ligne
			CDS_list.add(cds);
		} 
		catch (CDSInvalideException e) 
		{
			base_de_donnees.incr_nb_CDS_non_traites();
		}
	}
	
//--------------------------------------------------------------------------
//lire le code génétique
	
	//TODO
	void parser_gene()
	{
		//on parcours la liste des CDS
		for (CDS cds : CDS_list) 
		{   
			//try
			{
				//TODO
				
				base_de_donnees.push_tampon();
				base_de_donnees.incr_nb_CDS();
			}
			/*catch (CDSInvalideException ecds)
			{
				base_de_donnees.clear_tampon();
				base_de_donnees.incr_nb_CDS_non_traites();
			}*/
		}
	}
	
//--------------------------------------------------------------------------
//fonctions pour le scanner
	
	//fait avancer un scanner jusqu'a atteindre le préfixe donné
	//consomme la ligne qui contient le préfixe
	//renvois une exception si on ne peux pas lire l'élément suivant
	void trouver_prefix(String prefix) throws NoSuchElementException
	{
		String ligne_actuelle = scanner.next();
		
		if (!ligne_actuelle.startsWith(prefix))
		{
			trouver_prefix(prefix);
		}
	}
	
//--------------------------------------------------------------------------
//automate qui lit le format des CDS
		
	/*grammaire
	 * 
	 * sequence : interval | complement(sequence) | join(seq_list)
	 * 
	 * seq_list : sequence | sequence,seq_list
	 * 
	 * interval : int..int
	 * 
	 * int : [0-9]+
	 * 
	 */
		
	/*
	 * chaque fonction :
	 * 
	 * retourne la position dans le texte après lecture de sa séquence
	 * 
	 * renvois une exception si elle détecte un CDSinvalide (syntaxe non respectée)
	 * 
	 * transporte avec elle sa position actuelle et le sens de lecture (true pour directe, false pour complement)
	 */
		
	//match une séquence
	int automate_sequence(String ligne, int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException
	{
		if (ligne.charAt(position) == 'c') //complement
		{
			return automate_complement(ligne, position, sens_de_lecture, cds);
		}
		else if (ligne.charAt(position) == 'j') //joint
		{
			return automate_join(ligne, position, sens_de_lecture, cds);
		}
		else
		{
			return automate_interval(ligne, position, sens_de_lecture, cds); //interval simple
		}
	}
		
	//match un complement qui contient une sequence
	int automate_complement(String ligne, int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "complement("
		if ( !ligne.startsWith("complement(",position) ) 
		{
			throw new CDSInvalideException("token complement mal redige");
		}
		
		//on lit la sequence en changeant le sens de lecture
		int new_position = automate_sequence(ligne, position+11 , !sens_de_lecture, cds);
			
		//on vérifie qu'on a bien un ')'
		if (ligne.charAt(new_position) != ')') 
		{
			throw new CDSInvalideException("parenthese non fermee sur un complement");
		}
			
		return new_position+1;
	}
		
	//match un join qui contient une liste de sequence
	int automate_join(String ligne, int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "join("
		if ( !ligne.startsWith("join(",position) ) 
		{
			throw new CDSInvalideException("token join mal redige");
		}
		
		//on lit une sequences
		int new_position = automate_sequence(ligne, position+5, sens_de_lecture, cds);
		
		//on lit des séquences introduites par des virgules tant qu'il y en a
		while ( ligne.charAt(new_position) == ',' )
		{
			new_position = automate_sequence(ligne, new_position+1, sens_de_lecture, cds);
		}
		
		//on vérifie qu'on a bien un ')'
		if (ligne.charAt(new_position) != ')')
		{
			throw new CDSInvalideException("parenthese mal fermee sur un join");
		}
		
		return new_position+1;
	}

	//match un interval qui contient deux entier
	//on identifie chacun des entiers dans l'automate (faute de pouvoir sortir des triplet entier*position depuis un automate)
	int automate_interval(String ligne, int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException
	{
		int debut=0;
		int fin=0;
		int new_position = position;
			
		//on lit le premier entier
		try { 
			while ( true )
			{
				debut = debut*10 + chiffre_of_int(ligne, new_position);
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
			}
		} 
		catch (NumberFormatException eint1) 
		{
			//on vérifie qu'on a bien un ".."
			if ( !ligne.startsWith("..",new_position) )
			{
				throw new CDSInvalideException("separateur different de ..");
			}
			new_position = new_position+2;

			//on lit le second entier
			try 
			{ 
				while ( true )
				{
					fin = fin*10 + chiffre_of_int(ligne, new_position);
					new_position++; //il y avait un chiffre a la position, on avance à la position suivante
				}
			} 
			catch (NumberFormatException eint2) 
			{
				cds.ajouter_sequence(debut,fin,sens_de_lecture);
				return new_position; 
			}
		}
	}
		
	//match un chiffre et rend le chiffre (et pas une position comme le fond les automates)
	int chiffre_of_int(String ligne, int position) throws NumberFormatException
	{
		try
		{
			char c = ligne.charAt(position);
			int res = (int)c - (int)'0';
		
			if ((res<0)||(res>9)) 
			{
				throw new NumberFormatException();
			}
		
			return res ;
		}
		catch( IndexOutOfBoundsException e ) //possible si le chiffre est au bord de la fin de la ligne
		{
			throw new NumberFormatException();
		}
	}	
}
