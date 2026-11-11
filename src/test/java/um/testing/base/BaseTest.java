package um.testing.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Clase base de las pruebas.
 *
 * Centraliza el ciclo de vida de Playwright (arrancar y cerrar el navegador)
 * para que cada clase de test solo se ocupe de la logica de la prueba.
 *
 * Playwright maneja cuatro objetos encadenados:
 *   Playwright  -> proceso que habla con los navegadores
 *   Browser     -> la instancia del navegador (Chromium en este caso)
 *   Context     -> una sesion aislada (cookies/almacenamiento propios)
 *   Page        -> una pestania dentro del contexto
 */
public abstract class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    /**
     * Se ejecuta antes de CADA metodo @Test.
     * Un contexto nuevo por prueba garantiza que los tests sean independientes
     * (principio de las pruebas unitarias que figura en la teoria de la clase).
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();

        // headless=false permite ver el navegador durante la demo.
        // Se controla por la variable de entorno HEADLESS (por defecto, visible).
        boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"));

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setSlowMo(headless ? 0 : 400) // ralentiza los pasos para que se vean en la demo
        );

        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Se ejecuta despues de CADA metodo @Test, aunque la prueba falle.
     * Cierra todo para no dejar procesos de navegador abiertos.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
