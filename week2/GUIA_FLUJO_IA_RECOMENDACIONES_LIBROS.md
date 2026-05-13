# Guia del flujo de IA para recomendaciones de libros

Este documento explica como funciona la integracion actual de IA en el proyecto, por que esta dividida entre `AiLibroService`, `AiConfig`, `LibroAssistant` y `LibroController`, y como podria evolucionar para comportarse mas como una busqueda inteligente: tolerante a errores de escritura, flexible ante consultas incompletas y capaz de devolver libros recomendados con una respuesta similar a un `GET` tradicional.

## Objetivo de la integracion

La aplicacion ya tiene una API REST para gestionar libros. La parte de IA agrega una capacidad adicional:

```text
El usuario escribe una consulta en lenguaje natural.
La aplicacion revisa el catalogo de libros guardado en la base de datos.
La IA recomienda libros del catalogo que coincidan con esa consulta.
```

Ejemplo:

```text
GET /api/libros/ia/recomendaciones?consulta=quiero algo de aventura y fantasia
```

La respuesta actual es texto generado por IA:

```text
Te recomiendo "El Hobbit" porque combina aventura, fantasia y viaje heroico...
```

## Dependencias necesarias

En `pom.xml` se agregaron las dependencias de LangChain4j:

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>1.13.1</version>
</dependency>

<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>1.13.1-beta23</version>
</dependency>
```

La primera dependencia contiene las piezas base de LangChain4j, como `AiServices`, `@SystemMessage` y `@UserMessage`.

La segunda dependencia integra LangChain4j con Spring Boot y crea automaticamente el modelo de chat de OpenAI a partir de las propiedades configuradas.

## Configuracion en application.properties

La configuracion actual esta en `src/main/resources/application.properties`:

```properties
langchain4j.open-ai.chat-model.api-key=demo
langchain4j.open-ai.chat-model.base-url=http://langchain4j.dev/demo/openai/v1
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
```

Estas propiedades le dicen al starter de LangChain4j:

```text
Usa un modelo compatible con OpenAI.
Usa esta API key.
Usa esta URL base.
Usa este nombre de modelo.
```

En una aplicacion real, no conviene dejar la API key escrita directamente en el archivo. Lo ideal seria leerla desde una variable de entorno:

```properties
langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
```

## Flujo completo actual

El flujo actual tiene esta forma:

```text
Cliente HTTP
   |
   v
LibroController
   |
   v
AiLibroService
   |
   v
LibroRepository
   |
   v
Base de datos H2
   |
   v
AiLibroService arma el prompt con el catalogo
   |
   v
LibroAssistant
   |
   v
LangChain4j AiServices
   |
   v
ChatModel configurado por AiConfig
   |
   v
Modelo de IA
   |
   v
Respuesta al cliente
```

## AiConfig

Archivo:

```text
src/main/java/com/riwi/libros/config/AiConfig.java
```

Codigo principal:

```java
@Configuration
public class AiConfig {

    @Bean
    public LibroAssistant libroAssistant(ChatModel model) {

        return AiServices.create(
                LibroAssistant.class,
                model
        );
    }
}
```

### Que hace AiConfig

`AiConfig` es una clase de configuracion de Spring. Su responsabilidad es crear el bean `LibroAssistant`.

Spring Boot detecta la clase porque tiene:

```java
@Configuration
```

El metodo:

```java
@Bean
public LibroAssistant libroAssistant(ChatModel model)
```

le dice a Spring:

```text
Cuando alguien necesite un LibroAssistant, usa este metodo para crearlo.
```

### De donde sale ChatModel

`ChatModel` no lo creamos manualmente en el proyecto. Lo crea automaticamente el starter:

```text
langchain4j-open-ai-spring-boot-starter
```

Ese starter lee las propiedades:

```properties
langchain4j.open-ai.chat-model.api-key
langchain4j.open-ai.chat-model.base-url
langchain4j.open-ai.chat-model.model-name
```

Y con eso registra un bean de tipo `ChatModel`.

### Por que se usa ChatModel y no ChatLanguageModel

En versiones anteriores de LangChain4j existia `ChatLanguageModel`. En LangChain4j 1.x la API cambio y ahora se usa:

```java
dev.langchain4j.model.chat.ChatModel
```

Por eso la configuracion correcta para esta version es:

```java
public LibroAssistant libroAssistant(ChatModel model)
```

Si se usa `ChatLanguageModel`, Spring falla al iniciar porque esa clase ya no existe en el classpath de la version actual.

## LibroAssistant

Archivo:

```text
src/main/java/com/riwi/libros/assistant/LibroAssistant.java
```

Codigo actual:

```java
public interface LibroAssistant {

    @SystemMessage("""
        Eres un experto recomendando libros.
        Recomienda únicamente libros que existan en el catálogo recibido.
        Responde en español, de forma breve y clara.
        """)
    String recomendar(@UserMessage String mensaje);
}
```

### Que hace LibroAssistant

`LibroAssistant` es una interfaz que define como queremos hablar con el modelo de IA.

No tiene implementacion manual porque LangChain4j la crea automaticamente con:

```java
AiServices.create(LibroAssistant.class, model)
```

Eso significa que LangChain4j genera internamente una implementacion de la interfaz. Cuando llamamos:

```java
assistant.recomendar(prompt)
```

LangChain4j convierte esa llamada en una peticion al modelo de IA.

### Para que sirve @SystemMessage

`@SystemMessage` define el comportamiento general del asistente.

En este proyecto le estamos diciendo:

```text
Eres experto recomendando libros.
Solo puedes recomendar libros del catalogo recibido.
Responde en espanol de forma breve.
```

Esto es importante porque evita que el modelo invente libros que no estan registrados.

### Para que sirve @UserMessage

`@UserMessage` indica que el parametro del metodo sera el mensaje del usuario.

En este caso:

```java
String recomendar(@UserMessage String mensaje);
```

El parametro `mensaje` contiene el prompt que arma `AiLibroService`.

## AiLibroService

Archivo:

```text
src/main/java/com/riwi/libros/service/AiLibroService.java
```

Responsabilidad principal:

```text
Tomar la consulta del usuario, consultar los libros guardados, armar el prompt y pedirle a LibroAssistant una recomendacion.
```

Codigo resumido:

```java
@Service
public class AiLibroService {

    private final LibroRepository repository;
    private final LibroAssistant assistant;

    public AiLibroService(LibroRepository repository, LibroAssistant assistant) {
        this.repository = repository;
        this.assistant = assistant;
    }

    public String recomendarLibros(String consulta) {
        // Validar consulta
        // Consultar libros
        // Construir catalogo
        // Construir prompt
        // Llamar assistant.recomendar(prompt)
    }
}
```

### Por que AiLibroService recibe LibroRepository

Necesita leer los libros que existen en la base de datos:

```java
List<Libro> libros = repository.findAll();
```

Esto permite que la IA recomiende solamente sobre datos reales de la aplicacion.

### Por que AiLibroService recibe LibroAssistant

`LibroAssistant` es la puerta de entrada al modelo de IA.

`AiLibroService` no deberia saber detalles de OpenAI, API keys, HTTP, modelos ni configuracion. Solo necesita decir:

```java
assistant.recomendar(prompt)
```

Eso mantiene el servicio limpio y facil de probar.

### Validaciones actuales

Si la consulta llega vacia:

```java
if (consulta == null || consulta.isBlank()) {
    return "Debes escribir una consulta para recomendar libros.";
}
```

Si no hay libros:

```java
if (libros.isEmpty()) {
    return "No hay libros registrados en el catálogo para recomendar.";
}
```

Estas validaciones evitan llamadas innecesarias a la IA.

### Como arma el catalogo

El servicio convierte los libros en texto:

```java
String catalogo = libros.stream()
        .map(libro -> """
                ID: %s
                Titulo: %s
                Autor: %s
                ISBN: %s
                Anio de publicacion: %s
                """.formatted(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getAnioPublicacion()
        ))
        .collect(Collectors.joining("\n"));
```

El resultado queda parecido a esto:

```text
ID: 1
Titulo: El Hobbit
Autor: J. R. R. Tolkien
ISBN: 978000000001
Anio de publicacion: 1937

ID: 2
Titulo: Cien anos de soledad
Autor: Gabriel Garcia Marquez
ISBN: 978000000002
Anio de publicacion: 1967
```

### Como arma el prompt

El prompt combina el catalogo con la consulta:

```java
String prompt = """
        Catalogo disponible:
        %s

        Consulta del usuario:
        %s

        Devuelve maximo 3 recomendaciones basadas SOLO en el catalogo.
        Incluye titulo, autor y una explicacion breve de por que coincide.
        """.formatted(catalogo, consulta);
```

Esto le da contexto al modelo.

El modelo no esta consultando directamente la base de datos. El modelo solo sabe lo que le mandamos en el prompt.

## Integracion en LibroController

Archivo:

```text
src/main/java/com/riwi/libros/controllers/LibroController.java
```

Codigo relevante:

```java
private final LibroService service;
private final AiLibroService aiLibroService;

public LibroController(LibroService service, AiLibroService aiLibroService) {
    this.service = service;
    this.aiLibroService = aiLibroService;
}
```

Endpoint:

```java
@GetMapping("/ia/recomendaciones")
public ResponseEntity<String> recomendar(@RequestParam String consulta) {
    return ResponseEntity.ok(aiLibroService.recomendarLibros(consulta));
}
```

### Por que se integra en el controlador

El controlador es la capa que recibe peticiones HTTP.

Cuando llega una peticion como:

```text
GET /api/libros/ia/recomendaciones?consulta=fantasia
```

Spring ejecuta:

```java
recomendar("fantasia")
```

El controlador no deberia construir prompts ni consultar directamente la base de datos. Solo recibe la peticion y delega el trabajo:

```java
aiLibroService.recomendarLibros(consulta)
```

Esto respeta una separacion clara:

```text
Controller: recibe HTTP y devuelve HTTP.
Service: contiene la logica de negocio.
Repository: accede a datos.
Assistant: habla con IA.
Config: crea beans y configura integraciones.
```

## Como consumir el endpoint actual

Con navegador:

```text
http://localhost:8080/api/libros/ia/recomendaciones?consulta=quiero%20fantasia%20y%20aventura
```

Con `curl`:

```bash
curl "http://localhost:8080/api/libros/ia/recomendaciones?consulta=quiero%20fantasia%20y%20aventura"
```

Con Swagger:

```text
http://localhost:8080/docs
```

## Limitacion del flujo actual

El flujo actual funciona, pero tiene una limitacion importante:

```text
Se envia todo el catalogo al modelo en cada consulta.
```

Eso esta bien para pocos libros, pero no es optimo si el catalogo crece.

Problemas posibles:

```text
Mas libros significa prompts mas grandes.
Prompts mas grandes significan respuestas mas lentas.
Prompts mas grandes pueden costar mas si se usa una API real.
El modelo puede confundirse si se le manda demasiado texto.
No hay ranking propio antes de llamar a la IA.
```

## Que quieres construir realmente

Lo que describes se parece mas a un buscador inteligente que a una recomendacion simple.

Quieres algo como:

```text
El usuario escribe mal, incompleto o parecido.
La aplicacion entiende la intencion.
La aplicacion encuentra libros relevantes.
La respuesta parece un GET normal con datos estructurados.
```

Ejemplos:

```text
Consulta: "cien anios sole"
Resultado: "Cien anos de soledad"

Consulta: "algo como harry poter"
Resultado: libros de fantasia o aventura

Consulta: "libro de garcia marque"
Resultado: libros cuyo autor sea Gabriel Garcia Marquez
```

## Como hacer un endpoint mas optimo tipo buscador

La mejora recomendada es separar el proceso en dos fases:

```text
1. Busqueda rapida en la base de datos.
2. IA solo para mejorar, ordenar o explicar resultados.
```

El flujo mejorado seria:

```text
Cliente
   |
   v
GET /api/libros/recomendaciones?consulta=...
   |
   v
Controller
   |
   v
AiLibroService
   |
   v
Normalizar consulta
   |
   v
Buscar candidatos en base de datos
   |
   v
Si hay buenos candidatos, devolver JSON
   |
   v
Si la consulta es ambigua, usar IA para ordenar o explicar
```

## Respuesta recomendada como GET

En vez de devolver solo `String`, conviene devolver un DTO estructurado:

```json
{
  "consulta": "cien anios sole",
  "corregida": "cien anos soledad",
  "total": 1,
  "resultados": [
    {
      "id": 1,
      "titulo": "Cien anos de soledad",
      "autor": "Gabriel Garcia Marquez",
      "isbn": "9780307474728",
      "anioPublicacion": 1967,
      "score": 0.92,
      "razon": "Coincide con el titulo aunque la consulta tiene errores de escritura."
    }
  ]
}
```

Esto es mejor que devolver texto libre porque:

```text
El frontend puede mostrar tarjetas de libros.
Se puede paginar.
Se puede ordenar.
Se puede probar mas facil.
La respuesta es consistente.
La IA no controla completamente el formato.
```

## Endpoint propuesto

Endpoint mas natural:

```text
GET /api/libros/recomendaciones?consulta=...
```

O si quieres mantenerlo separado como IA:

```text
GET /api/libros/ia/recomendaciones?consulta=...
```

Pero la respuesta deberia cambiar de:

```java
ResponseEntity<String>
```

a:

```java
ResponseEntity<RecomendacionResponseDTO>
```

## DTOs sugeridos

Respuesta general:

```java
public class RecomendacionResponseDTO {

    private String consulta;
    private String consultaNormalizada;
    private int total;
    private List<LibroRecomendadoDTO> resultados;
}
```

Libro recomendado:

```java
public class LibroRecomendadoDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private int anioPublicacion;
    private double score;
    private String razon;
}
```

## Busqueda tolerante a errores

Para lograr algo parecido a YouTube o una barra de busqueda moderna, necesitas combinar varias tecnicas.

### 1. Normalizacion de texto

Antes de buscar, conviene convertir la consulta y los datos a una forma comparable:

```text
"Cien años de soledad" -> "cien anos de soledad"
"CIEN ANIOS SOLE" -> "cien anios sole"
```

Reglas utiles:

```text
Pasar a minusculas.
Quitar tildes.
Quitar signos innecesarios.
Eliminar espacios dobles.
Convertir ñ si tu estrategia lo requiere.
```

Ejemplo en Java:

```java
private String normalizar(String texto) {
    if (texto == null) {
        return "";
    }

    String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
    return normalizado
            .replaceAll("\\p{M}", "")
            .toLowerCase()
            .trim()
            .replaceAll("\\s+", " ");
}
```

### 2. Busqueda parcial

Buscar si el titulo o autor contiene la consulta:

```text
Consulta: "soledad"
Coincide con: "Cien anos de soledad"
```

Esto se puede hacer con consultas como:

```java
List<Libro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(
        String titulo,
        String autor
);
```

### 3. Similaridad de texto

Para errores de escritura, la busqueda parcial no alcanza.

Ejemplo:

```text
"garcia marque" no es igual a "Gabriel Garcia Marquez"
"harry poter" no es igual a "Harry Potter"
```

Aqui puedes usar metricas como:

```text
Levenshtein distance
Jaro-Winkler
Cosine similarity
Trigram similarity
```

Para una practica de Spring Boot, una solucion razonable seria usar Apache Commons Text:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.12.0</version>
</dependency>
```

Y luego usar:

```java
JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
Double score = similarity.apply(consultaNormalizada, tituloNormalizado);
```

### 4. Ranking

No basta con encontrar resultados. Hay que ordenarlos.

Puedes calcular un score por libro:

```text
Score por titulo: 70%
Score por autor: 20%
Score por ISBN: 10%
```

Ejemplo conceptual:

```java
double scoreTitulo = similarity.apply(consulta, normalizar(libro.getTitulo()));
double scoreAutor = similarity.apply(consulta, normalizar(libro.getAutor()));

double scoreFinal = (scoreTitulo * 0.7) + (scoreAutor * 0.3);
```

Luego ordenas de mayor a menor:

```java
.sorted(Comparator.comparing(Resultado::getScore).reversed())
```

### 5. IA como capa de explicacion, no como primer filtro

La IA no deberia recibir todo el catalogo siempre.

Mejor:

```text
Primero tu codigo encuentra 5 o 10 candidatos.
Luego la IA recibe solo esos candidatos.
La IA decide como explicarlos o reordenarlos.
```

Esto es mas optimo porque:

```text
Reduce tokens.
Reduce latencia.
Reduce costo.
Evita respuestas inventadas.
Permite mantener control sobre los datos.
```

## Flujo recomendado para la siguiente version

La version optimizada podria funcionar asi:

```text
1. Usuario llama GET /api/libros/recomendaciones?consulta=...
2. Controller recibe la consulta.
3. Service normaliza la consulta.
4. Repository trae libros candidatos.
5. Service calcula score de similitud.
6. Service toma los mejores 5 resultados.
7. Si los resultados son claros, responde JSON sin IA.
8. Si la consulta es ambigua, llama a LibroAssistant con solo esos candidatos.
9. Controller devuelve RecomendacionResponseDTO.
```

## Cuando usar IA y cuando no

No todas las busquedas necesitan IA.

Usa busqueda normal cuando:

```text
La consulta coincide claramente con titulo.
La consulta coincide claramente con autor.
Hay un ISBN exacto.
El score del primer resultado es muy alto.
```

Usa IA cuando:

```text
La consulta es conversacional.
Ejemplo: "quiero algo triste pero esperanzador"

La consulta habla de generos o sensaciones.
Ejemplo: "algo de misterio con aventura"

Hay varios candidatos parecidos.
Ejemplo: dos libros del mismo autor o saga.
```

## Ejemplo de endpoint optimizado

Controller:

```java
@GetMapping("/recomendaciones")
public ResponseEntity<RecomendacionResponseDTO> recomendar(@RequestParam String consulta) {
    return ResponseEntity.ok(aiLibroService.recomendarLibrosComoBusqueda(consulta));
}
```

Service:

```java
public RecomendacionResponseDTO recomendarLibrosComoBusqueda(String consulta) {
    String normalizada = normalizar(consulta);

    List<Libro> libros = repository.findAll();

    List<LibroRecomendadoDTO> resultados = libros.stream()
            .map(libro -> calcularResultado(libro, normalizada))
            .filter(resultado -> resultado.getScore() >= 0.60)
            .sorted(Comparator.comparing(LibroRecomendadoDTO::getScore).reversed())
            .limit(5)
            .toList();

    return new RecomendacionResponseDTO(
            consulta,
            normalizada,
            resultados.size(),
            resultados
    );
}
```

Este enfoque devuelve datos como un `GET` normal. La IA puede quedar como complemento para generar `razon`.

## Recomendacion de arquitectura final

Para que el proyecto crezca bien, conviene dividir responsabilidades asi:

```text
LibroController
Recibe HTTP y devuelve DTOs.

AiLibroService
Coordina busqueda, ranking e IA.

LibroSearchService
Normaliza texto, calcula similitud y ordena resultados.

LibroAssistant
Genera explicaciones naturales o interpreta consultas ambiguas.

LibroRepository
Accede a la base de datos.
```

Una estructura posible:

```text
src/main/java/com/riwi/libros/
  assistant/
    LibroAssistant.java
  config/
    AiConfig.java
  controllers/
    LibroController.java
  dto/
    response/
      RecomendacionResponseDTO.java
      LibroRecomendadoDTO.java
  service/
    AiLibroService.java
    LibroSearchService.java
    LibroService.java
```

## Mejoras futuras

### Agregar generos o descripcion al modelo Libro

Actualmente `Libro` tiene:

```text
titulo
autor
isbn
anioPublicacion
```

Para recomendaciones inteligentes seria mejor agregar:

```text
genero
descripcion
palabrasClave
```

Ejemplo:

```json
{
  "titulo": "El Hobbit",
  "autor": "J. R. R. Tolkien",
  "genero": "Fantasia",
  "descripcion": "Aventura fantastica con viaje, criaturas y crecimiento personal."
}
```

Con esos campos, la IA y el buscador tendrian mucho mas contexto.

### Usar embeddings

La version mas avanzada seria usar embeddings.

Un embedding convierte texto en vectores numericos. Eso permite encontrar libros por significado, no solo por palabras exactas.

Ejemplo:

```text
Consulta: "quiero una historia de crecimiento personal en un mundo magico"
Puede encontrar: "El Hobbit"
Aunque la consulta no diga "hobbit", "fantasia" o "aventura".
```

Pero para tu etapa actual, lo mas practico es empezar con:

```text
Normalizacion
Busqueda parcial
Similaridad de texto
Ranking
IA para explicar
```

## Resumen

La integracion actual funciona asi:

```text
Controller recibe la consulta.
AiLibroService consulta todos los libros.
AiLibroService arma un prompt con el catalogo.
LibroAssistant envia el prompt al modelo.
AiConfig conecta LibroAssistant con ChatModel.
La respuesta vuelve al cliente como texto.
```

La siguiente mejora recomendada es:

```text
No usar IA como buscador principal.
Primero buscar y rankear con codigo.
Luego usar IA solo para explicar o resolver consultas ambiguas.
Devolver DTOs JSON en vez de texto libre.
Crear un endpoint GET que se comporte como una barra de busqueda inteligente.
```

Esto haria que la aplicacion sea mas rapida, mas controlable y mas parecida a buscadores modernos.
