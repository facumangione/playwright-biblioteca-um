package um.testing.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.nio.file.Paths;
import java.util.List;

/**
 * Page Object de la pagina de resultados del catalogo Koha OPAC.
 *
 * En Koha cada titulo de la lista de resultados es un enlace con clase "title"
 * (<a class="title" href="/cgi-bin/koha/opac-detail.pl?biblionumber=...">).
 */
public class SearchResultsPage {

    private final Page page;
    private final Locator titulosResultado;

    public SearchResultsPage(Page page) {
        this.page = page;
        this.titulosResultado = page.locator("a.title");
    }

    /** URL actual (sirve para verificar que se ejecuto la busqueda). */
    public String url() {
        return page.url();
    }

    /** Cantidad de resultados listados en la pagina. */
    public int cantidadResultados() {
        // Espera a que aparezca al menos un titulo antes de contar. Si la
        // busqueda no devuelve nada, el waitFor expira: lo capturamos y
        // devolvemos 0, para que el test falle con una asercion clara.
        try {
            titulosResultado.first().waitFor(
                    new Locator.WaitForOptions().setTimeout(10_000));
        } catch (com.microsoft.playwright.TimeoutError e) {
            return 0;
        }
        return titulosResultado.count();
    }

    /** Textos de todos los titulos devueltos (para diagnostico/evidencia). */
    public List<String> titulos() {
        if (titulosResultado.count() == 0) return List.of();
        return titulosResultado.allInnerTexts();
    }

    /**
     * Locator de los resultados cuyo titulo contiene el texto.
     * setHasText(String) hace coincidencia de subcadena sin distinguir
     * mayusculas. Se usa el String (no un Pattern de Java) a proposito:
     * Playwright traduce los Pattern de Java a regex de JavaScript, y la
     * sintaxis \Q...\E que genera Pattern.quote no existe en JS.
     */
    public Locator resultadosQueContienen(String texto) {
        return page.locator("a.title",
                new Page.LocatorOptions().setHasText(texto));
    }

    /** true si existe al menos un resultado cuyo titulo contiene el texto. */
    public boolean contieneLibro(String texto) {
        return resultadosQueContienen(texto).count() > 0;
    }

    /** Guarda una captura de pantalla completa en la ruta dada. */
    public void capturar(String ruta) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(ruta))
                .setFullPage(true));
    }
}
