# Diagnostico de configuracion Spring Boot

## Problema principal

IntelliJ estaba intentando ejecutar esta clase:

```text
com.riwi.intro.IntroApplication
```

Pero esa clase ya no existia en el proyecto. La aplicacion real esta en:

```text
com.riwi.libros.LibrosApplication
```

Por eso aparecia un error similar a:

```text
ClassNotFoundException: com.riwi.intro.IntroApplication
```

Ese error no significaba que el controlador o el modelo estuvieran mal. Significaba que la configuracion de ejecucion de IntelliJ apuntaba a una clase vieja.

## Estructura correcta del proyecto

La clase principal debe estar ubicada en:

```text
src/main/java/com/riwi/libros/LibrosApplication.java
```

Y debe declarar el paquete:

```java
package com.riwi.libros;
```

La clase principal correcta queda asi:

```java
package com.riwi.libros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrosApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrosApplication.class, args);
    }
}
```

## Por que fallaba

Habia referencias antiguas a `IntroApplication` dentro de la configuracion de IntelliJ, especialmente en:

```text
.idea/workspace.xml
```

Alli existian configuraciones que apuntaban a:

```text
com.riwi.intro.IntroApplication
com.riwi.libros.IntroApplication
```

Ambas eran incorrectas para este proyecto.

La unica clase principal valida debe ser:

```text
com.riwi.libros.LibrosApplication
```

## Cambios realizados

Se corrigio el test de Spring Boot para que use el paquete correcto:

```text
src/test/java/com/riwi/libros/LibrosApplicationTests.java
```

Antes estaba en un paquete viejo:

```text
com.riwi.intro
```

Eso podia impedir que `@SpringBootTest` encontrara la aplicacion principal.

Tambien se limpio la configuracion de IntelliJ para que la configuracion de ejecucion seleccionada sea:

```text
Spring Boot.LibrosApplication
```

Y para que apunte a:

```text
com.riwi.libros.LibrosApplication
```

Ademas, se ajusto el `pom.xml` para usar Java 17:

```xml
<java.version>17</java.version>
```

Esto fue necesario porque el entorno estaba usando JDK 17, pero el proyecto pedia Java 21. Maven fallaba con:

```text
release version 21 not supported
```

Tambien se agrego el archivo faltante del Maven Wrapper:

```text
.mvn/wrapper/maven-wrapper.properties
```

Sin ese archivo, `./mvnw` fallaba con:

```text
cannot open ./.mvn/wrapper/maven-wrapper.properties
```

## Como revisar esto en el futuro

Si IntelliJ lanza `ClassNotFoundException`, revisa primero la configuracion de ejecucion:

1. Abre Run / Debug Configurations.
2. Busca la configuracion de Spring Boot.
3. Verifica que `Main class` sea:

```text
com.riwi.libros.LibrosApplication
```

4. Si aparece `IntroApplication`, borra esa configuracion o cambiala por `LibrosApplication`.

## Regla para paquetes en Spring Boot

El paquete de la clase principal debe estar por encima de los demas paquetes.

Correcto:

```text
com.riwi.libros
com.riwi.libros.controllers
com.riwi.libros.models
com.riwi.libros.config
```

Esto funciona porque `@SpringBootApplication` escanea automaticamente los paquetes hijos de `com.riwi.libros`.

Incorrecto:

```text
com.riwi.intro
com.riwi.libros.controllers
com.riwi.libros.models
```

En ese caso Spring puede no encontrar tus controladores, modelos o configuraciones.

## Comandos utiles

Para limpiar clases compiladas antiguas:

```bash
rm -rf target
```

Para compilar:

```bash
./mvnw compile
```

Para ejecutar la aplicacion:

```bash
./mvnw spring-boot:run
```

Si tienes Maven instalado globalmente, tambien puedes usar:

```bash
mvn compile
mvn spring-boot:run
```

## Estado esperado al compilar

Despues de compilar correctamente debe existir:

```text
target/classes/com/riwi/libros/LibrosApplication.class
```

Si esa clase existe, significa que Maven compilo la clase principal en el paquete correcto.

## Resumen

El proyecto fallaba porque IntelliJ seguia intentando ejecutar una clase vieja:

```text
com.riwi.intro.IntroApplication
```

La aplicacion correcta es:

```text
com.riwi.libros.LibrosApplication
```

La solucion fue alinear paquetes, clase principal, tests, configuracion de IntelliJ, version de Java en Maven y Maven Wrapper.
