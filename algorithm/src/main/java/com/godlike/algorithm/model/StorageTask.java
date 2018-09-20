package com.godlike.algorithm.model;

/**
 * @author Administrator
 *	此类为输送线、仓库调动等和作为所有任务类的父类
 */
public class StorageTask {
	public enum TaskType{
		RAWOFFLINE,
		RAWIN,//buy from out
		RAWFIRE,
		RAWOUT,
		MATUREOFFLINE,
		MATUREIN,//buy from out
		MATUREOUT,//sale to out
		MOVE
	}
	private TaskType type;
	private CBlockType cType;
	private int count;
	private int start;
	private int end;
	private int subWarehouse;
	
	public CBlockType getcType() {
		return cType;
	}
	public void setcType(CBlockType cType) {
		this.cType = cType;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public TaskType getType() {
		return type;
	}
	public void setType(TaskType type) {
		this.type = type;
	}
	public int getSubWarehouse() {
		return subWarehouse;
	}
	public void setSubWarehouse(int subWarehouse) {
		this.subWarehouse = subWarehouse;
	}
	
	
}
