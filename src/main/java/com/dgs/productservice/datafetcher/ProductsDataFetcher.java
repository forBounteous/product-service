package com.dgs.productservice.datafetcher;


import com.dgs.productservice.data.User;
import com.dgs.productservice.model.Product;
import com.dgs.productservice.repository.ProductRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.DgsQuery;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import java.util.*;

@DgsComponent
public class ProductsDataFetcher {

    @Autowired
    MongoClient mongoClient;

    @Autowired
    MongoConverter converter;

    @Autowired
    private ProductRepository productRepository;

    @DgsEntityFetcher(name = "User")
    public User user(Map<String, Object> values) {
        return new User((String) values.get("id"), null);
    }

    @DgsData(parentType = "User", field = "products")
    public List<Product> products(DgsDataFetchingEnvironment env) {
        User user = env.getSource();
        List<String> userIds = Collections.singletonList(user.getId());
        return productRepository.findAllById(userIds);
    }

    @DgsQuery
    List<Product> searchByText(String text) {
        List<Product> products = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase("productDB");
        MongoCollection<Document> collection = database.getCollection("products");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$search",
                new Document("index", "default").append("text",
                        new Document("query", text)
                                .append("path", "description")
                                .append("fuzzy", new Document())))));

        result.forEach(document -> products.add(converter.read(Product.class, document)));
        return products;
    }

    @DgsQuery
    List<Product> allProducts(String text) {
       return productRepository.findAll();
    }

}