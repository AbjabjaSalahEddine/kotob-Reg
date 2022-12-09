package com.example.apigateway.api_gateway.routes;


import com.example.apigateway.authentication.domain.User;
import com.example.apigateway.authentication.service.UserService;
import com.example.apigateway.service_discovery.entity.Service;
import com.example.apigateway.service_discovery.repository.ServiceRepository;
import com.example.apigateway.authentication.repo.UserRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/library_owner")
@RequiredArgsConstructor
@Slf4j
public class LibraryOwnerApi {
    private final UserService userService;
    private final ServiceRepository serviceRepository;

    private final UserRepo userRepo;
    private String LIBRARIESMSNAME="Libraries_MS";
    private String SCANNEDBOOKSMSNAME="ScannedBooks_MS";

    public String getHostUrl(String name){
        List<Service> services= serviceRepository.findAll();
        List<Service> Instances= new ArrayList<>();
        services.stream().forEach(s -> {
            if(s.getName().equals(name)){
                Instances.add(s);
            }
        });
        Random rand = new Random();
        if(Instances.size()>0){
            return Instances.get(rand.nextInt(Instances.size())).getUrl();
        }else{
            return "None";
        }
    }

    //Library realted endpoints

    @GetMapping("/libraries")
    public ResponseEntity<?> getProfile(Principal principal) {
        User actualuser=userService.getUser(principal.getName());
        List<Long> ids=actualuser.getLibraries();
        String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/librariesWithIds";
        try {
            List<Map> result = new RestTemplate().postForEntity(url, ids, List.class).getBody();
            for (int i = 0; i < ids.size(); i++) {
                if(result.get(i)==null){
                    actualuser.getLibraries().remove(i);
                }
            }
            result=result.stream().filter(l -> l!=null ) .collect(Collectors.toList());

            return new ResponseEntity<List>(result , HttpStatus.OK);

        }catch (RestClientException e){
            Map map = new HashMap<String,String>();
            map.put("msg","Service Libraries is down probably");
            return new ResponseEntity<Map>(map, HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/libraries")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> addLibrary(Principal principal,@RequestBody LibraryToAdd library) {
        User actualuser=userService.getUser(principal.getName());
        String userid=actualuser.getId();
        library.setLibraryowner(userid);
        log.info(Arrays.asList(library).toString());
        String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/mylibrary";
        try{
            log.info("sending to :"+url);
            Map result=new RestTemplate().postForEntity(url,  library ,Map.class).getBody();
            userRepo.findById(userid);

            actualuser.getLibraries().add(Long.valueOf(result.get("id").toString()));
            userRepo.save(actualuser);


            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (RestClientException e){
            Map errormap = new HashMap<String,String>();
            errormap.put("msg","Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

    @PatchMapping("/libraries/{id}")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> updatelibrary(Principal principal,@RequestBody Map o,@PathVariable Long id) {
        String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/libraries/"+id;
        User actualuser=userService.getUser(principal.getName());
        String userid=actualuser.getId();
        try {
            Map library=new RestTemplate().getForEntity(url ,Map.class).getBody();
            if (library.get("libraryowner").toString().equals(userid)){
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                Map result= template.patchForObject(url, o , Map.class);
                log.info(result.toString());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                Map errormap = new HashMap<String,String>();
                errormap.put("msg","Not Authorized to edit this library");
                return new ResponseEntity<Map>(errormap, FORBIDDEN);
            }
        }catch (RestClientException e){
            Map errormap = new HashMap<String,String>();
            errormap.put("msg2",e.toString());
            errormap.put("msg","Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping("/libraries/{id}")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> deletelibrary(Principal principal,@PathVariable Long id) {
        String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/libraries/"+id;
        User actualuser=userService.getUser(principal.getName());
        String userid=actualuser.getId();
        try {
            Map library=new RestTemplate().getForEntity(url ,Map.class).getBody();
            if (library.get("libraryowner").toString().equals(userid)){
//                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                Map result= new HashMap<>();
                result.put("msg","library deleted");
                try {
                    new RestTemplate().delete(new URI(url));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                int pos =actualuser.getLibraries().indexOf(id);
                actualuser.getLibraries().remove(pos);
                log.info(actualuser.getLibraries().toString());
                userRepo.save(actualuser);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                Map errormap = new HashMap<String,String>();
                errormap.put("msg","Not Authorized to edit this library");
                return new ResponseEntity<Map>(errormap, FORBIDDEN);
            }
        }catch (RestClientException e){
            Map errormap = new HashMap<String,String>();
            errormap.put("msg2",e.toString());
            errormap.put("msg","Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

    //Book realted endpoints

    @PostMapping("library/{id}/books")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> addBook(Principal principal,@RequestBody BookToAdd book,@PathVariable Long id) {
        User actualuser = userService.getUser(principal.getName());
        String userid = actualuser.getId();
        String url1 = "http://" + getHostUrl(LIBRARIESMSNAME) + "/libraries/" + id;
        try {
            Map library = new RestTemplate().getForEntity(url1, Map.class).getBody();
            if (library.get("libraryowner").toString().equals(userid)) {
                book.setLibrary("http://" + getHostUrl(LIBRARIESMSNAME) + "/libraries/" + id);
                log.info(Arrays.asList(book).toString());

                String url2 = "http://" + getHostUrl(LIBRARIESMSNAME) + "/books";

                try {
                    log.info("sending to :" + url2);
                    Map result = new RestTemplate().postForEntity(url2, book, Map.class).getBody();
                    return new ResponseEntity<>(result, HttpStatus.OK);

                } catch (RestClientException e) {
                    Map errormap = new HashMap<String, String>();
                    errormap.put("msg", "Service is down");
                    return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
                }

            } else {
                Map errormap = new HashMap<String,String>();
                errormap.put("msg","Not Authorized to add book to this library");
                return new ResponseEntity<Map>(errormap, FORBIDDEN);
            }
        } catch (RestClientException e) {
            Map errormap = new HashMap<String, String>();
            errormap.put("msg2", e.toString());
            errormap.put("msg", "Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }
    @GetMapping("library/{id}/books")
    public ResponseEntity<?> getBooksForLibrary(Principal principal,@PathVariable Long id) {
        String url = "http://" + getHostUrl(LIBRARIESMSNAME) + "/libraries/" + id + "/books";
        log.info("url : "+url);
        try {
            Map result = new RestTemplate().getForEntity(url, Map.class).getBody();
            log.info(Arrays.asList(result).toString());
            Map embeded=(Map) result.get("_embedded");

            return new ResponseEntity<>(embeded.get("books"), HttpStatus.OK);

        } catch (RestClientException e) {
            Map errormap = new HashMap<String, String>();
            errormap.put("msg", "Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

    @PatchMapping("/book/{id}")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> updateBook(Principal principal,@RequestBody Map o,@PathVariable Long id) {
        String url1= "http://"+getHostUrl(LIBRARIESMSNAME)+"/books/"+id+"/library";
        User actualuser=userService.getUser(principal.getName());
        String userid=actualuser.getId();
        try {
            Map library=new RestTemplate().getForEntity(url1 ,Map.class).getBody();
            if (library.get("libraryowner").toString().equals(userid)){
                String url="http://"+getHostUrl(LIBRARIESMSNAME)+"/books/"+id;
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                Map result= template.patchForObject(url, o , Map.class);
                log.info(result.toString());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                Map errormap = new HashMap<String,String>();
                errormap.put("msg","Not Authorized to edit this book");
                return new ResponseEntity<Map>(errormap, FORBIDDEN);
            }
        }catch (RestClientException e){
            Map errormap = new HashMap<String,String>();
            errormap.put("msg2",e.toString());
            errormap.put("msg","Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping("/books/{id}")
    @PostAuthorize("hasAuthority('LIBRARY_OWNER')")
    public ResponseEntity<?> deleteBook(Principal principal,@PathVariable Long id) {
        String url1= "http://"+getHostUrl(LIBRARIESMSNAME)+"/books/"+id+"/library";
        User actualuser=userService.getUser(principal.getName());
        String userid=actualuser.getId();
        try {
            Map library=new RestTemplate().getForEntity(url1 ,Map.class).getBody();
            if (library.get("libraryowner").toString().equals(userid)){
                String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/books/"+id;
                Map book=new RestTemplate().getForEntity(url ,Map.class).getBody();
                Boolean searchable= (Boolean) book.get("isSearchable");
                log.info(Arrays.asList(book).toString());
                String url2="http://"+getHostUrl(LIBRARIESMSNAME)+"/books/"+id;
                String url3="http://"+getHostUrl(SCANNEDBOOKSMSNAME)+"/scannedbooks/"+id;
                try {
                    if(searchable){
                        new RestTemplate().delete(new URI(url3));
                    }
                    new RestTemplate().delete(new URI(url2));

                    Map result= new HashMap<>();
                    result.put("msg","book deleted");
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }


            }else{
                Map errormap = new HashMap<String,String>();
                errormap.put("msg","Not Authorized to edit this library");
                return new ResponseEntity<Map>(errormap, FORBIDDEN);
            }
        }catch (RestClientException e){
            Map errormap = new HashMap<String,String>();
            errormap.put("msg2",e.toString());
            errormap.put("msg","Service is down");
            return new ResponseEntity<Map>(errormap, HttpStatus.BAD_GATEWAY);
        }
    }

}
@Data
class LibraryToAdd {
    private String libraryowner;
    private String name;
    private String address;
    private String imagelink;
}
@Data
class BookToAdd {
    private String libraryowner;
    private String name;
    private String imagelink;
    private String author ;
    private String library ;
    private String language ;
    private Float price;
    private Integer availableQuantity ;
    private Boolean isSearchable ;
}