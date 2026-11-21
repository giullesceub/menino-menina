package br.com.meninosmeninas;

import br.com.meninosmeninas.service.LoginNormalizer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LoginNormalizerTest {
    @Test
    void shouldRemoveAccentsCaseAndNonAlphanumericCharacters() {
        assertEquals("joaosilva", LoginNormalizer.normalizar("  João-Silva!  "));
    }

    @Test
    void shouldReturnEmptyLoginWhenNoWordRemains() {
        assertEquals("", LoginNormalizer.normalizar(" !!! @@@ --- "));
    }
}
