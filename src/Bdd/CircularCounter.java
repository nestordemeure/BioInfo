package Bdd;

import java.util.Arrays;

import configuration.Configuration;
import exceptions.CDSInvalideException;

public class CircularCounter 
{
	//-----------------------------------------------------------------------------
	// variables

    // converts a trinucleotide into its circular code
    private int[][][] codeOfTrinucleotide;
    static int[][][] codeOfTrinucleotide4 = new int[4][4][4];
    static int[][][] codeOfTrinucleotide16 = new int[4][4][4];
    static
    {
        // code number = 16
        for(int n1 = 0; n1<4; n1++)
        {
            for(int n2 = 0; n2<4; n2++)
            {
                for(int n3 = 0; n3<4; n3++)
                {
                    codeOfTrinucleotide16[n1][n2][n3] = n1*16 + n2*4 + n3;
                }
            }
        }

        // code number = 4
        String[] X = {"AAC", "AAT", "ACC", "ATC", "ATT", "CAG", "CTC", "CTG", "GAA", "GAC",
                "GAG", "GAT", "GCC", "GGC", "GGT", "GTA", "GTC", "GTT", "TAC", "TTC"};
        for (String trinucleotide : X)
        {
            int tri0 = Bdd.intOfNucleotideChar( trinucleotide.charAt(0) );
            int tri1 = Bdd.intOfNucleotideChar( trinucleotide.charAt(1) );
            int tri2 = Bdd.intOfNucleotideChar( trinucleotide.charAt(2) );
            codeOfTrinucleotide4[tri0][tri1][tri2] = 0; // X
            codeOfTrinucleotide4[tri1][tri2][tri0] = 1; // X1
            codeOfTrinucleotide4[tri2][tri0][tri1] = 2; // X2
        }
        // Xp
        codeOfTrinucleotide4[0][0][0] = 3;
        codeOfTrinucleotide4[1][1][1] = 3;
        codeOfTrinucleotide4[2][2][2] = 3;
        codeOfTrinucleotide4[3][3][3] = 3;
    }

	private int ciw1w2[][][][];
	public int geneLength;
	private CircularArray codeArray;

	//-----------------------------------------------------------------------------	
	// public methods
	
	public CircularCounter(int geneLengthArg) throws CDSInvalideException
	{
		if (geneLengthArg < Configuration.PARSER_MINGENELENGTH || geneLengthArg > Configuration.PARSER_MAXGENELENGTH)
		{
			throw new CDSInvalideException("invalid gene length");
		}
		else
		{
			geneLength = geneLengthArg - (Configuration.PARSER_IMAX + 3/*space to read forward*/) - 3 /*to equalize between mod0,1and2*/;
			ciw1w2 = new int[3][Configuration.PARSER_IMAX][Configuration.PARSER_CODENUMBER][Configuration.PARSER_CODENUMBER];
			codeArray = new CircularArray();

			if (Configuration.PARSER_USEFULLALPHABET)
            {
                codeOfTrinucleotide = codeOfTrinucleotide16;
            }
            else
            {
                codeOfTrinucleotide = codeOfTrinucleotide4;
            }
		}
	}
	
	// add ciw1w2 to a oiw1w2
	public void addCiw1w2(double[][][][] oiw1w2)
	{
        for(int i = 0 ; i<Configuration.PARSER_IMAX ; i++)
        {
            for(int w1 = 0 ; w1<Configuration.PARSER_CODENUMBER ; w1++)
            {
                for(int w2 = 0 ; w2<Configuration.PARSER_CODENUMBER ; w2++)
                {
                    int mod1 = 0;
                    for(int modulo = 0; modulo < 3; modulo++)
                    {
                        mod1 += ciw1w2[modulo][i][w1][w2];
                        oiw1w2[modulo][i][w1][w2] += 3.0*((double)ciw1w2[modulo][i][w1][w2])/((double)geneLength);
                    }
                    oiw1w2[3][i][w1][w2] += ((double)mod1)/((double)geneLength);
                }
            }
        }
    }
	
	public void AddTrinucleotide(int phase, int nucleotide1, int nucleotide2, int nucleotide3)
	{
		int code2 = codeOfTrinucleotide[nucleotide1][nucleotide2][nucleotide3];
		
		// update the counter
        if (codeArray.memorisationNumber < geneLength)
        {
            codeArray.incrWs(code2);
        }
        else
        {
            codeArray.incrWs();
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
        public int memory1;
        public int memory2;
        public int memory3;
		public int offSet;
        
		// constructor
		public CircularArray()
		{
			content = new int[Configuration.PARSER_IMAX];
			Arrays.fill(content, -1);
			memorisationNumber = 0;
			memory1 = -1;
            memory2 = -1;
            memory3 = -1;
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
			content[offSet] = memory3;
			memory3 = memory2;
			memory2 = memory1;
			memory1 = newMemory;
			memorisationNumber++;
		}
		
		// incr all w by one, get the value in memory and put -1 in memory
		public void incrWs()
		{
			incrOffSet();
			content[offSet] = memory3;
            memory3 = memory2;
            memory2 = memory1;
            memory1 = -1;
		}
		
		// iterate on all non -1 elements to incr the ciw1w2
		public void incrCiw1w2s(int phase, int code2)
		{
			// before offSet
			for(int w = 0; w < offSet; w++)
			{
				int code1 = content[w];
				if (code1 != -1)
				{
					int i = w + content.length - offSet;
                    int modulo = (phase - (i % 3 ) + 3) % 3;
					ciw1w2[modulo][i][code1][code2]++;
				}
			}
			// after offSet
			for(int w = offSet; w < content.length; w++)
			{
				int code1 = content[w];
				if (code1 != -1)
				{
					int i = w - offSet;
                    int modulo = (phase - (i % 3 ) + 3) % 3;
					ciw1w2[modulo][i][code1][code2]++;
				}
			}
		}

	}
}
