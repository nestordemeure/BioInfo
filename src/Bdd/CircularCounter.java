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
	
	// je perd 2 trinucleotides, où ?
	public static int imax = /*3*/99; // TODO
	public static int minGeneLength = /*0*/200; // TODO
	
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
			// TODO are we losing nucleotides ?
			//System.out.println("length " + geneLengthArg);
			
			// TODO -9 instead of -3, +3 in lefttoread
			geneLength = geneLengthArg - imax - 9 /* TODO was - 6*/; // number of nucleotides minus two trinucleotides and imax
			trinucleotidesLeftToRead = (geneLength+3)/3; //TODO added +3
			ciw1w2 = new int[imax+1][4][4]; // +1 because i goes from 0 to imax included
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
					oiw1w2[i][w1][w2]+= 3.0*((double)ciw1w2[i][w1][w2])/((double)geneLength);
				}
			}
		}
	}
	
	public void AddTrinucleotide(int phase, int nucleotide1, int nucleotide2, int nucleotide3)
	{
		int w2 = codeOfTrinucleotide[nucleotide1][nucleotide2][nucleotide3];
		//System.out.println("-"); //TODO
		
		// adding a code to the array if needed
		if (phase == 0)
		{	
			codeArray.incrWs();
			
			if (trinucleotidesLeftToRead > 0)
			{
				//System.out.println("ping " + trinucleotidesLeftToRead); // TODO
				trinucleotidesLeftToRead--;
				codeArray.addCode(w2);
			}
			else
			{
				//System.out.println("ding " + trinucleotidesLeftToRead); // TODO
				codeArray.addCode();
			}
		}
		
		// incrementing ciw1w2
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
		private int[] codes;
		private int codeNumber = imax/3 /* TODO + 1*/ + 1; // +1 to store the memory
		
		public CircularArray()
		{
			minW = 0;
			maxW = -1; // -1 because the first code will go into memory
			offSet = 0;
			codes = new int[codeNumber];
		}
		
		// get the code stored at the position
		public int getCode(int w)
		{
			//System.out.println("w " + w); // TODO
			return codes[ (offSet + 1 + w)%codeNumber ]; // w=-1 would return the memory hence the +1
		}
		
		public void addCode(int code)
		{
			codes[offSet] = code;
		}
		
		public void addCode()
		{
			minW++;
		}
		
		// incr all w
		public void incrWs()
		{
			// move offset
			if (offSet==0)
			{
				offSet=codeNumber-1;
			}
			else
			{
				offSet--;
			}
			
			// move maxW if needed
			if (maxW < codeNumber-1) // -1 we don't want to hit the memory
			{
				maxW++;
			}
		}
	}
}
