# Masquerade Service Response Simulator
Masquerade is a configurable Service Response Simulator that returns simulated responses for service requets.

Supports HTTP, JMS listeners by default (extensible) and contains simulation actions for XML request/response scenarios. Response simulations are based on simulation scripts that contain actions such

* Template-based response
* Adding elements to the request
* Groovy script steps
* Synchronous/asynchronous responses
* Wait steps
* ... any many more 

The simulator also provides an API to query for received requests (useful when asserting request contents in integration tests), and to specifiy expected responses externally before issuing requests. 

The project page on Google Code at http://code.google.com/p/masqueradesim/ contains release downloads.
