package com.example.opentelemetry.controller;

import com.example.opentelemetry.api.client.PriceClient;
import com.example.opentelemetry.model.Product;
import com.example.opentelemetry.repository.ProductRepository;
import io.opentelemetry.api.trace.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final PriceClient priceClient;

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(PriceClient priceClient, ProductRepository productRepository) {
        this.priceClient = priceClient;
        this.productRepository = productRepository;
    }

    @GetMapping(path = "/product/{id}")
    public Product getProductDetails(@PathVariable("id") long productId){
        LOGGER.info("Getting Product and Price Details With Product Id {}", productId);

        final Span span = Span.current();
        if (span != null && span.getSpanContext().isValid()) {
            LOGGER.warn("OpenTelemetry current span has been acquired!");

            if (productId == 100004L) {
                LOGGER.warn("OpenTelemetry putting custom attribute productId = " + productId);
                span.setAttribute("productId", productId);
            }
        }
        Product product = productRepository.getProduct(productId);
        product.setPrice(priceClient.getPrice(productId));

        return product;
    }
}
