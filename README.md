# spring-boot
* Spring boot bootstraps spring framework application which is stand alone and production grade and can be just run
* Spring boot project is technically driven bring spring MVC
* Spring boot parent define all the maven configurations(things like dependency version, properties etc) 
* Spring-boot-starter-web is the only starter dependency which is needed to build a simple spring web application. This bundles all the basic dependencies like spring MVC, tomcat server etc.
* Compatibility among all the dependencies is maintained by sping parent which is called as Bill of Material
* Spring MVC maps the endpoints with HTTP methods to java menthods and when menthod returns, it converts the response to a proper response and send to caller.
* Resources are exposed as REST API's. Identify the nouns/resources to expose them as API's.
* Endpoint URL should be plural of resource name
  * GET /topics           Response Code:  200
  * GET /topics/<id>      Response Code:  200
  * POST /topics          Response Code:  201
  * PUT /topics/<id>      Response Code:  200
  * DELETE /topics/<id>   Response Code:  204
  
  
  
