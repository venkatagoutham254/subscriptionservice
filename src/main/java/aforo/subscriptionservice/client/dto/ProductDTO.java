package aforo.subscriptionservice.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long productId;
    private String productName;
    private String icon;
    
    // Explicit getters (Lombok not configured in this project)
    public Long getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public String getIcon() {
        return icon;
    }
}
