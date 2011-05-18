# Masquerade Service Response Simulator
Masquerade is a configurable Service Simulator that returns simulated responses for service requets.

When building from source, you'll need jms.jar (JMS 1.1) in your Maven repo. See http://code.google.com/p/masqueradesim/wiki/DeveloperGuide for details.

Supports HTTP, JMS listeners by default (extensible) and contains simulation actions for XML request/response scenarios. Response simulations are based on simulation scripts that contain actions such

* Template-based response
*Adding content to the response
* Groovy script step
* Ruby script step
* Java Script step
* Synchronous/asynchronous responses
* Wait step
* Variable management and substitution steps
* ... and many more 

The simulator also provides an API to query for received requests (useful when asserting request contents in integration tests), and to specifiy expected responses externally before issuing requests. 

The project page on Google Code at http://code.google.com/p/masqueradesim/ contains release downloads as well as user and developer documentation.
