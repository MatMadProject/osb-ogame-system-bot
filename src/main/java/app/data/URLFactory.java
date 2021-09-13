package app.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * PL
 * Umożliwiająca edycję ścieżki.
 * EN
 * Enables editing path.
 */
public class URLFactory {
    private String path;
    private final List<String> packages = new ArrayList<>();
    private final int[] slashPosition = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    /**
     * PL
     * Określa indeksy występowania znaku "\".
     * EN
     * ***.
     * @param path Scieżka dostępu do aktualnego package, zwrócona przy użyciu metod
     *             <b>getClass().getResource("").getPath()</b>;Absolute path to actual folder, returned with method  <b>getClass().getResource("").getPath()</b>.
     */
    public URLFactory(String path) {
        updateData(path);
    }
     /**
     * PL
     * Aktualizacji danych obiektu.
     * EN
     * Updates data object.
     * @param path Scieżka dostępu do aktualnego package, zwrócona przy użyciu metod
     *             <b>getClass().getResource("").getPath()</b>;Absolute path to actual folder, returned with method  <b>getClass().getResource("").getPath()</b>.
     */
    public void updateData(String path) {
        this.path = path;
        setSlashPosition();
        setPackages();
    }
    /**
     * PL
     * Określa indeksy występowania znaku "\".
     * EN
     * ***.
     */
    private void setSlashPosition() {
        int a = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '\\') {
                slashPosition[a] = i;
                a++;
            }
        }
    }
    /**
     * PL
     * ***
     * EN
     * ***.
     */
    private void setPackages() {
        StringBuilder sb = new StringBuilder(path);

        for (int i = 0; i < slashPosition.length - 1; i++) {
            if (slashPosition[i + 1] != -1) {
                String s;
                if(i == 0) {
                    s = sb.substring(0, slashPosition[i]);
                }
                else {
                    s = sb.substring(slashPosition[i - 1] + 1, slashPosition[i]);
                }
                packages.add(s);
            }
        }
    }
    /**
     * PL
     * Usuwa nazwy folderów.
     * EN
     * Deletes names of folders.
     */
    public void clearPackages() {
        packages.clear();
    }
    /**
     * PL
     * Zwraca nazwę ścieżki.
     * EN
     * Returns name of path.
     * @return ***
     */
    public String getPath() {
        return path;
    }
    /**
     * PL
     * Usuwa od lewej strony foldery.
     * EN
     * Deletes folders from left side.
     * @param nr Ilość folderów do usunięcia; Amount folders to delete.
     * @return Scieżka po zmianach. The path after change.
     */
    public String path(int nr) {
        StringBuilder s = new StringBuilder("file:/");

        for (int i = 0; i < packages.size() - nr; i++) {
            s.append(packages.get(i)).append("/");
        }
        return s.toString();
    }
    /**
     * PL
     * Usuwa od lewej strony foldery, a następnie dodaję nową ściężkę na koniec.
     * EN
     * Deletes folders from left side and next add new path at end.
     * @param nr   Ilość folderów do usunięcia; Amount folders to delete.
     * @param path Nowa ścieżka, która zostanie dodana na koniec po usunięcu folderów; The new path which will add at end after delete folders.
     * @return Scieżka po zmianach. The path after change.
     */
    public String path(int nr, String path) {
        StringBuilder s = new StringBuilder("file:/");
        for (int i = 0; i < packages.size() - nr; i++) {
            s.append(packages.get(i)).append("/");
        }
        return s + path;
    }
    /**
     * PL
     * Nazwa systemowego dysku.
     * EN
     * The system name of disk.
     * @return ***
     */
    public String disk()
    {
        return packages.get(0)+ File.separator;
    }
    /**
     * PL
     * Nazwy folderów.
     * EN
     * Folders names.
     * @return ***
     */
    public String printPackages()
    {
        StringBuilder sb = new StringBuilder("\n");
        int a = 1;

        for(String s : packages)
        {
            sb.append(a).append(". ").append(s).append("\n");
            a++;
        }
        return sb.toString();
    }
}
