package Bdd;

public class CircularCounter 
{
	//-----------------------------------------------------------------------------
	// variables
	
	// converts a trinucleotide into its circular code
	static int[][][] codeOfTrinucleotide = new int[4][4][4];
	static // Initialization
	{
		String[] X = {"AAC", "AAT", "ACC", "ATC", "ATT", "CAG", "CTC", "CTG", "GAA", "GAC",
					  "GAG", "GAT", "GCC", "GGC", "GGT", "GTA", "GTC", "GTT", "TAC", "TTC"};
		for (String trinucleotide : X)
		{
			int tri0 = Bdd.intOfNucleotideChar( trinucleotide.charAt(0) );
			int tri1 = Bdd.intOfNucleotideChar( trinucleotide.charAt(1) );
			int tri2 = Bdd.intOfNucleotideChar( trinucleotide.charAt(2) );
			codeOfTrinucleotide[tri0][tri1][tri2] = 0; // X
			codeOfTrinucleotide[tri1][tri2][tri0] = 1; // X1
			codeOfTrinucleotide[tri2][tri0][tri1] = 2; // X2
		}
		// Xp
		codeOfTrinucleotide[0][0][0] = 3;
		codeOfTrinucleotide[1][1][1] = 3;
		codeOfTrinucleotide[2][2][2] = 3;
		codeOfTrinucleotide[3][3][3] = 3;
	}
	
	//-----
	
	private int imax;
	private int ciw1w2[][][];
	private int trinucleotidesLetf;
	
	//-----------------------------------------------------------------------------	

}
