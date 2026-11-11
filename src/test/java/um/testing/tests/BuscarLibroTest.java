package um.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import um.testing.base.BaseTest;
import um.testing.pages.HomePage;
import um.testing.pages.SearchResultsPage;

/**
 * Prueba E2E sobre el catalogo de la biblioteca de la UM (Koha OPAC).
 *
 * Caso: buscar "UML y Patrones" (Craig Larman) y comprobar que el libro existe.
 */
public class BuscarLibroTest extends BaseTest {

    private static final String TERMINO = "UML y Patrones";
    private static final String TEXTO_ESPERADO = "UML"; // palabra que debe estar en el titulo

    @Test(description = "Busca 'UML y Patrones' en el catalogo y verifica que exista")
    public void buscarLibroUmlYPatrones() {
        // 1) Ingresar al catalogo
        HomePage home = new HomePage(page).open();

        // 2) Buscar el libro
        SearchResultsPage resultados = home.buscar(TERMINO);

        // 3) Diagnostico + evidencia (se imprime SIEMPRE, antes de las aserciones)
        int cantidad = resultados.cantidadResultados();
        System.out.println("URL de resultados : " + resultados.url());
        System.out.println("Cantidad          : " + cantidad);
        System.out.println("Titulos devueltos : " + resultados.titulos());
        resultados.capturar("target/evidencia-busqueda.png");

        // 4) Comprobaciones
        Assert.assertTrue(cantidad > 0,
                "La busqueda de '" + TERMINO + "' no devolvio ningun resultado");

        Assert.assertTrue(resultados.contieneLibro(TEXTO_ESPERADO),
                "Ningun titulo contiene '" + TEXTO_ESPERADO + "'. Titulos: " + resultados.titulos());

        System.out.println("OK - El libro '" + TERMINO + "' existe en el catalogo.");
    }
}
