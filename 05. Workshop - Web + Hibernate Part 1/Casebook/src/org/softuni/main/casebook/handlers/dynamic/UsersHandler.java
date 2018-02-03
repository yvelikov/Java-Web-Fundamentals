package org.softuni.main.casebook.handlers.dynamic;

import org.softuni.main.casebook.annotations.ApplicationRequestHandler;
import org.softuni.main.casebook.annotations.Get;
import org.softuni.main.casebook.annotations.Post;
import org.softuni.main.database.models.User;
import org.softuni.main.database.repositories.UserRepository;
import org.softuni.main.javache.WebConstants;
import org.softuni.main.javache.http.*;

import javax.persistence.EntityManagerFactory;
import java.util.UUID;

@ApplicationRequestHandler
public class UsersHandler extends BaseDynamicHandler {

    protected UsersHandler(HttpSessionStorage sessionStorage, EntityManagerFactory entityManagerFactory) {
        super(sessionStorage, entityManagerFactory);
    }

    @Get(route = "/register")
    public HttpResponse register(HttpRequest request, HttpResponse response) {
        return super.view("register", request, response);
    }

    @Post(route = "/register")
    public HttpResponse registerConfirm(HttpRequest request, HttpResponse response) {
        UserRepository userRepository = new UserRepository(super.entityManagerFactory);

        String username = request.getBodyParameters().get("username");
        String password = request.getBodyParameters().get("password");

        userRepository.doAction("create", username, password);

        userRepository.dismiss();

        return super.redirect("/login", request, response);
    }

    @Get(route = "/login")
    public HttpResponse login(HttpRequest request, HttpResponse response) {
        return super.view("login", request, response);
    }

    @Post(route = "/login")
    public HttpResponse loginConfirm(HttpRequest request, HttpResponse response) {
        UserRepository userRepository = new UserRepository(super.entityManagerFactory);

        String username = request.getBodyParameters().get("username");
        String password = request.getBodyParameters().get("password");

        User user = (User) userRepository.doAction("findByUsername", username);


        if (user == null) {
            return super.redirect("/login", request, response);
        } else if (!user.getPassword().equals(password)) {
            return super.redirect("/login", request, response);
        }

        String sessionId = UUID.randomUUID().toString();

        HttpSession session = new HttpSessionImpl(sessionId);
        session.addAttribute("user-id", user.getId());
        super.sessionStorage.setSession(session.getId(), session);

        response.addCookie(new HttpCookieImpl(WebConstants.SERVER_SESSION_TOKEN, session.getId()));

        userRepository.dismiss();
        return super.redirect("/home", request, response);
    }

    @Post(route = "/logout")
    public HttpResponse logout(HttpRequest request, HttpResponse response) {
        if (!super.isLoggedIn(request)) {
            return super.redirect("/login", request, response);
        }

        HttpCookie cookie = request.getCookies().get(WebConstants.SERVER_SESSION_TOKEN);
        this.sessionStorage.getSession(cookie.getValue()).invalidate();
        this.sessionStorage.removeSession(cookie.getValue());

        response.addCookie(new HttpCookieImpl("JAVACHE", "token=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT"));

        return super.redirect("/", request, response);
    }

    @Get(route = "/profile")
    public HttpResponse profile(HttpRequest request, HttpResponse response) {

        if (!this.isLoggedIn(request)) {
            return super.redirect("/login", request, response);
        }

        UserRepository userRepository = new UserRepository(super.entityManagerFactory);
        User user = super.getCurrentUser(request, userRepository);

        StringBuilder listFriends = new StringBuilder("<table class=\"table\">");

        listFriends
                .append("<thead>")
                .append("<tr>")
                .append("<th>#</th>")
                .append("<th>Friend Name</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        int index = 0;

        for (User friend : user.getFriends()) {
            listFriends.append("<tr>")
                    .append("<td>" + ++index +"</td>")
                    .append("<td>" + friend.getUsername() +"</td>")
                    .append("</tr>");
        }

        listFriends.append("</tbody>");
        listFriends.append("</table>");

        this.viewData.putIfAbsent("username", user.getUsername());
        this.viewData.putIfAbsent("friends", listFriends.toString());

        userRepository.dismiss();
        return super.view("profile", request, response);
    }

    @Post(route = "/add-friend")
    public HttpResponse addFriend(HttpRequest request, HttpResponse response) {
        if (!this.isLoggedIn(request)) {
            return super.redirect("/login", request, response);
        }

        UserRepository userRepository = new UserRepository(super.entityManagerFactory);
        User user = super.getCurrentUser(request, userRepository);

        userRepository.doAction("addFriend", user.getUsername(), request.getBodyParameters().get("friend"));
        userRepository.dismiss();

        return super.redirect("/home", request, response);
    }
}
