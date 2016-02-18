package Parser;

import java.util.SortedSet;
import java.util.TreeSet;

public class ReservationTable 
{
	//table qui a un numéros de ligne associe un triplé (index de cds, index de sequence, ajout/retrait)
	SortedSet<Reservation> table = new TreeSet<Reservation>();
	
	//ajoute une réservation à la table
	void reserver(int ligne, int index_cds, int index_sequence, boolean ajout)
	{
		Reservation reserv = new Reservation(ligne,index_cds,index_sequence,ajout);
		table.add(reserv); 
	}
	
	//prend un interval et ajoute un couple de réservations à la table
	void reserver_interval(int ligne_deb, int ligne_fin, int index_cds, int index_sequence)
	{
		reserver(ligne_deb,index_cds,index_sequence,true);
		reserver(ligne_fin,index_cds,index_sequence,false);
	}
	
	//rend la table pour qu'on puisse l'itérée
	//on pourrait ajouter iterator ou quelque chose de similaire pour pouvoir la parcourir sans la sortir de la classe
	SortedSet<Reservation> getTable()
	{
		return table;
	}
	
	public class Reservation implements Comparable<Object>
	{
		IndexesSequence indexes_sequence;
		int ligne;
		boolean ajout; //true si il s'agit d'un ajout de sequence et false si il s'agit d'un retrait
		
		Reservation(int lign, int cds, int seq, boolean aj)
		{
			indexes_sequence = new IndexesSequence(cds,seq);
			ajout = aj;
			ligne=lign;
		}
		
		IndexesSequence getIndexesSequence()
		{
			return indexes_sequence;
		}
		
		int getLigne()
		{
			return ligne;
		}
		
		boolean getAjout()
		{
			return ajout;
		}
		
		@Override
		public int compareTo(Object o) 
		{
		    if (o instanceof Reservation)
		    {
		    	Reservation i = (Reservation) o;
				
		    	if (ligne==i.getLigne())
		    	{
		    		if(ajout==i.getAjout())
		    		{
		    			return indexes_sequence.compareTo(i.getIndexesSequence());
		    		}
		    		else
		    		{
		    			return Boolean.compare(ajout, i.getAjout());
		    		}
		    	}
		    	else
		    	{
		    		return (ligne - i.getLigne());
		    	}
		    		
		    }
		    else
		    {
		    	return -1;
		    }
		}
		
		public class IndexesSequence implements Comparable<Object>
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
			
			@Override
			public boolean equals(Object o)
			{
			    if (o instanceof IndexesSequence)
			    {
			    	IndexesSequence i = (IndexesSequence) o;
					return (index_cds == i.index_cds) && (index_sequence == i.index_sequence);
			     }
			    else
			    {
				     return false;
			    }
			}

			@Override
			public int compareTo(Object o) 
			{
			    if (o instanceof IndexesSequence)
			    {
			    	IndexesSequence i = (IndexesSequence) o;
			    	
			    	if (index_cds == i.index_cds)
			    	{
			    		return (index_sequence - i.index_sequence);
			    	}
			    	else
			    	{
			    		return (index_cds - i.index_cds);
			    	}
			    }
			    else
			    {
			    	return -1;
			    }
			}
		}
	}
}
