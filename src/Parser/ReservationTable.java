package Parser;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class ReservationTable 
{
	//table qui a un numéros de ligne associe un triplé (index de cds, index de sequence, ajout/retrait)
	TreeMap<Integer, Reservation> table = new TreeMap<Integer, Reservation>();
	
	//ajoute une réservation à la table
	void reserver(int ligne, int index_cds, int index_sequence, boolean ajout)
	{
		Reservation reserv = new Reservation(index_cds,index_sequence,ajout);
		table.put(ligne, reserv);
	}
	
	//prend un interval et ajoute un couple de réservations à la table
	void reserver_interval(int ligne_deb, int ligne_fin, int index_cds, int index_sequence)
	{
		reserver(ligne_deb,index_cds,index_sequence,true);
		reserver(ligne_fin,index_cds,index_sequence,false);
	}
	
	//rend la table pour qu'on puisse l'itérée
	//on pourrait ajouter iterator ou quelque chose de similaire pour pouvoir la parcourir sans la sortir de la classe
	Set<Entry<Integer, Reservation>> entrySet()
	{
		return table.entrySet();
	}
	
	public class Reservation
	{
		IndexesSequence indexes_sequence;
		boolean ajout; //true si il s'agit d'un ajout de sequence et false si il s'agit d'un retrait
		
		Reservation(int cds, int seq, boolean aj)
		{
			indexes_sequence = new IndexesSequence(cds,seq);
			ajout = aj;
		}
		
		IndexesSequence getIndexesSequence()
		{
			return indexes_sequence;
		}
		
		boolean getAjout()
		{
			return ajout;
		}
		
		public class IndexesSequence
		{
			int index_cds;
			int index_sequence;
			
			IndexesSequence(int cds, int seq)
			{
				index_cds=cds;
				index_sequence = seq;
			}
			
			int getIndexCds()
			{
				return index_cds;
			}
			
			int getIndexSequence()
			{
				return index_sequence;
			}
		}
	}
	


}
