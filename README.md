# Test E2E — Catálogo Biblioteca UM (Playwright + TestNG)

Prueba de punta a punta sobre el catálogo en línea de la biblioteca de la
Universidad de Mendoza (Koha OPAC, `https://biblioteca.um.edu.ar/`).

**Caso:** buscar el libro *"UML y Patrones"* (Craig Larman) y comprobar que
existe en el catálogo.

## Stack

- **Playwright for Java** — automatización del navegador
- **TestNG** — framework de pruebas (el mismo de la cátedra)
- **Maven** — build y ejecución
- **Page Object Model** — patrón de diseño visto en clase

## Estructura

```
src/test/java/um/testing/
├── base/BaseTest.java            # ciclo de vida de Playwright (abrir/cerrar navegador)
├── pages/HomePage.java           # Page Object: catálogo + buscador
├── pages/SearchResultsPage.java  # Page Object: lista de resultados
└── tests/BuscarLibroTest.java    # la prueba
testng.xml                        # suite de TestNG
pom.xml                           # dependencias y plugins
```

## Requisitos

- JDK 17 o superior
- Maven 3.8+

## Cómo ejecutar

1. Instalar los navegadores de Playwright (solo la primera vez):

```bash
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install chromium"
```

2. Ejecutar la prueba:

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

Por defecto el navegador se abre **visible** y en cámara lenta, para la demo.
Para correrlo sin ventana (headless, útil en CI):

```bash
HEADLESS=true mvn clean test
```

## Qué hace la prueba

1. Entra a `https://biblioteca.um.edu.ar/`.
2. Escribe *"UML y Patrones"* en el buscador de la cabecera y envía con Enter.
3. Comprueba que:
   - la búsqueda devolvió al menos un resultado, y
   - entre los resultados hay un título que contiene *"UML"*.

## Decisiones de diseño

- **Selector `input[name='q']`:** en Koha el campo de palabra clave siempre se
  llama `q` (es el parámetro GET que viaja a `opac-search.pl`), así que el
  selector es estable entre versiones y temas.
- **Envío con Enter** en vez de clickear el botón "Ir": no dependemos del
  selector del botón y replicamos lo que haría un usuario.
- **Resultados con `a.title`:** en la lista de Koha cada título es un enlace con
  esa clase.
- **`assertThat(...).isVisible()`** (aserción de Playwright): auto-espera por el
  elemento, así la prueba no es intermitente (flaky).

## Nota sobre un caso borde

Si una búsqueda devolviera **un único** registro, Koha redirige directamente a
la ficha del libro (`opac-detail.pl`) y no muestra la lista con `a.title`. Para
*"UML y Patrones"* la biblioteca tiene varias copias/ediciones, por lo que
siempre aparece la lista. Si quisieras cubrir también ese caso, bastaría con
verificar además el título `h1` de la ficha de detalle.
