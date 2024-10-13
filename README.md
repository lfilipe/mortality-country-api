The application is running at:

http://localhost:8082/

OpenAPI definition - Swagger UI:

http://localhost:8082/swagger-ui/index.html


### Instructions how to build and run the project locally

## Steps to deploy the app

1. Clone the repo
   ```sh
   $ git clone https://github.com/lfilipe/mortality-country-api.git
   ```
2. Access dir
   ```sh
   $ cd mortality-country-api/
   
3. Compile and generate jar
   ```sh
   ./mvnw clean package 
   ```
4. Running the application
   ```sh
   java -jar target/mortality-country-api.jar
   ```


[Postman Collection Download](https://github.com/lfilipe/mortality-country-api/blob/main/src/main/resources/MortalityAPI.postman_collection.json)


[Example .csv file to upload](https://github.com/lfilipe/mortality-country-api/blob/main/src/main/resources/teste.csv)




###### Preconditions
> Year records must be between `1960` and `2023`

> The file upload operation works if all the records in the lines are valid; otherwise, it does not insert anything.

> It is being validated that the number of deaths entered must be less than the population gender of that country in that year.

