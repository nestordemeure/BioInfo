import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;
import exceptions.NoOriginException;

//on crée un parser et lance la fonction parse

public class Parser 
{
	//Variables d'instance
	private Bdd base_de_donnees;
	private String texte;
	private int origine;
	private ArrayList<Integer> position_CDS;
	
	//Fonctions
	Parser (Bdd base, String txt)
	{
		base_de_donnees = base;
		texte = txt;
	}
	
	//repere l'origine du code génétique
	//etablit une liste des position des CDS
	//renvois une exception si il n'y a pas d'origine
	void trouve_origine_bdd() throws NoOriginException
	{
		//Cherche le mot clé features à partir duquel on peut trouver des mots clés "CDS"
		int deb_features = texte.indexOf("FEATURES");
		if (deb_features == -1) { throw new NoOriginException(); }
		
		//Cherche le mot clé CDS
		int deb_cds = texte.indexOf("CDS", deb_features);
		
		while (deb_cds != -1)
		{
			position_CDS.add(deb_cds);
			deb_cds = texte.indexOf("CDS", deb_cds);
		}
		
		origine = texte.indexOf("ORIGIN", (int) position_CDS.get(position_CDS.size()-1));
		if (origine == -1) { throw new NoOriginException(); }
	}
	
	//va extraire les informations relatives à un CDS dont on indique l'adresse du descripteur
	//renvois une exception si le CDS est invalide
	void lecture_de_CDS(int position) throws CDSInvalideException
	{
		//le string "CDS             " fait 16char
		int position_actuelle = position+16;
		//TODO on devrait désormais se trouver sur le charactère de début de séquence
		
		//l'automate qui va parcourir cette séquence, dans le sens directe par défaut
		Automate_lecteur_de_genes auto = new Automate_lecteur_de_genes(true);
		
		automate_sequence(position_actuelle, auto);
	}
	
	//fonction qui fait tourner le parseur
	void parse()
	{
		try 
		{ 
		
		//on initialise l'origine et la liste des position des CDS
		trouve_origine_bdd(); 
		
		//on parcours la liste des CDS
		for (int p : position_CDS) 
		{   
			try
			{
				lecture_de_CDS(p);
				base_de_donnees.push_tampon();
				base_de_donnees.incr_nb_CDS();
			}
			catch (CDSInvalideException ecds)
			{
				base_de_donnees.clear_tampon();
				base_de_donnees.incr_nb_CDS_non_traites();
			}
		}
		
		} 
		catch (NoOriginException eorig) { /*en l'absence d'origine dans un fichier, on n'en fait rien*/ }
	}
	
//-------------------------------------------------------------------------
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
	 * 
	 */
	
	//match une séquence
	int automate_sequence(int position, Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		if (texte.charAt(position) == 'c')
		{
			return automate_complement(position, auto);
		}
		else if (texte.charAt(position) == 'j')
		{
			return automate_join(position, auto);
		}
		else
		{
			return automate_interval(position, auto);
		}
	}
	
	//match un complement qui contient une sequence
	int automate_complement(int position, Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "complement("
		if ( !texte.startsWith("complement(",position) ) { throw new CDSInvalideException(); }
		
		//on lit la sequence en changeant le sens de lecture
		auto.renverser_sens_de_lecture();
		int new_position = automate_sequence(position+11 , auto);
		auto.renverser_sens_de_lecture();
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException(); }
		
		return new_position+1;
	}
	
	//match un join qui contient une liste de sequence
	int automate_join(int position, Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "join("
		if ( !texte.startsWith("join(",position) ) { throw new CDSInvalideException(); }
		
		//on lit une sequences
		int new_position = automate_sequence(position+5 , auto);
		
		//on lit des séquences introduites par des virgules tant qu'il y en a
		while ( texte.charAt(new_position) == ',' )
		{
			new_position = automate_sequence(new_position+1 , auto);
		}
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException(); }
		
		return new_position+1;
	}

	//match un interval qui contient deux entier
	//on identifie chacun des entiers dans l'automate (faute de pouvoir sortir des triplet entier*position depuis un automate)
	//on procède ensuite à la lecture du gene associé
	int automate_interval(int position, Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		int int1=0;
		int int2=0;
		int chiffre;
		int new_position = position;
		
		//on lit le premier entier
		try { while ( true )
		{
				chiffre = chiffre_of_int(new_position);
				int1 = int1*10 + chiffre;
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
		}
		} catch (NumberFormatException eint1) {
		
		//on vérifie qu'on a bien un ".."
		if ( !texte.startsWith("..",new_position) ) { throw new CDSInvalideException(); }
		new_position = new_position+2;

		//on lit le second entier
		try { while ( true )
		{
				chiffre = chiffre_of_int(new_position);
				int2 = int2*10 + chiffre;
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
		}
		} catch (NumberFormatException eint2) {
		
		try 
		{
			//char_erreur si il croise un caractère qui n'est pas un AGCT
			//CDS_erreur si int1>int2
			auto.lire(int1, int2);
			
			return new_position; 
		}
		catch (CharInvalideException echar)
		{
			throw new CDSInvalideException();
		} } } 
	}
	
	//match un chiffre et rend le chiffre (et pas une position comme le fond les automates)
	int chiffre_of_int(int position) throws NumberFormatException
	{
		char c = texte.charAt(position);
		int res = (int)c - (int)'0';
		
		if ((res<0)||(res>9)) { throw new NumberFormatException(); }
		
		return res ;
	}
	
//-------------------------------------------------------------------------
//classe qui modélise un automate lisant le code génétique
	
	public class Automate_lecteur_de_genes
	{
		boolean sens_de_lecture;
		int phase;
		boolean already_started;
		
		//nucleotides en mémoire
		//si l'automate a été amorcé, il ne faut pas réinitialiser ces valeurs
		char nucleotide1;
		char nucleotide2;
		char nucleotide3;
		
		//-----
		
		Automate_lecteur_de_genes (boolean sens_de_lect)
		{
			sens_de_lecture = sens_de_lect;
			phase=0;
			already_started=false;
		}
		
		//-----
		
		void renverser_sens_de_lecture()
		{
			sens_de_lecture = !sens_de_lecture;
		}
		
		void incrementer_phase()
		{
			phase = (phase+1)%3;
		}
		
		//-----
		
		void lire(int debut, int fin) throws CharInvalideException, CDSInvalideException
		{
			if (debut>fin) { throw new CDSInvalideException(); }
			
			int deb = position_string_of_numeros_nucleotide(debut);
			int fi = position_string_of_numeros_nucleotide(fin);
			
			try { 	//vérifier que les entiers ne sortes pas du texte
			if (sens_de_lecture)
			{
				if (!already_started) //première fois que l'automate tourne
				{
					already_started=true;
					nucleotide1 = texte.charAt(deb);
					nucleotide2 = texte.charAt(deb+1);
					lire_sens_directe(deb+2, fi);
				}
				else //on reprend après un join
				{
					//on sauve le triplet qui finissait la section précédente du join
					base_de_donnees.incr_tableautrinucleotides(phase, nucleotide1, nucleotide2, nucleotide3);
					incrementer_phase(); 
					nucleotide1=nucleotide2;
					nucleotide2=nucleotide3;
					
					lire_sens_directe(deb, fi);
				}
			}
			else
			{
				if (!already_started) //première fois que l'automate tourne
				{
					already_started=true;
					nucleotide1 = texte.charAt(fi);
					nucleotide2 = texte.charAt(fi-1);
					lire_sens_complement(deb, fi-2);
				}
				else  //on reprend après un join
				{
					//on sauve le triplet qui finissait la section précédente du join
					base_de_donnees.incr_tableautrinucleotides(phase, nucleotide1, nucleotide2, nucleotide3);
					incrementer_phase(); 
					nucleotide1=nucleotide2;
					nucleotide2=nucleotide3;
					
					lire_sens_complement(deb, fi);
				}
			}
			}
			catch (StringIndexOutOfBoundsException e) { throw new CDSInvalideException(); }
		}
		
		//on lit du début à la fin
		//TODO on pourrait facilement dérécurciver cette fonction avec un while ou un for (plus efficasse?)
		void lire_sens_directe(int deb, int fi) throws CharInvalideException
		{
			nucleotide3 = texte.charAt(deb);

			switch(nucleotide3)
			{
				case ' ' :
					lire_sens_directe(deb+1, fi); //on reprend la lecture après l'espace
					break;
				case '\n' : //si on croise un passage à la ligne, on doit encore lire tout un entete de ligne soit en tout 11 char
					lire_sens_directe(deb+11, fi); //on reprend la lecture après le passage à la ligne et le bloc de chiffres/espaces qui suit
					break;
				default:
					if (deb < fi) //si on a finit de lire la séquence, on arrete
					{//on fait ce test maintenant pour éviter de garder un caractère problèmatique en mémoire dans la variable nucleotide3
						//on inscrit le triplet dans le tableau
						base_de_donnees.incr_tableautrinucleotides(phase, nucleotide1, nucleotide2, nucleotide3);
						//on décale la fenetre de lecture
						incrementer_phase(); 
						nucleotide1=nucleotide2;
						nucleotide2=nucleotide3;
						lire_sens_directe(deb+1, fi);
					}
			}
		}
		
		//on lit de la fin au début
		//TODO on pourrait facilement dérécurciver cette fonction avec un while ou un for (plus efficasse?)
		void lire_sens_complement(int deb, int fi) throws CharInvalideException
		{
			//convertir un caractere ou renvoyer une exception
			nucleotide3 = fonction_complement(texte.charAt(fi));

			switch(nucleotide3)
			{
				case ' ' :
					lire_sens_directe(deb, fi-1); //on reprend la lecture après l'espace
					break;
				case '0' : //si on croise un chiffre, c'est le premier d'un entete, on est donc à 10char, '\n' inclus, du reste du genome
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '1' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '2' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '3' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '4' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '5' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '6' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '7' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '8' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				case '9' :
					lire_sens_directe(deb, fi-10); //on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					break;
				default:
					if (deb < fi) //si on a finit de lire la séquence, on arrete
					{//on fait ce test maintenant pour éviter de garder un caractère problèmatique en mémoire dans la variable nucleotide3
						//on inscrit le triplet dans le tableau
						base_de_donnees.incr_tableautrinucleotides(phase, nucleotide1, nucleotide2, nucleotide3);
						//on décale la fenetre de lecture
						incrementer_phase(); 
						nucleotide1=nucleotide2;
						nucleotide2=nucleotide3;
						lire_sens_directe(deb, fi-1);
					}
			}
		}
		
		//-----
		
		//rend le complementaire d'un nucleotide donné ou le laisse intacte
		char fonction_complement(char nucleotide)
		{
			switch(nucleotide)
			{
				case 'a' :
					return 't';
				case 'c' :
					return 'g';
				case 'g' :
					return 'c';
				case 't' :
					return 'a';
				default:
					return nucleotide;
			}
		}
		
		//prend une position dans le génome et rend une position dans la chaine de caractères
		int position_string_of_numeros_nucleotide(int p)
		{
			//position d'origine plus taille de la première ligne +
			//10 car en tete de chaque ligne de 60 nucléotides +
			//1 char en tete de chaque bloc de 10 nucleotides
			// ( origine+12 ) + (10)*( (p/60) +1 ) + ( (p/10) +1 )
			// se simplifie en (on ne simplifie pas plus pour préserver les divisions entières) :
			
			return origine + 23 + 10*(p/60) + (p/10);
		}
	}	
	
}
