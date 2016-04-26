package Bdd;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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

	//map qui associe un contenus a chaque cleft
	TreeMap<String,content> contenus;
	
	//tampon
	private int tampon_tableautrinucleotides[][][][]; //tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]
	private int tampon_tableaudinucleotides[][][]; //tableaudinucleotides[phase][nucleotide1][nucleotide2]
	private String tampon_cleft;
	
	boolean empty_tamp;
	
//-----------------------------------------------------------------------------	
//fonctions publiques
		
//constructeur
	
	public Bdd ()
	{
		contenus = new TreeMap<String,content>();
		
		tampon_tableautrinucleotides = new int[3][4][4][4];
		tampon_tableaudinucleotides = new int[2][4][4];
		
		empty_tamp=true;
	}
	
	public Bdd (String file) throws IOException
	{
		String adresse = file+".bdd";
		
		AccessManager.accessFile(adresse); //mutex
		FileInputStream chan = new FileInputStream(adresse);
		ObjectInputStream inputstream = new ObjectInputStream(chan);
		
		try 
		{
			contenus = (TreeMap<String,content>) inputstream.readObject(); //TODO a tester
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace(); //base mal écrite : improbable
		}

		inputstream.close();	
		AccessManager.doneWithFile(adresse); //mutex
		
		tampon_tableautrinucleotides = new int[3][4][4][4];
		tampon_tableaudinucleotides = new int[2][4][4];
		empty_tamp=true;
	}
	
//incrementeurs

	//TODO
	public void incr_nb_CDS (String cleft)
	{
		contenus.get(cleft).nb_CDS++;
	}
	
	//TODO
	public void incr_nb_CDS_non_traites (String cleft)
	{
		contenus.get(cleft).nb_CDS_non_traites++;
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

//tampon

	//déplace le contenus du tampon dans la mémoire
		public void close_tampon()
		{
			//TODO on apelle le contenus associé a la cleft si elle existe
			content contenus_cleft = contenus.get(tampon_cleft);
			if (contenus_cleft == null)
			{
				contenus_cleft = new content();
				contenus.put(tampon_cleft,contenus_cleft);
			}
			
			long valeur_tampon;
			for(int nucleotide1 = 0 ; nucleotide1<4 ; nucleotide1++)
			{
				for(int nucleotide2 = 0 ; nucleotide2<4 ; nucleotide2++)
				{
					//dinucleotide
					for(int phase = 0 ; phase<2 ; phase++)
					{
						valeur_tampon = tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2];
						
						contenus_cleft.nbDinucleotidesParPhase[phase]+=valeur_tampon;
						contenus_cleft.tableaudinucleotides[phase][nucleotide1][nucleotide2]+=valeur_tampon;
						
						tampon_tableaudinucleotides[phase][nucleotide1][nucleotide2]=0; //clear
					}
					
					//trinucleotide
					for(int nucleotide3 = 0 ; nucleotide3<4 ; nucleotide3++)
					{
						for(int phase = 0 ; phase<3 ; phase++)
						{
							valeur_tampon = tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
							
							contenus_cleft.nbTrinucleotidesParPhase[phase]+=valeur_tampon;
							contenus_cleft.tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]+=valeur_tampon;
							
							tampon_tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]=0; //clear
						}
					}
				}
			}
			
			empty_tamp=true;
		}
		
		//s'assure que le tampon est vide pour avancer
		public void open_tampon(String cleft)
		{
			if (empty_tamp)
			{
				empty_tamp=false;
			}
			else
			{
				clear_tampon();
			}
			
			tampon_cleft = cleft;
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
	public void fusionBase(Bdd base)
	{
		String cleft_foreign;
		content contenus_cleft_foreign;
		
		for (Entry<String, content> entry : base.contenus.entrySet())
		{
			//TODO ici il faut ajouter un test pour envoyer tout ce qui n'est pas mitochondrie/chloroplaste vers général
			cleft_foreign = entry.getKey();
			contenus_cleft_foreign = entry.getValue();
						
			content contenus_cleft_local = contenus.get(cleft_foreign);
			
			if (contenus_cleft_local == null)
			{
				contenus.put(cleft_foreign,contenus_cleft_foreign);
			}
			else
			{
				contenus_cleft_local.fusionContent(contenus_cleft_foreign);
			}
		}
	}
	
	//retourne le contenus de la base
	Set<Map.Entry<String,content>> getContenus()
	{
		return contenus.entrySet();
	}
	
//affichage
	
	//TODO exporte une base à l'adresse donnée
	public void exportBase(String file) throws IOException
	{
		String adresse = file+".bdd";
		
		AccessManager.accessFile(adresse); //mutex
		
		FileOutputStream chan = new FileOutputStream(adresse);
		ObjectOutputStream outputstream = new ObjectOutputStream(chan);

		outputstream.writeObject(contenus);

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
		content contenus_cleft;

		try
		{
			//TODO
			for (Entry<String, content> entry : contenus.entrySet())
			{
				contenus_cleft = entry.getValue();
				
				str += entry.getKey() + " :\n";
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
								str+="	"+contenus_cleft.tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
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
							str+="	"+contenus_cleft.tableaudinucleotides[phase][nucleotide1][nucleotide2];
						}
						
						str+="\n";
					}
				}
			}
		}
		catch (CharInvalideException e) { /* exception impossible mais néanmoins catchée*/ }
		
		return str;
	}
	
	//contient les valeures associées a un type (mitochondrie, géne, chloroplaste, general,...)
	public class content
	{
		private long nb_CDS;
		private long nb_CDS_non_traites;
		
		private long nbTrinucleotidesParPhase[]; //nb_trinucleotides_par_phase[phase]
		private long nbDinucleotidesParPhase[]; //nb_dinucleotides_par_phase[phase]
		
		private long tableautrinucleotides[][][][]; //tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]
		private long tableaudinucleotides[][][]; //tableaudinucleotides[phase][nucleotide1][nucleotide2]
		
		public content()
		{
			nb_CDS = 0;
			nb_CDS_non_traites = 0;

			nbTrinucleotidesParPhase = new long[3];
			nbDinucleotidesParPhase = new long[2];
			
			tableautrinucleotides = new long[3][4][4][4];
			tableaudinucleotides = new long[2][4][4];
		}
		
		//----------
		
		//getters (resultat final)
		public long get_nb_CDS ()
		{
			return nb_CDS;
		}
		
		//toutes phases confondues
		public long get_nb_trinucleotides ()
		{
			return (nbTrinucleotidesParPhase[0]+nbTrinucleotidesParPhase[1]+nbTrinucleotidesParPhase[2]);
		}
		
		public long get_nb_trinucleotides (int phase)
		{
			return nbTrinucleotidesParPhase[phase];
		}
		
		//toute phases confondues
		public long get_nb_dinucleotides ()
		{
			return (nbDinucleotidesParPhase[0]+nbDinucleotidesParPhase[1]);
		}
		
		public long get_nb_dinucleotides (int phase)
		{
			return nbDinucleotidesParPhase[phase];
		}
		
		public long get_nb_CDS_non_traites ()
		{
			return nb_CDS_non_traites;
		}
		
		public long get_tableautrinucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
		{
			return tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
		}
		
		public long get_tableaudinucleotides (int phase, int nucleotide1, int nucleotide2) throws CharInvalideException
		{
			return tableaudinucleotides[phase][nucleotide1][nucleotide2];
		}
		
		//----------
		
		//un contenus a un autre
		public void fusionContent(content cont)
		{
			nbDinucleotidesParPhase[0]+= cont.nbDinucleotidesParPhase[0];
			nbDinucleotidesParPhase[1]+= cont.nbDinucleotidesParPhase[1];

			nbTrinucleotidesParPhase[0]+= cont.nbTrinucleotidesParPhase[0];
			nbTrinucleotidesParPhase[1]+= cont.nbTrinucleotidesParPhase[1];
			nbTrinucleotidesParPhase[2]+= cont.nbTrinucleotidesParPhase[2];
			
			for(int nucleotide1 = 0 ; nucleotide1<4 ; nucleotide1++)
			{
				for(int nucleotide2 = 0 ; nucleotide2<4 ; nucleotide2++)
				{
					//dinucleotide
					for(int phase = 0 ; phase<2 ; phase++)
					{
						tableaudinucleotides[phase][nucleotide1][nucleotide2]+=
								cont.tableaudinucleotides[phase][nucleotide1][nucleotide2];
						
					}
					
					//trinucleotide
					for(int nucleotide3 = 0 ; nucleotide3<4 ; nucleotide3++)
					{
						for(int phase = 0 ; phase<3 ; phase++)
						{
							tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3]+=
									cont.tableautrinucleotides[phase][nucleotide1][nucleotide2][nucleotide3];
						}
					}
				}
			}
			
			nb_CDS += cont.nb_CDS;
			nb_CDS_non_traites += cont.nb_CDS_non_traites;
		}
	}
}
