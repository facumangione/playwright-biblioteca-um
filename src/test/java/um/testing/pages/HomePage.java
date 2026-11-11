package um.testing.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object de la pagina principal del catalogo (Koha OPAC) de la UM.
 *
 * El patron Page Object Model (visto en la clase) representa cada pagina como
 * una clase: los selectores viven aca y la prueba solo llama metodos con nombre
 * de negocio (open, buscar). Si Koha cambia un selector, se corrige en un solo
 * lugar y no en cada test.
 */
public class HomePage {

    private final Page page;

    // URL del catalogo. Koha redirige la raiz a /cgi-bin/koha/opac-main.pl
    private static final String URL = "https://biblioteca.um.edu.ar/";

    // Buscador de la cabecera. En Koha el campo de palabra clave siempre se
    // llama "q" (es el parametro que viaja por GET a opac-search.pl), por eso
    // ese selector es estable entre versiones. first() evita ambiguedad si el
    // tema de Koha renderiza mas de un formulario de busqueda.
    private final Locator campoBusqueda;

    public HomePage(Page page) {
        this.page = page;
        this.campoBusqueda = page.locator("input[name='q']").first();
    }

    /** Abre el catalogo y espera a que el buscador este disponible. */
    public HomePage open() {
        page.navigate(URL);
        campoBusqueda.waitFor(); // auto-espera de Playwright: no hace falta sleep()
        return this;
    }

    /**
     * Escribe el termino y envia la busqueda.
     *
     * Se usa Enter en lugar de hacer click en el boton "Ir": asi no dependemos
     * del selector del boton (que cambia segun el tema) y el envio es el mismo
     * que haria un usuario real. Devuelve el Page Object de resultados.
     */
    public SearchResultsPage buscar(String termino) {
        campoBusqueda.fill(termino);
        campoBusqueda.press("Enter");
        return new SearchResultsPage(page);
    }
}
