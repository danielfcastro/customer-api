package com.daniel.test.service;

import com.daniel.Application;
import com.daniel.test.AbstracBaseClass;
import com.daniel.test.util.TestUtil;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
public class CustomerServiceIntegrationEthiopiaTest extends AbstracBaseClass {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	// CustomerController controller;

	
	@Test
	public void getAllCustomersFilteredByCountryEthiopia() throws Exception {
		String url = "http://localhost:8080/v1/customers?country=251";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String linkText = null;
		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		TestUtil.getPageMap(pageMap, content);

		linkText = JsonPath.from(content).get("links").toString();
		int numberOfLinks = TestUtil.countLinks(linkText);
		assertEquals(HttpStatus.OK.value(), status);
		assertEquals(10, pageMap.get("size").intValue());
		assertEquals(9, pageMap.get("totalElements").intValue());
		assertEquals(1, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(1, numberOfLinks);
	}
	
	@Test
	public void getAllCustomersFilteredByCountryEthiopiaValid() throws Exception {
		String url = "http://localhost:8080/v1/customers?country=251&state=1";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String linkText = null;
		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		TestUtil.getPageMap(pageMap, content);

		linkText = JsonPath.from(content).get("links").toString();
		int numberOfLinks = TestUtil.countLinks(linkText);
		assertEquals(HttpStatus.OK.value(), status);
		assertEquals(10, pageMap.get("size").intValue());
		assertEquals(7, pageMap.get("totalElements").intValue());
		assertEquals(1, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(1, numberOfLinks);
	}
	
	@Test
	public void getAllCustomersFilteredByCountryEthiopiaNotValid() throws Exception {
		String url = "http://localhost:8080/v1/customers?country=251&state=0";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		String linkText = null;
		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		TestUtil.getPageMap(pageMap, content);

		linkText = JsonPath.from(content).get("links").toString();
		int numberOfLinks = TestUtil.countLinks(linkText);
		assertEquals(HttpStatus.OK.value(), status);
		assertEquals(10, pageMap.get("size").intValue());
		assertEquals(2, pageMap.get("totalElements").intValue());
		assertEquals(1, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(1, numberOfLinks);
	}
}
