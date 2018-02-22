
import org.softuni.summer.api.*;

@Controller
public class HomeController {

    @GetMapping(route = "/")
    public String index(){
        return "template:index";
    }

    @PostMapping(route = "/create")
    public String create(){
        return "template:create";
    }

    @GetMapping(route = "/delete/{id}/asd/{commentId}")
    public String delete(@PathVariable int id, @PathVariable int commentId){
        System.out.println(id);
        System.out.println(commentId);
        return "template:delete";
    }

    @GetMapping(route = "/delete/{id}")
    public String delete(@PathVariable int id){
        System.out.println(id);
        return "template:delete";
    }
}
