package com.tericcabrel.authapi.services;

import com.tericcabrel.authapi.dtos.OrdersDto;
import com.tericcabrel.authapi.dtos.ProductDto;
import com.tericcabrel.authapi.dtos.ShoppingCartDto;
import com.tericcabrel.authapi.dtos.ShoppingCartProductDto;
import com.tericcabrel.authapi.entities.*;
import com.tericcabrel.authapi.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrdersProductRepository ordersProductRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ModelMapper modelMapper;


    public List<OrdersDto> getAllOrders() {
        List<Orders> orders = this.ordersRepository.findAll();
        return orders.stream().map(orderEntity -> this.modelMapper.map(orderEntity, OrdersDto.class)).toList();
    }

    public List<OrdersDto> getAllOrdersByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<Orders> orders = this.ordersRepository.findByUserId(currentUser.getId());
        return orders.stream().map(orderEntity -> this.modelMapper.map(orderEntity, OrdersDto.class)).toList();
    }

    public OrdersDto getOrderById(Long orderId) {
        Optional<Orders> order = this.ordersRepository.findById(orderId);

        return order.map(orderEntity -> this.modelMapper.map(orderEntity, OrdersDto.class)).orElse(null);
    }

    public OrdersDto getOrderByIdAsUser(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<Orders> orders = this.ordersRepository.findByUserId(currentUser.getId());

        if(orders != null){
            Optional<Orders> order = orders.stream().filter(orderEntity -> orderEntity.getId().equals(orderId)).findFirst();
            return order.map(orderEntity -> this.modelMapper.map(orderEntity, OrdersDto.class)).orElse(null);
        }

        return null;
    }

    public boolean editOrderStatus(OrdersDto ordersDto){
        Optional<Orders> order = this.ordersRepository.findById(ordersDto.getId());
        if(order.isPresent()){
            order.get().setStatus(ordersDto.getStatus());
            this.ordersRepository.save(order.get());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean convertCartToOrder(OrdersDto ordersDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());

        if(shoppingCart.isPresent()){
            Orders order = new Orders();
            for(ShoppingCartProduct shoppingCartProduct : shoppingCart.get().getShoppingCartProducts()){
                //Optional<ShoppingCartProduct> matchedShoppingCartProduct =  shoppingCart.get().getShoppingCartProducts().stream().filter(cartProductEntity -> cartProductEntity.getProduct().getId().equals(wishlistProduct.getProduct().getId())).findFirst();

                OrdersProduct orderProduct = new OrdersProduct();
                orderProduct.setProduct(this.productRepository.findById(shoppingCartProduct.getProduct().getId()).get());
                orderProduct.setOrders(order);
                orderProduct.setQuantity(shoppingCartProduct.getQuantity());
                orderProduct.setPrice(shoppingCartProduct.getPrice());
                order.getOrdersProducts().add(orderProduct);
            }

            order.setUser(currentUser);
            order.setProvince(ordersDto.getProvince());
            order.setCity(ordersDto.getCity());
            order.setAddress1(ordersDto.getAddress1());
            order.setAddress2(ordersDto.getAddress2());
            order.setZipCode(ordersDto.getZipCode());
            order.setCardNumber(ordersDto.getCardNumber());
            order.setTotal(shoppingCartService.getShoppingCartTotal());
            order.setStatus(1);

            //this.shoppingCartService.clearShoppingCart();
            this.ordersRepository.save(order);
            this.shoppingCartProductRepository.deleteAllByCart(shoppingCart.get());
            return true;
        }
        return false;
    }
}
