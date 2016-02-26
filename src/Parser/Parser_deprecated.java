package Parser;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bdd.Bdd;
import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;
import exceptions.NoOriginException;

//on crée un parser et lance la fonction parse

public class Parser_deprecated 
{
	//Variables d'instance
	private Bdd base_de_donnees;
	private String texte;
	private int origine;
	private ArrayList<Integer> position_CDS;
	
	//Fonctions
	public Parser_deprecated (Bdd base, String txt)
	{
		base_de_donnees = base;
		texte = txt;
		position_CDS = new ArrayList<Integer>();
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
		int deb_cds = texte.indexOf("CDS", deb_features+8);
		
		while (deb_cds != -1)
		{
			position_CDS.add(deb_cds);
			deb_cds = texte.indexOf("CDS", deb_cds+3); //on ne veux pas reselectionner le cds precedent
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
		
		//l'automate qui va parcourir cette séquence, dans le sens directe par défaut
		Automate_lecteur_de_genes auto = new Automate_lecteur_de_genes(true);
		
		automate_sequence(position_actuelle, auto);
		
		//on test le codon stop et la taille du CDS
		test_CDS(auto);
	}
	
	//test le codon stop et vérifie que la taille du cds est un multiple de trois
	void test_CDS(Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		//taille
		//on vérifie que la taille de la sequence est bien un multiple de trois
		if (auto.get_phase() != 0)
		{
			throw new CDSInvalideException("taille invalide");
		}
		else
		{				
			//codon stop
			//on vérifie que le dernier triplet est bien un codon stop (TAA,TAG,TGA)
			int n1 = auto.get_nucleotide1();
			int n2 = auto.get_nucleotide2();
			int n3 = auto.get_nucleotide3();
			if ( ! ((n1 == 3) && ((n2==0)&&((n3==2)||(n3==0))) || ((n2==2)&&(n3==0))) )
			{
				throw new CDSInvalideException("codon stop invalide " +n1+n2+n3);
			}
		}
	}
	
	//fonction qui fait tourner le parseur
	public void parse()
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
				
				base_de_donnees.close_tampon();
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
		if ( !texte.startsWith("complement(",position) ) { throw new CDSInvalideException("token complement mal redige"); }
		
		//on lit la sequence en changeant le sens de lecture
		auto.renverser_sens_de_lecture();
		int new_position = automate_sequence(position+11 , auto);
		auto.renverser_sens_de_lecture();
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException("parenthese non fermee sur un complement"); }
		
		return new_position+1;
	}
	
	//match un join qui contient une liste de sequence
	int automate_join(int position, Automate_lecteur_de_genes auto) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "join("
		if ( !texte.startsWith("join(",position) ) { throw new CDSInvalideException("token join mal redige"); }
		
		//on lit une sequences
		int new_position = automate_sequence(position+5 , auto);
		
		//on lit des séquences introduites par des virgules tant qu'il y en a
		while ( texte.charAt(new_position) == ',' )
		{
			new_position = automate_sequence(new_position+1 , auto);
		}
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException("parenthese mal fermee sur un join"); }
		
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
		if ( !texte.startsWith("..",new_position) ) { throw new CDSInvalideException("separateur different de .."); }
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
			auto.lire_sequence(int1, int2);
			
			return new_position; 
		}
		catch (CharInvalideException echar)
		{
			throw new CDSInvalideException("char invalide dans la sequence");
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
		int phase; //0,1 ou 2
		boolean already_started; //indique si cet automate à déjà tourné (join)
		
		//exprimé en position dans la chaine de caracteres
		int debut_sequence;
		int fin_sequence;
				
		//nucleotides en mémoire
		//si l'automate a été amorcé, il ne faut pas réinitialiser ces valeurs
		int nucleotide1;
		int nucleotide2;
		int nucleotide3;
		
		//-----
		
		Automate_lecteur_de_genes (boolean sens_de_lect)
		{
			sens_de_lecture = sens_de_lect;
			phase=0;
			already_started=false;
		}
		
		int get_phase()
		{
			return phase;
		}
		
		int get_nucleotide1()
		{
			return nucleotide1;
		}
		
		int get_nucleotide2()
		{
			return nucleotide2;
		}
		
		int get_nucleotide3()
		{
			return nucleotide3;
		}
		
		//-----
		
		void renverser_sens_de_lecture()
		{
			sens_de_lecture = !sens_de_lecture;
		}
		
		void lire_sequence(int debut, int fin) throws CharInvalideException, CDSInvalideException
		{
			if (debut>fin) { throw new CDSInvalideException("debut superieur à fin"); }
			
			debut_sequence = position_string_of_numeros_nucleotide(debut);
			fin_sequence = position_string_of_numeros_nucleotide(fin);
			
			//permet de tester le codon start sans perdre le point de depart
			int position_sequence_tampon;
			
			try { 	//vérifier que les entiers ne sortes pas du texte
			if (sens_de_lecture)
			{
				if (!already_started) //première fois que l'automate tourne
				{
					//on amorce le triplet
					already_started=true;
					lire_nucleotide_sens_directe();
					nucleotide1=nucleotide3;
					lire_nucleotide_sens_directe();
					nucleotide2=nucleotide3;
					
					//test codon start
					//on enregistre le nucleotide3 sans perturber la position de debut sequence
					position_sequence_tampon = debut_sequence;
					lire_nucleotide_sens_directe();
					debut_sequence=position_sequence_tampon;
					test_codon_start();
					
					lire_sequence_sens_directe();
				}
				else //on reprend après un join
				{
					//on sauve le triplet qui finissait la section précédente du join
					ajoute_trinucleotide();
					
					lire_sequence_sens_directe();
				}
			}
			else
			{
				if (!already_started) //première fois que l'automate tourne
				{
					//on amorce le triplet
					already_started=true;
					lire_nucleotide_sens_complement();
					nucleotide1=nucleotide3;
					lire_nucleotide_sens_complement();
					nucleotide2=nucleotide3;
					
					//test codon start
					//on enregistre le nucleotide3 sans perturber la position de fin sequence
					position_sequence_tampon = fin_sequence;
					lire_nucleotide_sens_complement();
					fin_sequence=position_sequence_tampon;
					test_codon_start();
					
					lire_sequence_sens_complement();
				}
				else  //on reprend après un join
				{
					//on sauve le triplet qui finissait la section précédente du join
					ajoute_trinucleotide();
					
					lire_sequence_sens_complement();
				}
			}
			}
			catch (StringIndexOutOfBoundsException e) { throw new CDSInvalideException("index out of bound"); }
		}
		
		//-----
		
		//on lit du début à la fin
		void lire_sequence_sens_directe() throws CharInvalideException
		{
			//importe le nucléotide situé en debut de séqence dans nucleotide3
			//et décalle l'indice de début de séquence
			lire_nucleotide_sens_directe();
			
			//si on a finit de lire la séquence, on arrete
			//on ne lit pas le dernier triplet (il sera réinjecté plus tard si nécéssaire)
			if (debut_sequence <= fin_sequence)
			{
				ajoute_trinucleotide();
				lire_sequence_sens_directe();
			}
		}
		
		//on lit de la fin au début
		void lire_sequence_sens_complement() throws CharInvalideException
		{
			//importe le nucléotide situé en fin de séqence dans nucleotide3 
			//et décalle l'indice de début de séquence
			lire_nucleotide_sens_complement();
			
			//si on a finit de lire la séquence, on arrete
			//on ne lit pas le dernier triplet (il sera réinjecté plus tard si nécéssaire)
			if (debut_sequence <= fin_sequence)
			{
				ajoute_trinucleotide();
				lire_sequence_sens_complement();
			}
		}
		
		//-----
		
		//importe le nucléotide situé en debut de séqence dans nucleotide3 et décalle l'indice de début de séquence
		//cette fonction ne devrait jamais rencontrer de chiffre
		//retourne un -1 si le caractere est invalide
		void lire_nucleotide_sens_directe()
		{
			switch(texte.charAt(debut_sequence))
			{
				case 'a' :
					nucleotide3=0; 
					debut_sequence++;
					break;
				case 'c' :
					nucleotide3=1; 
					debut_sequence++;
					break;
				case 'g' :
					nucleotide3=2; 
					debut_sequence++;
					break;
				case 't' :
					nucleotide3=3; 
					debut_sequence++;
					break;
				case ' ' :
					debut_sequence++; //on reprend la lecture après l'espace
					lire_nucleotide_sens_directe();
					break;
				case '\n' : //si on croise un passage à la ligne, on doit encore lire tout un entete de ligne soit en tout 11 char
					debut_sequence=debut_sequence+11; //on reprend la lecture après le passage à la ligne et le bloc de chiffres/espaces qui suit
					lire_nucleotide_sens_directe();
					break;
				default:
					nucleotide3=-1; 
					debut_sequence++;
					break;
			}
		}
		
		//importe le nucléotide situé en fin de séqence dans nucleotide3 et décalle l'indice de début de séquence
		//cette fonction ne devrait jamais rencontree de '\n'
		//retourne un -1 si le caractere est invalide
		void lire_nucleotide_sens_complement()
		{
			switch(texte.charAt(fin_sequence))
			{
				case 'a' :
					nucleotide3=3; //code pour 't' 
					fin_sequence--;
					break;
				case 'c' :
					nucleotide3=2; //code pour 'g' 
					fin_sequence--;
					break;
				case 'g' :
					nucleotide3=1; //code pour 'c' 
					fin_sequence--;
					break;
				case 't' :
					nucleotide3=0; //code pour 'a' 
					fin_sequence--;
					break;
				case ' ' :
					fin_sequence--; //on reprend la lecture après l'espace
					lire_nucleotide_sens_complement();
					break;
				case '0' : //si on croise un chiffre, c'est le premier d'un entete, on est donc à 10char, '\n' inclus, du reste du genome
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '1' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '2' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '3' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '4' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '5' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '6' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '7' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '8' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				case '9' :
					fin_sequence = fin_sequence-10;//on reprend la lecture après un chiffres et le bloc de chiffres/espace/passage à la ligne qui suit
					lire_nucleotide_sens_complement();
					break;
				default:
					nucleotide3=-1; 
					fin_sequence--;
					break;
			}
		}
				
		//ajoute un trinucléotide à la bdd
		//change de phase et décale chaque nucléotide
		void ajoute_trinucleotide() throws CharInvalideException
		{
			base_de_donnees.ajoute_trinucleotide(phase, nucleotide1, nucleotide2, nucleotide3);
			incrementer_phase(); 
			nucleotide1=nucleotide2;
			nucleotide2=nucleotide3;
		}
		
		//prend une position dans le génome et rend une position dans la chaine de caractères
		int position_string_of_numeros_nucleotide(int p)
		{
			//position d'origine plus taille de la première ligne +
			//1 char en tete de chaque bloc de 10 nucleotides +
			//10 char en tete de chaque ligne de 60 nucléotides
			
			int position;
			
			if (p<=10) //premier bloc
			{
				position= (origine + 22 + p);
			}
			else if (p<=60) //première ligne
			{
				position= (origine + 22 + p) + ((p-1)/10);
			}
			else //cas général
			{
				position= (origine + 22 + p) + ((p-1)/10) + 10*((p-1)/60);
			}
			
			return position;
		}
		
		
		void incrementer_phase()
		{
			phase = (phase+1)%3;
		}
		
		//cet fonction vérifie que le triplet de nucleotides en mémoire est un codon start
		void test_codon_start() throws CDSInvalideException
		{
			if (nucleotide2 != 3) //il faut un 't' au milieu du codon
			{
				throw new CDSInvalideException("codon start invalide (pas de t au centre)");
			}
			else if ( nucleotide3 == 2 ) //finit par un 'g'
			{
				if ((nucleotide1!=0)&&(nucleotide1!=1)&&(nucleotide1!=2)&&(nucleotide1!=3)) //ne commence pas par une lettre
				{
					throw new CDSInvalideException("codon start invalide (ne commence pas par un char valide ?tg)");
				}
			}
			else //ne finit pas par un 'g'
			{
				if (nucleotide3 == 0) //finit par un 'a'
				{
					if ((nucleotide1!=0)&&(nucleotide1!=3))
					{
						throw new CDSInvalideException("codon start invalide (ne commence ni par un a, ni par un t ?ta)");
					}
				} else if ((nucleotide3 == 1)||(nucleotide3 == 3)) //finit par 'c' ou 't' 
				{
					if (nucleotide1!=0) //ne commence pas par un 'a'
					{
						throw new CDSInvalideException("codon start invalide (ne comence pas par un a ?t(c|t)) ");
					}
				} else //ne finit pas par un char
				{
					throw new CDSInvalideException("codon start invalide (ne finit pas par un char)");
				}
			}
		}

	}	
	
}
