package sqli.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.model.Column;
import database.model.Database;
import database.model.Table;

import sqli.model.InjectionModel;

public class InjectionController {

	private InjectionModel injectionModel;
	
	public InjectionController(InjectionModel newModel){
		injectionModel = newModel;
	}
	
	public void controlInput(String getData, String postData, String cookieData, String headerData, String method, 
			boolean isProxy, String proxyAdress, String proxyPort) {
		try {
			injectionModel.setProxyfied(isProxy);
			injectionModel.setProxyAdress(proxyAdress);
			injectionModel.setProxyPort(proxyPort);
			
			Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(getData);
			if(regexSearch.find()){
				URL url = new URL( getData );
				injectionModel.setInitialUrl( regexSearch.group(1) );
				if( !url.getQuery().equals("") )
					injectionModel.setGetData( regexSearch.group(2) );
			}else{
				injectionModel.setInitialUrl( getData );
			}
			
			injectionModel.setPostData(postData);
			injectionModel.setCookieData(cookieData);
			injectionModel.setHeaderData(headerData);
			injectionModel.setMethod(method);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					injectionModel.inputValidation();
				}
			}).start();
			
		} catch (MalformedURLException e) {
			injectionModel.sendMessage(e.getMessage());
		}
	}

	public void selectDatabase(final Database databaseSelected){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				injectionModel.listTables(databaseSelected);
			}
		}).start();
		
	}

	public void selectTable(final Table selectedTable) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				injectionModel.listColumns(selectedTable);
			}
		}).start();
		
	}
	
	public void selectValues(final List<Column> values) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				injectionModel.listValues(values);
			}
		}).start();
		
	}
}
