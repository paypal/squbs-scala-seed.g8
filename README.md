Getting Started
---------------

* Run `sbt` from root directory to enter the interactive mode.

* Start the server by using `re-start` or `run`.

* Run test coverage by `;clean;coverage;test`.

* Endpoints:
   * GET [http://localhost:8080](http://localhost:8080): Simple `Hello!` response in `text/plain`
   * GET [http://localhost:8080/hello](http://localhost:8080/hello): Greeting `anonymous` in `text/plain`: 

      ```
      Hello anonymous welcome to squbs!
      ```
   * GET [http://localhost:8080/hello/{name}](http://localhost:8080/hello/John): Greeting `{name}` in `application/json`.  This endpoint shows example usage of marshallers. 
   
      ```
      {
          "message": "Hello John welcome to squbs!"
      }
      ```
      
   * GET [http://localhost:8080/hello/{name}/{delay}](http://localhost:8080/hello/John/1000): Greeting `{name}` in `text/plain` with `Transfer-Encoding: "chunked"`.  This example demonstrates the usage of Akka Streams.  Each word is sent as a chunk with the provided delay in milliseconds.  For instance, if `name` is set to `John` and `delay` is set to `1000`, then it will send each word with 1 second delay:

      ```
      Hello 
      Hello John
      Hello John welcome 
      Hello John welcome to 
      Hello John welcome to squbs!
      ```   

   * POST [http://localhost:8080/hello]() with Json body:

      ```
      { "who" : "John" }
      ```
       
      Greeting `who` in `application/json`.  This endpoint demonstrates example usage of marshallers and unmarshallers.

      ```
      {
          "message": "Hello John welcome to squbs!"
      }
      ```

* Admin Console: [http://localhost:8080/adm](http://localhost:8080/adm)   lists all JMX beans in Json format.

* `sbt docker` to create a docker image.

* `docker run -p 8080:8080 <IMAGE NAME>` to run as a docker container.

Most important - have fun!
--------------------------
