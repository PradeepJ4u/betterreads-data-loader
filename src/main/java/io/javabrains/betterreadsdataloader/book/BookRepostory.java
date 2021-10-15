package io.javabrains.betterreadsdataloader.book;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepostory extends CassandraRepository<Book, String>{
    
}
