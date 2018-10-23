package net.codecrete.qrbill.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2018-10-23T18:47:07.538749+02:00[Europe/Zurich]")

@Configuration
@EnableSwagger2
public class OpenAPIDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Swiss QR Bill API")
            .description("The Swiss QR Bill API for generating the new payment slip for invoices. The QR code contains the complete payment information in a machine-readable form:  - Payment amount and currency - Creditor address and account - Final creditor address (if needed) - Reference number issued by the creditor - Additional informationn for the recipient of the invoice - Debtor address - Due date (optional)  Debtor and amount can be omitted and filled in by the payer.  The QR bill payment part can be generated as an A6, A5 or A4 sheet. Alternatively, the QR code only can be generated. Supported formats are SVG and PDF.  To successfully validate the bill data and generate a QR bill, the following main requirements must be met:  - A valid address must at least contain *name*, *postal code*, *town* and *country ISO code*. - The creditor address and accout number are mandatory. - The final creditor and the debtor addresses are optional. If they are not used, they must be omitted entirely   or all address fields must be *null* or empty. - The account number must be a valid IBAN of Switzerland or Liechtenstein. - If a reference number is provided, it must be either a valid QR/ISR reference number   or a valid ISO 11649 creditor reference (i.e. the applicable check digits must be valid) - The currency must be either *CHF* or *EUR*.  If fields are too long, they are automaticalluy truncated. If characters outside the restricted range of characters are used, they are automatically replaced. The validation result contains warnings about truncations and replacements. ")
            .license("MIT License")
            .licenseUrl("https://opensource.org/licenses/MIT")
            .termsOfServiceUrl("")
            .version("1.0.1")
            .contact(new Contact("","", ""))
            .build();
    }

    @Bean
    public Docket customImplementation(ServletContext servletContext, @Value("${openapi.swissQRBill.base-path:/qrbill-api}") String basePath) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("net.codecrete.qrbill.web.api"))
                    .build()
                .pathProvider(new BasePathAwareRelativePathProvider(servletContext, basePath))
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

    class BasePathAwareRelativePathProvider extends RelativePathProvider {
        private String basePath;

        public BasePathAwareRelativePathProvider(ServletContext servletContext, String basePath) {
            super(servletContext);
            this.basePath = basePath;
        }

        @Override
        protected String applicationPath() {
            return  Paths.removeAdjacentForwardSlashes(UriComponentsBuilder.fromPath(super.applicationPath()).path(basePath).build().toString());
        }

        @Override
        public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
            return Paths.removeAdjacentForwardSlashes(
                    uriComponentsBuilder.path(operationPath.replaceFirst("^" + basePath, "")).build().toString());
        }
    }

}
