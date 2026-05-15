# Guia rapida: recomendaciones de libros (flujo simple)

## Objetivo
Buscar libros de forma rapida y solo usar IA cuando la busqueda normal no encuentre resultados.

## Flujo recomendado (2 pasos)
1. Busqueda local (sin IA):
   - Normalizar consulta (minusculas, sin tildes, sin simbolos raros).
   - Comparar contra titulo, autor e ISBN.
   - Calcular score y devolver mejores coincidencias.
2. Fallback con IA (solo si paso 1 no devuelve nada):
   - Enviar consulta + catalogo a IA.
   - IA responde IDs sugeridos del catalogo.
   - Mapear esos IDs a libros reales y devolver respuesta JSON.

## Regla clave
- Si hay resultados locales: NO llamar IA.
- Si no hay resultados locales: SI llamar IA.

## Endpoint
- `GET /api/libros/recomendaciones?consulta=...`
- Alias disponible: `GET /api/libros/ia/recomendaciones?consulta=...`

## Estructura de respuesta
- `consulta`: texto original
- `consultaNormalizada`: texto limpio para comparar
- `total`: cantidad de resultados
- `recomendado`: primer resultado
- `resultados`: lista de coincidencias

## Ventajas de este flujo
- Menor costo y latencia (IA solo cuando hace falta).
- Respuesta consistente para frontend (DTO estable).
- Mejor control del ranking y del formato.

## Casos esperados
- Consulta con typo: "harry poter"
  - Si el score local no alcanza, IA puede sugerir el libro correcto del catalogo.
- Consulta vacia:
  - Respuesta vacia, sin llamada a IA.
- Catalogo vacio:
  - Respuesta vacia, sin llamada a IA.

## Donde esta implementado
- Servicio principal: `src/main/java/com/riwi/libros/service/AiLibroService.java`
- Endpoint: `src/main/java/com/riwi/libros/controllers/LibroController.java`
