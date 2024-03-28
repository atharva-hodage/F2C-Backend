package com.F2C.jwt.mongodb.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.F2C.jwt.mongodb.models.Cart;
import com.F2C.jwt.mongodb.models.CartItem;
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ImageResponse;
import com.F2C.jwt.mongodb.models.Order;
import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.OrderResponse;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.services.ConsumerService;
import com.F2C.jwt.mongodb.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rolec")
public class ConsumerController {
	// @Autowired
	private ConsumerService consumerService;

	@Autowired
	public ConsumerController(ConsumerService consumerService) {
		this.consumerService = consumerService;
	}
    //1
	// ** getUserById //Working fine
	// http://localhost:8081/api/rolec/64ed747973ed467ba956457b
	@GetMapping("/{consumerId}")
	@PreAuthorize("hasRole('CONSUMER')") // not found 404 , 403
	public ResponseEntity<User> getUserById(@PathVariable("consumerId") String consumerId,
			@AuthenticationPrincipal UserDetails userDetails) {
		UserDetailsImpl loggedInUser = (UserDetailsImpl) userDetails;
		if (!loggedInUser.getId().equals(consumerId)) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not authorized to access this
																		// resource
		}

		User consumer = consumerService.getUserById(consumerId);
		if (consumer == null) {
			System.out.println(loggedInUser.getId());
			System.out.println(consumerId);
			System.out.println(consumer);
			return ResponseEntity.notFound().build(); // User not found
		}

		return ResponseEntity.ok(consumer);
	}

	//// ** viewAllCropDetails //working fine
	// http://localhost:8080/api/rolec/viewAllCropDetails

	//2
	@PreAuthorize("hasRole('CONSUMER')")
	@GetMapping("/viewAllCropDetails")
	public ResponseEntity<List<CropDetails>> viewAllCropDetails() {
		List<CropDetails> cropDetailsList = consumerService.getAllCropsForConsumer();
		return ResponseEntity.ok(cropDetailsList);
	}
	
	//3

	//// ** getCropById //working fine
	// http://localhost:8080/api/rolec/64e2fd176e461108d0c521f2/cropById/b4c99d29-14a4-4e77-99b0-a608515c5578
	@GetMapping("/{consumerId}/cropById/{cropId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<CropDetails> getCropById(@PathVariable String consumerId, @PathVariable String cropId) {
		CropDetails crop = consumerService.getCropById(consumerId, cropId);

		if (crop == null) {
			return ResponseEntity.notFound().build();
		}
		System.out.println("\n");
		System.out.println("Crop for the ID"+crop);
		return ResponseEntity.ok(crop);
	}
	// problem is for rahul mahi it display with any ones token

	//
	@GetMapping("/{consumerId}/cropByIdimg/{cropId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<ImageResponse> getCropByIdimg(@PathVariable String consumerId, @PathVariable String cropId) {
        ImageResponse crop = consumerService.getCropByIdWithImages(consumerId, cropId);
        
         //System.out.println("************************getCropByIdimg******************************");
        if (crop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(crop);
    }
	//4
	// ** getCropsBySubType //working fine
	// http://localhost:8080/api/rolec/bySubType in form data give cropSubType
	@GetMapping("/bySubType")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<List<CropDetails>> getCropsBySubType(@RequestParam String cropSubType) {
		List<CropDetails> crops = consumerService.getCropsBySubType(cropSubType);
		return ResponseEntity.ok(crops);
	}

	//5
	@GetMapping("/byPrice")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<List<CropDetails>> getCropsByPriceRange(@RequestParam Double minPrice,
			@RequestParam Double maxPrice) {
		List<CropDetails> crops = consumerService.getCropsByPriceRange(minPrice, maxPrice);
		return ResponseEntity.ok(crops);
	}

	// add to cart
//6
//http://localhost:8080/api/rolec/64f59f5baac9a36ed8c4dc4a/add-to-cart
	@PostMapping("/{consumerId}/{cropId}/add-to-cart/{quantity}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<String> addToCart(@PathVariable String consumerId, @PathVariable String cropId,
			@PathVariable Long quantity) {
		System.out.print("hi");
		System.out.print(consumerId);
		System.out.print(quantity);
		try {
			consumerService.addToCart(consumerId, cropId,quantity);
			return ResponseEntity.ok("Crop added to cart successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	//7
	//http://localhost:8080/api/rolec/64f59f5baac9a36ed8c4dc4a/cart
	@GetMapping ("/{consumerId}/cart")// get consumer view its cart
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<Cart> viewCart(@PathVariable String consumerId) {
		Cart cart = consumerService.viewCart(consumerId);
		if (cart != null) {
			System.out.println("Cart Details are : "+cart);
			return ResponseEntity.ok(cart);
			
		} 
			return ResponseEntity.notFound().build();
		

	}
//8
	// http://localhost:8080/api/rolec/64e5e18b086a9a5f8d9ee18d/item/99fd7347-0645-46c3-9360-190ed6ea4c7d
	@GetMapping("/{consumerId}/item/{cartItemId}")
	// @GetMapping("/item/{cartItemId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<CartItem> viewCartItem(@PathVariable String consumerId, @PathVariable String cartItemId) {
		CartItem cartItem = consumerService.viewCartItem(consumerId, cartItemId);
		if (cartItem != null) {
			return ResponseEntity.ok(cartItem);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	
	//9
	// remove from cart
	// http://localhost:8080/api/rolec/64e2fd486e461108d0c521f3/cart/3c6ad7de-ffdf-4282-ae3c-fca1be48505c
	@DeleteMapping("/{consumerId}/cart/{cartItemId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<String> removeFromCart(@PathVariable String consumerId, @PathVariable String cartItemId) {

		try {
			consumerService.removeFromCart(consumerId, cartItemId);
			return ResponseEntity.ok("Crop removed from cart successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	//10
	// working
	@PutMapping("/{consumerId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<User> updateConsumerProfile(@PathVariable String consumerId, @RequestBody User updatedUser,
			@AuthenticationPrincipal UserDetails userDetails) {

		UserDetailsImpl loggedInUser = (UserDetailsImpl) userDetails;
		if (!loggedInUser.getId().equals(consumerId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not authorized to update this profile
		}

		User updatedProfile = consumerService.updateConsumerProfile(consumerId, updatedUser);
		if (updatedProfile != null) {
			return ResponseEntity.ok(updatedProfile);
		} else {
			return ResponseEntity.notFound().build(); // User not found
		}
	}
	@PostMapping("/{consumerId}/place-order/{cartItemIds}") ///{cartItemId} //for using request param we should have to specify name in bracket 
	//@PreAuthorize("hasRole('CONSUMER')")
	public String placeOrder(@PathVariable String consumerId, @PathVariable("cartItemIds") List<String> cartItemIds) {
		try {
			String orderId =consumerService.placeOrder(consumerId,cartItemIds);
			System.out.println("\n\n\nOrder ID after Placing Order"+orderId);
			return orderId;
		} catch (IllegalArgumentException e) {
			return "Failed to place order: ";
		}
	}
	
	@PostMapping("/{consumerId}/placeDirectOrder/{cropId}/{quantity}")
	// @PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<String> placeDirectOrder(@PathVariable String consumerId, @PathVariable String cropId, @PathVariable Long quantity) {
	try {
	String orderId = consumerService.placeDirectOrder(consumerId, cropId,quantity);
	// return ResponseEntity.ok("Direct order placed successfully. Order ID: " + orderId);
	System.out.println(" orderId "+ orderId);
	return ResponseEntity.ok(orderId);
	} catch (IllegalArgumentException e) {
	return ResponseEntity.badRequest().body("Failed to place direct order: " );
	}}


/* Running 
	/// Order API Started
	//11
	// Place - order //updated api for multiple elements order 
	// http://localhost:8080/api/rolec/64ed86596de71e6e73973c98/place-order/b86e277a-c9af-444a-be43-451e216ec059
	@PostMapping("/{consumerId}/place-order/{cartItemIds}") ///{cartItemId} //for using request param we should have to specify name in bracket 
	//@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<String> placeOrder(@PathVariable String consumerId, @PathVariable("cartItemIds") List<String> cartItemIds) {
		try {
			consumerService.placeOrder(consumerId,cartItemIds);
			return ResponseEntity.ok("Order placed successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Failed to place order: " + e.getMessage());
		}
	}
     */
	//12
	// View- all- orders
	@GetMapping("/view-all-orders/{consumerId}")
	@PreAuthorize("hasRole('CONSUMER') or hasRole('FARMER')")
	public ResponseEntity<List<OrderResponse>> viewOrders(@PathVariable String consumerId) {
	    List<OrderResponse> orderResponses = consumerService.getOrdersByConsumerId(consumerId);
	    System.out.println("\n\n\n\n This is OrderResponses "+orderResponses);
	    return ResponseEntity.ok(orderResponses);
	}

	//13
	// view single order
	@GetMapping("/{consumerId}/view-order-details/{orderId}/{orderItemId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<OrderResponse> viewSingleOrderDetails(@PathVariable String consumerId, @PathVariable String orderId,@PathVariable String orderItemId) {
		OrderResponse order = consumerService.getOrderDetailsById(consumerId, orderId,orderItemId);
		System.out.println("\n\n\n\n This is Order "+order);
		if (order != null) {
			System.out.println("\n\nDate"+order.getOrderDateTime());
			return ResponseEntity.ok(order);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@GetMapping("/{consumerId}/view-order/{orderId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<Order> viewSingleOrder(@PathVariable String consumerId, @PathVariable String orderId) {
		Order order = consumerService.getOrderById(consumerId, orderId);
		System.out.println("\n\n\n\n This is Order "+order);
		if (order != null) {
			System.out.println("\n\nDate"+order.getOrderDateTime());
			return ResponseEntity.ok(order);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	
	//14 cancel order
	@PostMapping("/{consumerId}/cancel-order/{orderId}")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<String> cancelOrder(@PathVariable String consumerId, @PathVariable String orderId) {
		consumerService.cancelOrder(consumerId, orderId);
		return ResponseEntity.ok("Order canceled successfully.");
	}
	
	
	//15
	@GetMapping("/orders/items")
	@PreAuthorize("hasRole('CONSUMER')")
	public ResponseEntity<OrderItem> getOrderItemById(
	        @RequestParam String consumerId,
	        @RequestParam String orderId,
	        @RequestParam String orderItemId) {
	    OrderItem orderItem = consumerService.getOrderItemById(consumerId, orderId, orderItemId);
	    if (orderItem != null) {
	        return ResponseEntity.ok(orderItem);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
}
