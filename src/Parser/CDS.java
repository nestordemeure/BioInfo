package Parser;

import java.util.ArrayList;

//un CDS est une liste de séquences
public class CDS 
{
	private ArrayList<sequence> sequence_list;
	
	CDS()
	{
		sequence_list = new ArrayList<sequence>();
	}

	//permet d'ajouter une séquence à la liste
	void ajouter_sequence(int deb, int fi, boolean sens_de_lect)
	{
		sequence seq = new sequence(deb,fi,sens_de_lect);
		sequence_list.add(seq);
	}
	
	//une séquence est un interval dans le code génétique associéee à un sens de lecture
	public class sequence 
	{
		int debut;
		int fin;
		boolean sens_de_lecture;

		sequence (int deb, int fi, boolean sens_de_lect)
		{
			debut = deb;
			fin = fi;
			sens_de_lecture = sens_de_lect;
		}
		
		public void setDebut (int deb)
		{
			debut = deb;
		}
		
		public void setFin (int fi)
		{
			debut = fi;
		}
	}
}
