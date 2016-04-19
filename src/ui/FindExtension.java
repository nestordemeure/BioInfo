package ui;

import java.io.File;
import java.io.FilenameFilter;

public class FindExtension {
	public static String check(String folder, String ext) {
        return new FindExtension().listFile(folder, ext);
    }
	
	public String listFile(String folder, String ext) {

        GenericExtFilter filter = new GenericExtFilter(ext);

        File dir = new File(folder);

        if(dir.isDirectory()==false){
            System.out.println("Directory does not exists : " + folder);
            return "";
        }

        // list out all the file name and filter by the extension
        String[] list = dir.list(filter);

        if (list.length == 0) {
            System.out.println("no files end with : " + ext);
            return "";
        }

        String xls = new StringBuffer(folder).append(File.separator).append(list[0]).toString();
        return xls;
    }

    // inner class, generic extension filter
    public class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
}
