package com.indra.davinci.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Se encarga de procesar la información de los ficheros de recursos.
 *
 * @author jafcalvente
 *
 */
public class ResourcesLoader {

    public final static Pattern PATTERN_COMMENT = Pattern.compile("^#");
    public final static Pattern PATTERN_FILE_EXTENSION = Pattern.compile("^*.properties");
    public final static String EQUAL = "=";

    /** Map para los recursos. */
    public static Map<String, List<Resource>> resourcesMap = new TreeMap<>();
    public static Map<String, List<Resource>> wrongResourcesMap = new HashMap<>();

    public static void load(final String startupPath) {

        // Java 7
        // Recuperamos recursivamente todos los ficheros
        // de recuersos que penden de la ruta de inicio
        File startupFolder = new File(startupPath);
        List<File> files = new ArrayList<>();
        listFilesInFolder(startupFolder, files);

        InputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            // Procesamos cada fichero mapeando la información en una entidad
            // Resource. Dependiendo de si está o no bien formada pasará a la
            // lista correspondiente
            if (files != null) {
                for (File file : files) {
                    String line = null;
                    Matcher m = null;
                    fis = new FileInputStream(file);
                    isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                    br = new BufferedReader(isr);

                    // Procesamos el fichero hasta que la lectura de línea no devuelva nada
                    while ((line = br.readLine()) != null) {

                        // Obviamos los espacios al principio y final y saltamos las líneas
                        // que no vengan informadas
                        line = line.trim();
                        if (line.equals("")) {
                            continue;
                        }

                        // Obviamos las líneas que sean comentarios
                        m = PATTERN_COMMENT.matcher(line);
                        if (m.find()) {
                            continue;
                        }

                        // Cada clave/valor bien formada vendrá separada por un '='.
                        // Separamos por el primero que encontremos
                        String[] key_value  = line.split(EQUAL, 2);

                        if (key_value.length == 2) {
                            // Hemos encontrado dos partes. Estamos ante un recurso bien formado
                            Resource resource = new Resource();
                            resource.setPath(file.getCanonicalPath());
                            resource.setFileName(file.getName());
                            resource.setKey(key_value[0]);
                            resource.setValue(key_value[1]);

                            addResourceToMap(resourcesMap, resource, startupPath);

                        } else if (key_value.length == 1) {
                            // Hemos encontrado una parte. Estamos ante un recurso mal formado
                            Resource resource = new Resource();
                            resource.setPath(file.getCanonicalPath());
                            resource.setFileName(file.getName());
                            resource.setValue(key_value[0]);

                            addResourceToMap(wrongResourcesMap, resource, startupPath);

                        } else {
                            // No estamos ante ninguno de los casos anteriores.
                            // Algo no ha ido bien. Abortamos la funcionalidad
                            throw new ResourceError();
                        }
                    }
                    br.close();
                    isr.close();
                    fis.close();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                	br.close();
                if (isr != null)
                    isr.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
        // From Java 8 ******************************************************************
        System.out.println("From Java 8");
        try {
            Files.walk(Paths.get(PATH)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    System.out.println(filePath);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * Construye una lista de ficheros de recursos con todos aquellos que
     * cuelgan de un determinado directorio pasado como parámetro.
     * Para ello recorre recursivamente el árbol de directorios a partir
     * del raíz y cuando encuentra un fichero comprueba si es un fichero
     * properties; si lo es lo incluye en la lista.
     *
     * @param startupFolder Directorio raíz
     * @param files Lista de ficheros
     */
    public static void listFilesInFolder(File startupFolder, List<File> files) {

        if (files != null && startupFolder != null) {
            for (File fileEntry : startupFolder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesInFolder(fileEntry, files);
                } else {
                    Matcher file_matcher = PATTERN_FILE_EXTENSION.matcher(fileEntry.getName());
                    if (file_matcher.find()) {
                        files.add(fileEntry);
                    }
                }
            }
        }
    }

    /**
     * Construye un Map que asocia rutas relativas a los ficheros a partir
     * del directorio raíz 'startupPath' a la lista de recursos (Resource)
     * que dicho fichero contiene.
     *
     * @param resourcesMap Map con la relación ruta-recursos
     * @param resource Recurso a incluir en el Map
     * @param startupPath Directorio raíz
     */
    public static void addResourceToMap(Map<String, List<Resource>> resourcesMap, Resource resource, String startupPath) {

    	if (resource != null && resourcesMap != null && startupPath != null) {

			// Queremos solo la ruta relativa al fichero. Eliminamos el resto
			String relativePath;
			if (resource.getPath().startsWith(startupPath)) {
				relativePath = resource.getPath().substring(startupPath.length() + 1 , resource.getPath().length());
			} else {
				relativePath = resource.getPath();
			}

    		if (!resourcesMap.containsKey(relativePath)) {
    			// Si el recurso pertenece a un fichero que aún no ha
    			// sido registrado añadimos una nueva entrada al map
    			List<Resource> resources = new ArrayList<>();
    			resources.add(resource);

    			// Registramos el primer recurso del fichero
    			resourcesMap.put(relativePath, resources);

    		} else {
    			// Si el recurso pertenece a un fichero que ya ha sido registrado
    			// añadimos el recurso a la lista correspondiente
    			List<Resource> resources = resourcesMap.get(relativePath);
    			resources.add(resource);
    		}
    	} else {
            throw new ResourceError();
    	}
    }

}
