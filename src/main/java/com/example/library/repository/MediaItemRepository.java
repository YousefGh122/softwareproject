package com.example.library.repository;

import com.example.library.domain.MediaItem;
import java.util.List;
import java.util.Optional;

public interface MediaItemRepository {
    
    /**
     * Save a new media item to the database
     * @param mediaItem the media item to save
     * @return the saved media item with generated ID
     */
    MediaItem save(MediaItem mediaItem);
    
    /**
     * Update an existing media item
     * @param mediaItem the media item to update
     * @return the updated media item
     */
    MediaItem update(MediaItem mediaItem);
    
    /**
     * Find a media item by ID
     * @param itemId the item ID
     * @return Optional containing the media item if found
     */
    Optional<MediaItem> findById(Integer itemId);
    
    /**
     * Find a media item by ISBN
     * @param isbn the ISBN
     * @return Optional containing the media item if found
     */
    Optional<MediaItem> findByIsbn(String isbn);
    
    /**
     * Find all media items
     * @return list of all media items
     */
    List<MediaItem> findAll();
    
    /**
     * Find media items by type
     * @param type the media type (e.g., BOOK, DVD, MAGAZINE)
     * @return list of media items with the specified type
     */
    List<MediaItem> findByType(String type);
    
    /**
     * Find media items by title (partial match)
     * @param title the title to search for
     * @return list of matching media items
     */
    List<MediaItem> findByTitleContaining(String title);
    
    /**
     * Find media items by author (partial match)
     * @param author the author to search for
     * @return list of matching media items
     */
    List<MediaItem> findByAuthorContaining(String author);
    
    /**
     * Find available media items (availableCopies > 0)
     * @return list of available media items
     */
    List<MediaItem> findAvailableItems();
    
    /**
     * Delete a media item by ID
     * @param itemId the item ID
     * @return true if deleted successfully
     */
    boolean deleteById(Integer itemId);
    
    /**
     * Update available copies count
     * @param itemId the item ID
     * @param availableCopies the new available copies count
     */
    void updateAvailableCopies(Integer itemId, Integer availableCopies);
    
    /**
     * Search for media items by keyword (title, author, isbn, or type)
     * @param keyword the search keyword
     * @return list of matching media items
     */
    List<MediaItem> search(String keyword);
    
    /**
     * Check if a media item exists by ISBN
     * @param isbn the ISBN to check
     * @return true if a media item with this ISBN exists
     */
    boolean existsByIsbn(String isbn);
}
