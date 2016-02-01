package com.indra.davinci.resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
    private static final String RESOURCES = "Resources";
    private static final String WRONG_RESOURCES = "Wrong resources";

    /** Componentes gráficos. */
    private JList<String> fileNameList;
    private JList<Resource> resourcesList;

    /** Componentes gráficos. */
    private JList<String> errorFileNameList;
    private JList<Resource> wrongResourcesList;

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
	@SuppressWarnings("unchecked")
	private void defineScreen() {

		// Lista de nombres de ficheros
		fileNameList = createNewJList(new FileNameCellRenderer(), true);
        JScrollPane fileNameListScroller = new JScrollPane(fileNameList);
        fileNameListScroller.setPreferredSize(new Dimension(650, 150));

        // Lista de recursos
        resourcesList = createNewJList(new ResourceCellRenderer(), false);
        JScrollPane resourcesListScroller = new JScrollPane(resourcesList);
        resourcesListScroller.setPreferredSize(new Dimension(650, 350));

		// Lista de nombres de ficheros con errores
        errorFileNameList = createNewJList(new FileNameCellRenderer(), true);
        JScrollPane errorFileNameListScroller = new JScrollPane(errorFileNameList);
        errorFileNameListScroller.setPreferredSize(new Dimension(650, 150));

        // Lista de recursos
        wrongResourcesList = createNewJList(new ResourceCellRenderer(), false);
        JScrollPane wrongResourcesListScroller = new JScrollPane(wrongResourcesList);
        wrongResourcesListScroller.setPreferredSize(new Dimension(650, 350));

        // Panel con los recursos bien formados
        JPanel resourcesPanel = new JPanel(new BorderLayout());
        resourcesPanel.add(new JLabel(RESOURCES), BorderLayout.NORTH);
        resourcesPanel.add(fileNameListScroller, BorderLayout.CENTER);
        resourcesPanel.add(resourcesListScroller, BorderLayout.SOUTH);

        // Panel con los recursos mal formados
        JPanel wrongResourcesPanel = new JPanel(new BorderLayout());
        wrongResourcesPanel.add(new JLabel(WRONG_RESOURCES), BorderLayout.NORTH);
        wrongResourcesPanel.add(errorFileNameListScroller, BorderLayout.CENTER);
        wrongResourcesPanel.add(wrongResourcesListScroller, BorderLayout.SOUTH);

        // Panel principal
        JTabbedPane tabs = new JTabbedPane();
        tabs.add(RESOURCES, resourcesPanel);
        tabs.add(WRONG_RESOURCES, wrongResourcesPanel);
        JPanel mainPanel = new JPanel();
        mainPanel.add(tabs);

        // Definimos el JFrame
        add(mainPanel);
        setSize(700, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JList createNewJList(ListCellRenderer renderer, boolean enabled) {
		JList list = new JList<>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setCellRenderer(renderer);
		list.setEnabled(enabled);
		return list;
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

    	String[] errorFileNameIndex = ResourcesLoader.wrongResourcesMap.keySet().toArray(
                new String[ResourcesLoader.wrongResourcesMap.keySet().size()]);
        errorFileNameList.setListData(errorFileNameIndex);
	}

    /**
     * Define los listeners.
     */
	private void defineListeners() {

		// Este listener provocará que cuando se seleccione un fichero en
		// el JList con los nombres de los ficheros se cargue el JList de
		// recursos con la información de los mismos que tenga asociados
        fileNameList.addListSelectionListener(new FileNameListSelectionListener(ResourcesLoader.resourcesMap,
        		fileNameList, resourcesList));
        errorFileNameList.addListSelectionListener(new FileNameListSelectionListener(ResourcesLoader.wrongResourcesMap,
        		errorFileNameList, wrongResourcesList));
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

	/**
	 * Renderer para los registros del JList con la información de los recursos.
	 *
	 * @author jafcalvente
	 *
	 */
    class ResourceCellRenderer2 extends JLabel implements ListCellRenderer<Resource> {

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
	 * Renderer para los registros del JList con la información de los recursos.
	 *
	 * @author jafcalvente
	 *
	 */
    class ResourceCellRenderer extends JPanel implements ListCellRenderer<Resource> {

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





			// Clave del recurso
			JLabel labelKey = new JLabel(value.getKey());
			if (value.getKey() == null) {
				labelKey.setText("Falta key!");
				labelKey.setForeground(Color.RED);
			}

			// Separador
			JLabel labelSeparator = new JLabel(" ===> ");
			labelSeparator.setForeground(Color.BLUE);

			// Valor del recurso
			String myString = "\u062e\u0637\u0623";
			myString = value.getValue();
			String str = myString.split(" ")[0];
			str = str.replace("\\","");
			String[] arr = str.split("u");
			for(int i = 1; i < arr.length; i++){
			    int hexVal = Integer.parseInt(arr[i], 16);
			    text += (char)hexVal;
			}
			
			
			
			JLabel labelValue = new JLabel(value.getValue());

			// Panel completo con toda la informacion de una linea
			FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
			flowLayout.setVgap(0);
			JPanel infoPanel = new JPanel(flowLayout);
			infoPanel.add(labelKey);
			infoPanel.add(labelSeparator);
			infoPanel.add(labelValue);


//			labelKey.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//			labelKey.setWrapStyleWord(true);
//			labelKey.setLineWrap(true);
//			labelKey.setFont(new Font("Arabic Typesetting", Font.PLAIN, 14));



            return infoPanel;
        }

    }

    class FileNameListSelectionListener implements ListSelectionListener {

    	/** Map con los recursos a manejar por el Listener. */
    	private Map<String, List<Resource>> resourcesMap;
        private JList<String> fileNameList;
        private JList<Resource> resourcesList;

    	/**
    	 * Constructor.
    	 *
    	 * @param resources Map con los recursos a asociar
    	 */
    	public FileNameListSelectionListener(Map<String, List<Resource>> resourcesMap,
    			JList<String> fileNameList, JList<Resource> resourcesList) {
			this.resourcesMap = resourcesMap;
			this.fileNameList = fileNameList;
			this.resourcesList = resourcesList;
		}

        @SuppressWarnings("unchecked")
		@Override
        public void valueChanged(ListSelectionEvent e) {

        	// Será false cuando el usuario ya no esté interactuando con el componente
            if (e.getValueIsAdjusting() == false) {

                if (fileNameList.getSelectedIndex() == -1) {
                	// No hay ningún fichero seleccionado: vaciamos la lista de recursos
                    resourcesList.setEnabled(false);
                    resourcesList.setListData(new Resource[0]);

                } else if (e.getSource() != null) {
                	// Hay un fichero seleccionado: informamos la lista de recursos
                    resourcesList.setEnabled(true);
                    String path = ((JList<String>) e.getSource()).getSelectedValue();

                    if (path != null && resourcesMap.containsKey(path)) {
                    	List<Resource> associatedResources = resourcesMap.get(path);
                    	if (associatedResources != null) {
                    		resourcesList.setListData(
                    				associatedResources.toArray(new Resource[associatedResources.size()]));
                    	}
                    }
                }
            }
        }

    }

}
