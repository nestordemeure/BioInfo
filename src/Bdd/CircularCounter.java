package Bdd;

import exceptions.CDSInvalideException;

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
	
	public static int imax = 99;
	public static int minGeneLength = 200;
	
	private int ciw1w2[][][];
	private int trinucleotidesLeftToRead;
	private int geneLength;
	private CircularArray codeArray;

	//-----------------------------------------------------------------------------	
	// public methods
	
	public CircularCounter(int geneLengthArg) throws CDSInvalideException
	{
		if (geneLengthArg < minGeneLength)
		{
			throw new CDSInvalideException("gene too short");
		}
		else
		{
			geneLength = geneLengthArg - imax - 6;
			trinucleotidesLeftToRead = geneLength/3 + 1;
			ciw1w2 = new int[imax+1][4][4];
			codeArray = new CircularArray();
		}
	}
	
	// add ciw1w2 to a oiw1w2
	public void addCiw1w2(double[][][] oiw1w2)
	{
		for(int i = 0 ; i<=imax ; i++)
		{
			for(int w1 = 0 ; w1<4 ; w1++)
			{
				for(int w2 = 0 ; w2<4 ; w2++)
				{
					oiw1w2[i][w1][w2]+= 3*ciw1w2[i][w1][w2]/geneLength;
				}
			}
		}
	}
	
	public void AddTrinucleotide(int phase, int nucleotide1, int nucleotide2, int nucleotide3)
	{
		int w2 = codeOfTrinucleotide[nucleotide1][nucleotide2][nucleotide3];
				
		if (phase == 0)
		{	
			if (trinucleotidesLeftToRead>0)
			{
				trinucleotidesLeftToRead--;
				codeArray.addCode(w2);
			}
			else
			{
				codeArray.addCode();
			}
		}
		
		for (int w=codeArray.minW; w<codeArray.maxW; w++)
		{
			int w1 = codeArray.getCode(w);
			int i = phase + w*3;
			if (i <= imax)
			{
				ciw1w2[i][w1][w2]++;
			}
		}
	}
	
	//-----------------------------------------------------------------------------	
	// a circular array to store the circular codes (ironic)
	
	public class CircularArray
	{
		public int minW; // included
		public int maxW; // excluded
		public int offSet;
		private int nextCode;
		private int[] codes;
		private int codeNumber = imax/3 + 1;
		
		public CircularArray()
		{
			minW = 0;
			maxW = 0;
			offSet = 0;
			nextCode=-1;
			codes = new int[codeNumber];
		}
		
		// get the code stored at the position
		// undefined if w<minW || w>=maxW
		public int getCode(int w)
		{
			return codes[ (offSet + w)%codeNumber ];
		}
		
		public void addCode(int code)
		{
			if (nextCode>=0)
			{
				if (maxW<codeNumber)
				{
					maxW++;
				}
				
				if (offSet==0)
				{
					offSet=codeNumber-1;
				}
				else
				{
					offSet--;
				}

				codes[offSet] = nextCode;
			}
			nextCode = code;
		}
		
		public void addCode()
		{
			if (maxW<imax)
			{
				maxW++;
			}
			
			codeArray.minW++;
			
			if (offSet==0)
			{
				offSet=imax-1;
			}
			else
			{
				offSet--;
			}
		}
	}
}
