import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.util.Scanner;

public class SearchDrawer {
    public static void main(String[] args) throws Exception {
        search(new File("C:\\Users\\LorenPC\\.gradle\\caches\\fabric-loom"));
    }

    private static void search(File dir) throws Exception {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                search(f);
            } else if (f.getName().endsWith("-sources.jar") && f.getName().contains("minecraft-project") && f.getName().contains("yarn")) {
                try (ZipFile zip = new ZipFile(f)) {
                    ZipEntry entry = zip.getEntry("net/minecraft/client/gui/DrawContext.java");
                    if (entry != null) {
                        System.out.println("Found in: " + f.getAbsolutePath());
                        try (InputStream is = zip.getInputStream(entry);
                             java.io.FileOutputStream fos = new java.io.FileOutputStream("F:\\dd\\HideArmorMod\\DrawContext.java")) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                        System.out.println("Extracted to DrawContext.java");
                        return;
                    }
                } catch (Exception e) {}
            }
        }
    }
}
