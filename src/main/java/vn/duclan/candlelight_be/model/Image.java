package vn.duclan.candlelight_be.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "icon")
    private Boolean icon = false;

    @Column(name = "link")
    private String link;

    @Transient // Trường này sẽ không được lưu trong cơ sở dữ liệu
    private String productId;

    // Longtext to store big data
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    @Lob
    private String imageData;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST, CascadeType.MERGE,
                CascadeType.DETACH, CascadeType.REFRESH
            })
    // image could not exist without product
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
