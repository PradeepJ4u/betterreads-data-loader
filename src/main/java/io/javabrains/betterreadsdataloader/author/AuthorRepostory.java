package io.javabrains.betterreadsdataloader.author;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepostory extends CassandraRepository<Author,String>{
    
}
