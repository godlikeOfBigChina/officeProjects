package com.godlike.algorithm.caculate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;

import com.godlike.algorithm.configure.SystemConfigure;
import com.godlike.algorithm.exception.MyException;
import com.godlike.algorithm.model.CraneTask;
import com.godlike.algorithm.model.RowInfo;
import com.godlike.algorithm.model.StorageTask;

/**
 * @author godlike
 *
 */
public class Situation {
	private Connection conn;
	
	
	
	public Connection conncet() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useSSL=false&allowPublicKeyRetrieval=true",
				"root","ABCabc123");
		return conn;
	}
	//先进先出，从两边到中间，相遇爆仓
	
	/**
	 * @param storageTasks 可同时输入多个仓储相关任务
	 * @return 完善后的两个工区的天车任务，自动补充了起点终点等，计划任务之后对row进行锁定
	 * @throws Exception
	 */
	public Map<Integer, List<CraneTask>>  storageTaskMakeUp(List<StorageTask> storageTasks) throws Exception{
		Map<Integer,List<CraneTask>> craneTaskList=new HashMap<>();
		List<CraneTask> craneATasks=new ArrayList<CraneTask>();
		List<CraneTask> craneBTasks=new ArrayList<CraneTask>();
		for(StorageTask stask:storageTasks) {
			boolean ifInput;
			boolean ifRaw;
			int warehouse;
			switch(stask.getType()) {
			//总体分为出和入两种
			//入库
			case RAWOFFLINE:
				ifInput=true;
				ifRaw=true;
				warehouse=SystemConfigure.storage1Account<SystemConfigure.storage2Account?0:1;
				stask.setStart(SystemConfigure.fireHouseLocation[warehouse][1]);
				break;
			case RAWIN:
				ifInput=true;
				ifRaw=true;
				warehouse=SystemConfigure.storage1Account<SystemConfigure.storage2Account?0:1;
				stask.setStart(SystemConfigure.restLocation);
				break;
			case MATUREOFFLINE:
				ifInput=true;
				ifRaw=false;//in
				warehouse=SystemConfigure.storage1Account<SystemConfigure.storage2Account?0:1;
				stask.setStart(SystemConfigure.fireHouseLocation[warehouse][1]);
				break;
			case MATUREIN:
				ifInput=true;
				ifRaw=false;//in
				warehouse=SystemConfigure.storage1Account<SystemConfigure.storage2Account?0:1;
				stask.setStart(SystemConfigure.restLocation);
				break;
			//出库	
			case RAWFIRE:
				ifInput=false;
				ifRaw=true;
				warehouse=SystemConfigure.storage1Account>SystemConfigure.storage2Account?0:1;
				stask.setEnd(SystemConfigure.fireHouseLocation[warehouse][0]);
				break;
			case RAWOUT:
				ifInput=false;
				ifRaw=true;
				warehouse=SystemConfigure.storage1Account>SystemConfigure.storage2Account?0:1;
				stask.setEnd(SystemConfigure.restLocation);
				break;
			case MATUREOUT:
				ifInput=false;
				ifRaw=false;//out
				warehouse=SystemConfigure.storage1Account>SystemConfigure.storage2Account?0:1;
				stask.setEnd(SystemConfigure.restLocation);
				break;
			default:
				warehouse=-1;
				ifRaw=false;
				ifInput=false;
			}
			RowInfo tmpRow=getBreakPosition(ifRaw, warehouse, stask.getcType().name(),ifInput);
			if(tmpRow.getId()==-1) {//没有不满的
				tmpRow=getBlankOrFullPostion(ifRaw, warehouse,stask.getcType().name(),ifInput);
				if(tmpRow.getId()==-1) {//没有内部空位
					throw new MyException(MyException.Type.NO_BLANK_POSITON.name());
				}
			}
			stask.setEnd(tmpRow.getId());
			stask.setSubWarehouse(warehouse);
			lockRow(tmpRow.getId());
			CraneTask now=new CraneTask();
			now.setTask(stask);
			if(now.getTask().getSubWarehouse()==0) {
				craneATasks.add(now);
			}else if(now.getTask().getSubWarehouse()==1) {
				craneBTasks.add(now);
			}
			
		}
		Collections.sort(craneATasks,new CraneTask());
		Collections.sort(craneBTasks,new CraneTask());
		craneTaskList.put(0, craneATasks);
		craneTaskList.put(1, craneBTasks);
		return craneTaskList;
	}
	
	
	/**
	 * @param craneTask 输入多个天车任务，没有具体分配
	 * @param cranesLocation 系统中多个天车的位置，入0分库的1号天车在100Row处，则为cranesLocation[0][1]=100
	 * @return 做出具体的最佳分配结果
	 */
	public Map<Integer,List<CraneTask>> craneTaskDecide(List<CraneTask> craneTask,int[][] cranesLocation){
		List<CraneTask> craneATask=new ArrayList<>();
		List<CraneTask> craneBTask=new ArrayList<>();
		List<CraneTask> craneATaskFinal=new ArrayList<>();
		List<CraneTask> craneBTaskFinal=new ArrayList<>();
		int minPath=0;
		int n=craneTask.size();
		for(int i=0;i< Math.pow(2, n);i++) {
			String code=(Integer.toBinaryString(i)+"00000000").substring(0, n);
			craneATask.clear();
			craneBTask.clear();
			for(int k=0;k<code.length();k++) {
				if('0'==code.charAt(k)) {
					craneATask.add(craneTask.get(k));
				}else {
					craneBTask.add(craneTask.get(k));
				}
			}
//			if(!ifCross(craneATask, craneBTask, cranesLocation)) {
			if(true) {
				ifCross(craneATask, craneBTask, cranesLocation);
				minPath=pathLengthAll(craneATask, craneBTask, cranesLocation);
				craneATaskFinal=craneATask;
				craneBTaskFinal=craneBTask;
				System.out.println(minPath);
			}
		}
		Map<Integer,List<CraneTask>> craneTaskList=new HashMap<>();
		craneTaskList.put(0, craneATaskFinal);
		craneTaskList.put(1, craneBTaskFinal);
		return craneTaskList;
	}
	
	/**
	 * @param craneATask A天车任务
	 * @param craneBTask B天车任务
	 * @param cranesLocation 系统中多个天车的位置，入0分库的1号天车在100Row处，则为cranesLocation[0][1]=100
	 * @return 路径是否交错
	 */
	private boolean ifCross(List<CraneTask> craneATask,List<CraneTask> craneBTask,int[][] cranesLocation) {
		boolean rtv=false;
		int subWarehouse=craneATask.size()>0?craneATask.get(0).getTask().getSubWarehouse():craneBTask.get(0).getTask().getSubWarehouse();
		opencv_highgui window=new opencv_highgui();
		Mat image=new Mat(new Size(800,800),opencv_core.CV_8UC3);
		int time0=0;
		int time1=0;
		for(int i=0;i<craneATask.size();i++) {
			int timeMiddle=5;
			opencv_imgproc.line(image, 
					new Point(10*time0, i==0?10*cranesLocation[subWarehouse][0]:10*craneATask.get(i-1).getTask().getEnd()), 
					new Point(10*(time0+timeMiddle), 10*craneATask.get(i).getTask().getStart()), 
					new Scalar(0, 0, 255, 0),2,0,0);
			time0+=timeMiddle;
			opencv_imgproc.line(image, 
					new Point(10*time0, 10*craneATask.get(i).getTask().getStart()), 
					new Point(10*(time0+craneATask.get(i).getTimeUse()), 10*craneATask.get(i).getTask().getEnd()), 
					new Scalar(0, 0, 255, 0),2,0,0);
			time0+=craneATask.get(i).getTimeUse();
		}
		for(int i=0;i<craneBTask.size();i++) {
			int timeMiddle=5;
			opencv_imgproc.line(image, 
					new Point(10*time1, i==0?10*cranesLocation[subWarehouse][0]:10*craneBTask.get(i-1).getTask().getEnd()), 
					new Point(10*(time1+timeMiddle), 10*craneBTask.get(i).getTask().getStart()), 
					new Scalar(0, 255, 0, 0),2,0,0);
			time1+=timeMiddle;
			opencv_imgproc.line(image, 
					new Point(10*time1, 10*craneBTask.get(i).getTask().getStart()), 
					new Point(10*(time1+craneBTask.get(i).getTimeUse()), 10*craneBTask.get(i).getTask().getEnd()), 
					new Scalar(0, 255, 0, 0),2,0,0);
			time1+=craneBTask.get(i).getTimeUse();
		}
		Mat tmp=image.clone();
		Mat t=new Mat(tmp.size(), opencv_core.CV_32SC1);
		opencv_imgproc.cvtColor(tmp, tmp, opencv_imgproc.COLOR_RGB2GRAY);
		opencv_imgproc.threshold(tmp, tmp, 12, 255, opencv_imgproc.THRESH_BINARY);
		opencv_imgproc.erode(tmp,tmp, 
				opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ERODE, new Size(5,5)));
		opencv_imgproc.Canny(tmp, tmp, 50, 100);
		MatVector tmpContours=new MatVector();
		opencv_imgproc.findContours(tmp, tmpContours, opencv_imgproc.CHAIN_APPROX_SIMPLE,opencv_imgproc.CHAIN_APPROX_NONE);
		for(int i=0;i<tmpContours.size();i++) {
			opencv_imgproc.drawContours(image, tmpContours, i, new Scalar(0, 0, 0, 255));
		}
		window.imshow(tmpContours.size()+"个", image);
		window.waitKey();
		rtv=tmpContours.size()>0?true:false;
		return rtv;
	} 
	
	/**
	 * @param craneATask A天车任务
	 * @param craneBTask B天车任务
	 * @param cranesLocation 系统中多个天车的位置，入0分库的1号天车在100Row处，则为cranesLocation[0][1]=100
	 * @return 总体路径之和
	 */
	private int pathLengthAll(List<CraneTask> craneATask,List<CraneTask> craneBTask,int[][] cranesLocation) {
		int subWarehouse=craneATask.size()>0?craneATask.get(0).getTask().getSubWarehouse():craneBTask.get(0).getTask().getSubWarehouse();
		int rtv=0;
		for(int i=0;i<craneATask.size();i++) {
			rtv+=Math.abs(craneATask.get(i).getTask().getStart()-(i==0?cranesLocation[subWarehouse][0]:craneATask.get(i-1).getTask().getEnd()));
			rtv+=Math.abs(craneATask.get(i).getTask().getEnd()-craneATask.get(i).getTask().getStart());
		}
		for(int i=0;i<craneBTask.size();i++) {
			rtv+=Math.abs(craneBTask.get(i).getTask().getStart()-(i==0?cranesLocation[subWarehouse][1]:craneBTask.get(i-1).getTask().getEnd()));
			rtv+=Math.abs(craneBTask.get(i).getTask().getEnd()-craneBTask.get(i).getTask().getStart());
		}
		return rtv;
	}
	
	public RowInfo getBreakPosition(boolean ifRaw,int warehouse,String ctype,boolean ifInput) throws SQLException, ClassNotFoundException {
		RowInfo row=new RowInfo();
		row.setId(-1);
		conn=conncet();
		PreparedStatement prmt=conn.prepareStatement("select * from row_info where layers<max_layers "
					+ "and state=? and category=? and sub_warehouse_id=? and cb_type_id=? order by row_id ASC");//最新的
		prmt.setString(1, ifInput?"I":"O");
		prmt.setString(2, ifRaw?"G":"B");
		prmt.setInt(3, warehouse);
		prmt.setString(4, ctype);
		ResultSet r=prmt.executeQuery();
		while(r.next()){
			row.setId(r.getInt("row_id"));
			row.setState(r.getString("state"));
			row.setcType(r.getString("type_id_list"));
			row.setLayers(r.getInt("layers"));
			row.setPosx(r.getInt("row_posx"));
			row.setMax(r.getInt("max_layers"));
			row.setIfRaw(r.getBoolean("category"));
			break;
		}
		conn.close();
		return row;
	}
	
	public RowInfo getBlankOrFullPostion(boolean ifRaw,int warehouse,String ctype,boolean ifInput) throws SQLException, ClassNotFoundException {
		RowInfo row=new RowInfo();
		row.setId(-1);
		conn=conncet();
		PreparedStatement prmt;
		if(ifRaw) {
			prmt=conn.prepareStatement("select * from row_info where layers=? "
				+ "and state=? and category_list like '%G%' and sub_warehouse_id=? "
				+ "and type_id_list like ? and category=? order by row_id ASC");
		}else {
			prmt=conn.prepareStatement("select * from row_info where layers=? "
				+ "and state=? and category_list like '%B%' and sub_warehouse_id=? "
				+ "and type_id_list like ? and category=? order by row_id DESC");
		}
		prmt.setInt(1, ifInput?0:8);
		prmt.setString(2, ifInput?"I":"O");
		prmt.setInt(3, warehouse);
		prmt.setString(4, "%"+ctype+"%");
		prmt.setString(5, ifInput?"":(ifRaw?"G":"B"));
		ResultSet r=prmt.executeQuery();
		while(r.next()){
			row.setId(r.getInt("row_id"));
			row.setState(r.getString("state"));
			row.setcType(r.getString("type_id_list"));
			row.setLayers(r.getInt("layers"));
			row.setPosx(r.getInt("row_posx"));
			row.setMax(r.getInt("max_layers"));
			row.setIfRaw(r.getBoolean("category"));
			break;
		}
		conn.close();
		return row;
	}
	
	private void lockRow(int id) throws SQLException, ClassNotFoundException {
		conn=conncet();
		PreparedStatement prmt=conn.prepareStatement("update row_info set state='L' where row_id=?");
		prmt.setInt(1, id);
		prmt.execute();
		conn.close();
	}
}
