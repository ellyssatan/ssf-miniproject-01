package vttp.miniproject01game.controllers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.miniproject01game.models.Category;
import vttp.miniproject01game.models.Trivia;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.repositories.TriviaRepository;
import vttp.miniproject01game.services.TriviaService;
import vttp.miniproject01game.services.UserService;

@Controller
@RequestMapping
public class TriviaController {

    @Autowired
    private TriviaService triviaSvc;

    @Autowired
    private UserService userSvc;

    // @Autowired
    // @Qualifier("triviaValidator")
    // private Validator validator;
    
    // allows us to configure web data binding directly within the controller
    // initialize the WebDataBinder that is used for data binding from web request parameters to JavaBean objects. Here, the WebDataBinder is where the validator is set.
    // @InitBinder
    // private void initBinder(WebDataBinder binder) {
    //     binder.setValidator(validator);
    // }

    List<Trivia> questionList;

    @PostMapping("/register")
    public String registerUser(@RequestBody MultiValueMap<String, String> form, Model model) {

        String name = form.getFirst("name");
        String email = form.getFirst("email");
        String password = form.getFirst("password");

        if (!userSvc.checkUser(email)) {
            System.out.println("CREATING NEW USER");
            User u = new User();
            u = u.create(name, email, password);

            userSvc.saveUser(u);
            System.out.println("USER REGISTERED");
            return "start";
        }
        
        System.out.println(">>>>USER EXISTS, USE LOGIN PAGE");

        // model.addAttribute("name", name);
        return "login";
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody MultiValueMap<String, String> form, Model model) {

        String email = form.getFirst("username");
        String password = form.getFirst("password");

        User u = userSvc.getUser(email);
        String error;

        if (u == null) {
            System.out.printf("Cannot find user %s", email);
        }

        if (u.getPassword().equals(password)) {
            model.addAttribute("name", u.getName());
            return "start";

        } else {
            error = "WRONG PASSWORD";
            System.out.println("WRONG PASSWORD");
            model.addAttribute("error", error);
            return "login";
        }
        
    }

    @GetMapping("/trivia")
    public String getCategories(Model model) {

        List<Category> options = triviaSvc.getCategories();
        // System.out.println(">>>OPTIONS LIST: " + options);

        model.addAttribute("options", options);
        return "start";
    }

    @PostMapping("/trivia")
    public String getTrivia(@RequestBody MultiValueMap<String, String> form, Model model) {

        String qn = form.getFirst("qn");
        String cat = form.getFirst("cat");
        String dif = form.getFirst("dif");
        String type = form.getFirst("type");
        triviaSvc.getTrivia(qn, cat, dif, type);
        // sess.setAttribute("questions", listOfTrivia);

        return listByPage(model, 1);
    }


    @GetMapping("/trivia/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") int pageNum) {
        Page<Trivia> page = triviaSvc.listByPage(pageNum);
        System.out.println("page: " + page);
        List<Trivia> listTrivia = page.getContent();

        System.out.println("PageNum = " + pageNum);
        long totalItems = page.getTotalElements();
        System.out.println("Total element = " + page.getTotalElements());
        int totalPages = page.getTotalPages();
        System.out.println("Total Pages = " + page.getTotalPages());
      
        model.addAttribute("listTrivia", listTrivia);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        return "index";
    }

    // @PostMapping("/")
    // public String prevNext(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

    //     List<String> answers = null;

    //     String ans = form.getFirst("ans");
    //     System.out.println(">>>>ans selected: " + ans);

        // <input  type="hidden" name="name" data-th-value="${name}">


        // if (!isNull(name)) {
		// 	// new session
		// 	System.out.println("name not in session");
		// 	sess.setAttribute("name", name);
		// 	cart = new LinkedList<>();
		// 	sess.setAttribute("cart", cart);

		// } 

        // name = (String)sess.getAttribute("name");
		// cart = (List<String>)sess.getAttribute("cart");
		// String item = form.getFirst("item");
		// if (!isNull(item))
		// 	cart.add(item);


		// model.addAttribute("name", name.toUpperCase());
		// model.addAttribute("cart", cart);
        // if (ans.is)
    //     return null;

    // }

    // @GetMapping("/getresults")
    // public String getResults (@RequestParam(value="ans") String[] radioCheckedValues, Model model) {
    //     for (String s : radioCheckedValues) {
    //         System.out.println(s);
    //     }
    //     return null;
    // }

    // @PostMapping("postresults")
    // public String submitForm(Model model, @Validated Trivia trivia, BindingResult result) {
    //     String returnVal = "successfully completed";
    //     if(result.hasErrors()) {
    //         initModelList(model);
    //         returnVal = "order";
    //     } else {
    //         model.addAttribute("order", order);
    //     }       
    //     return returnVal;
    // }

    private boolean isNull(String s) {
		return ((null == s) || (s.trim().length() <= 0));
	}
 
}
