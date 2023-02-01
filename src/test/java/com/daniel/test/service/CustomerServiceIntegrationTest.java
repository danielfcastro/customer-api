package com.daniel.test.service;

import com.daniel.Application;
import com.daniel.db.model.Customer;
import com.daniel.test.AbstracBaseClass;
import com.daniel.test.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CustomerServiceIntegrationTest extends AbstracBaseClass {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	// CustomerController controller;

	@Test
	public void getCustomer() throws Exception{
		String url = "http://localhost:8080/v1/customers/3";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}

	@Test
	public void getNonExitantCustomer() throws Exception{
		String url = "http://localhost:8080/v1/customers/321";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(204, status);
	}

	@Test
	public void getNonExitantCustomerNegativeId() throws Exception{
		String url = "http://localhost:8080/v1/customers/-321";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
	}

	@Test
	public void saveAndDeleteCustomer() throws Exception{
		String url = "http://localhost:8080/v1/customers";
		Customer customer = new Customer();
		customer.setCode(237);
		customer.setName("Daniel Castro");
		customer.setPhone("(237) 912072339");
		customer.setValid(0);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(customer);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(url).content(json).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		json = mvcResult.getResponse().getContentAsString();
		assertEquals(201, status);
		mvcResult = mvc.perform(MockMvcRequestBuilders.delete(url).content(json).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(202, status);
	}
	@Test
	public void getAllCustomersWithoutFilter() throws Exception {
		String url = "http://localhost:8080/v1/customers";
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
		assertEquals(41, pageMap.get("totalElements").intValue());
		assertEquals(5, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(4, numberOfLinks);
	}

	@Test
	public void getNoContent() throws Exception {
		String urlNoContent = "http://localhost:8080/v1/customers?country=1";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(urlNoContent).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(HttpStatus.NO_CONTENT.value(), status);
	}

	@Test
	public void followPagesNoFilter() throws Exception {
		String url = "http://localhost:8080/v1/customers";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String linkText = null;
		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		String nextLinkTemplate = "http://localhost:8080/v1/customers?page=PGNUMBER&size=10&sort=id,asc";
		TestUtil.getPageMap(pageMap, content);

		linkText = JsonPath.from(content).get("links").toString();
		int numberOfLinks = TestUtil.countLinks(linkText);
		int totalPages = pageMap.get("totalPages").intValue();
		assertEquals(HttpStatus.OK.value(), status);
		assertEquals(10, pageMap.get("size").intValue());
		assertEquals(41, pageMap.get("totalElements").intValue());
		assertEquals(5, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(4, numberOfLinks);
		
		for(int i=1;i<totalPages;i++) {
			String nextLink = nextLinkTemplate.replaceFirst("PGNUMBER", Integer.toString(i));
			mvcResult = mvc.perform(MockMvcRequestBuilders.get(nextLink).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
			status = mvcResult.getResponse().getStatus();
			content = mvcResult.getResponse().getContentAsString();
			pageMap.clear();
			TestUtil.getPageMap(pageMap, content);
			linkText = JsonPath.from(content).get("links").toString();
			numberOfLinks = TestUtil.countLinks(linkText);
			totalPages = pageMap.get("totalPages").intValue();
			assertEquals(HttpStatus.OK.value(), status);
			assertEquals(10, pageMap.get("size").intValue());
			assertEquals(41, pageMap.get("totalElements").intValue());
			assertEquals(5, pageMap.get("totalPages").intValue());
			assertEquals(i, pageMap.get("number").intValue());
			if(i == totalPages-1) {
				assertEquals(4, numberOfLinks);	
			}else assertEquals(5, numberOfLinks);
		}
	}

	@Test
	public void getAllCustomersFilteredValid() throws Exception {
		String url = "http://localhost:8080/v1/customers?page=0&size=10&sort=id,asc&country=&state=1";
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
		assertEquals(27, pageMap.get("totalElements").intValue());
		assertEquals(3, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(4, numberOfLinks);
	}
	
	@Test
	public void getAllCustomersFilteredNotValid() throws Exception {
		String url = "http://localhost:8080/v1/customers?page=0&size=10&sort=id,asc&country=&state=0";
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
		assertEquals(14, pageMap.get("totalElements").intValue());
		assertEquals(2, pageMap.get("totalPages").intValue());
		assertEquals(0, pageMap.get("number").intValue());
		assertEquals(4, numberOfLinks);
	}		
}
