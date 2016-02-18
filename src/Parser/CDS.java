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
	//sort l'index de la séquence dans la liste
	int ajouter_sequence(int deb, int fi, boolean sens_de_lect)
	{
		sequence seq = new sequence(deb,fi,sens_de_lect);
		sequence_list.add(seq);
		return sequence_list.size()-1;
	}
	
	//ajoute la ligne à la sequence indiquée
	void appendLigne(int index_sequence, String ligne)
	{
		sequence_list.get(index_sequence).appendLigne(ligne);
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
		
		public void setDebut (int deb)
		{
			debut = deb;
		}
		
		public void setFin (int fi)
		{
			debut = fi;
		}
		
		//ajoute une ligne au code genetique
		public void appendLigne(String ligne)
		{
			code_genetique.append(ligne);
		}
	}
}
