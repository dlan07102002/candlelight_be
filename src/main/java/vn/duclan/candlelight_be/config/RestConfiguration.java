package vn.duclan.candlelight_be.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import vn.duclan.candlelight_be.model.Category;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Expose ID in Repository get from starter data rest
        config.exposeIdsFor(
                entityManager.getMetamodel().getEntities().stream()
                        // method reference ::
                        .map(Type::getJavaType)
                        .toArray(Class[]::new));

        // config disable specific http methods
        // HttpMethod[] disabledMethod = { HttpMethod.POST, HttpMethod.PUT,
        // HttpMethod.DELETE, HttpMethod.PATCH };
        // disableHttpMethods(Category.class, config, disabledMethod);
    }

    private void disableHttpMethods(Class c, RepositoryRestConfiguration configuration, HttpMethod[] methods) {
        configuration.getExposureConfiguration()
                .forDomainType(c)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(methods))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(methods));
    }
}