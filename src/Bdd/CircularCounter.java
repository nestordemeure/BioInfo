package Bdd;

import java.util.Arrays;

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
	public int geneLength;
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
			geneLength = geneLengthArg - (imax + 3/*space to read forward*/);
			ciw1w2 = new int[imax][4][4];
			codeArray = new CircularArray();
		}
	}
	
	// add ciw1w2 to a oiw1w2
	public void addCiw1w2(double[][][] oiw1w2)
	{
		for(int i = 0 ; i<imax ; i++)
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
		int code2 = codeOfTrinucleotide[nucleotide1][nucleotide2][nucleotide3];
		
		// update the counter
		if (phase == 0)
		{				
			if (codeArray.memorisationNumber*3 < geneLength)
			{
				codeArray.incrWs(code2);
			}
			else
			{
				codeArray.incrWs();
			}
		}
		
		// incrementing ciw1w2
		codeArray.incrCiw1w2s(phase, code2);
	}
	
	//-----------------------------------------------------------------------------	
	// a circular array to store the circular codes (ironic)
	
	public class CircularArray
	{
		public int[] content;
		public int memorisationNumber;
		public int memory;
		public int offSet;
        
		// constructor
		public CircularArray()
		{
			content = new int[imax/3]; // TODO fill with -1
			Arrays.fill(content, -1);
			memorisationNumber = 0;
			memory = -1;
			offSet = 0;
		}
		
		// incr all w by decrementing the offset
		private void incrOffSet()
		{
			if (offSet==0)
			{
				offSet=content.length-1;
			}
			else
			{
				offSet--;
			}
		}
		
		// incr all w by one, get the value in memory replace it with the given value
		public void incrWs(int newMemory)
		{
			incrOffSet();
			content[offSet] = memory;
			memory = newMemory;
			memorisationNumber++;
		}
		
		// incr all w by one, get the value in memory and put -1 in memory
		public void incrWs()
		{
			incrOffSet();
			content[offSet] = memory;
			memory = -1;
		}
		
		// iterate on all non -1 elements to incr the ciw1w2
		public void incrCiw1w2s(int phase, int code2)
		{
			// before offSet
			for(int w=0; w<offSet; w++)
			{
				int code1 = content[w];
				if (code1 != -1)
				{
					int i = 3*(w + content.length - offSet) + phase;
					ciw1w2[i][code1][code2]++;
				}
			}
			// after offSet
			for(int w=offSet; w<content.length; w++)
			{
				int code1 = content[w];
				if (code1 != -1)
				{
					int i = 3*(w - offSet) + phase;
					ciw1w2[i][code1][code2]++;
				}
			}
		}

	}
}
