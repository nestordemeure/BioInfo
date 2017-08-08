package configuration;

import java.io.File;

public class Configuration {
	
	// --- General configuration ---
	public static String FOLDER_SEPARATOR = File.separator; // Separateur pour les dossiers (/ sous linux et \ sous windows)
	public static String BASE_FOLDER = "/tmp/results/"; // Dossier de base pour le stockage de nos résultats
	public static boolean USE_GUI = true; // Permet de switcher entre le mode graphique et console.
	public static boolean STORE_DATA = false; // Permet de stocker les fichiers
	
	// --- Net configuration ---
	public static int NET_MAX_DOWNLOAD_TRIES = 10; // Nombre d'essais pour télécharger un fichier.
	public static int NET_TIME_BETWEEN_TRIES = 10000; // Temps entre deux essais (en ms).
	public static int NET_MAX_DOWNLOAD_TIME = 30; // TODO temps maximal autorisé pour télécharger un fichier (en minutes)

	// --- IdFetcher Configuration ---
	public static int IDS_PER_PAGE = 100; // Id par page
	public static int IDS_MAX_TRIES = 10; // Nombre de fois ou l'on essaie de parser la page des IDs.
	public static String IDS_SEARCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&retmax=<PER_PAGE>&term=<TERM>[Organism]&retstart=<START>"; // URL de recherche pour les IDS

	// --- ParserManager Configuration ---	
	public static int GEN_PER_DOWNLOAD = 10;
	public static String GEN_DOWNLOAD_URL = "https://www.ncbi.nlm.nih.gov/sviewer/viewer.cgi?tool=portal&save=file&log$=seqview&db=nuccore&report=gbwithparts&sort=&from=begin&to=end&maxplex=3&id=<ID>"; // URL a utiliser pour télécharger les genes
	
	// --- ThreadManager Configuration ---
	public static int THREADS_NUMBER = 10; // Nombre de threads a lancer pour la récupération des fichiers de genes.
	
	// --- TreeManager Configuration ---
	public static String TREE_URL = "http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomeList4Grid&filterText=%7CAll&page=";
	public static String TREE_EUKARYOTES_URL = "https://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomes4Grid&king=Eukaryota&mode=2&filterText=%7C%7C--+All+Eukaryota+--%7C--+All+Eukaryota+--%7C%7C50%2C40%2C30%2C20%7Cnopartial%7Cnoanomalous&pageSize=100&page=";
	public static String TREE_PROKARYOTES_URL = "https://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomes4Grid&king=Bacteria&mode=2&filterText=%7C%7C--+All+Prokaryotes+--%7C--+All+Prokaryotes+--%7C%7C50%2C40%7Cnopartial%7Cnoanomalous&pageSize=100&page=";
	public static String TREE_VIRUSES_URL = "https://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomes4Grid&king=Viruses&mode=2&pageSize=100&page=";
}
