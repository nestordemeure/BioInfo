package Parser;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Bdd.Bdd;
import Parser.ReservationTable.Reservation;
import Parser.ReservationTable.Reservation.IndexesSequence;
import exceptions.CDSInvalideException;
import exceptions.DeadCDSException;
import exceptions.NoOriginException;
import exceptions.ScannerNullException;

public class Parser 
{
	private Bdd base_de_donnees;
	private Scanner scanner;
	private ArrayList<CDS> CDS_list;
	private ReservationTable table_des_reservations;
	String cleft;
	
	public Parser (Bdd base, Scanner scan)
	{
		base_de_donnees = base;
		scanner = scan;
	}

	//fonction qui fait tourner le parseur
	public void parse() throws ScannerNullException
	{
		while ( scanner.hasNext() )
		{
			//on réinitialise le systhème de réservation
			CDS_list = new ArrayList<CDS>();
			table_des_reservations = new ReservationTable();
			cleft = "Général";
			
			try 
			{
				parser_entete();
				parser_genome();
			} 
			catch (NoOriginException eorig) 
			{ 
				/*en l'absence d'origine dans un fichier, on n'en fait rien*/
			}
		}
	}
	
	public void parse(int filenum /* nombre de fichiers aglomérées*/ ) throws ScannerNullException
	{
		for ( int i=1 ; i <= filenum ; i++ )
		{
			if (scanner.hasNext())
			{
				//on réinitialise le systhème de réservation
				CDS_list = new ArrayList<CDS>();
				table_des_reservations = new ReservationTable();
				cleft = "Général";
				
				try 
				{
					parser_entete();
					parser_genome();
				} 
				catch (NoOriginException eorig) 
				{ 
					/*en l'absence d'origine dans un fichier, on n'en fait rien*/
				}
			}
		}
	}

//--------------------------------------------------------------------------
//lire l'entete
	
	void parser_entete() throws NoOriginException, ScannerNullException
	{
		try
		{
			//on se place dans la catégorie features
			trouverPrefix("FEATURES");
			
			/*
			 * on parcours l'entete
			 * si on croise ORIGIN, on s'arrete
			 * si on croise un CDS, on l'ajoute et on repars
			 * sinon, on continue
			 */
			boolean recherche_en_cour = true;
			while (recherche_en_cour)
			{
				importAndCheckNull(); //succeptible de renvoyer une exception qu'on va catcher
				
				if (ligne_actuelle.startsWith("//")) //on est arrivé au bout du fichier sans succès
				{
					throw new NoOriginException("no origin");
				}
				else if (ligne_actuelle.startsWith("ORIGIN")) //on a finit
				{
					recherche_en_cour=false;
				}
				else if (ligne_actuelle.startsWith("CDS",5)) //on a un CDS (5 espaces après le début de la ligne)
				{
					parser_descripteur_CDS();
				}
				else if (ligne_actuelle.startsWith("organelle=",22)) //TODO
				{
					if (ligne_actuelle.startsWith("mitochondrion",33))
					{
						cleft = "mitochondrion";
					}
					else if (ligne_actuelle.startsWith("chloroplast",41))
					{
						cleft = "chloroplast";
					}
				}
				else if (ligne_actuelle.startsWith("chromosome=",22)) //TODO
				{
					//'chromosome="11"' par exemple
					cleft = ligne_actuelle.substring(22);
				}
			}
		}
		catch (NoSuchElementException e)
		{
			throw new NoOriginException();
		}
	}
		
	//prend une ligne contenant un CDS en entrée et l'ajoute à la liste de CDS en le parsant
	void parser_descripteur_CDS() throws ScannerNullException
	{		
		CDS cds = new CDS(base_de_donnees,cleft);
		try 
		{
			table_des_reservations.open(); //on indique qu'on va passer de nouvelles réservations
			automate_sequence(21, true, cds); //la description du CDS commence 21char après le début de la ligne
			CDS_list.add(cds);
			table_des_reservations.close(); //on officialise les réservations passées
		} 
		catch (CDSInvalideException e) 
		{
			base_de_donnees.incr_nb_CDS_non_traites("General"); //TODO mettre la bonne cleft
		}
	}
	
//--------------------------------------------------------------------------
//lire le code génétique
	
	void parser_genome() throws ScannerNullException
	{
		int ligne_cible;
		int ligne_actuelle = -1;//numeros de la dernière ligne consommée par le scanner
		ArrayList<IndexesSequence> indexesSequenceList = new ArrayList<IndexesSequence>();
				
		//on parcours la liste des numéros de début et de fin de séquence par numéros de ligne croissant
		for (Reservation reservation : table_des_reservations.getTable()) 
		{
			//on doit ateindre la ligne indiquée avec la liste de sequences actuelles
			ligne_cible = reservation.getLigne();
			ligne_actuelle=distribuer_lignes(ligne_actuelle,ligne_cible,indexesSequenceList);

			//on modifie la liste de séquences
			if (reservation.getAjout()) //on doit ajouter la sequence à la liste de sequence en cours
			{
				indexesSequenceList.add(reservation.getIndexesSequence());
			}
			else //on doit retirer la sequence de la liste en cours
			{
				indexesSequenceList.remove(reservation.getIndexesSequence());
			}
		}
	}
	
//--------------------------------------------------------------------------
//fonctions pour le scanner
	
	//la dernière ligne importée par le scanner
	private String ligne_actuelle = new String();
	
	//fait avancer un scanner jusqu'a atteindre le préfixe donné
	//consomme la ligne qui contient le préfixe
	//renvois une exception si on ne peux pas lire l'élément suivant
	void trouverPrefix(String prefix) throws NoSuchElementException, ScannerNullException, NoOriginException
	{
		do
		{
			importAndCheckNull();
			
			if (ligne_actuelle.startsWith("//"))
			{
				throw new NoOriginException();
			}
		}
		while(!ligne_actuelle.startsWith(prefix));
	}
		
	//renvois une exception si le scanneur a retourner un null au lieu d'un string
	void importAndCheckNull() throws ScannerNullException
	{
		String str = scanner.next();
		
		if (str == null)
		{
			throw new ScannerNullException();
		}
		else
		{
			ligne_actuelle = str;
		}
	}
	
	//lit des lignes et les distribues au séquences listées
	//retourne le nouveau numéros de ligne actuelle (dernière ligne consommée en date)
	int distribuer_lignes(int num_ligne_actuelle, int num_ligne_cible, ArrayList<IndexesSequence> indexesSequenceList) throws ScannerNullException
	{
		while (num_ligne_actuelle<num_ligne_cible) //on n'a pas encore consommé la ligne cible
		{
			importAndCheckNull();
			
			//on ajoute la ligne lue à toute les séquences qu'elle interesse
			for(IndexesSequence i : indexesSequenceList)
			{
				try
				{
					CDS_list.get(i.getIndexCds()).appendLigne(i.getIndexSequence(),ligne_actuelle);
				}
				catch (DeadCDSException e) //le cds ne sert plus
				{
					CDS_list.set(i.getIndexCds(),null);
				}
			}
			
			num_ligne_actuelle++;
		}
		
		return num_ligne_actuelle;
	}
	
	//convertit une position dans le génome en son numéros de ligne
	int positionToLigne(int position)
	{
		return (position-1)/60;
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
	int automate_sequence(int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException, ScannerNullException
	{
		try
		{
			if (ligne_actuelle.charAt(position) == 'c') //complement
			{
				return automate_complement(position, sens_de_lecture, cds);
			}
			else if (ligne_actuelle.charAt(position) == 'j') //joint
			{
				return automate_join(position, sens_de_lecture, cds);
			}
			else
			{
				return automate_interval(position, sens_de_lecture, cds); //interval simple
			}
		}
		catch ( StringIndexOutOfBoundsException e) //fin de la ligne
		{
			//le CDS continue peut-etre à la ligne suivante, il faut l'importer et la traiter
			importAndCheckNull();
			int new_position =21;
			
			return automate_sequence(new_position, sens_de_lecture, cds);
		}
	}
		
	//match un complement qui contient une sequence
	int automate_complement(int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException, ScannerNullException
	{
		//on verifie qu'on est bien face au mot "complement("
		if ( !ligne_actuelle.startsWith("complement(",position) ) 
		{
			throw new CDSInvalideException("token complement mal redige");
		}
		
		//on lit la sequence en changeant le sens de lecture
		int new_position = automate_sequence(position+11 , !sens_de_lecture, cds);
			
		//on vérifie qu'on a bien un ')'
		if (ligne_actuelle.charAt(new_position) != ')') 
		{
			throw new CDSInvalideException("parenthese non fermee sur un complement");
		}
			
		return new_position+1;
	}
		
	//match un join qui contient une liste de sequence
	int automate_join(int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException, ScannerNullException
	{
		//on verifie qu'on est bien face au mot "join("
		if ( !ligne_actuelle.startsWith("join(",position) ) 
		{
			throw new CDSInvalideException("token join mal redige");
		}
		
		//on lit une sequences
		int new_position = automate_sequence(position+5, sens_de_lecture, cds);
		
		//on lit des séquences introduites par des virgules tant qu'il y en a
		while ( ligne_actuelle.charAt(new_position) == ',' )
		{
			new_position = automate_sequence(new_position+1, sens_de_lecture, cds);
		}
		
		//on vérifie qu'on a bien un ')'
		if (ligne_actuelle.charAt(new_position) != ')')
		{
			throw new CDSInvalideException("parenthese mal fermee sur un join");
		}
		
		return new_position+1;
	}

	//match un interval qui contient deux entier
	//on identifie chacun des entiers dans l'automate (faute de pouvoir sortir des triplet entier*position depuis un automate)
	int automate_interval(int position, boolean sens_de_lecture, CDS cds) throws CDSInvalideException
	{
		int debut=0;
		int fin=0;
		int new_position = position;
		int index_cds=CDS_list.size(); //l'index du cds auquel appartiendra cette séquence (si elle est validée, cds_list sera nicrémenté)
		int index_sequence; //l'index de la séquence dans la liste de séquences du cds
			
		//on lit le premier entier
		try { 
			while ( true )
			{
				debut = debut*10 + chiffre_of_int(ligne_actuelle, new_position);
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
			}
		} 
		catch (NumberFormatException eint1) 
		{
			//on vérifie qu'on a bien un ".."
			if ( !ligne_actuelle.startsWith("..",new_position) )
			{
				throw new CDSInvalideException("separateur different de ..");
			}
			new_position = new_position+2;

			//on lit le second entier
			try 
			{ 
				while ( true )
				{
					fin = fin*10 + chiffre_of_int(ligne_actuelle, new_position);
					new_position++; //il y avait un chiffre a la position, on avance à la position suivante
				}
			} 
			catch (NumberFormatException eint2) 
			{
				if (debut>fin) 
				{ 
					throw new CDSInvalideException("debut superieur à fin");
				}
				else
				{
					index_sequence=cds.ajouter_sequence(debut,fin,sens_de_lecture);
					//on passe une réservation qui commence juste avant le début de la séquence et juste avant qu'elle ai disparue
					table_des_reservations.reserver_interval(positionToLigne(debut)-1, positionToLigne(fin), index_cds, index_sequence);
					
					return new_position; 
				}
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
