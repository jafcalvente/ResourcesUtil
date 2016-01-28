package com.indra.davinci.resources;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {

		// El hilo principal sólo se encarga del pasarle
		// al EDT la responsabilidad de construir la GUI
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new ResourcesGUI();
			}
		});
	}

}
