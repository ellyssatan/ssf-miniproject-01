package vttp.miniproject01game.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp.miniproject01game.models.Category;
import vttp.miniproject01game.models.Trivia;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.services.TriviaService;
import vttp.miniproject01game.services.UserService;

@Controller
@RequestMapping
public class TriviaController {

    @Autowired
    private TriviaService triviaSvc;

    @Autowired
    private UserService userSvc;

    List<Trivia> questionList;

    @PostMapping("/registered")
    public String registerUser(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String name = form.getFirst("name");
        // String name = (String) sess.getAttribute("name");
        String email = form.getFirst("email").toLowerCase();
        String password = form.getFirst("password");

        if (!userSvc.checkUser(email)) {
            System.out.println("CREATING NEW USER");
            User u = new User();
            u = u.create(name, email, password);

            userSvc.saveUser(u);
            sess.setAttribute("name", name);
            // model.addAttribute("name", name);
            // System.out.println("USER REGISTERED");

            List<Category> options = triviaSvc.getCategories();
            // System.out.println(">>>OPTIONS LIST: " + options);

            model.addAttribute("options", options);
            model.addAttribute("name", name);
            // String nameRetrieved = (String) sess.getAttribute("name");
            // System.out.println("NAME RETRIEVED------ " + nameRetrieved);
            return "start";
        }
        
        // System.out.println(">>>>USER EXISTS, USE LOGIN PAGE");
        return "login";
    }

    @PostMapping("/loginPage")
    public String userLogin(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String email = form.getFirst("username").toLowerCase();
        String password = form.getFirst("password");

        User u = userSvc.getUser(email);
        String error;

        // check if user exists
        if (u == null) {
            error = "User does not exist...Please register";
            // System.out.printf("Cannot find user %s", email);
            model.addAttribute("error", error);
            return "index";
        }

        // check password
        if (BCrypt.checkpw(password, u.getPassword())) {

            // System.out.println("Password matches");
            String name = u.getName();

            List<Category> options = triviaSvc.getCategories();
            // System.out.println(">>>OPTIONS LIST: " + options);

            sess.setAttribute("name", name);

            model.addAttribute("name", name);
            model.addAttribute("options", options);
            return "start";

        } else {

            error = "WRONG PASSWORD!!";
            // System.out.println("Password does not match");
            model.addAttribute("error", error);
            return "login";
        }
    }

    @RequestMapping("/register")
    public String registerPage() {
        return "index";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }

    // @GetMapping("/trivia")
    // public String getCategories(Model model, HttpSession sess) {

    //     List<Category> options = triviaSvc.getCategories();
    //     System.out.println(">>>OPTIONS LIST: " + options);
    //     String name = (String) sess.getAttribute("name");
    //     sess.setAttribute("name", name);
    //     System.out.println("NAME FROM getcat:  " + name);

    //     model.addAttribute("name", name);
    //     model.addAttribute("options", options);
    //     return "start";
    // }

    @PostMapping("/trivia")
    public String getTrivia(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String qn = form.getFirst("qn");
        String cat = form.getFirst("cat");
        String dif = form.getFirst("dif");
        String type = form.getFirst("type");
        triviaSvc.getTrivia(qn, cat, dif, type);

        sess.setAttribute("qn", qn);
        
        String name = (String) sess.getAttribute("name");
        sess.setAttribute("name", name);
        System.out.println("NAME FROM TRIVIA METHOD:  " + name);

        return listByPage(model, 1);
    }


    @GetMapping("/trivia/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") int pageNum) {
        Page<Trivia> page = triviaSvc.listByPage(pageNum-1);
        // System.out.println("page: " + page);
        List<Trivia> listTrivia = page.getContent();

        // System.out.println("PageNum = " + pageNum);
        long totalItems = page.getTotalElements();
        // System.out.println("Total element = " + page.getTotalElements());
        int totalPages = page.getTotalPages();
        // System.out.println("Total Pages = " + page.getTotalPages());
      
        model.addAttribute("listTrivia", listTrivia);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        return "question";
    }

    // @PostMapping("/next")
    // public String saveOptions(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

    //     String name = (String) sess.getAttribute("name");
    //     sess.setAttribute("name", name);

    //     String ans = form.getFirst("ans");
    //     System.out.println("ANSWER SELECTED: " + ans);

    //     String qnNum = form.getFirst("page");
    //     System.out.println("QN NUMBER: " + qnNum);

    //     ansList.add((Integer.parseInt(qnNum)-1), ans);
    //     System.out.println("ans list: " + ansList.toString());
    //     sess.setAttribute("anslist", ansList);
    //     System.out.println("CALLED NEXT");

    //     return listByPage(model, Integer.parseInt(qnNum)+1);
    // }

    @GetMapping("/next")
    public String saveOptions(@RequestParam String ans, @RequestParam String page, Model model, HttpSession sess) {


        String qn = (String) sess.getAttribute("qn");
        sess.setAttribute("qn", qn);
        String[] ansList = new String[Integer.parseInt(qn)];

        String name = (String) sess.getAttribute("name");
        sess.setAttribute("name", name);

    
        System.out.println("ANSWER SELECTED: " + ans);
        System.out.println("QN NUMBER: " + page);

        ansList[Integer.parseInt(page)-1] = ans;
        // ansList.add((), ans);
        System.out.println("ans list: " + Arrays.toString(ansList));
        sess.setAttribute("anslist", ansList);

        return listByPage(model, Integer.parseInt(page)+1);
    }

    @GetMapping("/back")
    public String getOptions(@RequestParam String page, Model model, HttpSession sess) {

        sess.getAttribute("name");
        String[] ansList = (String[]) sess.getAttribute("anslist");

        String ans = ansList[Integer.parseInt(page)+1];
        System.out.println("CALLED BACK");

        return listByPage(model, Integer.parseInt(page)-1);

    }

    // SCOREBOARD
    @RequestMapping("/scoreboard")
    public String getScoreboard(Model model) {

        List<User> users = userSvc.getAllUsers();
        System.out.println(users.toString());

        model.addAttribute("listUsers", users);
        return "scoreboard";
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
    // public String getResults (@RequestParam(value="ans") String[] radioCheckedValues, Model model, HttpSession sess) {

    //     for (String s : radioCheckedValues) {
    //         System.out.println(s);
    //     }
    //     return null;
    // }

    private boolean isNull(String s) {
		return ((null == s) || (s.trim().length() <= 0));
	}
 
}
