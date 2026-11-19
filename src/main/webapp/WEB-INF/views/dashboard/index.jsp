<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<div class="app-shell">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content">
        <div class="topbar">
            <div><span class="topbar-meta">Espace administrateur</span><h1>Tableau de bord</h1></div>
            <div class="topbar-user"><span>Administrateur</span><span class="avatar" aria-hidden="true">A</span></div>
        </div>
        <section class="ledger" aria-label="Statistiques des colis">
            <div class="ledger-cell"><span>Total des colis</span><strong>${totalShipments}</strong><small>Tous les colis enregistrés</small></div>
            <div class="ledger-cell"><span>En transit</span><strong>—</strong><small>À connecter aux statistiques</small></div>
            <div class="ledger-cell"><span>Livrés</span><strong>—</strong><small>À connecter aux statistiques</small></div>
            <div class="ledger-cell"><span>En attente</span><strong>—</strong><small>À connecter aux statistiques</small></div>
        </section>
        <section class="panel mt-4">
            <div class="panel-head">
                <div><h2>Derniers colis</h2><p>Les expéditions récemment enregistrées</p></div>
                <a class="btn btn-track btn-sm" href="${pageContext.request.contextPath}/shipments/new">+ Nouveau colis</a>
            </div>
            <div class="table-responsive"><table class="table align-middle"><thead><tr><th>Suivi</th><th>Destinataire</th><th>Statut</th><th>Date</th></tr></thead><tbody>
            <c:choose><c:when test="${not empty recentShipments}">
                <c:forEach var="s" items="${recentShipments}">
                    <tr><td class="track-no">${s.trackingNumber}</td><td>${s.recipientName}</td><td><t:status value="${s.status}"/></td><td class="cell-date"><t:datetime value="${s.createdAt}"/></td></tr>
                </c:forEach>
            </c:when><c:otherwise><tr><td colspan="4" class="empty-row">Aucun colis enregistré pour le moment.</td></tr></c:otherwise></c:choose>
            </tbody></table></div>
        </section>
    </main>
</div>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
