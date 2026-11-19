package vn.iotstar.filter;

import jakarta.servlet.annotation.WebFilter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

@WebFilter("/*")
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {
    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/*", "/WEB-INF/decorators/main.jsp")
               .addExcludedPath("/media/*")
               .addExcludedPath("/assets/*");
    }
}