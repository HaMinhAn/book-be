package com.example.candy.services;

import com.example.candy.dto.BookRequest;
import com.example.candy.dto.BookResponse;
import com.example.candy.models.Book;
import com.example.candy.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<BookResponse> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<BookResponse> searchBooks(String title, String author, String category) {
        List<Book> books;
        
        if (title != null && !title.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (category != null && !category.isEmpty()) {
            books = bookRepository.findByCategory(category);
        } else {
            books = bookRepository.findAll();
        }
        
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = convertToEntity(bookRequest);
        Book savedBook = bookRepository.save(book);
        return convertToDto(savedBook);
    }
    
    public Optional<BookResponse> updateBook(Long id, BookRequest bookRequest) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            updateBookFromDto(book, bookRequest);
            Book updatedBook = bookRepository.save(book);
            return Optional.of(convertToDto(updatedBook));
        }
        
        return Optional.empty();
    }
    
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private BookResponse convertToDto(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setDescription(book.getDescription());
        response.setPrice(book.getPrice());
        response.setImageUrl(book.getImageUrl());
        response.setStockQuantity(book.getStockQuantity());
        response.setIsbn(book.getIsbn());
        response.setCategory(book.getCategory());
        response.setPublishYear(book.getPublishYear());
        return response;
    }
    
    private Book convertToEntity(BookRequest bookRequest) {
        Book book = new Book();
        updateBookFromDto(book, bookRequest);
        return book;
    }
    
    private void updateBookFromDto(Book book, BookRequest bookRequest) {
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setDescription(bookRequest.getDescription());
        book.setPrice(bookRequest.getPrice());
        book.setImageUrl(bookRequest.getImageUrl());
        book.setStockQuantity(bookRequest.getStockQuantity());
        book.setIsbn(bookRequest.getIsbn());
        book.setCategory(bookRequest.getCategory());
        book.setPublishYear(bookRequest.getPublishYear());
    }
}
