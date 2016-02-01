package com.indra.davinci.resources;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static void main(String[] args) {

		// El hilo principal sólo se encarga del pasarle
		// al EDT la responsabilidad de construir la GUI
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

				new ResourcesGUI();
			}
		});
	}

}
