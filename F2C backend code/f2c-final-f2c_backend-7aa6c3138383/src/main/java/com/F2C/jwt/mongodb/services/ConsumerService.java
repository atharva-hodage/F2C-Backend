package com.F2C.jwt.mongodb.services;


import java.util.List;

import com.F2C.jwt.mongodb.models.Cart;
import com.F2C.jwt.mongodb.models.CartItem;
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ImageResponse;
import com.F2C.jwt.mongodb.models.Order;
import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.OrderResponse;
import com.F2C.jwt.mongodb.models.User;

public interface ConsumerService {
	User getUserById(String id);

	List<CropDetails> getAllCropsForConsumer();

	CropDetails getCropById(String consumerId, String cropId);

	List<CropDetails> getCropsBySubType(String cropSubType);

	List<CropDetails> getCropsByPriceRange(Double minPrice, Double maxPrice);

	void addToCart(String consumerId, String cropId, Long requiredQuantity);

	Cart viewCart(String consumerId);

	CartItem viewCartItem(String consumerId, String cartItemId);

	void removeFromCart(String consumerId, String cartItemId);

	User updateConsumerProfile(String consumerId, User updatedUser);

//	void placeOrder(String consumerId, List<String> cartItemIds);
	String placeOrder(String consumerId, List<String> cartItemIds);
	public List<OrderResponse> getOrdersByConsumerId(String consumerId);

//	 public Order getOrderById(String consumerId, String orderId);
	 public OrderResponse getOrderDetailsById(String consumerId, String orderId,String orderItemId);
	Order getOrderById(String consumerId, String orderId);
	 CropDetails getCropDetailsById(String cropId);
	 public OrderItem getOrderItemById(String consumerId, String orderId, String orderItemId);
	 
	 void cancelOrder(String consumerId, String orderId);
	 
	 ImageResponse getCropByIdWithImages(String consumerId, String cropId);
	 String placeDirectOrder(String consumerId, String cropId, Long quantity);
}
