package com.platform.drockley.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;
    
    public static Pageable getPageable(Integer page, Integer size, String sortBy, String direction) {
        int pageNum = Math.max(0, page != null && page >= 0 ? page : DEFAULT_PAGE);
        int pageSize = size != null && size > 0 && size <= MAX_SIZE ? size : DEFAULT_SIZE;
        
        if (sortBy == null || sortBy.isEmpty()) {
            return PageRequest.of(pageNum, pageSize);
        }
        
        Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        return PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, sortBy));
    }
    
    public static Pageable getPageableWithSort(Integer page, Integer size, String sortBy) {
        return getPageable(page, size, sortBy, "ASC");
    }
    
    public static Pageable getPageableDefault(Integer page, Integer size) {
        return getPageable(page, size, null, null);
    }
    
    public static int validatePageSize(Integer size) {
        if (size == null || size <= 0) return DEFAULT_SIZE;
        if (size > MAX_SIZE) return MAX_SIZE;
        return size;
    }
}
