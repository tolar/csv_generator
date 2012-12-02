package controllers

import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import models.DAO
import models.User
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import play.api.cache.Cache
import play.api.Play.current
import models.GenerationSession

object Application extends Controller {
  
  val LOGGED: String = "logged"
  val UUID = "uuid"  
       
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def logout = Action { implicit request =>
    Ok(views.html.index()).flashing("successKey" -> "user_was_logout").withNewSession
  }
  
  
    
  def getCacheId(session: Session) : String = {
	  session.get(UUID).get    	  
  }    
  
  def connect(session: Session, user: User) : Session = {
    session.+(LOGGED, user.id.toString())
  }

  def connectedUser(session: Session) : Option[User] = {
    val id = session.get(LOGGED)
    id match {
    	case Some(id) => DAO.findUserById(id.toLong)       	
     	case None => None
    }          	     
  } 
  
  def getXml(gs: GenerationSession) : String = {
    val baos = new ByteArrayOutputStream()

	val xmlEncoder = new XMLEncoder(baos);
	xmlEncoder.writeObject(gs);
	xmlEncoder.close();			
		
	baos.toString();
 }  
  
  def getGs(xml: String) : GenerationSession = {
	val bais = new ByteArrayInputStream(xml.getBytes());
	val xmlDecoder = new XMLDecoder(bais);
	var gs = xmlDecoder.readObject().asInstanceOf[GenerationSession]
	xmlDecoder.close();

	gs
}  
  	
  
  def getSessionValue(session: Session): GenerationSession = {
    
	val user = connectedUser(session);	
	
	user match {
	  // there is logged in user
	  case Some(user) => {
	    user.generationSession match {
	      case None => {
	        // logged user does not have generation session in DB - create it
	        user.generationSession = Option(getXml(new GenerationSession()))	        
	        DAO.updateUserSession(user.id.get, user.generationSession.get)
	        return getGs(user.generationSession.get)
	      }	      
	      case Some(gs) => {
	        // logged user has generation session in DB
	        return getGs(gs)
	      }
	    }
	  }
	  // no logged in user
	  case None => {
		val gs = Cache.get(getCacheId(session)).asInstanceOf[Option[GenerationSession]]
		gs match {
		  // there is no generation session in HTTP session
		  case None => {
		    val gs = new GenerationSession()
		    Cache.set(getCacheId(session), gs)
		    println("getSessionValue" + gs)
		    return gs
		  }
		  case Some(gs) => {
		    println("getSessionValue" + gs)
		    return gs
		  }
		}		    
	  }
	}
  }
  
    
  def updateSessionValue(gs: GenerationSession, session: Session) {
	val user = connectedUser(session);
	user match {
	  case Some(user) => {
	    user.generationSession = Option(getXml(gs))
	    DAO.updateUserSession(user.id.get, user.generationSession.get)
	  }
	  case None => {
	    Cache.set(getCacheId(session), gs)
	  }
	}
	
  }      
  
        
    /*
    
    def processLogin(String username, String password) {
    	
    	UserLogin userLogin = new UserLogin(username, password);
    	validation.valid(userLogin);
    	
        if(validation.hasErrors()) {
        	renderArgs.put("username", username);
        	renderArgs.put("password", password);
            render("@login");
        }     	
    	
    	User storedUser = User.find("byUsername", username).first();
    	if (storedUser == null) {
    		validation.addError("login.message", Messages.get("username_does_not_exist"));
    	} else {
    		if (Codec.hexMD5(password).equals(storedUser.passwordHash)) {
    			connect(storedUser);
    			index();
    		} else {
    			validation.addError("login.message", Messages.get("password_not_valid_for_user"));
    		}
    	}
    	
        if(validation.hasErrors()) {
        	renderArgs.put("username", username);
        	renderArgs.put("password", password);
            render("@login");
        } 
    }     
    

    def step1() {
    	GenerationSession gs = getSessionValue();
    	renderArgs.put("step1", gs.getStep1Params());
        render();
    }

    def step2_fromStep1(@Valid Step1Params step1) {

        if(validation.hasErrors()) {
        	renderArgs.put("step1", step1);
            render("@step1");
        }

    	GenerationSession gs = updateSession(step1);
    	renderArgs.put("gs", gs);

        Step2Params step2 = gs.getStep2Params();
        renderArgs.put("step2", step2);

        render("@step2");
    }

	def step2_addCellValue(@Valid Step2Params step2) {

		GenerationSession gs = getSessionValue();
    	renderArgs.put("gs", gs);

        if(validation.hasErrors()) {
        	renderArgs.put("step2", step2);
            render("@step2");
        }

        gs = updateSession(step2);
        renderArgs.put("gs", gs);
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
    		renderArgs.put("message", Messages.get("no_value_defined"));
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
    */
    
    def feedback = Action { index() } 
    
    def processFeedback() {
    	/*
    	Logger.info(description);
    	
    	Feedback feedback = new Feedback(description, connectedUser() != null ? connectedUser().username : null);
    	validation.valid(feedback);
    	
        if(validation.hasErrors()) {
            render("@feedback");
        } else {
        	feedback.save();
        	index();
        }
        */
    	
    }
    
    /*

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
	*/
    

    
    /*

    static void connect(User user) {
    	Logger.info("user logged in - userId:" + user.id);
        session.put("logged", user.id);
    }

    static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }

  */
    
    

}