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
import java.util.List;

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
	 * @return 完善后的天车任务，自动补充了起点终点等，计划任务之后对row进行锁定
	 * @throws Exception
	 */
	public List<CraneTask> storageTaskMakeUp(List<StorageTask> storageTasks) throws Exception{
		List<CraneTask> craneTasks=new ArrayList<>();
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
//			lockRow(tmpRow.getId());
			CraneTask now=new CraneTask();
			now.setTask(stask);
			craneTasks.add(now);
		}
		Collections.sort(craneTasks,new CraneTask());
		return craneTasks;
	}
	
	
	/**
	 * @param craneTask 输入多个天车任务，没有具体分配
	 * @return 做出具体的最佳分配结果
	 */
	public List<CraneTask> craneTaskDecide(List<CraneTask> craneTask){
		int n=craneTask.size();
		for(int i=0;i< Math.pow(2, n);i++) {
			String code=(Integer.toBinaryString(i)+"00000000").substring(0, n);
			System.out.println(code);
		}
		return craneTask;
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
