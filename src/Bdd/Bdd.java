package Bdd;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import configuration.Configuration;
import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;
import manager.AccessManager;
import tree.Organism;

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
	public CircularCounter tampon_circularCounter;
	
	private String tampon_cleft;
	private Organism tampon_organism;
	
	private OutputStream tampon_streamer;
	private StringBuilder tampon_toStream;
		
//-----------------------------------------------------------------------------	
//fonctions publiques
		
//constructeur
	
	public Bdd ()
	{
		contenus = new TreeMap<String,content>();
	}
	
	public Bdd (String file) throws IOException
	{
		String adresse = file+".bdd";
		
		AccessManager.accessFile(adresse); //mutex
		FileInputStream chan = new FileInputStream(adresse);
		ObjectInputStream inputstream = new ObjectInputStream(chan);
		
		try 
		{
			contenus = (TreeMap<String,content>) inputstream.readObject();
			inputstream.close();
			chan.close();
		}
		catch(Exception e)
		{
			// base mal écrite : improbable
		}
		
		AccessManager.doneWithFile(adresse); //mutex
	}
	
//incrementeurs

	public void incr_nb_CDS_non_traites (String cleft, Organism organism)
	{
		content contenus_cleft = contenus.get(cleft);
		
		if (contenus_cleft == null)
		{
			contenus_cleft = new content(organism);
			contenus.put(cleft,contenus_cleft);
		}
		
		contenus_cleft.nb_CDS_non_traites++;
	}

	//tampon
	// ajoute un tri nucleotides a la phase indiquée
	public void ajoute_nucleotides (int phase, int nucleotide1, int nucleotide2, int nucleotide3) throws CharInvalideException
	{
		tampon_circularCounter.AddTrinucleotide(phase, nucleotide1, nucleotide2, nucleotide3);
		//tampon_nb_trinucleotides++;
		ecrit_nucleotideToStream(nucleotide1); // incrémente toStream pour le streamer
	}
	
	public content get_contenu(String cleft, Organism organismArg){
		
		content contenus_cleft = contenus.get(cleft);
		if (contenus_cleft == null)
		{
			contenus_cleft = new content(organismArg);
			contenus_cleft.nb_items=0;
			contenus.put(cleft,contenus_cleft);
			
		}
		return contenus_cleft;
	}

	// ajoute un nucleotide au stringbuilder qu'on feedera au streamer
	public void ecrit_nucleotideToStream(int nucleotide)
	{
		if(tampon_streamer!=null){
			char c;
			try { c = charOfNucleotideInt(nucleotide); } 
			catch (CharInvalideException e) { c='?'; }
			tampon_toStream.append(c);
		}
	}
	
//tampon

	//déplace le contenus du tampon dans la mémoire
		public void close_tampon()
		{
			// streamer le texte si le streamer est non-null
			if(tampon_streamer!=null)
			{
				byte[] bytes = tampon_toStream.append('\n').toString().getBytes();
				try { tampon_streamer.write(bytes); } 
				catch (IOException e) { System.out.println(e.getMessage()); }
				tampon_toStream = null;
			}
			
			//on apelle le contenus associé a la cleft si elle existe
			content contenus_cleft = contenus.get(tampon_cleft);
			if (contenus_cleft == null)
			{
				contenus_cleft = new content(tampon_organism);
				contenus.put(tampon_cleft,contenus_cleft);
			}
			
			//on suppose qu'on ne ferme le tampon que pour écrire un CDS
			contenus_cleft.nb_CDS++;
			
			// TODO : build oiw1w2
			tampon_circularCounter.addCiw1w2(contenus_cleft.oiw1w2);
			contenus_cleft.nb_trinucleotides += tampon_circularCounter.geneLength / 3;
			tampon_circularCounter = null; // optional, clear memory until next use of the database
		}
		
		//s'assure que le tampon est vide pour avancer
		public void open_tampon(int geneLength, String cleft, Organism organism, OutputStream streamer) throws CDSInvalideException
		{
			tampon_circularCounter = new CircularCounter(geneLength);
			tampon_cleft = cleft;
			tampon_organism = organism;
			tampon_streamer = streamer;
			tampon_toStream = new StringBuilder();
		}
	
	// ajoute le contenus de la base donnée en argument à la base actuelle
	public void fusionBase(Bdd base)
	{
		String cleft_foreign;
		content contenus_cleft_foreign;
		
		for (Entry<String, content> entry : base.contenus.entrySet())
		{
			contenus_cleft_foreign = entry.getValue();		
			cleft_foreign = entry.getKey();
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
	public Set<Map.Entry<String,content>> getContenus()
	{
		return contenus.entrySet();
	}
	
//affichage
	
	//exporte une base à l'adresse donnée
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

	//rend le nucleotide, associé à un entier, sous forme de Char
	public static int intOfNucleotideChar(char nucleotide)
	{
		switch(nucleotide)
		{
			case 'A' :
				return 0;
			case 'C' :
				return 1;
			case 'G' :
				return 2;
			case 'T' :
				return 3;
			default:
				return -1;
		}
	}

	//sort un string qui représente le contenus de la base
	public String toString ()
	{
		content contenus_cleft;
		// TODO we display only codes and not all trinucleotides
		String codes[] = {"X", "X1", "X2", "Xp"};
		String str = "";
	
		for (Entry<String, content> entry : contenus.entrySet())
		{
			contenus_cleft = entry.getValue();
			
			// header
			str += entry.getKey() + String.format(" (%d CDS) :\n", contenus_cleft.nb_CDS);
			str += "i";
			for (int w1 = 0; w1 < 4; w1++)
			{
				for (int w2 = 0; w2 < 4; w2++)
				{
					str += "	" + String.format("%s-%s", codes[w1], codes[w2]);
				}
			}
			str += "\n";
			
			// body
			for (int i = 0; i < Configuration.PARSER_IMAX; i++)
			{
				str += i;
				for (int w1 = 0; w1 < 4; w1++)
				{
					for (int w2 = 0; w2 < 4; w2++)
					{
					    // TODO we display only mod0
						str += "	" + String.format("%.3f",contenus_cleft.oiw1w2[0][i][w1][w2]);
					}
				}
				str += "\n";
			}
		}
		
		return str;
	}
	
	//contient les valeures associées a un type (mitochondrie, géne, chloroplaste, general,...)
	public class content implements Serializable
	{
		public long nb_CDS;
		public long nb_CDS_non_traites;
		public long nb_trinucleotides;
		
		public double oiw1w2[][][][]; // sum of all oiw1w2

		public long nb_items;
		public Organism organism;
		
		public content(Organism organismArg)
		{
			nb_CDS = 0;
			nb_CDS_non_traites = 0;
			nb_trinucleotides = 0;
			
			oiw1w2 = new double[4][Configuration.PARSER_IMAX][Configuration.PARSER_CODENUMBER][Configuration.PARSER_CODENUMBER];
			
			nb_items = 1;
			organism = organismArg;
		}
		
		//----------
		
		public double A(int modulo, int i, int w1, int w2)
		{
			return oiw1w2[modulo][i][w1][w2]/((double)nb_CDS);
		}
				
		//un contenus a un autre
		public void fusionContent(content cont)
		{
			for(int modulo = 0; modulo<4; modulo++)
            {
                for(int i = 0 ; i<Configuration.PARSER_IMAX ; i++)
                {
                    for(int w1 = 0 ; w1<Configuration.PARSER_CODENUMBER ; w1++)
                    {
                        for(int w2 = 0 ; w2<Configuration.PARSER_CODENUMBER ; w2++)
                        {
                            oiw1w2[modulo][i][w1][w2] += cont.oiw1w2[modulo][i][w1][w2];
                        }
                    }
                }
            }

			nb_CDS += cont.nb_CDS;
			nb_CDS_non_traites += cont.nb_CDS_non_traites;
			nb_trinucleotides += cont.nb_trinucleotides;
			nb_items += cont.nb_items;
			// no fusion for the organisms
		}
		
		//serialization
		
	   private void readObject(ObjectInputStream inputstream) throws IOException, ClassNotFoundException 
	   {
		   nb_CDS = inputstream.readLong();
		   nb_CDS_non_traites = inputstream.readLong();
		   nb_trinucleotides = inputstream.readLong();
		   oiw1w2 = (double[][][][]) inputstream.readObject();
		   organism = (Organism) inputstream.readObject();
		   nb_items = inputstream.readLong();
	   }

	   private void writeObject(ObjectOutputStream outputstream) throws IOException
	   {
			outputstream.writeLong(nb_CDS);
			outputstream.writeLong(nb_CDS_non_traites);
			outputstream.writeLong(nb_trinucleotides);
			outputstream.writeObject(oiw1w2);
			outputstream.writeObject(organism);
			outputstream.writeLong(nb_items);
	  }
	}
}
