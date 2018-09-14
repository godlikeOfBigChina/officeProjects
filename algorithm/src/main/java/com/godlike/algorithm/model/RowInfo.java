package com.godlike.algorithm.model;

public class RowInfo {
	private CBlockType cType;
	private int id;
	private String state;
	private int max=8;
	private int layers;
	private boolean ifMax;
	private boolean ifRaw;
	private int posx;
	
	public CBlockType getcType() {
		return cType;
	}
	public void setcType(String type) {
		
		switch(type){
		case "A":
			this.cType = CBlockType.A;
			break;
		case "B":
			this.cType = CBlockType.B;
			break;
		case "C":
			this.cType = CBlockType.C;
			break;
		case "D":
			this.cType = CBlockType.D;
			break;
		default:
			this.cType=CBlockType.Z;
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getMax() {
		return max;
	}
	public int getLayers() {
		return layers;
	}
	public void setLayers(int layers) {
		this.layers = layers;
	}
	public boolean isIfMax() {
		ifMax=this.layers>=this.max;
		return ifMax;
	}
	public boolean isIfRaw() {
		return ifRaw;
	}
	public void setIfRaw(boolean ifRaw) {
		this.ifRaw = ifRaw;
	}
	public int getPosx() {
		return posx;
	}
	public void setPosx(int posx) {
		this.posx = posx;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
}
