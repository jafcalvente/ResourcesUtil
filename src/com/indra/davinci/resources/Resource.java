package com.indra.davinci.resources;

/**
 * Entidad que representa un recurso-
 *
 * @author jafcalvente
 *
 */
public class Resource {

	/** Path del fichero donde se encuentra el recurso. */
	private String path;

	/** Nombre del fichero donde se encuentra el recurso. */
	private String fileName;

	/** Clave del recurso. */
	private String key;

	/** Valor del recurso. */
	private String value;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Resource [key=" + key + ", value=" + value
				+ "]";
	}

}
