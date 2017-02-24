package com.choym.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tmatesoft.svn.core.SVNException;

import com.choym.model.RepositoryLocationInfo;
import com.choym.service.MainService;

@RestController("MainController")
public class MainController {
	@Autowired
	MainService service;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<String> index() {
		return new ResponseEntity<String>("Welcome SVN Server!", HttpStatus.OK);
	}

	@RequestMapping(value = "/sayHello/", method = RequestMethod.GET)
	public ResponseEntity<String> sayHello() {
		return new ResponseEntity<String>(service.onSayHello(), HttpStatus.OK);
	}

	/**
	 * Printing out a Subversion repository tree
	 * @param rli RepositoryLocationInfo
	 * @return
	 */
	@RequestMapping(value = "/displayRepositoryTree/", method = RequestMethod.POST)
	public ResponseEntity<String> displayRepositoryTree(@RequestBody RepositoryLocationInfo rli) {
		ResponseEntity<String> response = null;
		
		// set response header with utf-8
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
				
		try {
			response = new ResponseEntity<String>(service.onDisplayRepositoryTree(rli), responseHeaders, HttpStatus.OK);
		} catch (SVNException e) {
			response = new ResponseEntity<String>("ERROR on displayRepositoryTree", HttpStatus.CONFLICT);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/update/", method = RequestMethod.POST)
	public ResponseEntity<Object> update(@RequestBody RepositoryLocationInfo rli) {
		ResponseEntity<Object> response = null;
		
		// set response header with utf-8
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
				
		try {
			response = new ResponseEntity<Object>(service.onUpdate(rli, new File("D://SQLInceptor//")), responseHeaders, HttpStatus.OK);
		} catch (SVNException e) {
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/checkStatus/", method = RequestMethod.POST)
	public ResponseEntity<String> checkStatus(@RequestBody RepositoryLocationInfo rli) throws SVNException {
		File file = new File("D:\\SQLInceptor\\file.txt");
		
		return new ResponseEntity<String>(service.onShowStatus(rli, file), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/add/", method = RequestMethod.POST)
	public ResponseEntity<String> add(@RequestBody RepositoryLocationInfo rli) throws SVNException {
		List<File> fileList = new ArrayList<>();
		fileList.add(new File("D:\\SQLInceptor\\choym"));// add a directory
		fileList.add(new File("D:\\SQLInceptor\\file3.txt"));// add a file
		File[] fileArr = new File[fileList.size()];
		fileArr = fileList.toArray(fileArr);
		
		return new ResponseEntity<String>(service.onAdd(rli, fileArr), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/delete/", method = RequestMethod.POST)
	public ResponseEntity<String> delete(@RequestBody RepositoryLocationInfo rli) throws SVNException {
		List<File> fileList = new ArrayList<>();
		fileList.add(new File("D:\\SQLInceptor\\choym"));// delete a directory
		fileList.add(new File("D:\\SQLInceptor\\file3.txt"));// delete a file
		File[] fileArr = new File[fileList.size()];
		fileArr = fileList.toArray(fileArr);
		
//		File[] fileArr = new File[]{new File("D:\\SQLInceptor\\file.txt")};
		return new ResponseEntity<String>(service.onDelete(rli, fileArr), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/commit/", method = RequestMethod.POST)
	public ResponseEntity<Object> commit(@RequestBody RepositoryLocationInfo rli) {
		ResponseEntity<Object> response = null;
		
		// set response header with utf-8
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
				
		try {
//			List<File> fileList = new ArrayList<>();
//			fileList.add(new File("D:\\SQLInceptor\\choym"));// commit a directory
//			fileList.add(new File("D:\\SQLInceptor\\file3.txt"));// commit a file
//			File[] fileArr = new File[fileList.size()];
//			fileArr = fileList.toArray(fileArr);
			
			File[] fileArr = new File[]{new File("D:\\SQLInceptor\\")};
			
			response = new ResponseEntity<Object>(service.onCommit(rli, fileArr, "commit by choym"), HttpStatus.OK);
		} catch (SVNException e) {
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/checkout/", method = RequestMethod.POST)
	public ResponseEntity<Object> checkout(@RequestBody RepositoryLocationInfo rli) {
		ResponseEntity<Object> response = null;
		
		// set response header with utf-8
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
				
		try {
			response = new ResponseEntity<Object>(service.onCheckout(rli), responseHeaders, HttpStatus.OK);
		} catch (SVNException e) {
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return response;
	}
}