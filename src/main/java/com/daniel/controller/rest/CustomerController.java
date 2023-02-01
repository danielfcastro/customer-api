package com.daniel.controller.rest;

import com.daniel.controller.util.CustomerUtils;
import com.daniel.db.model.Customer;
import com.daniel.db.model.CustomerView;
import com.daniel.db.repositories.ICustomerRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("/v1")
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "2.0", description = "Customer Information"))
/**
 * REST Controller responsible for exposing the methods to be fired/consumed by the front end or any external consumer.
 * @author dfcastro
 *
 */
public class CustomerController {
	Logger logger = LoggerFactory.getLogger(CustomerController.class);

	public static String buildLinkHeader(final String uri, final String rel) {
		return "<" + uri + ">; rel=\"" + rel + "\"";
	}

	@Autowired
	private ICustomerRepository repository;

	@CrossOrigin
	@PostMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Add a customers", description = "Add a customer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "successful operation",
					content = @Content(schema = @Schema(implementation = CustomerView.class))),
			@ApiResponse(responseCode = "500", description = "Can not add the customer") })
	public <T> ResponseEntity createCustomer(@RequestBody Customer customer){
		try{
			customer = repository.save(customer);
			return new ResponseEntity<>(customer, HttpStatus.CREATED);
		}catch(DataAccessException | IllegalArgumentException e){
			return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin
	@DeleteMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a customers", description = "Delete a customer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "successful operation",
					content = @Content(schema = @Schema(implementation = CustomerView.class))),
			@ApiResponse(responseCode = "500", description = "Can not add the customer") })
	public <T> ResponseEntity deleteCustomer(@RequestBody Customer customer){
		try{
			repository.delete(customer);
			return new ResponseEntity<>(customer, HttpStatus.ACCEPTED);
		}catch(DataAccessException e){
			return new ResponseEntity<>(customer, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin
	@GetMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get the customers", description = "Get the customers using pagination and filtert", tags = { "CustomerView" })
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "successful operation",
	                content = @Content(schema = @Schema(implementation = CustomerView.class))),
	        @ApiResponse(responseCode = "204", description = "Customer not found") })
	public <T> ResponseEntity<PagedResources<CustomerView>> getAllCustomers(
			@RequestParam(defaultValue = "0", name = "page") Integer pageNo,
			@RequestParam(defaultValue = "10", name = "size") Integer pageSize,
			@RequestParam(defaultValue = "id", name = "sort") String sortBy,
			@RequestParam(defaultValue = "", name = "country") String country,
			@RequestParam(defaultValue = "", name = "state") String state, PagedResourcesAssembler assembler) {
		if (pageNo < 0)
			pageNo = 0;
		// Pre processing state and country
		if ("".equalsIgnoreCase(state) || "null".equalsIgnoreCase(state) || "undefined".equalsIgnoreCase(state))
			state = null;
		if ("".equalsIgnoreCase(country) || "null".equalsIgnoreCase(country) || "undefined".equalsIgnoreCase(country))
			country = null;
		// End of Pre processing state and country

		// Pre-processing sorting parameters
		String sorting[] = sortBy.split(",");
		Sort sort = Sort.by(sorting[0]);
		Pageable pageable = null;
		Page<Customer> customers = null;
		// End of pre-processing sorting parameters
		
		pageable = createPeageable(pageNo, pageSize, sorting, sort);

		// Selection of what repository method will be consumed based on the numbers of filters.
		if (null == country && state == null)
			customers = repository.findAll(pageable);
		else if (null != country && state == null) {
			customers = repository.findAllByCountry(pageable, Integer.valueOf(country));
		} else if (null == country && state != null) {
			customers = repository.findAllByEstate(pageable, Integer.valueOf(state));
		} else {
			customers = repository.findAllByCountryAndEstate(pageable,Integer.valueOf(country),Integer.valueOf(state));
		}
		
		//Conversion of Customer Entity to CustomerView and later on return a Paged Customer View
		Page<CustomerView> customerViews = customers.map(new Function<Customer, CustomerView>() {
			public CustomerView apply(Customer customer) {
				CustomerView view = null;
				view = new CustomerView();
				view.setId(customer.getId());
				view.setName(customer.getName());
				view.setPhone(customer.getPhone());
				CustomerUtils.setCountryInfo(customer, view);
				CustomerUtils.validatePhone(customer, view);
				return view;
			}
		});


		PagedResources<CustomerView> pr = assembler.toResource(customerViews);

		//Sets the response depending if there is or not content
		if (customerViews.getTotalElements() != 0) {
			return new ResponseEntity<>(pr, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(assembler.toEmptyResource(customerViews, Page.class), HttpStatus.NO_CONTENT);
		}
	}

	@CrossOrigin
	@GetMapping(path = "/customers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get the customers", description = "Get the customer", tags = { "CustomerView" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "successful operation",
					content = @Content(schema = @Schema(implementation = CustomerView.class))),
			@ApiResponse(responseCode = "204", description = "Customer not found"),
			@ApiResponse(responseCode = "404", description = "id null supplied")})
	public <T> ResponseEntity getCustomer(@PathVariable Long id) {
		Optional<Customer> customer = null;

		if (null == id || id.equals(null) || id <0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		else  {
			customer = repository.findById(id);
		}

		if(!customer.isPresent()){
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}else{
			return new ResponseEntity(customer, HttpStatus.OK);
		}

	}

	private Pageable createPeageable(Integer pageNo, Integer pageSize, String[] sorting, Sort sort) {
		Pageable pageable;
		if (sorting.length > 1) {
			if (sorting[1].equalsIgnoreCase("asc"))
				pageable = PageRequest.of(pageNo, pageSize, sort.ascending());
			else
				pageable = PageRequest.of(pageNo, pageSize, sort.descending());
		} else
			pageable = PageRequest.of(pageNo, pageSize, sort.ascending());
		return pageable;
	}
}