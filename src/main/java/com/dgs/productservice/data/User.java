package com.dgs.productservice.data;

import com.dgs.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
public class User {

    private String id;
     private List<Product> products;

    public User(String id, List<Product> products) {
        this.id = id;
        this.products = products;
    }

}


