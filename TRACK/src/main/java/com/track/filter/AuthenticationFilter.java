package com.track.filter;

import com.track.dao.UserDAO;
import com.track.security.CurrentUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Exige une session connectée ; revérifie périodiquement que le compte est toujours actif. */
public class AuthenticationFilter implements Filter {
    private static final long RECHECK_MILLIS = 60_000L;
    private static final String LAST_CHECK = "accountCheckedAt";
    private final UserDAO users = new UserDAO();

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        CurrentUser user = session == null ? null : (CurrentUser) session.getAttribute(CurrentUser.SESSION_KEY);
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }

        long now = System.currentTimeMillis();
        Long last = (Long) session.getAttribute(LAST_CHECK);
        if (last == null || now - last > RECHECK_MILLIS) {
            boolean active = true;
            try { active = users.isStillActive(user.getId()); }
            catch (RuntimeException ex) { request.getServletContext().log("Vérification du compte impossible", ex); }
            if (!active) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            session.setAttribute(LAST_CHECK, now);
        }
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(req, res);
    }
}
