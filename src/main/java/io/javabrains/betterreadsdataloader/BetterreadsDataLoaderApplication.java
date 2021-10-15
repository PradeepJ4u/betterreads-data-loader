package io.javabrains.betterreadsdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.javabrains.betterreadsdataloader.author.Author;
import io.javabrains.betterreadsdataloader.author.AuthorRepostory;
import io.javabrains.betterreadsdataloader.book.BookRepostory;
import io.javabrains.betterreadsdataloader.book.Book;
import io.javabrains.betterreadsdataloader.connection.DataStacksAstraProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStacksAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	@Autowired
	AuthorRepostory authorRepostory;

	@Autowired
	BookRepostory workRepostory;


	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {

		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
	}

	private void initAuthors() {
		Path path = Paths.get(authorDumpLocation);
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse string
				String jsonString = line.substring(line.indexOf("{"));
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// Construct Author Object
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonalName(jsonObject.optString("personal_name"));
					author.setId(jsonObject.optString("key").replace("/authors/", ""));

					// Persit to the repository
					System.out.println("Saving Author -- " + author.getName() + "----");
					authorRepostory.save(author);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initWorks() {
		Path path = Paths.get(worksDumpLocation);
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse string
				String jsonString = line.substring(line.indexOf("{"));
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
				try {
					JSONObject jsonObject = new JSONObject(jsonString);

					// Construct Book Object
					Book book = new Book();
					book.setBookid(jsonObject.getString("key").replace("/works/", ""));
					book.setBookname(jsonObject.optString("title"));	
					JSONObject descriptionJsonObject = jsonObject.optJSONObject("description");
					if(descriptionJsonObject != null){
						book.setBookdescription(descriptionJsonObject.optString("value"));	
					}	else{
						book.setBookdescription("No Description Found");
					}
					
					JSONObject pulishedDateJsonOnject = jsonObject.optJSONObject("created");
					if(pulishedDateJsonOnject != null){
						String strPublishedDate = pulishedDateJsonOnject.optString("value");
						book.setPublishedDate(LocalDate.parse(strPublishedDate, dateFormat));
					}	

					JSONArray coversJsonArray = jsonObject.optJSONArray("covers");
					if(coversJsonArray != null){
						List<String> coverIds = new ArrayList<>();						
						for(int i = 0; i < coversJsonArray.length() ; i++){
							coverIds.add(coversJsonArray.getString(i));
						}
						book.setCoverIds(coverIds);
					}

					JSONArray authorsJsonArray = jsonObject.optJSONArray("authors");
					if(authorsJsonArray != null){
						List<String> authorIds = new ArrayList<>();	
						for(int i = 0; i < authorsJsonArray.length() ; i++){
							String authorName = authorsJsonArray.getJSONObject(i).getJSONObject("author").getString("key").replace("/authors/", "");
							authorIds.add(authorName);
						}
						book.setAuthorIds(authorIds);
						List<String> authorNames = authorIds.stream().map(id -> authorRepostory.findById(id))
						.map(optionalAuthor -> {
							if(!optionalAuthor.isPresent()) return "Unknown Author";
							return optionalAuthor.get().getName();
						}).collect(Collectors.toList());
						book.setAuthorNames(authorNames);

						// Persit to the repository
						System.out.println("Saving Work -- " + book.getBookname() + "----");
						workRepostory.save(book);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	

	@PostConstruct
	public void start() {
		initAuthors();
		initWorks();
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStacksAstraProperties asraProperties) {
		Path bundle = asraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);

	}
}