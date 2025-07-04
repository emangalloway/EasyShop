package org.yearup.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart
{
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();
    private int userId;

    public ShoppingCart(int userId) {
        this.userId = userId;
        this.items = new HashMap<>();
    }

    public ShoppingCart() {
    }

    public Map<Integer, ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items) {
        this.items = items;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean contains(int productId) {
        return items.containsKey(productId);
    }

    public void add(ShoppingCartItem item) {
        items.put(item.getProductId(), item);
    }

    public void increaseQtyByOne(ShoppingCartItem item){
        if (items.containsKey(item.getProductId())){
            item = get(item.getProductId());
            int qty = item.getQuantity();
            item.setQuantity(qty + 1);
        }else
            add(item);
    }

    public ShoppingCartItem get(int productId) {
        return items.get(productId);
    }

    public BigDecimal getTotal() {
        BigDecimal total = items.values()
                                .stream()
                                .map(i -> i.getLineTotal())
                                .reduce( BigDecimal.ZERO, (lineTotal, subTotal) -> subTotal.add(lineTotal));

        return total;
    }

}
