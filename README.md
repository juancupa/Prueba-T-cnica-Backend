#  Proyecto Microservicios: Productos e Inventario

##  Descripción General


Este proyecto implementa dos microservicios **Spring Boot** comunicados entre sí:
- **Productos**: gestiona información de productos (nombre, precio).
- **Inventario**: administra las existencias asociadas a cada producto.

Ambos servicios utilizan **PostgreSQL**, están **dockerizados** y se comunican a través de **REST** con autenticación básica mediante API Key.


+-------------------+ +------------------+
| productos (8081) | <---> | inventario (8082)|
+-------------------+ +------------------+
| |
+------ PostgreSQL -------+


- **Comunicación**: REST con 'RestTemplate'
- **Seguridad**: Header 'x-api-key'
- **Timeouts & Retries**: Configurados en 'RestTemplate'
- **Documentación API**: Swagger (OpenAPI 3)
- **Pruebas**: Unitarias e integración con JUnit + Mockito

  ##  Docker Compose

El entorno completo se ejecuta con:

--bash
cada uno de los servisios
 - mvn clean package -DskipTests
prpyecto principal 
 - docker-compose up --build


 Servicio    Puerto  Descripción                   
 ----------  ------  ----------------------------- 
 postgres    5432    Base de datos compartida      
 productos   8081    API de gestión de productos   
 inventario  8082    API de gestión de inventarios 

Seguridad API

Cada microservicio valida la API Key en las solicitudes HTTP usando el header:

x-api-key: SECRET123


 
Productos  (http://localhost:8081/api/productos)
Método	Endpoint	Descripción
POST	  /	Crea    un producto y notifica inventario
GET	    /all	    Lista todos los productos
GET	    /{id}	    Obtiene producto por ID
PUT	    /{id}	    Actualiza un producto
DELETE	/{id}	    Elimina un producto

Inventario  (http://localhost:8082/api/inventarios)
Método	   Endpoint	                          Descripción
POST	     /Crea                              inventario asociado a producto
GET	       /{productoId}	                    Consulta inventario por producto
PUT	       /{productoId}/reducir?cantidad=N 	Reduce stock disponible



Pruebas Automatizadas

Se implementaron pruebas unitarias y de integración con JUnit 5 y Mockito.

Ejecución de todas las pruebas

Tipo	     Microservicio	        Descripción
Unit Test - Creación	            Productos	Verifica creación y guardado correcto
Unit Test - Error	                Productos	Simula timeout o fallo al comunicar con inventario
Unit Test - Reducción	Inventario	Reduce cantidad de stock

Dependencias clave

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- JUnit 5
- Mockito


Documentación API (Swagger UI)
Cada microservicio expone su documentación OpenAPI en:
Microservicio	URL Swagger
Productos	
  -http://localhost:8081/swagger-ui.html
Inventario	
  -http://localhost:8082/swagger-ui.html
Swagger muestra los endpoints disponibles, los modelos y ejemplos de peticiones/respuestas.
