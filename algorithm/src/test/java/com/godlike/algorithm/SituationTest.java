package com.godlike.algorithm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.godlike.algorithm.caculate.Situation;
import com.godlike.algorithm.configure.SystemConfigure;
import com.godlike.algorithm.exception.MyException;
import com.godlike.algorithm.model.CBlockType;
import com.godlike.algorithm.model.StorageTask;
import com.godlike.algorithm.model.StorageTask.TaskType;

import junit.framework.TestCase;

public class SituationTest extends TestCase {

    
    public void testGetFinalPosition()
    {
    	Situation s=new Situation();
    	StorageTask task1=new StorageTask();
    	task1.setType(TaskType.RAWOFFLINE);
    	task1.setcType(CBlockType.A);
    	StorageTask task2=new StorageTask();
    	task2.setType(TaskType.MATUREOFFLINE);
    	task2.setcType(CBlockType.A);
    	List<StorageTask>pretasks=new ArrayList<StorageTask>();
    	pretasks.add(task1);
    	pretasks.add(task2);
    	SystemConfigure.storage1Account=0;
    	SystemConfigure.storage2Account=1;
    	
        try {
        	List<StorageTask> tasks=s.storageTaskMakeUp(pretasks);
			assertEquals(1, tasks.get(0).getEnd());
			assertEquals(0, tasks.get(1).getEnd());
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
