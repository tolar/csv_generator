package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.mvc.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import models.*;

public class Application extends Controller {
	
    @Before
    static void globals() {
        renderArgs.put("connected", connectedUser());
    }

    public static void index() {
        render();
    }
    
    public static void registration() {
        render();
    }
    
    public static void login() {
        render();
    }     
    
    public static void register(String username, String password, String passwordConfirm) {
    	
    	User user = new User(username, password, passwordConfirm);
    	
    	User storedUser = User.find("byUsername", username).first();
    	if (storedUser != null) {
    		validation.addError("user.username", "Zadané uživatelské jméno je již v systému použito");
    	}
    	
    	validation.valid(user);
    	
        if(validation.hasErrors()) {
            render("@registration");
        } else {
        	user.generatePassHash();
        	user.save();
        	index();
        }
    }
    
    public static void processLogin(String username, String password) {
    	
    	UserLogin userLogin = new UserLogin(username, password);
    	validation.valid(userLogin);
    	
        if(validation.hasErrors()) {
            render("@login");
        }     	
    	
    	User storedUser = User.find("byUsername", username).first();
    	if (storedUser == null) {
    		validation.addError("login.message", "Zadané uživatelské jméno v systému neexistuje");
    	} else {
    		if (Codec.hexMD5(password).equals(storedUser.passwordHash)) {
    			connect(storedUser);
    			index();
    		} else {
    			validation.addError("login.message", "Zadané heslo není platné pro daného uživatele");
    		}
    	}

        if(validation.hasErrors()) {
            render("@login");
        } 
    }     
    

    public static void step1() {
    	GenerationSession gs = getSessionValue();
    	renderArgs.put("step1", gs.getStep1Params());
        render();
    }

    public static void step2_fromStep1(@Valid Step1Params step1) {


        if(validation.hasErrors()) {
        	System.out.println("step1 errors");
        	renderArgs.put("step1", step1);
            render("@step1");
        }

    	GenerationSession gs = updateSession(step1);
    	renderArgs.put("gs", gs);

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

	public static void step2_removeCellValue(String cellValue) {
		GenerationSession gs = getSessionValue();
		gs.cellValues.remove(cellValue);
		updateSessionValue(gs);
    	renderArgs.put("gs", gs);
        render("@step2");
    }

	public static void step3() {
    	GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);
    	if (gs.cellValues.size() <= 0) {
    		renderArgs.put("message", "Není definována žádná hodnota");
    		render("@step2");
    	} else {
    		render("@step3");
    	}
    }

	public static void newMatrix(String submitPrev, String submitNext, List<String> matrix) {
		if (submitPrev != null) {
			processMatrix(matrix);
			render("@step2");
		} else {
			processMatrix(matrix);
			GenerationSession gs = getSessionValue();
			renderArgs.put("step4", gs.getStep4Params());
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

    	GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);
    	renderArgs.put("step4", gs.getStep4Params());
        render("@step4");
    }

    public static void generationParams(String prevSubmit, String generationSubmit, @Valid Step4Params step4) {

       	GenerationSession gs = getSessionValue();
       	updateSession(step4);
    	renderArgs.put("gs", gs);

        if(validation.hasErrors()) {
        	renderArgs.put("step4", step4);
            render("@step4");
            return;
        }

		if (prevSubmit != null) {
			render("@step3");
		} else {
			generate(step4);
    	}
    }


    public static void generate(Step4Params step4) {

       	GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);


    	final String delimiter = step4.delimiter;
    	final String fileName = step4.fileName;


    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < gs.matrix.length; i++) {
    		for (int j = 0; j < gs.matrix[i].length; j++) {
				sb.append(gs.matrix[i][j]).append(delimiter);
    		}
    		sb.append("\r\n");
    	}

        ByteArrayInputStream bais;
		try {
			bais = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			renderBinary(bais, fileName, "text/csv", true);
		} catch (UnsupportedEncodingException e) {
			error("Unexpected error");
		}

    }

	private static String getCacheId() {
		return session.getId() + "generation";
	}

	private static GenerationSession getSessionValue() {
		
		User user = connectedUser();
		
		if (user != null) {
			if (user.generationSession == null) {
				user.generationSession = getXml(new GenerationSession());
				user.save();
			} 
			return getGs(user.generationSession);
		}

		GenerationSession gs = Cache.get(getCacheId(), GenerationSession.class);
		if (gs == null) {

			gs = new GenerationSession();
			Cache.add(getCacheId(), gs);

		}

		return gs;
	}

	private static void updateSessionValue(GenerationSession gs) {

		
		
		User user = connectedUser();
		if (user != null) {
			user.generationSession = getXml(gs);
			user.save();
		} else {
			Cache.replace(getCacheId(), gs);
		}
	}

	private static String getXml(GenerationSession gs) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(baos);
		xmlEncoder.writeObject(gs);
		xmlEncoder.close();

		return baos.toString();
	}
	
	private static GenerationSession getGs(String xml) {
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
		XMLDecoder xmlDecoder = new XMLDecoder(bais);
		GenerationSession gs = (GenerationSession) xmlDecoder.readObject();
		xmlDecoder.close();

		return gs;
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

    private static GenerationSession updateSession(Step4Params step4) {
    	GenerationSession gs = getSessionValue();
    	gs.delimiter = step4.delimiter;
    	gs.fileName = step4.fileName;
    	updateSessionValue(gs);
    	return gs;
	}
    
    public static void logout() {
    	session.remove("logged");
    	index();
    }

    static void connect(User user) {
        session.put("logged", user.id);
    }

    static User connectedUser() {
    	System.out.println("connectedUser invoked");
        String userId = session.get("logged");
        System.out.println("connectedUser userId:" + userId);
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }


}