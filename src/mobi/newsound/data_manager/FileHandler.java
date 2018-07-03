package mobi.newsound.data_manager;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class FileHandler {

    @FunctionalInterface
    public interface UpdateCallback {
        void update(double progress);
    }

    public static void updateProjectNames(int assignmentId,UpdateCallback callback){
        File file = new File("./output/assignment/"+assignmentId);
        String[] directories = file.list((current, name) ->
                new File(current, name).isDirectory() && !name.equals("failed")
        );

        if (directories != null) {
            int counter = 0;
            for (String dir  : directories) {
                Collection<File> projects =
                        FileUtils.listFiles(new File(file.getPath() + "/" + dir)
                                , new String[]{"project"} ,
                                true);
                System.out.println("dir: "+dir);
                for (File project : projects) {
                    try {
                        //read file contents
                        String contents = readStringFromFile(project.getPath());

                        //extract project name
                        String nameValue = contents.split("\n")[2].split(">")[1].split("<")[0];

                        //remove any ids in project name (if exists)
                        nameValue = nameValue.replaceAll(dir,"");

                        //write back to file
                        writeToFile(project.getPath(),contents.replaceAll(nameValue,dir+"_"+nameValue));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ++counter;
                if (callback != null){

                    callback.update((double) counter / directories.length);
                }
            }
        }


    }

    public static String readStringFromFile(String filePath) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(new File(filePath).getAbsolutePath())));
        return fileContent;
    }

    public static void writeToFile(String filePath,String data) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(data.getBytes());
        out.close();
    }

    public static void unzip(String fileZip) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
            String fileName = zipEntry.getName();
            File newFile = new File(fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static void decompress7z(String in, File destination) throws IOException {
        SevenZFile sevenZFile = new SevenZFile(new File(in));
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null){
            if (entry.isDirectory()){
                continue;
            }
            File curfile = new File(destination, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(curfile);
            byte[] content = new byte[(int) entry.getSize()];
            sevenZFile.read(content, 0, content.length);
            out.write(content);
            out.close();
        }
    }

    public static void extractRar(String archive, String destination) throws IOException, RarException {
        if (archive == null || destination == null) {
            throw new RuntimeException("archive and destination must me set");
        }
        File arch = new File(archive);
        if (!arch.exists()) {
            throw new RuntimeException("the archive does not exit: " + archive);
        }
        File dest = new File(destination);
        if (!dest.exists() || !dest.isDirectory()) {
            throw new RuntimeException(
                    "the destination must exist and point to a directory: "
                            + destination);
        }
        extractArchive(arch, dest);
    }

    public static void extractArchive(File archive, File destination) throws IOException, RarException {
        Archive arch = null;
        arch = new Archive(archive);
        if (arch != null) {
            if (arch.isEncrypted()) {
                return;
            }
            FileHeader fh = null;
            while (true) {
                fh = arch.nextFileHeader();
                if (fh == null) {
                    break;
                }
                if (fh.isEncrypted()) {
                    continue;
                }
                if (fh.isDirectory()) {
                    createDirectory(fh, destination);
                } else {
                    File f = createFile(fh, destination);
                    OutputStream stream = new FileOutputStream(f);
                    arch.extractFile(fh, stream);
                    stream.close();
                }
            }
        }
    }

    private static void createDirectory(FileHeader fh, File destination) {
        File f = null;
        if (fh.isDirectory() && fh.isUnicode()) {
            f = new File(destination, fh.getFileNameW());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameW());
            }
        } else if (fh.isDirectory() && !fh.isUnicode()) {
            f = new File(destination, fh.getFileNameString());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameString());
            }
        }

    }

    private static void makeDirectory(File destination, String fileName) {
        String[] dirs = fileName.split("\\\\");
        if (dirs == null) {
            return;
        }
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            new File(destination, path).mkdir();
        }

    }

    private static File createFile(FileHeader fh, File destination) throws IOException {
        File f = null;
        String name = null;
        if (fh.isFileHeader() && fh.isUnicode()) {
            name = fh.getFileNameW();
        } else {
            name = fh.getFileNameString();
        }
        f = new File(destination, name);
        if (!f.exists()) {
            f = makeFile(destination, name);
        }
        return f;
    }

    private static File makeFile(File destination, String name)
            throws IOException {
        String[] dirs = name.split("\\\\");
        if (dirs == null) {
            return null;
        }
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        } else if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                new File(destination, path).mkdir();
            }
            path = path + File.separator + dirs[dirs.length - 1];
            File f = new File(destination, path);
            f.createNewFile();
            return f;
        } else {
            return null;
        }
    }
}


