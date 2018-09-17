package com.godlike.algorithm.configure;

import java.util.HashMap;
import java.util.Map;

import com.godlike.algorithm.model.StorageTask;

/**
 * @author Administrator
 * 	一排共8*21块认为任何轻快都够，如果人工出入库任务过大，自行分解任务,每次整排
 *
 */
public class SystemConfigure {
	public static int craneSpeed;
	public static int restLocation;
	public static int[][] fireHouseLocation=new int[][]{{0,0},{0,0}};//焙烧车间I，II分别的上下线位置
	public static int[] rawBlockCrane=new int[]{0,3};
	public static int[] matureBlockCrane=new int[]{1,2};
	public static int storage1Account=10;
	public static int storage2Account=1;
	
	public static Map getUseTimeMap() {
		Map useTimeMap=new HashMap<StorageTask.TaskType,Integer>();
		useTimeMap.put(StorageTask.TaskType.RAWOFFLINE, 1);
		useTimeMap.put(StorageTask.TaskType.RAWIN, 2);
		useTimeMap.put(StorageTask.TaskType.RAWFIRE, 3);
		useTimeMap.put(StorageTask.TaskType.MATUREOFFLINE, 4);
		useTimeMap.put(StorageTask.TaskType.MATUREIN, 5);
		useTimeMap.put(StorageTask.TaskType.MATUREOUT, 6);
		useTimeMap.put(StorageTask.TaskType.MOVE, 7);
		return useTimeMap;
	}
	public static Map getDelayTimeMap() {
		Map delayTimeMap=new HashMap<StorageTask.TaskType,Integer>();
		delayTimeMap.put(StorageTask.TaskType.RAWOFFLINE, 1);
		delayTimeMap.put(StorageTask.TaskType.RAWIN, 4);
		delayTimeMap.put(StorageTask.TaskType.RAWFIRE, 3);
		delayTimeMap.put(StorageTask.TaskType.MATUREOFFLINE, 2);
		delayTimeMap.put(StorageTask.TaskType.MATUREIN, 5);
		delayTimeMap.put(StorageTask.TaskType.MATUREOUT, 6);
		delayTimeMap.put(StorageTask.TaskType.MOVE, 7);
		return delayTimeMap;
	}
	
	
}
