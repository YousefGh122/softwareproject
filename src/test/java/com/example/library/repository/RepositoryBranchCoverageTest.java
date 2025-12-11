package com.example.library.repository;

import com.example.library.DatabaseConnection;
import com.example.library.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite targeting missed branches in repository classes
 * to increase branch coverage from 81% to 90%+
 */
class RepositoryBranchCoverageTest {

    private MockedStatic<DatabaseConnection> mockedDbConnection;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        
        mockedDbConnection = mockStatic(DatabaseConnection.class);
        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDbConnection != null) {
            mockedDbConnection.close();
        }
    }

    // ==================== JdbcUserRepository Tests ====================

    @Test
    void testUserRepository_ExistsByUsername_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            }
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        assertThrows(RuntimeException.class, () -> repository.existsByUsername("testuser"));
    }

    @Test
    void testUserRepository_ExistsByUsername_EmptyResultSet() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        boolean exists = repository.existsByUsername("nonexistent");
        assertFalse(exists);
    }

    @Test
    void testUserRepository_ExistsByEmail_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        assertThrows(RuntimeException.class, () -> repository.existsByEmail("test@test.com"));
    }

    @Test
    void testUserRepository_ExistsByEmail_EmptyResultSet() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        boolean exists = repository.existsByEmail("nonexistent@test.com");
        assertFalse(exists);
    }

    @Test
    void testUserRepository_FindById_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findById(1L));
    }

    @Test
    void testUserRepository_FindByUsername_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findByUsername("testuser"));
    }

    @Test
    void testUserRepository_FindByEmail_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcUserRepository repository = new JdbcUserRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findByEmail("test@test.com"));
    }

    @Test
    void testUserRepository_Save_NoRowsAffected() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        
        JdbcUserRepository repository = new JdbcUserRepository();
        User user = new User(null, "testuser", "test@test.com", "password", UserRole.MEMBER, null);
        
        assertThrows(RuntimeException.class, () -> repository.save(user));
    }

    @Test
    void testUserRepository_Save_NoGeneratedKeys() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcUserRepository repository = new JdbcUserRepository();
        User user = new User(null, "testuser", "test@test.com", "password", UserRole.MEMBER, null);
        
        assertThrows(RuntimeException.class, () -> repository.save(user));
    }

    // ==================== JdbcFineRepository Tests ====================

    @Test
    void testFineRepository_ExistsByLoanId_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        assertThrows(RuntimeException.class, () -> repository.existsByLoanId(1L));
    }

    @Test
    void testFineRepository_ExistsByLoanId_EmptyResultSet() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        boolean exists = repository.existsByLoanId(999L);
        assertFalse(exists);
    }

    @Test
    void testFineRepository_CalculateTotalUnpaid_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        assertThrows(RuntimeException.class, () -> repository.calculateTotalUnpaidByUserId(1L));
    }

    @Test
    void testFineRepository_CalculateTotalUnpaid_NoResults() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        BigDecimal total = repository.calculateTotalUnpaidByUserId(999L);
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testFineRepository_Save_NoRowsAffected() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        
        JdbcFineRepository repository = new JdbcFineRepository();
        Fine fine = new Fine(null, 1L, BigDecimal.TEN, LocalDate.now(), FineStatus.UNPAID);
        
        assertThrows(RuntimeException.class, () -> repository.save(fine));
    }

    @Test
    void testFineRepository_Save_NoGeneratedKeys() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcFineRepository repository = new JdbcFineRepository();
        Fine fine = new Fine(null, 1L, BigDecimal.TEN, LocalDate.now(), FineStatus.UNPAID);
        
        assertThrows(RuntimeException.class, () -> repository.save(fine));
    }

    @Test
    void testFineRepository_FindById_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findById(1L));
    }

    @Test
    void testFineRepository_FindByLoanId_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcFineRepository repository = new JdbcFineRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findByLoanId(1L));
    }

    // ==================== JdbcMediaItemRepository Tests ====================

    @Test
    void testMediaItemRepository_ExistsByIsbn_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        
        assertThrows(RuntimeException.class, () -> repository.existsByIsbn("123456"));
    }

    @Test
    void testMediaItemRepository_ExistsByIsbn_EmptyResultSet() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        
        boolean exists = repository.existsByIsbn("nonexistent");
        assertFalse(exists);
    }

    @Test
    void testMediaItemRepository_FindById_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findById(1L));
    }

    @Test
    void testMediaItemRepository_FindByIsbn_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findByIsbn("123456"));
    }

    @Test
    void testMediaItemRepository_Save_NoRowsAffected() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        MediaItem item = new MediaItem(null, "Test Book", "Author", "123456", MediaType.BOOK, 
                                       LocalDate.now(), 5, 5, BigDecimal.ONE);
        
        assertThrows(RuntimeException.class, () -> repository.save(item));
    }

    @Test
    void testMediaItemRepository_Save_NoGeneratedKeys() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcMediaItemRepository repository = new JdbcMediaItemRepository();
        MediaItem item = new MediaItem(null, "Test Book", "Author", "123456", MediaType.BOOK, 
                                       LocalDate.now(), 5, 5, BigDecimal.ONE);
        
        assertThrows(RuntimeException.class, () -> repository.save(item));
    }

    // ==================== JdbcLoanRepository Tests ====================

    @Test
    void testLoanRepository_CountActiveByUserId_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcLoanRepository repository = new JdbcLoanRepository();
        
        assertThrows(RuntimeException.class, () -> repository.countActiveByUserId(1L));
    }

    @Test
    void testLoanRepository_CountActiveByUserId_NoResults() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcLoanRepository repository = new JdbcLoanRepository();
        
        int count = repository.countActiveByUserId(999L);
        assertEquals(0, count);
    }

    @Test
    void testLoanRepository_FindById_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcLoanRepository repository = new JdbcLoanRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findById(1L));
    }

    @Test
    void testLoanRepository_Save_NoRowsAffected() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        
        JdbcLoanRepository repository = new JdbcLoanRepository();
        Loan loan = new Loan(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        
        assertThrows(RuntimeException.class, () -> repository.save(loan));
    }

    @Test
    void testLoanRepository_Save_NoGeneratedKeys() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcLoanRepository repository = new JdbcLoanRepository();
        Loan loan = new Loan(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        
        assertThrows(RuntimeException.class, () -> repository.save(loan));
    }

    // ==================== JdbcReservationRepository Tests ====================

    @Test
    void testReservationRepository_CountActiveByItemId_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcReservationRepository repository = new JdbcReservationRepository();
        
        assertThrows(RuntimeException.class, () -> repository.countActiveByItemId(1L));
    }

    @Test
    void testReservationRepository_CountActiveByItemId_NoResults() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcReservationRepository repository = new JdbcReservationRepository();
        
        int count = repository.countActiveByItemId(999L);
        assertEquals(0, count);
    }

    @Test
    void testReservationRepository_FindById_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        
        JdbcReservationRepository repository = new JdbcReservationRepository();
        
        assertThrows(RuntimeException.class, () -> repository.findById(1L));
    }

    @Test
    void testReservationRepository_Save_NoGeneratedKeys() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        JdbcReservationRepository repository = new JdbcReservationRepository();
        Reservation reservation = new Reservation(null, 1L, 1L, LocalDate.now(), ReservationStatus.ACTIVE);
        
        assertThrows(RuntimeException.class, () -> repository.save(reservation));
    }

    @Test
    void testReservationRepository_Update_NoRowsAffected() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        
        JdbcReservationRepository repository = new JdbcReservationRepository();
        Reservation reservation = new Reservation(999L, 1L, 1L, LocalDate.now(), ReservationStatus.CANCELLED);
        
        assertThrows(RuntimeException.class, () -> repository.update(reservation));
    }
}
