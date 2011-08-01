package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Valid;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void step1() {
    	System.out.println("step1");
    	GenerationSession gs = Cache.get(getCacheId(), GenerationSession.class);
    	if (gs == null) {
    		gs = new GenerationSession();
    		Cache.add(getCacheId(), gs);
    	}
        render(gs);
    }

    public static void step2(@Valid GenerationSession gs) {
    	System.out.println("step2:" + gs);
        if(validation.hasErrors()) {
        	System.out.println("errors");
            render("@step1");
        }
        render();
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

}