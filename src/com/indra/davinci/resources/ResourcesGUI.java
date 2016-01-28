package com.indra.davinci.resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Interfaz gráfica de usuario.
 *
 * @author jafcalvente
 *
 */
public class ResourcesGUI extends JFrame {

	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	//FIXME Path temporal para la fase de desarrollo
    private final static String PATH = "D:\\WS_DAVINCI\\PLN_V10_HHR_20151202\\resources_test";
    private static final String SCORE = " - ";

    /** Componentes gráficos. */
    private JList<String> fileNameList;
    private JList<Resource> resourceList;

    /**
     * Constructor.
     */
    public ResourcesGUI() {

    	// Cargamos la información de los ficheros
        ResourcesLoader.load(PATH);

        // Definimos la GUI
        defineScreen();

        // Cargamos la información inicial necesaria
        loadInitialInfo();

        // Definimos los listeners
        defineListeners();
    }

    /**
     * Construye los elementos gráficos.
     */
	private void defineScreen() {

		// Lista de nombres de ficheros
        fileNameList = new JList<>();
        fileNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileNameList.setLayoutOrientation(JList.VERTICAL);
        fileNameList.setCellRenderer(new FileNameCellRenderer());
        JScrollPane fileNameListScroller = new JScrollPane(fileNameList);
        fileNameListScroller.setPreferredSize(new Dimension(300, 350));

        // Lista de recursos
		resourceList = new JList<>();
        resourceList.setEnabled(false);
        resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceList.setLayoutOrientation(JList.VERTICAL);
        resourceList.setCellRenderer(new ResourceCellRenderer());
        JScrollPane resourceListScroller = new JScrollPane(resourceList);
        resourceListScroller.setPreferredSize(new Dimension(650, 350));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Resources"), BorderLayout.NORTH);
        mainPanel.add(fileNameListScroller, BorderLayout.CENTER);
        mainPanel.add(resourceListScroller, BorderLayout.SOUTH);

        // Definimos el JFrame
        add(mainPanel);
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

	/**
	 * Carga la información inicial necesaria.
	 */
    private void loadInitialInfo() {

    	// Construye el array con los nombres de ficheros que se mostrará
    	// en el JList definido para tal fin
    	String[] fileNameIndex = ResourcesLoader.resourcesMap.keySet().toArray(
                new String[ResourcesLoader.resourcesMap.keySet().size()]);
        fileNameList.setListData(fileNameIndex);
	}

    /**
     * Define los listeners.
     */
	private void defineListeners() {

		// Este listener provocará que cuando se seleccione un fichero en
		// el JList con los nombres de los ficheros se cargue el JList de
		// recursos con la información de los mismos que tenga asociados
        fileNameList.addListSelectionListener(new ListSelectionListener() {

            @SuppressWarnings("unchecked")
			@Override
            public void valueChanged(ListSelectionEvent e) {

            	// Será false cuando el usuario ya no esté interactuando con el componente
                if (e.getValueIsAdjusting() == false) {

                    if (ResourcesGUI.this.fileNameList.getSelectedIndex() == -1) {
                    	// No hay ningún fichero seleccionado: vaciamos la lista de recursos
                        ResourcesGUI.this.resourceList.setEnabled(false);
                        ResourcesGUI.this.resourceList.setListData(new Resource[0]);

                    } else if (e.getSource() != null) {
                    	// Hay un fichero seleccionado: informamos la lista de recursos
                        ResourcesGUI.this.resourceList.setEnabled(true);
                        String path = ((JList<String>) e.getSource()).getSelectedValue();

                        if (path != null && ResourcesLoader.resourcesMap.containsKey(path)) {
                        	List<Resource> resourceList = ResourcesLoader.resourcesMap.get(path);
                        	if (resourceList != null) {
                        		ResourcesGUI.this.resourceList.setListData(
                        				resourceList.toArray(new Resource[resourceList.size()]));
                        	}
                        }
                    }
                }
            }
        });

	}

	/**
	 * Renderer para los registros del JList con la información de los recursos.
	 *
	 * @author jafcalvente
	 *
	 */
    class ResourceCellRenderer extends JLabel implements ListCellRenderer<Resource> {

    	/** Serial version UID. */
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(
		 * javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
        public Component getListCellRendererComponent(JList<? extends Resource> list, Resource value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText(value.getKey() + " ===> " + value.getValue());
            return this;
        }

    }

    /**
     * Renderer para los registros del JList con la información de los nombres de los ficheros.
     *
     * @author jafcalvente
     *
     */
    class FileNameCellRenderer extends JLabel implements ListCellRenderer<String> {

    	/** Serial version UID. */
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(
		 * javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {

            // De la ruta relativa al fichero que vendrá en el atributo 'value' mostramos
            // sólo la primera carpeta que tengamos (que indicará la entidad que engloba
            // al fichero) y el nombre del fichero
            String pattern = Pattern.quote(File.separator);
            String[] path = value.split(pattern);
            if (path != null && path.length > 0) {
                setText(path[0] + (path.length > 1 ? SCORE + path[path.length-1] : ""));
            } else {
                throw new ResourceError();
            }

            // Si el registro está seleccionado se marca en azul
            if (isSelected) {
                setForeground(Color.BLUE);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

}
