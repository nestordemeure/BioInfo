package Parser;

import java.io.OutputStream;
import java.util.ArrayList;
import Bdd.Bdd;
import exceptions.CDSInvalideException;
import exceptions.DeadCDSException;
import tree.Organism;

//un CDS est une liste de séquences
public class CDS 
{
	private ArrayList<sequence> sequence_list;
	private Bdd base_de_donnees;
	int expected_ligne_number;
	String cleft;
	Organism organism;
	int geneLength;
	OutputStream streamer;
	
	CDS(Bdd base, String clef, Organism organismArg, OutputStream stream)
	{
		sequence_list = new ArrayList<sequence>();
		expected_ligne_number=0;
		base_de_donnees=base;
		cleft = clef;
		organism = organismArg;
		streamer = stream;
		geneLength = 0;
	}

	//on coupe les ponts vers les séquences et on envois une exception pour signaler que ce cds ne sert plus
	void suicide() throws DeadCDSException
	{
		sequence_list = null;
		base_de_donnees = null;
		expected_ligne_number=-1;
		throw new DeadCDSException();
	}
	
	//permet d'ajouter une séquence à la liste
	//sort l'index de la séquence dans la liste
	int ajouter_sequence(int deb, int fi, boolean sens_de_lect)
	{
		sequence seq = new sequence(deb,fi,sens_de_lect);
		sequence_list.add(seq);
		expected_ligne_number += ((fi-1)/60) - ((deb-1)/60) +1;
		geneLength += fi - deb + 1; // TODO +1 because deb is included in the range
		return sequence_list.size()-1;
	}
	
	//ajoute la ligne à la sequence indiquée
	//déclenche la lecture de la séquence si on a toute les lignes nécéssaires
	void appendLigne(int index_sequence, String ligne) throws DeadCDSException
	{
		//on ajoute une ligne
		sequence_list.get(index_sequence).appendLigne(ligne);
		expected_ligne_number--;
		
		//on regarde si on a lut toute les lignes indiquées
		if (expected_ligne_number<=0)
		{
			try
			{
				//on s'assure que le tampon est vide avant d'attaquer
				base_de_donnees.open_tampon(geneLength,cleft,organism,streamer);
				
				//l'automate qui va parcourir cette séquence, dans le sens directe par défaut
				automateLecteurDeGenes auto = new automateLecteurDeGenes(base_de_donnees);
			
				//lecture des sequences
				for (sequence seq : sequence_list)
				{
					//char_erreur si il croise un caractère qui n'est pas un AGCT
					//CDS_erreur si int1>int2
					auto.lire_sequence(seq);
				}
			
				//on test le codon stop et la taille du CDS
				auto.test_CDS();
				base_de_donnees.close_tampon();
			}
			catch (CDSInvalideException e)
			{
				base_de_donnees.incr_nb_CDS_non_traites(cleft,organism);
			}
			
			//ce CDS ne sert plus
			suicide();
		}
	}
	
	//une séquence est un interval dans le code génétique associéee à un sens de lecture
	public class sequence 
	{
		int debut;
		int fin;
		boolean sens_de_lecture;
		StringBuilder code_genetique;

		sequence (int deb, int fi, boolean sens_de_lect)
		{
			debut = deb;
			fin = fi;
			sens_de_lecture = sens_de_lect;
			code_genetique = new StringBuilder();
		}
		
		//-----
		
		public int getDebut ()
		{
			return debut;
		}
		
		public int getFin ()
		{
			return fin;
		}
		
		public boolean getSens ()
		{
			return sens_de_lecture;
		}
		
		public char charAt (int i)
		{
			return code_genetique.charAt(i);
		}
		
		//-----
		
		//ajoute une ligne au code genetique
		public void appendLigne(String ligne)
		{
			code_genetique.append('\n'); //on conserve un caracter séparateur de lignes
			code_genetique.append(ligne);
		}
	}
}
