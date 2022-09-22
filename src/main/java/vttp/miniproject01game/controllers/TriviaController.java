package vttp.miniproject01game.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String email = form.getFirst("email").toLowerCase();
        String password = form.getFirst("password");

        if (!userSvc.checkUser(email)) {
            System.out.println("CREATING NEW USER");
            User u = new User();
            u = u.create(name, email, password);

            userSvc.saveUser(u);
            sess.setAttribute("name", name);
            // System.out.println("USER REGISTERED");

            List<Category> options = triviaSvc.getCategories();
            // System.out.println(">>>OPTIONS LIST: " + options);

            model.addAttribute("options", options);
            model.addAttribute("name", name);

            return "start";
        }

        return "login";
    }

    @PostMapping(path="/loginPage", consumes = "application/x-www-form-urlencoded", produces = "text/html")
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
            email = u.getEmail();

            List<Category> options = triviaSvc.getCategories();
            // System.out.println(">>>OPTIONS LIST: " + options);
            sess.setAttribute("name", name);
            sess.setAttribute("email", email);

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

    @RequestMapping("/start")
    public String startPage(Model model, HttpSession sess) {

        String name = (String) sess.getAttribute("name");

        List<Category> options = triviaSvc.getCategories();

        model.addAttribute("name", name);
        model.addAttribute("options", options);
        return "start";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession sess) {
        sess.invalidate();
        return "login";
    }

    @PostMapping("/trivia")
    public String getTrivia(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String qn = form.getFirst("qn");
        String cat = form.getFirst("cat");
        String dif = form.getFirst("dif");
        String type = form.getFirst("type");
        List<Trivia> trivia = triviaSvc.getTrivia(qn, cat, dif, type);
        sess.setAttribute("trivia", trivia);

        List<String> ansSheet = triviaSvc.getAnswers(triviaSvc.getTrivia(qn, cat, dif, type));
        sess.setAttribute("ansSheet", ansSheet);

        String[] ansList = new String[Integer.parseInt(qn)];
        sess.setAttribute("ansList", ansList);
        
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

    List<String> ansList = new ArrayList<>();

    @PostMapping("/next")
    public String saveOptions(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String page = form.getFirst("page");
        String ans = form.getFirst("ans");

        // System.out.println("ANSWER SELECTED: " + ans);
        // System.out.println("QN NUMBER: " + page);

        String[] ansList = (String[]) sess.getAttribute("ansList");
        ansList[Integer.parseInt(page)-1] = ans;

        System.out.println("ans list: " + Arrays.toString(ansList));
        sess.setAttribute("anslist", ansList);

        return listByPage(model, Integer.parseInt(page)+1);
    }

    @PostMapping("/back")
    public String getOptions(@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String[] ansList = (String[]) sess.getAttribute("anslist");

        String page = form.getFirst("page");

        String ans = ansList[Integer.parseInt(page)-2];
        System.out.println("page---" + page);
        System.out.println("CALLED BACK, ans selected: " + ans);

        model.addAttribute("checked", ans);
        return listByPage(model, Integer.parseInt(page)-1);

    }

    @PostMapping("/results")
    public String getResults (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {

        String qn = (String) sess.getAttribute("qn");
        sess.setAttribute("qn", qn);

        String ans = form.getFirst("ans");
        String page = form.getFirst("page");

        String[] ansList = (String[]) sess.getAttribute("ansList");
        ansList[Integer.parseInt(page)-1] = ans;
        sess.setAttribute("anslist", ansList);

        ansList = (String[]) sess.getAttribute("anslist");
        System.out.println(">>>>> USER ANSWERS" + Arrays.toString(ansList));
        List<String> answers= (List<String>) sess.getAttribute("ansSheet");
        System.out.println(">>>>> ANSWERS" + answers);

        String email = (String) sess.getAttribute("email");
        int score = triviaSvc.getScore(answers, ansList);
        int total = answers.size();

        userSvc.updateScore(email, score, total);
        System.out.println(score);

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        return "score";
    }

    // SCOREBOARD
    @RequestMapping("/scoreboard")
    public String getScoreboard(Model model) {

        List<User> users = userSvc.getAllUsers();
        // System.out.println(users.toString());

        model.addAttribute("listUsers", users);
        return "scoreboard";
    }
}
