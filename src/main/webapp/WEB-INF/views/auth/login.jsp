<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<main class="auth-shell">
    <aside class="auth-aside">
        <a class="brand" href="${pageContext.request.contextPath}/track"><span class="brand-mark">T</span><span>TRACK</span></a>
        <div>
            <h2>Chaque colis, du départ à la remise.</h2>
            <p>Enregistrez les envois, suivez leur progression et gardez vos clients informés.</p>
        </div>
        <svg class="auth-route" viewBox="0 0 420 260" fill="none" aria-hidden="true">
            <path d="M10 240 C 110 240, 120 150, 210 150 S 320 60, 410 40" stroke="#ffc400" stroke-width="4" stroke-linecap="round" stroke-dasharray="2 12"/>
            <circle cx="10" cy="240" r="9" fill="#0e2b3d" stroke="#ffffff" stroke-width="4"/>
            <circle cx="210" cy="150" r="9" fill="#0e2b3d" stroke="#ffffff" stroke-width="4"/>
            <circle cx="410" cy="40" r="11" fill="#ffc400" stroke="#0e2b3d" stroke-width="4"/>
        </svg>
    </aside>
    <section class="auth-main">
        <div class="auth-card">
            <div class="brand-mobile"><a class="brand" href="${pageContext.request.contextPath}/track"><span class="brand-mark">T</span><span>TRACK</span></a></div>
            <h1>Bienvenue</h1>
            <p class="text-secondary mb-4">Connectez-vous à votre espace de suivi.</p>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <label class="form-label" for="email">Adresse e-mail</label>
                <input class="form-control mb-3" id="email" type="email" name="email" required placeholder="admin@track.local" autocomplete="username">
                <label class="form-label" for="password">Mot de passe</label>
                <input class="form-control mb-3" id="password" type="password" name="password" required placeholder="Votre mot de passe" autocomplete="current-password">
                <button class="btn btn-track w-100 mt-2">Se connecter</button>
            </form>
            <div class="demo-hint">Démo locale : admin@track.local / TrackDemo2026!</div>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
