package ru.vasyunin.springcloudrive.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Component;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;

@Component
@Transactional
public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user = userService.getUserByUsername(authentication.getName());
        user.setLastseenNow();

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // Getting requested url from session
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest)request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        // and redirect if not null
        if (defaultSavedRequest != null) {
            System.out.println(defaultSavedRequest.getRedirectUrl());
            getRedirectStrategy().sendRedirect(request, response, defaultSavedRequest.getRedirectUrl());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    public CustomAuthSuccessHandler() {
        super();
        setUseReferer(true);
    }

    //region Autowires
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    //endregion
}
