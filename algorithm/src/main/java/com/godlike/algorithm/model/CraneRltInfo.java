package com.godlike.algorithm.model;

public class CraneRltInfo {
	private int id;
	private int workMode;
	private int workState;
	private CraneTask curentTask;
	
	private int position;
	private int speedAverage;
	
	public CraneRltInfo(int id) {
		super();
		this.id = id;
	}

	public int getWorkMode() {
		return workMode;
	}

	public void setWorkMode(int workMode) {
		this.workMode = workMode;
	}

	public int getWorkState() {
		return workState;
	}

	public void setWorkState(int workState) {
		this.workState = workState;
	}

	public CraneTask getCurentTask() {
		return curentTask;
	}

	public void setCurentTask(CraneTask curentTask) {
		this.curentTask = curentTask;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getSpeed() {
		return speedAverage;
	}

	public void setSpeed(int speed) {
		this.speedAverage = speed;
	}

	public int getId() {
		return id;
	}
	
	
}
