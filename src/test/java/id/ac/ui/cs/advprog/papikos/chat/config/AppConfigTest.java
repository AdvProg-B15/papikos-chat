package id.ac.ui.cs.advprog.papikos.chat.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        RestTemplate restTemplateBean = context.getBean(RestTemplate.class);

        assertNotNull(restTemplateBean, "RestTemplate bean should not be null.");
        context.close();
    }

    @Test
    void testRestTemplateIsSingletonByDefault() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        RestTemplate restTemplate1 = context.getBean(RestTemplate.class);
        RestTemplate restTemplate2 = context.getBean(RestTemplate.class);

        assertNotNull(restTemplate1);
        assertNotNull(restTemplate2);
        assertSame(restTemplate1, restTemplate2, "RestTemplate bean should be a singleton by default.");

        context.close();
    }
}