package controllers

import play.api._
import play.api.mvc._
import play.api.i18n.Messages
import play.api.i18n.Lang
import play.api.data._
import play.api.data.Forms._
import views.html.defaultpages.badRequest
import models._
import org.apache.commons.codec.digest.DigestUtils

object Application extends Controller {
  
	//val Home = Redirect(routes.Application.index);
  
	/*
    @Before
    def globals() {
        renderArgs.put("connected", connectedUser());
    }
    */

	def index(messageToUser: String = "") = Action {
	  Ok(views.html.index(messageToUser))
	} 
    
    case class User(
        username: String, 
        password: String, 
        passwordConfirm: String)
    
    val registrationForm = Form[User] (
        mapping (
            "username" -> text(minLength = 6),
            "password" -> tuple (
                "main" -> text(minLength = 6),
                "again" -> text
            ).verifying (Messages("password_not_same"), passwords => passwords._1 == passwords._2)
            
        ) 
        {
          (username, passwords) => User(username, passwords._1, "")
        }
        {
          user => Some(user.username, (user.password, ""))
        }.verifying(
            Messages("username_already_exists"), 
            user => DAO.findUserByUsername(user.username).isEmpty
        )
       
    )
    
    def registration = Action {
      Ok(views.html.registration(registrationForm));
    }
    
    def register = Action { implicit request => 
      	registrationForm.bindFromRequest.fold(
      			errors => BadRequest(views.html.registration(errors)),
      			user => {
      			  DAO.insertUser(user.username, DigestUtils.md5Hex(user.password))
      			  Redirect(routes.Application.index(Messages("registration_successfull")))
      			}
    	)
    }    
    
    
    def login = Action {
      Ok(views.html.login());
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
    
    def logout = Action {
    	//Session. .remove("logged");
    	index()
    }
    
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