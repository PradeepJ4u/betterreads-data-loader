package io.javabrains.betterreadsdataloader.book;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@Table(value = "book_by_id")
public class Book {

    @Id @PrimaryKeyColumn(name = "book_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String bookid;

    @Column("book_name")
    @CassandraType(type = Name.TEXT)
    private String bookname;

    @Column("book_description")
    @CassandraType(type = Name.TEXT)
    private String bookDescription;

    @Column("published_Date")
    @CassandraType(type = Name.DATE)
    private LocalDate publishedDate;
    
    @Column("cover_Ids")
    @CassandraType(type = Name.LIST,typeArguments = Name.TEXT)
    private List<String> coverIds;
    
    @Column("author_names")
    @CassandraType(type = Name.LIST,typeArguments = Name.TEXT)
    private List<String> authorNames;

    @Column("author_Ids")
    @CassandraType(type = Name.LIST,typeArguments = Name.TEXT)
    private List<String> authorIds;

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookdescription() {
        return bookDescription;
    }

    public void setBookdescription(String bookdescription) {
        this.bookDescription = bookdescription;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

   public List<String> getCoverIds() {
        return coverIds;
    }

    public void setCoverIds(List<String> coverIds) {
        this.coverIds = coverIds;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public List<String> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }

    
}