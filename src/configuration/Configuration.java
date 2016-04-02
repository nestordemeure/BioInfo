package configuration;

public class Configuration {
	
	// --- General configuration ---
	
	public static String FOLDER_SEPARATOR = "/"; // Separateur pour les dossiers (/ sous linux et \ sous windows)
	public static String BASE_FOLDER = "/tmp/results/"; // Dossier de base pour le stockage de nos résultats
	
	// --- Net configuration ---
	public static int NET_MAX_DOWNLOAD_TRIES = 10; // Nombre d'essais pour télécharger un fichier.
	public static int NET_TIME_BETWEEN_TRIES = 10000; // Temps entre deux essais (en ms).
	
	
	// --- IdFetcher Configuration ---
	public static int IDS_PER_PAGE = 100; // Id par page
	public static int IDS_MAX_TRIES = 10; // Nombre de fois ou l'on essaie de parser la page des IDs.
	public static String IDS_SEARCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&retmax=<PER_PAGE>&term=<TERM>[Organism]&retstart=<START>"; // URL de recherche pour les IDS

	// --- ParserManager Configuration ---	
	public static int GEN_PER_DOWNLOAD = 10;
	public static String GEN_DOWNLOAD_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=<ID>&rettype=gb"; // URL a utiliser pour télécharger les genes
	
	// --- ThreadManager Configuration ---
	public static int THREADS_NUMBER = 15; // Nombre de threads a lancer pour la récupération des fichiers de genes.
	
	// --- TreeManager Configuration ---
	public static String TREE_URL = "http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomeList4Grid&filterText=%7CAll&page=";

}
