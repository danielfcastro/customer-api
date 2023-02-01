# REST API - Customers

This documentation describes aspects and procedures to run this REST API

## Overview

This API was developed using Springboot 2.1.6.  It uses paging to reduce the overall weight over networking and increase the overall performance.

## Running the Example

1. Using the bash or command prompt navigate to the project directory
2. Run the command:
	```docker build -t customer-api .```
3. Run the command
	```docker run -p 8080:8080 customer-api```
4. Visit: <http://127.0.0.1:8080/v1/customers> (should display a JSON with the customers data)
5. Visit <http://localhost:8080/swagger-ui/index.html> (should display the Swagger Interface)

## Concepts

### Features
- Pagination - It uses 2 query string parameters, namely: 'page', 'size' 
- Sorting - It uses the parameter 'sort' to sort the results

### Testing Strategy
- In order to keep the test as close as possible to the real case use I decided to run the tests using SpringBoot and not Mock any interfaces or object, rather than this I run the Springboot application and fire requests as it should be fired from the Frontend.

## License

[MIT](https://opensource.org/licenses/MIT)