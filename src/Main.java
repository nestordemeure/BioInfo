import exceptions.CharInvalideException;
import ui.UIManager;


public class Main {
	public static void main(String[] args) throws CharInvalideException {
		UIManager.log("Hello World");
		UIManager.setProgress(0.);
		UIManager.setProgress(50.2);
		UIManager.setProgress(100);
		UIManager.log("DONE !");
		
		/*
		 * correspondance nucleotide/entier :
		 * a = 0
		 * c = 1
		 * g = 2
		 * t = 3
		 */
		
		Bdd base = new Bdd("cheminee");
		/*
		base.ajoute_trinucleotide(0, 0, 0, 0);
		int nbr_000 = base.get_tableautrinucleotides(0, 0, 0, 0);
		int nbr_tri = base.get_nb_trinucleotides();
		System.out.println("nbr_tri "+nbr_tri);
		System.out.println("nbr_000 "+nbr_000);
				
		base.push_tampon();
		nbr_000 = base.get_tableautrinucleotides(0, 0, 0, 0);
		nbr_tri = base.get_nb_trinucleotides();
		System.out.println("nbr_tri "+nbr_tri);
		System.out.println("nbr_000 "+nbr_000);
		
		base.clear_tampon();
		nbr_tri = base.get_nb_trinucleotides();
		System.out.println("nbr_tri "+nbr_tri);
		nbr_000 = base.get_tableautrinucleotides(0, 0, 0, 0);
		System.out.println("nbr_000 "+nbr_000);
		
		String tri = base.int_to_trinucleotide(base.position_of_trinucleotide(1, 0, 2));
		System.out.println(tri); */
		
		String str = "LOCUS       NC_010314               1090 bp ss-DNA     circular VRL 20-OCT-2015\nDEFINITION  Abaca bunchy top virus DNA-N, complete genome.\nACCESSION   NC_010314\nVERSION     NC_010314.1  GI:167006425\nDBLINK      BioProject: PRJNA28697\nKEYWORDS    RefSeq.\nSOURCE      Abaca bunchy top virus (ABTV)\n  ORGANISM  Abaca bunchy top virus\n            Viruses; ssDNA viruses; Nanoviridae; Babuvirus.\nREFERENCE   1  (bases 1 to 1090)\n  AUTHORS   Sharman,M., Thomas,J.E., Skabo,S. and Holton,T.A.\n  TITLE     Abaca bunchy top virus, a new member of the genus Babuvirus (family\n            Nanoviridae)\n  JOURNAL   Arch. Virol. 153 (1), 135-147 (2008)\n   PUBMED   17978886\nREFERENCE   2  (bases 1 to 1090)\n  CONSRTM   NCBI Genome Project\n  TITLE     Direct Submission\n  JOURNAL   Submitted (06-FEB-2008) National Center for Biotechnology\n            Information, NIH, Bethesda, MD 20894, USA\nREFERENCE   3  (bases 1 to 1090)\n  AUTHORS   Sharman,M., Thomas,J.E. and Skabo,S.\n  TITLE     Direct Submission\n  JOURNAL   Submitted (11-APR-2007) Department of Primary Industries and\n            Fisheries, Queensland Government, Plant Pathology Building, 80\n            Meiers Road, Indooroopilly, Brisbane, Queensland 4068, Australia\nCOMMENT     PROVISIONAL REFSEQ: This record has not yet been subject to final\n            NCBI review. The reference sequence is identical to EF546808.\n            COMPLETENESS: full length.\nFEATURES             Location/Qualifiers\n     source          1..1090\n                     /organism=Abaca bunchy top virus\n                     /mol_type=genomic DNA\n                     /isolate=Q767\n                     /host=Musa sp.\n                     /db_xref=taxon:438782\n                     /segment=DNA N\n                     /country=Malaysia\n                     /acronym=ABTV\n     gene            236..700\n                     /locus_tag=ABTV_sNgp1\n                     /db_xref=GeneID:10987403\n     CDS             236..700\n                     /locus_tag=ABTV_sNgp1\n                     /codon_start=1\n                     /product=putative nuclear shuttle protein\n                     /protein_id=YP_001661656.1\n                     /db_xref=GI:167006426\n                     /db_xref=GeneID:10987403\n                     /translation=MDWMESQFKTCTHGCDWKAIAPEAQDNIQVITCSDSGYGRKNPR\n                     KVLLRSIQIGFNGSFRGSNRNVRGFIYVSVRQDDGQMRPIMVVPFGGYGYHNDYYYFE\n                     GQSSTNCEIVSDYIPAGQDWSRDMEISISNSNNCNQECDIKCYVVCNLRIKE\nORIGIN      \n        1 agcagggggg cttattatta ccccccctgc tcggggcggg acattctgtg atgggctggg\n       61 ctttatgcgg ccaaataagc ccataaagcc agatctgggc ccatttaagg gcccgtggtt\n      121 tgaaaatgtc gcgttcccgc ctaaattgtt tgcttgccct gcaaggaaac gaaaactcta\n      181 taaatagggt tgttctctgc ttgtttaata catcaggcgc aaatcttttg caacgatgga\n      241 ttggatggaa tcacaattca agacatgtac gcatggctgc gactggaagg cgatagctcc\n      301 agaagcacaa gataatatac aggtaattac atgttccgat tcaggttacg gaagaaagaa\n      361 ccctcgtaag gttcttctga ggagtattca gatagggttc aatggaagct tcagaggaag\n      421 taatagaaat gttcgaggct tcatatacgt gtctgtaaga caggatgatg gccaaatgag\n      481 accaattatg gtcgttccat tcggagggta tggatatcat aacgactact attattttga\n      541 aggacaatcc agtacgaatt gtgagatagt gtcggactat attccggccg gtcaagactg\n      601 gagcagagat atggagataa gtataagtaa cagcaacaat tgtaatcaag agtgcgatat\n      661 caagtgttat gtagtatgta atttaagaat taaggaataa wattgttgcc gaaggtctgt\n      721 tatttgaatg ttgagataag gaaaggggcg gcgaagcatg tgtgtataat aacatataac\n      781 acactattat atattttgta aagaataaaa ttatgacctg tcagattaag tttagaatga\n      841 actgaggccg aaggcctcac cgaggccgaa ggccgtcagg atggttttac aaaataatta\n      901 taagcacctg tactaagtac gaagagcggt ataatatctg aaaggaaaaa ataataatat\n      961 aataaaaata ttatgatgtc ccaaaatagc agaatgctaa aggaacaaaa ggatgctcta\n     1021 agtacagggt tgcgtgctct ggacgccact ttagtggtgg gccagatgtc ccgagttagt\n     1081 gcgccacgtc\n//\n";
		System.out.println(str);
		
		Parser parsou = new Parser(base,str);
		parsou.parse();
		
		int nbr_tri = base.get_nb_trinucleotides();
		System.out.println("nbr_tri "+nbr_tri);
		
		int nbr_cds = base.get_nb_CDS();
		System.out.println("nbr_cds "+nbr_cds);
		
		int nbr_cds_nt = base.get_nb_CDS_non_traites();
		System.out.println("nbr_cds_nt "+nbr_cds_nt);
		
	}

}
