package Bdd;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import exceptions.CharInvalideException;
import manager.AccessManager;

public class Bdd 
{
/*
 * s'utilise en faisant open_tampon, remplissant la base puis close_tampon si tout c'est bien passé
 * (on ne fait rien de spécial sinon)
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
		
	private int nb_CDS;
	private int nb_CDS_non_traites;
	
	private int nbTrinucleotidesParPhase[]; //nb_trinucleotides_par_phase[phase]
	private int nbDinucleotidesParPhase[]; //nb_dinucleotides_par_phase[phase]
	
	private int tableautrinucleotides[][][][]; //tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]
	private int tableaudinucleotides[][][]; //tableaudinucleotides[phase][nucleotide1][nucleotide2]
	
	//tampon
	private int tampon_tableautrinucleotides[][][][]; //tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]
	private int tampon_tableaudinucleotides[][][]; //tableaudinucleotides[phase][nucleotide1][nucleotide2]
	
	boolean empty_tamp;
	
//-----------------------------------------------------------------------------	
//fonctions publiques
		
//constructeur
	
	//TODO WARNING les utilisateur de la base doivent cesser de lui donner un argument
	public Bdd ()
	{
		nb_CDS = 0;
		nb_CDS_non_traites = 0;

		nbTrinucleotidesParPhase = new int[3];
		nbDinucleotidesParPhase = new int[2];
		
		tableautrinucleotides = new int[3][4][4][4];
		tampon_tableautrinucleotides = new int[3][4][4][4];
		tableaudinucleotides = new int[2][4][4];
		tampon_tableaudinucleotides = new int[2][4][4];
		
		empty_tamp=true;
	}
	
	//TODO importe la base située à l'adresse donnée
	public Bdd (String file) throws IOException
	{
		String adresse = file+".bdd";
		
		AccessManager.accessFile(adresse); //mutex
		FileInputStream chan = new FileInputStream(adresse);
		ObjectInputStream inputstream = new ObjectInputStream(chan);

		nb_CDS = inputstream.readInt();
		nb_CDS_non_traites = inputstream.readInt();
		
		try 
		{
			nbTrinucleotidesParPhase = (int[]) inputstream.readObject();
			nbDinucleotidesParPhase = (int[]) inputstream.readObject();
			tableautrinucleotides = (int[][][][]) inputstream.readObject();
			tableaudinucleotides = (int[][][]) inputstream.readObject();		
		} 
		catch (ClassNotFoundException e) //la base en mémoire n'est pas correctement écrite : improbable
		{
			e.printStackTrace();
		}

		inputstream.close();	
		AccessManager.doneWithFile(adresse); //mutex
		
		tampon_tableautrinucleotides = new int[3][4][4][4];
		tampon_tableaudinucleotides = new int[2][4][4];
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
		tampon_tableautrinucleotides[phase3][nucleotide1][nucleotide2][nucleotide3]++;
		tampon_tableaudinucleotides[phase2][nucleotide1][nucleotide2]++;
	}
	
	//ajoute un tri nucleotides a la phase indiquée
	public void ajoute_nucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]++;
	}
	
	//ajoute un dinucleotide à la phase indiquée
	public void ajoute_nucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2]++;
	}
	
	//retire un dinucleotide à la phase indiquée
	public void retire_nucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2]--;
	}
	
	//unsafe (cf Excel)
	//ces fonctions s'utilises sans avoir besoin d'ouvrir ou fermer la base mais n'ont pas la sécuritée anti-exceptions du tampon
	
	//ajoute un dinucleotide et un tri nucleotides au phases indiquées
	public void ajoute_nucleotides_unsafe (int phase2, int phase3, int nucleotide1, int nucleotide2, int nucleotide3, int nbrebucléotides) throws CharInvalideException
	{
		tableautrinucleotides[phase3][nucleotide1][nucleotide2][nucleotide3]+=nbrebucléotides;
		tableaudinucleotides[phase2][nucleotide1][nucleotide2]+=nbrebucléotides;
		nbTrinucleotidesParPhase[phase3]+=nbrebucléotides;
		nbDinucleotidesParPhase[phase2]+=nbrebucléotides;
	}
	
	//ajoute un tri nucleotides a la phase indiquée
	public void ajoute_nucleotides_unsafe (int phase, int nucleotide1, int nucleotide2, int nucleotide3, int nbrebucléotides) throws CharInvalideException
	{
		tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]+=nbrebucléotides;
		nbTrinucleotidesParPhase[phase]+=nbrebucléotides;
	}
	
	//ajoute un dinucleotide à la phase indiquée
	public void ajoute_nucleotides_unsafe (int phase, int nucleotide1, int nucleotide2, int nbrebucléotides) throws CharInvalideException
	{
		tableaudinucleotides[phase][nucleotide1][nucleotide2]+=nbrebucléotides;
		nbDinucleotidesParPhase[phase]+=nbrebucléotides;
	}
	
//getters (resultat final)
	public int get_nb_CDS ()
	{
		return nb_CDS;
	}
	
	//toutes phases confondues
	public int get_nb_trinucleotides ()
	{
		return (nbTrinucleotidesParPhase[0]+nbTrinucleotidesParPhase[1]+nbTrinucleotidesParPhase[2]);
	}
	
	public int get_nb_trinucleotides (int phase)
	{
		return nbTrinucleotidesParPhase[phase];
	}
	
	//toute phases confondues
	public int get_nb_dinucleotides ()
	{
		return (nbDinucleotidesParPhase[0]+nbDinucleotidesParPhase[1]);
	}
	
	public int get_nb_dinucleotides (int phase)
	{
		return nbDinucleotidesParPhase[phase];
	}
	
	public int get_nb_CDS_non_traites ()
	{
		return nb_CDS_non_traites;
	}
	
	public int get_tableautrinucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		return 	tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
	}
	
	public int get_tableaudinucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
	{
		return tableaudinucleotides[phase][nucleotide1][nucleotide2];
	}

//tampon

	//déplace le contenus du tampon dans la mémoire
		public void close_tampon()
		{
			int valeur_tampon;
			
			for(int nucleotide1 = 0 ; nucleotide1<4 ; nucleotide1++)
			{
				for(int nucleotide2 = 0 ; nucleotide2<4 ; nucleotide2++)
				{
					//dinucleotide
					for(int phase = 0 ; phase<2 ; phase++)
					{
						valeur_tampon = tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2];
						
						nbDinucleotidesParPhase[phase]+=valeur_tampon;
						tableaudinucleotides[phase][nucleotide1][nucleotide2]+=valeur_tampon;
						
						tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2]=0; //clear
					}
					
					//trinucleotide
					for(int nucleotide3 = 0 ; nucleotide3<4 ; nucleotide3++)
					{
						for(int phase = 0 ; phase<3 ; phase++)
						{
							valeur_tampon = tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
							
							nbTrinucleotidesParPhase[phase]+=valeur_tampon;
							tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]+=valeur_tampon;
							
							tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]=0; //clear
						}
					}
				}
			}
			
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
		for(int nucleotide1 = 0 ; nucleotide1<4 ; nucleotide1++)
		{
			for(int nucleotide2 = 0 ; nucleotide2<4 ; nucleotide2++)
			{
				tampon_tableaudinucleotides[0][nucleotide1][nucleotide2]=0;
				tampon_tableaudinucleotides[1][nucleotide1][nucleotide2]=0;

				for(int nucleotide3 = 0 ; nucleotide3<4 ; nucleotide3++)
				{
					tampon_tableautrinucleotides[0][nucleotide1][nucleotide2][nucleotide3]=0;
					tampon_tableautrinucleotides[1][nucleotide1][nucleotide2][nucleotide3]=0;
					tampon_tableautrinucleotides[2][nucleotide1][nucleotide2][nucleotide3]=0;
				}
			}
		}
	}
	
	//ajoute le contenus de la base donnée en argument à la base actuelle
	void fusionBase(Bdd base)
	{
		int valeur_interm;
		
		for(int nucleotide1 = 0 ; nucleotide1<4 ; nucleotide1++)
		{
			for(int nucleotide2 = 0 ; nucleotide2<4 ; nucleotide2++)
			{
				//dinucleotide
				for(int phase = 0 ; phase<2 ; phase++)
				{
					valeur_interm = base.tableaudinucleotides[phase][nucleotide1][nucleotide2];
					
					nbDinucleotidesParPhase[phase]+=valeur_interm;
					tableaudinucleotides[phase][nucleotide1][nucleotide2]+=valeur_interm;
				}
				
				//trinucleotide
				for(int nucleotide3 = 0 ; nucleotide3<4 ; nucleotide3++)
				{
					for(int phase = 0 ; phase<3 ; phase++)
					{
						valeur_interm = base.tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
						
						nbTrinucleotidesParPhase[phase]+=valeur_interm;
						tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]+=valeur_interm;
					}
				}
			}
		}
		
		nb_CDS += base.nb_CDS;
		nb_CDS_non_traites += base.nb_CDS_non_traites;
	}
	
//affichage
	
	//TODO exporte une base à l'adresse donnée
	public void exportBase(String file) throws IOException
	{
		String adresse = file+".bdd";
		
		AccessManager.accessFile(adresse); //mutex
		
		FileOutputStream chan = new FileOutputStream(adresse);
		ObjectOutputStream outputstream = new ObjectOutputStream(chan);

		outputstream.writeInt(nb_CDS);
		outputstream.writeInt(nb_CDS_non_traites);
		outputstream.writeObject(nbTrinucleotidesParPhase);
		outputstream.writeObject(nbDinucleotidesParPhase);
		outputstream.writeObject(tableautrinucleotides);
		outputstream.writeObject(tableaudinucleotides);

		outputstream.close();
		
		AccessManager.doneWithFile(adresse); //mutex
	}
	
	//rend le nucleotide, associé à un entier, sous forme de Char
	public static char charOfNucleotideInt(int nucleotide) throws CharInvalideException
	{
		switch(nucleotide)
		{
			case 0 :
				return 'A';
			case 1 :
				return 'C';
			case 2 :
				return 'G';
			case 3 :
				return 'T';
			default:
				throw new CharInvalideException() ;
		}
	}
	
	//sort un string qui représente le profil du tableau de trinucleotides
	public String get_tableauxnucleotides_string ()
	{
		String str = "";
		StringBuilder triplet = new StringBuilder("---");

		try
		{
			str += "trinucleotide	phase1	phase2	phase3\n";
			for (int nucleotide1=0 ; nucleotide1<4 ; nucleotide1++)
			{
				triplet.setCharAt(0, charOfNucleotideInt(nucleotide1));
				
				for (int nucleotide2=0 ; nucleotide2<4 ; nucleotide2++)
				{
					triplet.setCharAt(1, charOfNucleotideInt(nucleotide2));
					
					for (int nucleotide3=0 ; nucleotide3<4 ; nucleotide3++)
					{
						triplet.setCharAt(2, charOfNucleotideInt(nucleotide3));

						str+="	"+triplet+" :";
						
						for (int phase=0 ; phase<3 ; phase++)
						{
							str+="	"+tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
						}
						
						str+="\n";
					}
				}
			}

			
			str += "dinucleotide	phase1	phase2\n";
			
			triplet.setCharAt(2, ' ');
			for (int nucleotide1=0 ; nucleotide1<4 ; nucleotide1++)
			{
				triplet.setCharAt(0, charOfNucleotideInt(nucleotide1));
				
				for (int nucleotide2=0 ; nucleotide2<4 ; nucleotide2++)
				{
					triplet.setCharAt(1, charOfNucleotideInt(nucleotide2));
					str+="	"+triplet+" :";
					
					for (int phase=0 ; phase<2 ; phase++)
					{
						str+="	"+tableaudinucleotides[phase][nucleotide1][nucleotide2];
					}
					
					str+="\n";
				}
			}
		}
		catch (CharInvalideException e) { /* exception impossible mais néanmoins catchée*/ }
		
		return str;
	}
}
