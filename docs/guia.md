# LABORATORIO DÍA 4 — ChatTech 💬

## Arquitectura Híbrida Escalable con MongoDB, WebSockets y Spring AI

---

# 📋 Objetivo del Proyecto

Construir una aplicación Full Stack en Spring Boot 4 capaz de funcionar simultáneamente como:

* Aplicación Web MVC (Thymeleaf)
* API REST
* Sistema de comunicación en tiempo real (WebSockets)
* Plataforma integrada con IA usando Spring AI + OpenAI
* Persistencia NoSQL con MongoDB

---

# 🏗️ Arquitectura General

```text
┌─────────────────────────────────────────────┐
│                 CLIENTE WEB                 │
│─────────────────────────────────────────────│
│ Thymeleaf + HTML + JS + STOMP + SockJS     │
└─────────────────────┬───────────────────────┘
                      │ HTTP/WebSocket
                      ▼
┌─────────────────────────────────────────────┐
│                CONTROLADORES                │
│─────────────────────────────────────────────│
│ ChatUIController                            │
│ MensajeRestController                       │
│ ChatSocketController                        │
└─────────────────────┬───────────────────────┘
                      ▼
┌─────────────────────────────────────────────┐
│                 SERVICIOS                   │
│─────────────────────────────────────────────│
│ MensajeService                              │
│ BotIAService                                │
└──────────────┬──────────────────────────────┘
               ▼
      ┌──────────────────┐
      │ MongoDB          │
      │ Colección        │
      │ mensajes         │
      └──────────────────┘

               ▼

      ┌──────────────────┐
      │ Spring AI        │
      │ OpenAI GPT       │
      └──────────────────┘
```

---

# 📂 Estructura Recomendada del Proyecto

```text
src/main/java/com/chattech
│
├── config
│   └── WebSocketConfig
│
├── controller
│   ├── api
│   │   └── MensajeRestController
│   │
│   ├── ui
│   │   └── ChatUIController
│   │
│   └── ws
│       └── ChatSocketController
│
├── model
│   └── Mensaje
│
├── repository
│   └── MensajeRepository
│
├── service
│   ├── MensajeService
│   └── BotIAService
│
└── resources
    ├── templates
    │   └── chat
    │       └── sala.html
    │
    └── application.properties
```

---

# ⚙️ Configuración Inicial

## Dependencias necesarias

Agregar:

* Spring Web
* Thymeleaf
* Spring Data MongoDB
* Spring WebSocket
* Spring AI OpenAI Starter

---

# application.properties

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/chattech

spring.ai.openai.api-key=TU_API_KEY
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
```

---

# 👥 División del Trabajo (4 Personas)

---

# 👤 Integrante 1 — Persistencia MongoDB

## Objetivo

Implementar toda la capa de datos.

---

## Tareas

### 1. Crear modelo `Mensaje`

```java
@Document(collection = "mensajes")
public class Mensaje {
    @Id
    private String id;

    private String remitente;
    private String contenido;
    private LocalDateTime fechaEnvio;
}
```

---

### 2. Crear `MensajeRepository`

```java
public interface MensajeRepository
    extends MongoRepository<Mensaje, String> {
}
```

---

### 3. Crear `MensajeService`

Métodos mínimos:

```java
guardarMensaje()
obtenerHistorial()
```

---

## Entregables

* Modelo Mensaje
* Repositorio MongoDB
* Servicio funcional
* MongoDB conectado

---

# 👤 Integrante 2 — WebSockets y Tiempo Real

## Objetivo

Implementar la comunicación bidireccional.

---

## Tareas

### 1. Configurar WebSocket

```java
@EnableWebSocketMessageBroker
```

---

### 2. Crear `WebSocketConfig`

Canales:

```text
/app
/tema
```

Endpoint:

```text
/chat-websocket
```

---

### 3. Crear `ChatSocketController`

```java
@MessageMapping("/enviar")
@SendTo("/tema/mensajes")
```

---

### 4. Integrar `SimpMessagingTemplate`

```java
messagingTemplate.convertAndSend(...)
```

---

## Entregables

* Configuración WebSocket
* Broadcast funcionando
* Mensajes en tiempo real

---

# 👤 Integrante 3 — Spring AI

## Objetivo

Integrar OpenAI usando Spring AI.

---

## Tareas

### 1. Configurar OpenAI

Agregar API Key.

---

### 2. Crear `BotIAService`

Método principal:

```java
generarRespuestaIA()
```

---

### 3. Construir prompts usando MongoDB

Ejemplo:

```text
Usuario: hola
Bot: hola ¿en qué ayudo?
```

---

### 4. Integrar Spring AI

```java
chatClient.prompt()
```

---

## Entregables

* IA respondiendo
* Historial contextual
* Integración OpenAI funcional

---

# 👤 Integrante 4 — Frontend + MVC

## Objetivo

Construir la interfaz gráfica.

---

## Tareas

### 1. Crear `ChatUIController`

Ruta:

```text
/admin/chat
```

---

### 2. Crear vista `chat/sala.html`

Debe incluir:

* Historial Thymeleaf
* Caja de mensajes
* Inputs
* Botón enviar

---

### 3. Integrar STOMP JS

Funciones:

```javascript
conectar()
enviarMensaje()
mostrarMensaje()
```

---

### 4. Crear `MensajeRestController`

Ruta:

```text
/api/mensajes
```

---

## Entregables

* Frontend funcional
* Vista Thymeleaf
* Cliente WebSocket
* API REST

---

# 🔄 Flujo Completo del Sistema

```text
1. Usuario abre /admin/chat
2. Thymeleaf carga historial desde MongoDB
3. JS conecta WebSocket
4. Usuario envía mensaje
5. Spring recibe mensaje
6. Se guarda en MongoDB
7. Se retransmite a usuarios
8. IA recibe contexto
9. OpenAI genera respuesta
10. IA responde
11. Se guarda respuesta
12. Se retransmite respuesta IA
```

---

# 🧪 Pruebas Recomendadas

## WebSocket

Abrir múltiples pestañas:

```text
localhost:8080/admin/chat
```

Verificar:

* mensajes en tiempo real
* respuestas IA
* sincronización

---

# API REST

```text
GET /api/mensajes
```

Debe retornar JSON.

---

# MongoDB

Verificar colección:

```text
mensajes
```

---

# 🔀 Organización Git Recomendada

## Ramas

| Persona      | Rama                |
| ------------ | ------------------- |
| Integrante 1 | feature/mongodb     |
| Integrante 2 | feature/websocket   |
| Integrante 3 | feature/spring-ai   |
| Integrante 4 | feature/frontend-ui |

---

## Flujo

```text
main
 └── develop
      ├── feature/mongodb
      ├── feature/websocket
      ├── feature/spring-ai
      └── feature/frontend-ui
```

---

# 🚀 Resultado Esperado

Al finalizar el laboratorio el sistema deberá:

✅ Persistir mensajes en MongoDB
✅ Tener chat en tiempo real
✅ Integrar IA contextual
✅ Funcionar como MVC + REST + WebSocket
✅ Tener frontend Thymeleaf funcional
✅ Responder automáticamente usando OpenAI

---

# 📌 Tecnologías Utilizadas

| Tecnología          | Uso                      |
| ------------------- | ------------------------ |
| Spring Boot 4       | Backend                  |
| Thymeleaf           | Frontend MVC             |
| MongoDB             | Persistencia             |
| Spring Data MongoDB | Acceso datos             |
| WebSockets + STOMP  | Tiempo real              |
| Spring AI           | Integración IA           |
| OpenAI GPT          | Modelo LLM               |
| SockJS              | Compatibilidad WebSocket |

---

# 🧠 Conceptos Arquitectónicos Aplicados

* MVC
* REST API
* Repository Pattern
* Service Layer
* Event Driven Architecture
* WebSockets
* NoSQL
* AI-Augmented Architecture
* Hybrid Monolith

---

# 📅 Recomendación de Trabajo

| Fase  | Actividad             |
| ----- | --------------------- |
| Día 1 | Desarrollo individual |
| Día 2 | Integración           |
| Día 3 | Testing               |
| Día 4 | Correcciones          |
| Día 5 | Demo final            |

---

# ✅ Checklist Final

## Backend

* [ ] MongoDB conectado
* [ ] Repositorio funcionando
* [ ] Servicios creados
* [ ] REST API funcional

## Tiempo Real

* [ ] WebSocket conectado
* [ ] Broadcast funcionando
* [ ] STOMP operativo

## IA

* [ ] OpenAI conectado
* [ ] IA responde
* [ ] Historial contextual

## Frontend

* [ ] Thymeleaf renderiza
* [ ] Chat visible
* [ ] Mensajes llegan en tiempo real

---
