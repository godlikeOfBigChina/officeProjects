package com.godlike.algorithm.configure;


/**
 * @author Administrator
 * 	一排共8*21块认为任何轻快都够，如果人工出入库任务过大，自行分解任务,每次整排
 *
 */
public class SystemConfigure {
	public static int craneSpeed;
	public static int restLocation;
	public static int[][] fireHouseLocation=new int[][]{{0,0},{0,0}};//焙烧车间I，II分别的上下线位置
	public static int minRowId=0;
	public static int maxRowId=9;
	public static int[] rawBlockCrane=new int[]{0,3};
	public static int[] matureBlockCrane=new int[]{1,2};
	public static int storage1Account=0;
	public static int storage2Account=1;
}
