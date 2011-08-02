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

    public static void step1(GenerationSession gs) {
    	System.out.println("step1" + gs);
    	gs = synchronizeWithSession(gs);
    	System.out.println("step1" + gs);
        render(gs);
    }

    public static void step2_fromStep1(@Valid GenerationSession gs) {
    	System.out.println("step2:");
    	
    	gs = synchronizeWithSession(gs);
    	
    	renderArgs.put("gs", gs);
        if(validation.hasErrors()) {
        	System.out.println("step1 errors");
     
            render("@step1");
        }
        
        render("@step2");
    }

    public static void step2_addCellValue(@Required String cellValue) {
    	System.out.println("step2:" + cellValue);
        if(validation.hasErrors()) {
        	System.out.println("step2 errors");
            render("@step2");
        }
    	GenerationSession gs = synchronizeWithSession();
    	gs.cellValues.add(cellValue);
    	gs = synchronizeWithSession(gs);
    	renderArgs.put("gs", gs);
        render("@step2");
    }

    public static void step3() {
        render();
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

	private static GenerationSession synchronizeWithSession(GenerationSession gs) {
		if (gs == null) {
			System.out.println("SYNC:in:null");
			gs = Cache.get(getCacheId(), GenerationSession.class);
			if (gs == null) {
				System.out.println("SYNC: gs is not in cache");
				gs = new GenerationSession();
				Cache.add(getCacheId(), gs);
				System.out.println("SYNC: gs added cache");
			}
			System.out.println("SYNC: gs was in cache");
			//cachedGs = gs;
		} else {
			System.out.println("SYNC: replace gs on session");
			Cache.replace(getCacheId(), gs);
		}
		System.out.println("SYNC:result" + gs);
		return gs;
	}

	private static GenerationSession synchronizeWithSession() {
		return Cache.get(getCacheId(), GenerationSession.class);
	}


}