package vttp.miniproject01game.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject01game.models.Category;
import vttp.miniproject01game.models.Trivia;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.repositories.TriviaRepository;

@Service
public class TriviaService {
    
    private static final String url = "https://opentdb.com/api.php";
    private static final String categoriesUrl = "https://opentdb.com/api_category.php";
    private static final int QUIZ_PER_PAGE = 1;

    @Autowired
    private TriviaRepository tRepo;

    public List<Trivia> getTrivia(String qn, String cat, String dif, String type) {

        String payload;

        // Create url with query string (add parameters)
        String uri = UriComponentsBuilder.fromUriString(url)
        .queryParam("amount", qn)
        .queryParam("category", cat)
        // .queryParamIfPresent("category", Optional.of(cat))
        .queryParam("difficulty", dif)
        .queryParamIfPresent("type", Optional.of(type))
        .toUriString();

        System.out.println("URI>> " + uri);
        // Create the GET request, GET url
        RequestEntity<Void> req = RequestEntity.get(uri).build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp;

        try {
            // Throws exception if status code is not between 200 - 399
            resp = template.exchange(req, String.class);
        } catch (Exception e) {
            System.err.printf("Error: %s\n", e);
            return Collections.emptyList();
        }

        if (resp.getStatusCodeValue() != 200) {
            System.err.println("Error status code is not 200\n");
            return Collections.emptyList();
        }

        // Get payload 
        payload = resp.getBody();
        // System.out.println(">>> Payload: \n" + payload);
        
        // Convert payload into JsonObject
        // Convert string to a Reader
        Reader strReader = new StringReader(payload);

        // Create a JsonReader from reader
        JsonReader jsonReader = Json.createReader(strReader);

        // Read and save the payload as Json Object
        JsonObject jObject = jsonReader.readObject();
                                         // should tally with the object name from api
        JsonArray resultArray = jObject.getJsonArray("results");

        List<Trivia> list = new LinkedList<>();
        for (int i = 0; i < resultArray.size(); i++) {
            // loop through the top _ coins
            JsonObject jo = resultArray.getJsonObject(i);
            JsonArray ansJo = jo.getJsonArray("incorrect_answers");
            // System.out.println(ansJo);
            list.add(Trivia.create(jo, ansJo));
        }
        tRepo.saveTrivia(list);
        return list;
    }

    public Page<Trivia> listByPage(int pageNo) {
        List<Trivia> list = tRepo.getTrivia();
        int totalpages = list.size() / QUIZ_PER_PAGE;
        // PageRequest pg = new PageRequest(pageNo, pagesize);
        PageRequest pageable = PageRequest.of(pageNo, QUIZ_PER_PAGE);

        int max = pageNo >= totalpages ? list.size() : QUIZ_PER_PAGE*(pageNo+1);
        int min = pageNo > totalpages ? max : QUIZ_PER_PAGE*pageNo;

        Page<Trivia> pageResponse = new PageImpl<Trivia>(list.subList(min, max), pageable, list.size());

        return pageResponse;
    }

    public List<Category> getCategories() {

        String payload;

        // Create url with query string (add parameters)
        String URI = UriComponentsBuilder.fromUriString(categoriesUrl)
                .toUriString();
        // Create the GET request, GET url
        RequestEntity<Void> request = RequestEntity.get(URI).build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response;

        try {
            // Throws exception if status code is not between 200 - 399
            response = template.exchange(request, String.class);
        } catch (Exception e) {
            System.err.printf("Error: %s\n", e);
            return Collections.emptyList();
        }

        if (response.getStatusCodeValue() != 200) {
            System.err.println("Error status code is not 200\n");
            return Collections.emptyList();
        }

        // Get payload 
        payload = response.getBody();
        // System.out.println(">>> Payload: \n" + payload);
        
        // Convert payload into JsonObject
        // Convert string to a Reader
        Reader strReader = new StringReader(payload);

        // Create a JsonReader from reader
        JsonReader jsonReader = Json.createReader(strReader);

        // Read and save the payload as Json Object
        JsonObject jObject = jsonReader.readObject();
                                         // should tally with the object name from api
        JsonArray triviaArray = jObject.getJsonArray("trivia_categories");

        List<Category> categoryList = new LinkedList<>();
        for (int i = 0; i < triviaArray.size(); i++) {

            JsonObject jo = triviaArray.getJsonObject(i);

            categoryList.add(Category.create(jo));
        }
        return categoryList;
    }

    

}
