package vn.duclan.candlelight_be.model;

import java.sql.Blob;

import lombok.Data;

@Data
public class Image {
    private int imageId;
    private String imageName;
    private boolean icon;
    private String link;

    private Blob data;

    private Product product;
}
