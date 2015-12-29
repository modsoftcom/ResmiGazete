package com.modsoft.resmigazete.engine;

public class Haber {
	private String Title;
	private String Link;
	private String Ext = "";
	private boolean isFile = false;
	private HaberType Type;
	
	public Haber(String title, String link, String ext, boolean isFile,
			HaberType type) {
		super();
		Title = title;
		Link = link;
		Ext = ext;
		this.isFile = isFile;
		Type = type;
	}

	
	public Haber(String title, String link, HaberType type) {
		super();
		Title = title;
		Link = link;
		Type = type;
	}


	public Haber() {
		// TODO Auto-generated constructor stub
	}

	public Haber(String link) {
		this.Link = link;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return Link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		Link = link;
	}

	/**
	 * @return the ext
	 */
	public String getExt() {
		return Ext;
	}

	/**
	 * @param ext the ext to set
	 */
	public void setExt(String ext) {
		Ext = ext;
	}

	/**
	 * @return the isFile
	 */
	public boolean isFile() {
		return isFile;
	}

	/**
	 * @param isFile the isFile to set
	 */
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}


	/**
	 * @return the type
	 */
	public HaberType getType() {
		return Type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(HaberType type) {
		Type = type;
	}
	
	
	
}
