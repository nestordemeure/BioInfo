package Parser;

import Bdd.Bdd;
import Parser.CDS.sequence;
import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;

public class automateLecteurDeGenes 
{
	Bdd base_de_donnees;
	int phase2; //0,1
	int phase3; //0,1 ou 2
	boolean already_started; //indique si cet automate à déjà tourné (join)
	
	//exprimé en position dans la chaine de caracteres
	int debut_sequence;
	int fin_sequence;
	sequence sequence;
		
	//nucleotides en mémoire
	//si l'automate a été amorcé, il ne faut pas réinitialiser ces valeurs
	int nucleotide0;//TODO ce nucléotide n'est mit en mémoire que lors du dernier enregistrement, il permet d'éliminer le dernir dinucléotide si besoin est
	int nucleotide1;
	int nucleotide2;
	int nucleotide3;
	
	//-----
	
	automateLecteurDeGenes (Bdd base)
	{
		phase2=0;
		phase3=0;
		already_started=false;
		base_de_donnees=base;
	}

	//-----
	
	//test le codon stop
	//vérifie que la taille du cds est un multiple de trois
	//retire le dernier dinucleotide de la base si on l'a ajouté abusivement
	void test_CDS() throws CDSInvalideException
	{
		if (phase3 != 0)
		{
			//taille
			//on vérifie que la taille de la sequence est bien un multiple de trois
			throw new CDSInvalideException("taille invalide");
		}
		else if ( ! ((nucleotide1 == 3) && ((nucleotide2==0)&&((nucleotide3==2)||(nucleotide3==0))) || ((nucleotide2==2)&&(nucleotide3==0))) )
		{
			//codon stop
			//on vérifie que le dernier triplet est bien un codon stop (TAA,TAG,TGA)
			throw new CDSInvalideException("codon stop invalide " +nucleotide1+nucleotide2+nucleotide3);
		}
		else if (phase2 != 0)
		{
			try
			{
			//le dernier dinucléotide rentré doit etre un 1 -> la phase2 doit valoir 0
			//TODO ne pas prendre le dernier dinucléotide en date
			base_de_donnees.retire_nucleotides(0, nucleotide0, nucleotide1);
			}
			catch(CharInvalideException e)
			{
				throw new CDSInvalideException("char invalide");
			}
		}
			
	}
	
	void lire_sequence(sequence seq) throws CDSInvalideException
	{
		sequence=seq;
		
		debut_sequence = position_string_of_numeros_nucleotide(sequence.getDebut());
		fin_sequence = position_string_of_numeros_nucleotide(sequence.getFin());
		
		//permet de tester le codon start sans perdre le point de depart
		int position_sequence_tampon;
		
		try { 	//vérifier que les entiers ne sortes pas du texte
		if (sequence.getSens())
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
				ajoute_nucleotides();
				
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
				ajoute_nucleotides();
				
				lire_sequence_sens_complement();
			}
		}
		}
		catch (StringIndexOutOfBoundsException e) { throw new CDSInvalideException("index out of bound"); }
	}
	
	//-----

	//on lit du début à la fin
	void lire_sequence_sens_directe() throws CDSInvalideException
	{
		//importe le nucléotide situé en debut de séqence dans nucleotide3
		//et décalle l'indice de début de séquence
		nucleotide0=nucleotide1;
		lire_nucleotide_sens_directe();
		
		//si on a finit de lire la séquence, on arrete
		//on ne lit pas le dernier triplet (il sera réinjecté plus tard si nécéssaire)
		while (debut_sequence < fin_sequence)
		{
			ajoute_nucleotides();
			lire_nucleotide_sens_directe();
		}
		
		//TODO on gére le cas égal séparément pour les dinucléotides
		if (debut_sequence == fin_sequence)
		{
			nucleotide0=nucleotide1;
			ajoute_nucleotides();
			lire_nucleotide_sens_directe();
		}
	}

	//on lit de la fin au début
	void lire_sequence_sens_complement() throws CDSInvalideException
	{
		//importe le nucléotide situé en fin de séqence dans nucleotide3 
		//et décalle l'indice de début de séquence
		nucleotide0=nucleotide1;
		lire_nucleotide_sens_complement();
		
		//si on a finit de lire la séquence, on arrete
		//on ne lit pas le dernier triplet (il sera réinjecté plus tard si nécéssaire)
		while (debut_sequence < fin_sequence)
		{
			ajoute_nucleotides();
			lire_nucleotide_sens_complement();
		}
		
		//TODO on gére le cas égal séparément pour les dinucléotides
		if (debut_sequence == fin_sequence)
		{
			nucleotide0=nucleotide1;
			ajoute_nucleotides();
			lire_nucleotide_sens_complement();
		}

	}
	
	//-----
	
	//importe le nucléotide situé en debut de séqence dans nucleotide3 et décalle l'indice de début de séquence
	//cette fonction ne devrait jamais rencontrer de chiffre
	//retourne un -1 si le caractere est invalide
	void lire_nucleotide_sens_directe()
	{
		switch(sequence.charAt(debut_sequence))
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
		switch(sequence.charAt(fin_sequence))
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
	void ajoute_nucleotides() throws CDSInvalideException
	{
		try
		{
			base_de_donnees.ajoute_nucleotides(phase2,phase3, nucleotide1, nucleotide2, nucleotide3);
			incrementer_phases(); 
			nucleotide1=nucleotide2;
			nucleotide2=nucleotide3;
		}
		catch (CharInvalideException e)
		{
			throw new CDSInvalideException("char invalide");
		}
	}
	
	//prend une position dans le génome et rend une position dans la chaine de caractères
	int position_string_of_numeros_nucleotide(int position)
	{
		//position d'origine plus taille de la première ligne +
		//1 char en tete de chaque bloc de 10 nucleotides +
		//10 char en tete de chaque ligne de 60 nucléotides
		int position_relative= position - ((sequence.getDebut()-1)/60)*60;
		
		return (position_relative+10) + ((position_relative-1)/10) + 10*((position_relative-1)/60);
	}
	
	
	void incrementer_phases()
	{
		phase2 = (phase2+1)%2;
		phase3 = (phase3+1)%3;
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
