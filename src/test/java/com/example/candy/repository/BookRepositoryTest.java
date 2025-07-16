package com.example.candy.repository;

import com.example.candy.models.Book;
import com.example.candy.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testSaveAndFindBook() {
        // Given
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
        book.setPrice(new BigDecimal("19.99"));
        book.setStockQuantity(10);
        book.setIsbn("1234567890");
        book.setCategory("Test");
        book.setPublishYear(2023);

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Test Book");
        
        // Verify we can find it
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("Test Book");
    }
}
