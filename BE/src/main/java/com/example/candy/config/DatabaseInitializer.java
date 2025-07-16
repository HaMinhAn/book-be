package com.example.candy.config;

import com.example.candy.models.Book;
import com.example.candy.models.ERole;
import com.example.candy.models.Role;
import com.example.candy.models.User;
import com.example.candy.repositories.BookRepository;
import com.example.candy.repositories.RoleRepository;
import com.example.candy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile("!test") // Don't run this component during tests
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.username:admin}")
    private String adminUsername;
    
    @Value("${app.admin.password:admin123}")
    private String adminPassword;
    
    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting database initialization for PostgreSQL...");
        
        // Initialize roles if they don't exist
        initRoles();
        
        // Create admin account if it doesn't exist
        initAdminUser();
        
        // Create sample books if none exist
        initSampleBooks();
        
        System.out.println("PostgreSQL database initialization completed successfully");
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            // For PostgreSQL, we need to save roles one by one to ensure proper sequence generation
            Role userRole = new Role(ERole.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("User role created with ID: " + userRole.getId());
            
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("Admin role created with ID: " + adminRole.getId());
            
            System.out.println("Roles initialized in PostgreSQL database");
        } else {
            System.out.println("Roles already exist in the database");
        }
    }
    
    private void initAdminUser() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            
            // Assign ADMIN role
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));
            
            // Also add USER role to admin for full permissions
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: User Role not found."));
            
            roles.add(adminRole);
            roles.add(userRole);
            adminUser.setRoles(roles);
            
            User savedAdmin = userRepository.save(adminUser);
            System.out.println("Admin user created in PostgreSQL with ID: " + savedAdmin.getId() + 
                              " and username: " + adminUsername);
        } else {
            System.out.println("Admin user already exists in PostgreSQL database");
        }
    }
    
    private void initSampleBooks() {
        if (bookRepository.count() == 0) {
            System.out.println("Creating sample books in PostgreSQL...");
            
            // Create books individually to ensure proper sequence handling in PostgreSQL
            createAndSaveBook("The Great Gatsby", "F. Scott Fitzgerald", 
                "A novel about the American Dream set in the Jazz Age", 
                new BigDecimal("12.99"), "https://m.media-amazon.com/images/I/81TLiZrasVL._UF1000,1000_QL80_.jpg", 
                50, "978-0743273565", "Fiction", 1925);
                
            createAndSaveBook("To Kill a Mockingbird", "Harper Lee", 
                "A novel about racial injustice and moral growth in the American South", 
                new BigDecimal("14.99"), "https://m.media-amazon.com/images/I/81aY1lxk+9L._SY466_.jpg", 
                45, "978-0061120084", "Fiction", 1960);
                
            createAndSaveBook("1984", "George Orwell", 
                "A dystopian novel about totalitarianism and mass surveillance", 
                new BigDecimal("11.99"), "https://m.media-amazon.com/images/I/91VsLImyJgL._AC_UY327_FMwebp_QL65_.jpg", 
                60, "978-0451524935", "Science Fiction", 1949);
                
            createAndSaveBook("The Hobbit", "J.R.R. Tolkien", 
                "A fantasy novel about the journey of Bilbo Baggins", 
                new BigDecimal("15.99"), "https://m.media-amazon.com/images/I/712cDO7d73L._AC_UY218_.jpg", 
                40, "978-0547928227", "Fantasy", 1937);
                
            createAndSaveBook("Pride and Prejudice", "Jane Austen", 
                "A romantic novel about manners and marriage in early 19th-century England", 
                new BigDecimal("10.99"), "https://m.media-amazon.com/images/I/A1cEpZkj-2L._AC_UY218_.jpg", 
                55, "978-0141439518", "Romance", 1813);
                
            createAndSaveBook("The Catcher in the Rye", "J.D. Salinger", 
                "A novel about teenage alienation and identity", 
                new BigDecimal("13.99"), "https://m.media-amazon.com/images/I/81TRBjfC5fL._AC_UY218_.jpghttps://m.media-amazon.com/images/I/81TRBjfC5fL._AC_UY218_.jpg", 
                35, "978-0316769488", "Fiction", 1951);
                
            createAndSaveBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 
                "The first book in the Harry Potter series", 
                new BigDecimal("16.99"), "https://m.media-amazon.com/images/I/91eopoUCjLL._AC_UY218_.jpg", 
                70, "978-0590353427", "Fantasy", 1997);
                
            createAndSaveBook("The Lord of the Rings", "J.R.R. Tolkien", 
                "An epic fantasy novel about the quest to destroy the One Ring", 
                new BigDecimal("24.99"), "https://m.media-amazon.com/images/I/81InoQC18SL._AC_UY218_.jpg", 
                30, "978-0618640157", "Fantasy", 1954);
                
            createAndSaveBook("The Alchemist", "Paulo Coelho", 
                "A philosophical novel about following one's dreams", 
                new BigDecimal("12.49"), "https://m.media-amazon.com/images/I/71+2-t7M35L._AC_UY218_.jpg", 
                65, "978-0062315007", "Fiction", 1988);
                
            createAndSaveBook("Brave New World", "Aldous Huxley", 
                "A dystopian novel about a genetically engineered future society", 
                new BigDecimal("13.49"), "https://m.media-amazon.com/images/I/71GNqqXuN3L._AC_UY218_.jpg", 
                50, "978-0060850524", "Science Fiction", 1932);
                
            System.out.println("Sample books created in PostgreSQL database");
        } else {
            System.out.println("Books already exist in the PostgreSQL database, skipping initialization");
        }
    }
    
    private void createAndSaveBook(String title, String author, String description, 
                           BigDecimal price, String imageUrl, Integer stockQuantity, 
                           String isbn, String category, Integer publishYear) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPrice(price);
        book.setImageUrl(imageUrl);
        book.setStockQuantity(stockQuantity);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setPublishYear(publishYear);
        
        Book savedBook = bookRepository.save(book);
        System.out.println("Created book: " + savedBook.getTitle() + " with ID: " + savedBook.getId());
    }
}
