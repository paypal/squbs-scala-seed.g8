Getting Started
---------------

1. Run `sbt` from root directory to enter the interactive mode.

2. Start the server by using `re-start` or `run`.

3. Run test coverage by `;clean;coverage;test`.

4. URLs:
   * http://localhost:8080/hello: Simple hello response
   * http://localhost:8080/hello/{some_name}: Hello response greeting name and return Json response
   * http://localhost:8080/hello/{some_name}/{delay}: Sends chunked response in intervals with delay in milliseconds

5. Console URL: http://localhost:8080/adm

6. `sbt docker` to create a docker image.

7. `docker run -p 8080:8080 <IMAGE NAME>` to run as a docker container.

Most important - have fun!
--------------------------
