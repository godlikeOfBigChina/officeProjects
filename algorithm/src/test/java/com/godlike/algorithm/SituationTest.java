package com.godlike.algorithm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.godlike.algorithm.caculate.Situation;
import com.godlike.algorithm.configure.SystemConfigure;
import com.godlike.algorithm.exception.MyException;
import com.godlike.algorithm.model.CBlockType;
import com.godlike.algorithm.model.CraneTask;
import com.godlike.algorithm.model.StorageTask;
import com.godlike.algorithm.model.StorageTask.TaskType;

import junit.framework.TestCase;

public class SituationTest extends TestCase {

    
    public void testGetFinalPosition()
    {
    	Situation s=new Situation();
    	StorageTask task1=new StorageTask();
    	task1.setType(TaskType.MATUREOFFLINE);
    	task1.setcType(CBlockType.A);
    	StorageTask task2=new StorageTask();
    	task2.setType(TaskType.RAWOFFLINE);
    	task2.setcType(CBlockType.A);
    	StorageTask task3=new StorageTask();
    	task3.setType(TaskType.MATUREOUT);
    	task3.setcType(CBlockType.A);
    	StorageTask task4=new StorageTask();
    	task4.setType(TaskType.RAWFIRE);
    	task4.setcType(CBlockType.A);
    	List<StorageTask>pretasks=new ArrayList<StorageTask>();
    	pretasks.add(task1);
    	pretasks.add(task2);
    	pretasks.add(task3);
    	pretasks.add(task4);
        try {
        	List<CraneTask> tasks=s.storageTaskMakeUp(pretasks);
/*        	for(CraneTask tk:tasks) {
        		System.out.println(tk.getTask().getType());
        	}*/
			assertEquals(TaskType.RAWOFFLINE, tasks.get(0).getTask().getType());
			assertEquals(TaskType.MATUREOFFLINE, tasks.get(1).getTask().getType());
			assertEquals(TaskType.RAWFIRE, tasks.get(2).getTask().getType());
			assertEquals(TaskType.MATUREOUT, tasks.get(3).getTask().getType());
			s.craneTaskDecide(tasks);
		} catch (ClassNotFoundException e) {
			System.out.println("jdbc driver not found");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			if(e instanceof MyException) {
				System.out.println(e.getMessage());
			}else {
				e.printStackTrace();
			}
			
		};
    }
    
}
