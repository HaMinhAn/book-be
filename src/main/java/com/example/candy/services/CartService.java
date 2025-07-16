package com.example.candy.services;

import com.example.candy.dto.CartItemRequest;
import com.example.candy.dto.CartItemResponse;
import com.example.candy.dto.CartRequest;
import com.example.candy.dto.CartResponse;
import com.example.candy.models.Book;
import com.example.candy.models.Cart;
import com.example.candy.models.CartItem;
import com.example.candy.models.User;
import com.example.candy.repositories.BookRepository;
import com.example.candy.repositories.CartRepository;
import com.example.candy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public Optional<CartResponse> getUserCart(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        return cartOpt.map(this::convertToDto);
    }

    @Transactional
    public CartResponse createOrUpdateCart(Long userId, CartRequest cartRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Cart> existingCartOpt = cartRepository.findByUser(user);
        Cart cart;

        if (existingCartOpt.isPresent()) {
            cart = existingCartOpt.get();
            // Clear existing items
            cart.getItems().clear();
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        for (CartItemRequest itemRequest : cartRequest.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + itemRequest.getBookId()));

            if (book.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(itemRequest.getQuantity());
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        return convertToDto(cart);
    }

    @Transactional
    public boolean deleteCart(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isPresent()) {
            cartRepository.delete(cartOpt.get());
            return true;
        }
        return false;
    }

    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest itemRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(itemRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + itemRequest.getBookId()));

        if (book.getStockQuantity() < itemRequest.getQuantity()) {
            throw new RuntimeException("Not enough stock for book: " + book.getTitle());
        }

        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        Cart cart;

        if (cartOpt.isPresent()) {
            cart = cartOpt.get();
            
            // Check if the book is already in the cart
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getBook().getId().equals(itemRequest.getBookId()))
                    .findFirst();
            
            if (existingItem.isPresent()) {
                // Update quantity
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
            } else {
                // Add new item
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setBook(book);
                cartItem.setQuantity(itemRequest.getQuantity());
                cart.getItems().add(cartItem);
            }
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(itemRequest.getQuantity());
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        return convertToDto(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long bookId, Integer newQuantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isEmpty()) {
            throw new RuntimeException("Cart not found");
        }

        Cart cart = cartOpt.get();
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isEmpty()) {
            throw new RuntimeException("Item not found in cart");
        }

        CartItem item = existingItem.get();
        
        if (newQuantity <= 0) {
            cart.getItems().remove(item);
        } else {
            Book book = item.getBook();
            if (book.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
            item.setQuantity(newQuantity);
        }

        cartRepository.save(cart);
        return convertToDto(cart);
    }

    @Transactional
    public Optional<CartResponse> removeItemFromCart(Long userId, Long bookId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return Optional.empty();
        }

        Cart cart = cartOpt.get();
        boolean removed = cart.getItems().removeIf(item -> item.getBook().getId().equals(bookId));

        if (!removed) {
            return Optional.empty();
        }

        cartRepository.save(cart);
        return Optional.of(convertToDto(cart));
    }

    private CartResponse convertToDto(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
                
        response.setItems(itemResponses);
        
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        response.setTotalPrice(totalPrice);
        
        return response;
    }

    private CartItemResponse convertItemToDto(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setTitle(item.getBook().getTitle());
        response.setAuthor(item.getBook().getAuthor());
        response.setImageUrl(item.getBook().getImageUrl());
        response.setPrice(item.getBook().getPrice());
        response.setQuantity(item.getQuantity());
        
        BigDecimal subtotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        response.setSubtotal(subtotal);
        
        return response;
    }
}
