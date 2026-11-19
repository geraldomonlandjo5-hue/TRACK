package com.track.controller;

import com.track.dao.UserDAO;
import com.track.security.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long WINDOW = 15 * 60_000L;
    /** 5 échecs / 15 min par adresse e-mail, 20 échecs / 15 min par adresse IP. */
    private static final RateLimiter MAIL_FAILURES = new RateLimiter(5, WINDOW);
    private static final RateLimiter IP_FAILURES = new RateLimiter(20, WINDOW);
    /** Hash factice : on le vérifie même si le compte n'existe pas, pour ne pas révéler les e-mails par le temps de réponse. */
    private static final String DUMMY_HASH = PasswordUtil.hash("timing-equalizer-2026".toCharArray());

    private final UserDAO users = new UserDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (CurrentUser.from(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        render(request, response, null, HttpServletResponse.SC_OK);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        if (!Csrf.valid(session, Csrf.LOGIN_TOKEN, request.getParameter(Csrf.PARAM))) {
            render(request, response, "La page a expiré. Réessaie.", HttpServletResponse.SC_OK);
            return;
        }
        String email = request.getParameter("email");
        String rawPassword = request.getParameter("password");
        if (email == null || rawPassword == null || email.isBlank() || rawPassword.isEmpty() || email.length() > 150) {
            render(request, response, "Renseigne ton adresse e-mail et ton mot de passe.", HttpServletResponse.SC_OK);
            return;
        }
        String mailKey = "mail:" + email.trim().toLowerCase(Locale.ROOT);
        String ipKey = "ip:" + request.getRemoteAddr();
        if (MAIL_FAILURES.isLimited(mailKey) || IP_FAILURES.isLimited(ipKey)) {
            render(request, response, "Trop de tentatives. Réessaie dans quelques minutes.", 429);
            return;
        }

        char[] password = rawPassword.toCharArray();
        try {
            var account = users.findActiveByEmail(email.trim());
            boolean ok = PasswordUtil.verify(password, account.map(UserDAO.UserAccount::passwordHash).orElse(DUMMY_HASH))
                    && account.isPresent();
            if (!ok) {
                MAIL_FAILURES.hit(mailKey);
                IP_FAILURES.hit(ipKey);
                getServletContext().log("Échec de connexion TRACK pour " + mailKey + " depuis " + request.getRemoteAddr());
                render(request, response, "Identifiants incorrects ou compte désactivé.", HttpServletResponse.SC_OK);
                return;
            }
            MAIL_FAILURES.reset(mailKey);
            UserDAO.UserAccount a = account.get();
            session.invalidate();
            HttpSession fresh = request.getSession(true);
            fresh.setAttribute(CurrentUser.SESSION_KEY, new CurrentUser(a.id(), a.email(), a.fullName(),
                    Role.fromName(a.role()), a.agencyId(), a.agencyName()));
            Csrf.ensure(fresh, Csrf.APP_TOKEN);
            fresh.setMaxInactiveInterval(30 * 60);
            try { users.touchLastLogin(a.id()); }
            catch (RuntimeException ex) { getServletContext().log("last_login_at non mis à jour", ex); }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (RuntimeException ex) {
            getServletContext().log("Échec de l'authentification TRACK", ex);
            render(request, response, "Connexion indisponible. Vérifie la configuration de la base de données.",
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response, String error, int status)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        request.setAttribute("loginToken", Csrf.ensure(session, Csrf.LOGIN_TOKEN));
        request.setAttribute("error", error);
        response.setStatus(status);
        response.setHeader("Cache-Control", "no-store");
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
}
