package com.godlike.algorithm.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.godlike.algorithm.configure.SystemConfigure;

public class CraneTask implements Comparator<CraneTask>{

	private StorageTask task;
	private int craneId;
	private List<Integer> path;
	private int timeUse;
	private int timeDelay;
	
	
	public StorageTask getTask() {
		return task;
	}
	public void setTask(StorageTask task) {
		this.task=new StorageTask();
		this.task = task;
		timeUse=(int) SystemConfigure.getUseTimeMap().get(task.getType());
		timeDelay=(int) SystemConfigure.getDelayTimeMap().get(task.getType());
	}
	public int getCraneId() {
		return craneId;
	}
	public void setCraneId(int craneId) {
		this.craneId = craneId;
	}
	public List<Integer> getPath() {
		return path;
	}
	public void setPath(List<Integer> path) {
		this.path = path;
	}
	public int getTimeUse() {
		return timeUse;
	}
	public int getTimeDelay() {
		return timeDelay;
	}
	@Override
	public int compare(CraneTask arg0, CraneTask arg1) {
		return arg0.getTimeDelay()<arg1.getTimeDelay()? -1: 1;
	}
	
}
