package com.choym.controller;

import com.choym.model.RepositoryLocationInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class test {

	public static void main(String[] args) throws JsonProcessingException {
		RepositoryLocationInfo rli = new RepositoryLocationInfo("https://10.40.41.119:11443/svn/TESTSVN", null, "06200", "06200");
		
		System.out.println(new ObjectMapper().writeValueAsString(rli));
//		{"url":"https://10.40.41.119:11443/svn/TESTSVN","label":null,"username":"06200","password":"06200"}

	}

}
