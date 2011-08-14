package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void step1() {
    	//System.out.println("step1" + gs);
    	GenerationSession gs = getSessionValue();
    	//System.out.println("PARAMS:" + params);
    	//Step1Params params = gs.getStep1Params(gs);
    	//Step1Params step1 = new Step1Params();
    	//step1.columns = "22";
    	
    	renderArgs.put("step1", gs.getStep1Params());
        render();
    }

    public static void step2_fromStep1(@Valid Step1Params step1) {
    	System.out.println("step2:");
    	
    	GenerationSession gs = updateSession(step1);
    	renderArgs.put("gs", gs);
    	
        if(validation.hasErrors()) {
        	System.out.println("step1 errors");
        	renderArgs.put("step1", step1);
            render("@step1");
        }
        Step2Params step2 = gs.getStep2Params();
        renderArgs.put("step2", step2);
        
        render("@step2");
    }
    
	public static void step2_addCellValue(@Valid Step2Params step2) {
    	System.out.println("step2:" + step2);
	
		GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);
    	
        if(validation.hasErrors()) {
        	System.out.println("step2 errors");
        	renderArgs.put("step2", step2);
            render("@step2");
        }
  
        gs = updateSession(step2);
        step2.cellValue = "";
        renderArgs.put("step2", step2);
        render("@step2");
    }    
	


	public static void step3() {
    	GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);
        render("@step3");
    }
	
	public static void newMatrix(String submitPrev, String submitNext, List<String> matrix) {
		System.out.println("submitPrev:" + submitPrev);
		System.out.println("submitNext:" + submitNext);
		if (submitPrev != null) {
			processMatrix(matrix);
			render("@step2");
		} else {
			processMatrix(matrix);
			render("@step4");
		}
	}
	

	private static void processMatrix(List<String> matrix) {
		System.out.println("matrix:" + matrix);
    	GenerationSession gs = getSessionValue();
    	int rows = Integer.parseInt(gs.rows);
    	int columns = Integer.parseInt(gs.columns);
    	int index = 0;
    	String[][] gsMatrix = new String[rows][columns];
    	for (int i = 0; i < rows; i++) {
    		for (int j = 0; j < columns; j++) {
    			gsMatrix[i][j] = matrix.get(index++);
    		}
    	}  
    	gs = updateSession(gsMatrix);
    	renderArgs.put("gs", gs);
	}
	
    public static void step4() {
        render();
    }

    public static void step5() {
        render();
    }

    public static void generate() {

    }

	private static String getCacheId() {
		return session.getId() + "generation";
	}
	
	private static GenerationSession getSessionValue() {
		
		GenerationSession gs = Cache.get(getCacheId(), GenerationSession.class);
		if (gs == null) {
						
			gs = new GenerationSession();
			Cache.add(getCacheId(), gs);
				
		
			System.out.println("GSV: gs added to cache");
			//cachedGs = gs;
		} 
		System.out.println("GSV:cache value:" + gs);
		return gs;
	}
	
	private static void updateSessionValue(GenerationSession gs) {
		System.out.println("USV:cache value:" + gs);
		Cache.replace(getCacheId(), gs);
	}
	
    private static GenerationSession updateSession(Step1Params step1) {    	
    	GenerationSession gs = getSessionValue();
    	gs.columns = step1.columns;
    	gs.rows = step1.rows;
    	gs.reallocateMatrix();
    	updateSessionValue(gs);
    	return gs;
	}
    
    private static GenerationSession updateSession(Step2Params step2) {
    	GenerationSession gs = getSessionValue();
    	gs.cellValues.add(step2.cellValue);
    	updateSessionValue(gs);
    	return gs;
	}    	

	private static GenerationSession updateSession(String[][] gsMatrix) {
    	GenerationSession gs = getSessionValue();
    	gs.matrix = gsMatrix;
    	updateSessionValue(gs);
    	return gs;
	}
    
//	private static GenerationSession synchronizeWithSession() {
//		
//		GenerationSession gs = Cache.get(getCacheId(), GenerationSession.class);
//		if (gs == null) {
//			System.out.println("SYNC:in:null");
//			gs = Cache.get(getCacheId(), GenerationSession.class);
//			if (gs == null) {
//				System.out.println("SYNC: gs is not in cache");
//				gs = new GenerationSession();
//				Cache.add(getCacheId(), gs);
//				System.out.println("SYNC: gs added cache");
//			}
//			System.out.println("SYNC: gs was in cache");
//			//cachedGs = gs;
//		} else {
//			System.out.println("SYNC: replace gs on session");
//			
//		}
//		System.out.println("SYNC:result" + gs);
//		return gs;
//	}


}