import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.CDSInvalideException;
import exceptions.CharInvalideException;
import exceptions.NoOriginException;

public class Parser 
{
	//Variables d'instance
	private Bdd base_de_donnees;
	private String texte;
	private int origine;
	private ArrayList position_CDS;
	
	//Fonctions
	Parser (Bdd base, String txt)
	{
		base_de_donnees = base;
		texte = txt;
	}
	
	//repere l'origine du code génétique
	//etablit une liste des position des CDS
	//renvois une exception si il n'y a pas d'origine
	void trouve_origine_bdd() throws NoOriginException
	{
		//Cherche le mot clé features à partir duquel on peut trouver des mots clés "CDS"
		int deb_features = texte.indexOf("FEATURES");
		if (deb_features == -1) { throw new NoOriginException(); }
		
		//Cherche le mot clé CDS
		int deb_cds = texte.indexOf("CDS", deb_features);
		
		while (deb_cds != -1)
		{
			position_CDS.add(deb_cds);
			deb_cds = texte.indexOf("CDS", deb_cds);
		}
		
		origine = texte.indexOf("ORIGIN", (int) position_CDS.get(position_CDS.size()-1));
		if (origine == -1) { throw new NoOriginException(); }
	}
	
	//va extraire les informations relatives à un CDS dont on indique l'adresse du descripteur
	//renvois une exception si le CDS est invalide
	void lecture_de_CDS(int position) throws CDSInvalideException
	{
		//le string "CDS             " fait 16char
		int position_actuelle = position+16;
		//TODO on doit désormais se trouver sur le charactère de début de séquence
		
		//TODO catcher l'exception ou la laisser remontée ?
		automate_sequence(position_actuelle, true);
		
		base_de_donnees.push();
	}
	
	
//-------------------------------------------------------------------------
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
	 * 
	 */
	
	//match une séquence
	int automate_sequence(int position, boolean sens_de_lecture) throws CDSInvalideException
	{
		if (texte.charAt(position) == 'c')
		{
			return automate_complement(position, sens_de_lecture);
		}
		else if (texte.charAt(position) == 'j')
		{
			return automate_join(position, sens_de_lecture);
		}
		else
		{
			return automate_interval(position, sens_de_lecture);
		}
	}
	
	//match un complement qui contient une sequence
	int automate_complement(int position, boolean sens_de_lecture) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "complement("
		if ( !texte.startsWith("complement(",position) ) { throw new CDSInvalideException(); }
		
		//on lit la sequence en changeant le sens de lecture
		int new_position = automate_sequence(position+11 , !sens_de_lecture);
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException(); }
		
		return new_position+1;
	}
	
	//match un join qui contient une liste de sequence
	int automate_join(int position, boolean sens_de_lecture) throws CDSInvalideException
	{
		//on verifie qu'on est bien face au mot "join("
		if ( !texte.startsWith("join(",position) ) { throw new CDSInvalideException(); }
		
		//on lit une sequences
		int new_position = automate_sequence(position+5 , sens_de_lecture);
		
		//on lit des séquences introduites par des virgules tant qu'il y en a
		while ( texte.charAt(new_position) == ',' )
		{
			new_position = automate_sequence(new_position+1 , sens_de_lecture);
		}
		
		//on vérifie qu'on a bien un ')'
		if (texte.charAt(new_position) != ')') { throw new CDSInvalideException(); }
		
		return new_position+1;
	}

	//match un interval qui contient deux entier
	//on identifie chacun des entiers dans l'automate (faute de pouvoir sortir des triplet entier*position depuis un automate)
	//on procède ensuite à la lecture du gene associé
	int automate_interval(int position, boolean sens_de_lecture) throws CDSInvalideException
	{
		int int1=0;
		int int2=0;
		int chiffre;
		int new_position = position;
		
		//on lit le premier entier
		try { while ( true )
		{
				chiffre = chiffre_of_int(new_position);
				int1 = int1*10 + chiffre;
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
		}
		} catch (NumberFormatException eint1) {
		
		//on vérifie qu'on a bien un ".."
		if ( !texte.startsWith("..",new_position) ) { throw new CDSInvalideException(); }
		new_position = new_position+2;

		//on lit le second entier
		try { while ( true )
		{
				chiffre = chiffre_of_int(new_position);
				int2 = int2*10 + chiffre;
				new_position++; //il y avait un chiffre a la position, on avance à la position suivante
		}
		} catch (NumberFormatException eint2) {
		
		try 
		{
			//succeptible de renvoyer une erreur si il croise un caractère qui n'est pas un AGCT
			lecture_de_gene(int1, int2, sens_de_lecture);
			
			return new_position; 
		}
		catch (CharInvalideException echar)
		{
			throw new CDSInvalideException();
		} } } 
	}
	
	//match un chiffre et rend le chiffre (et pas une position comme le fond les automates)
	int chiffre_of_int(int position) throws NumberFormatException
	{
		char c = texte.charAt(position);
		int res = (int)c - (int)'0';
		
		if ((res<0)||(res>9)) { throw new NumberFormatException(); }
		
		return res ;
	}
	
//-------------------------------------------------------------------------
//fonction qui lit la sequence de gene demandee
	
	//TODO placeholder
	void lecture_de_gene(int debut, int fin, boolean sens_de_lecture) throws CharInvalideException
	{
		
	}
	
	
}
